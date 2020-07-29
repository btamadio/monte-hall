(ns monte-hall.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

; Level 2 subs: just extract keys from DB
(reg-sub
 ::doors
 (fn [db]
   (:doors db)))

(reg-sub
 ::game-mode
 (fn [db]
   (:mode db)))

(reg-sub
 ::history
 (fn [db]
   (:history db)))

(reg-sub
 ::first-selection
 (fn [db]
   (:first-selection db)))

(reg-sub
 ::second-selection
 (fn [db]
   (:second-selection db)))

; Level 3 subs: derived from level 2
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
 :<- [::first-selection]
 :<- [::second-selection]
 (fn [[first-selection second-selection] [_ _]]
   (cond
     (nil? first-selection) :new-game
     (nil? second-selection) :first-reveal
     :else :final-reveal)))
