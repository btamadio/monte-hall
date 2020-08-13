(ns monte-hall.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as reagent :refer [atom]]
   [monte-hall.subs :as subs]
   [monte-hall.events :as events]))

(defn door-image
  [open? prize?]
  (case open?
    false "images/closed-door.jpg"
    true (if prize? "images/prize.gif" "images/goat.png")))


(defn door
  [{:keys [id open? prize? selected?]}]
  [:div.card.has-text-centered {:on-click (when (not open?) #(dispatch [::events/set-selected-door id]))}
   [:div.card-content
    [:img.door {:src (door-image open? prize?)
                :height (if open? 300 200)
                :width (if open? 300 200)}]]])

(defn doors
  []
  (let [doors @(subscribe [::subs/doors])]
    [:div.columns
     (for [d doors]
       ^{:key (:id d)} [:div.column [door d]])]))

(defn select-button
  [on-click]
  (if-let [door-id @(subscribe [::subs/selected-door])]
    [:button.button.is-primary.is-medium {:on-click on-click}
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
    :new-game "Select a door"
    :first-reveal "Now choose another door!"
    :final-reveal (str "You " game-result "!")))

(defn main-panel
  []
  (let [game-mode @(subscribe [::subs/game-mode])
        game-stage @(subscribe [::subs/game-stage])
        game-result @(subscribe [::subs/game-result])
        conf-matrix @(subscribe [::subs/confusion-matrix])]
    [:div.block [:div.block
                 [:div.game
                  [:div.notification (notif-text game-stage game-result)]
                  [doors]
                  (when (= game-mode :play) [play-button])]]
     [:div.block
      [:div.box
       [:div.content.is-medium
        [conf-matrix-table conf-matrix]]]]]))
