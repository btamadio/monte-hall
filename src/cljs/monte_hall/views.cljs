(ns monte-hall.views
  (:require
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as reagent :refer [atom]]
   [monte-hall.subs :as subs]
   [monte-hall.events :as events]))

(defn door-image
  [open? prize?]
  (case open?
    false "http://www.sbrusticdoors.com/wp-content/uploads/2011/11/double-big-arch-rustic-door-200x300.jpg"
    true (if prize?
           "https://i.giphy.com/media/l1rrKjeprm2VsbCc1w/giphy.gif"
           "https://thriftyhomesteader.com/wp-content/uploads/2020/02/1.pin_-2-200x300.png")))

(defn door-class
  [open? selected?]
  (if open? "door-open"
      (if selected? "door-selected" "door"))
)

(defn door
  [{:keys [id open? prize? selected?]}]
  [:div.card.has-text-centered {:on-click (when (not open?) #(dispatch [::events/set-selected-door id]))}
   [:div.card-content
    [:img.door {:src (door-image open? prize?)
                :class (door-class open? selected?)}]]])

(defn doors
  []
  (let [doors @(subscribe [::subs/doors])]
    [:div.columns
     (for [d doors]
       ^{:key (:id d)} [:div.column [door d]])]))

(defn select-button
  [on-click]
  (if-let [door-id @(subscribe [::subs/selected-door])]
    [:button.button.is-primary.is-medium {:class "select-door"
              :on-click on-click}
     "Choose Door " (inc door-id)]))

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

(defn nav-bar
  []
  [:nav.navbar.is-fixed-top
   [:div.navbar-brand
    [:a.navbar-item
     [:h1.title.is-1 "Monte Hall"]]]
   [:div.navbar-menu
    [:div.navbar-start
     [:a.navbar-item "Play"]]
    [:div.navbar-end
     [:a.navbar-item {:href "https://github.com/btamadio/monte-hall" :target "_blank"}
      [:span.icon.is-large
       [:i.fa.fa-2x.fa-github]]]]]])

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
    [:div.container
     [:div.block [nav-bar]]
     [:div.block
      [:div.game
       [:div.notification (notif-text game-stage game-result)]
       [doors]
       (when (= game-mode :play) [play-button])]]
     [:div.block
      [:div.box
       [:div.content.is-medium
        [conf-matrix-table conf-matrix]]]]]))
