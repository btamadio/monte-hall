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

(defn door
  [{:keys [id open? prize?]} selected?]
  [:img#door {:src (door-image open? prize?)
              :width 200
              :height 300
              :border (when selected? "5px")
              :on-click (when (not open?) #(dispatch [::events/set-selected-door id]))}])

(defn door-set
  []
  (let [doors @(subscribe [::subs/doors])
        selected-door @(subscribe [::subs/selected-door])]
    [:div
     (for [d doors]
       ^{:key (:id d)} [door d (= (:id d) selected-door)])]))

(defn select-button
  [on-click]
  (when-let [door-id @(subscribe [::subs/selected-door])]
    
    [:button {:on-click on-click}
     "Choose Door " (inc door-id)]))

(defn new-game-button
  []
  [:button {:on-click #(dispatch [::events/new-game])}
   "New Game"])

(defn main-panel
  []
  [:<>
   [door-set]
   (case @(subscribe [::subs/game-stage])
     :new-game [select-button #(dispatch [::events/first-reveal])]
     :first-reveal [select-button #(dispatch [::events/second-reveal])]
     :final-reveal [new-game-button])])
