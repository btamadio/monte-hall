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
  [{:keys [id open? prize?]}]
  (fn []
    [:img#door {:src (door-image open? prize?)
           :width 200
           :height 300
           :border (when @(subscribe [::subs/door-selected? id]) "5px")
           :on-click #(dispatch [::events/select-door id])}]))

(defn door-set
  []
  (let [doors @(subscribe [::subs/doors])]
    [:div
     (for [d doors]
       ^{:key (:id d)} [door d])]))

(defn main-panel
  []
  [:<>
   [door-set]])
