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
;           "https://i.giphy.com/media/3ov9jWu7BuHufyLs7m/giphy.gif"
           "https://i.giphy.com/media/l1rrKjeprm2VsbCc1w/giphy.gif"
;           "https://placekitten.com/200/300"
           "https://thriftyhomesteader.com/wp-content/uploads/2020/02/1.pin_-2-200x300.png")))

(defn door-class
  [open? selected?]
  (if open? "door-open"
      (if selected? "door-selected" "door"))
)

(defn door
  [{:keys [id open? prize? selected?]}]
  [:img#door {:src (door-image open? prize?)
              :width 200
              :height 300
              :class (door-class open? selected?)
              :on-click (when (not open?) #(dispatch [::events/set-selected-door id]))}])

(defn doors
  []
  (let [doors @(subscribe [::subs/doors])]
    [:div
     (for [d doors]
       ^{:key (:id d)} [door d])]))

(defn select-button
  [on-click]
  (if-let [door-id @(subscribe [::subs/selected-door])]
    [:button {:class "select-door"
              :on-click on-click}
     "Choose Door " (inc door-id)]
    [:p "Choose a door!"]))

(defn new-game-button
  []
  [:button {:class "new-game"
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

(defn main-panel
  []
  (let [game-mode @(subscribe [::subs/game-mode])
        game-stage @(subscribe [::subs/game-stage])
        game-result @(subscribe [::subs/game-result])
        conf-matrix @(subscribe [::subs/confusion-matrix])]
    [:<>
     [:div.game
      [doors]
      (when (= game-mode :play) [play-button])
      (when (= game-stage :final-reveal) [:p "You " game-result "!"])]
     [:div.conf-matrix
      [conf-matrix-table conf-matrix]]]))
