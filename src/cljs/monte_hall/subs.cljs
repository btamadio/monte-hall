(ns monte-hall.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::doors
 (fn [db]
   (:doors db)))

(reg-sub
 ::game-mode
 (fn [db]
   (:mode db)))

(reg-sub
 ::door-selected?
 :<- [::doors]
 (fn [doors [_ id]]
   ((doors id) :selected?)))

(reg-sub
 ::door-prize?
 :<- [::doors]
 (fn [doors [_ id]]
   ((doors id) :prize?)))

(reg-sub
 ::door-open?
 :<- [::doors]
 (fn [doors [_ id]]
   ((doors id) :open?)))

(reg-sub
 ::selected-door
 :<- [::doors]
 (fn [doors [_ _]]
   (:id (first (filter :selected? doors)))))

(reg-sub
 ::game-stage
 (fn [db]
   (cond
     (nil? (:first-selection db)) :new-game
     (nil? (:second-selection db)) :first-reveal
     :else :final-reveal)))
