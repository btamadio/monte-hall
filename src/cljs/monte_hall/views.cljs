(ns monte-hall.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as reagent :refer [atom]]
   [monte-hall.subs :as subs]
   [monte-hall.events :as events]))

(defn door-image
  [open? prize?]
  (case open?
    false "images/door.jpg"
    true (if prize? "images/prize.gif" "images/goat.png")))

(defn door
  [{:keys [id open? prize? selected?]}]
  [:div.card.has-text-centered {:on-click (when (not open?) #(dispatch [::events/set-selected-door id]))}
   [:div.card-content
    [:figure.image.is-2by3
     [:img.door {:src (door-image open? prize?)
                 :class (if open? "py-6")}]]]])

(defn select-button
  [on-click]
  (if-let [door-id @(subscribe [::subs/selected-door])]
    [:button.button.is-medium.is-dark {:on-click on-click}
     "Choose Door " (inc door-id)]
    [:button.button.is-medium {:disabled true} "Choose a door"]))

(defn new-game-button
  []
  [:button.button.is-primary.is-medium {:class "new-game"
            :on-click #(dispatch [::events/new-game])}
   "New Game"])

(defn play-button
  []
  (let [game-stage @(subscribe [::subs/game-stage])]
    (case game-stage
      :new-game [select-button #(dispatch [::events/first-reveal])]
      :first-reveal [select-button #(dispatch [::events/final-reveal])]
      :final-reveal [new-game-button])))

(defn conf-matrix-table
  [conf-matrix]
  [:table
   [:thead [:tr
            [:td]
            [:th {:scope "col"} "won"]
            [:th {:scope "col"} "lost"]]]
   [:tbody [:tr
            [:th {:scopoe "row"} "switched"]
            [:td (:switched-won conf-matrix)]
            [:td (:switched-lost conf-matrix)]]
    [:tr
     [:th {:scope "row"} "stayed"]
     [:td (:stayed-won conf-matrix)]
     [:td (:stayed-lost conf-matrix)]]]])

(defn notif-text
  [game-stage game-result]
  (case game-stage
    :new-game "Select a door and click the button to confirm"
    :first-reveal "Choose the same door again or switch"
    :final-reveal (str "You " game-result "!")))

(defn notif-class
  [game-stage winner?]
  (case game-stage
    :new-game "is-primary"
    :first-reveal "is-info"
    :final-reveal (if winner? "is-success" "is-danger")))

(defn main-panel
  []
  (let [game-mode @(subscribe [::subs/game-mode])
        game-stage @(subscribe [::subs/game-stage])
        game-result @(subscribe [::subs/game-result])
        winner? @(subscribe [::subs/winner?])
        conf-matrix @(subscribe [::subs/confusion-matrix])
        doors @(subscribe [::subs/doors])]
    [:div.block
     [:div.columns
      [:div.column.is-three-fifths
       [:div.notification.is-size-5 {:class (notif-class game-stage winner?)} (notif-text game-stage game-result)]]]
     [:div.columns.is-centered
      [:div.column.is-one-fifth
       [door (doors 0)]]
      [:div.column.is-one-fifth
       [door (doors 1)]]
      [:div.column.is-one-fifth
       [door (doors 2)]]
      [:div.column.is-two-fifths
       [:div.box
        [:div.content
         [conf-matrix-table conf-matrix]]]]]

     (when (= game-mode :play) [play-button])]))
