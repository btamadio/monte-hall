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
           "https://placekitten.com/200/300"
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
  (when-let [door-id @(subscribe [::subs/selected-door])]
    [:button {:class "select-door"
              :on-click on-click}
     "Choose Door " (inc door-id)]))

(defn new-game-button
  []
  [:button {:class "new-game"
            :on-click #(dispatch [::events/new-game])}
   "New Game"])

(defn play-button
  []
  (case @(subscribe [::subs/game-stage])
    :new-game [select-button #(dispatch [::events/first-reveal])]
    :first-reveal [select-button #(dispatch [::events/final-reveal])]
    :final-reveal [new-game-button]))

(defn main-panel
  []
  [:<>
   [doors]
   (when (= @(subscribe [::subs/game-mode]) :play) 
     [play-button])])
