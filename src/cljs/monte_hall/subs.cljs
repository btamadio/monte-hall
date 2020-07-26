(ns monte-hall.subs
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 ::doors
 (fn [db]
   (:doors db)))

(reg-sub
 ::selected-door
 (fn [db]
   (:selected-door db)))

(reg-sub
 ::door-selected?
 :<- [::selected-door]
 (fn [selected-door [_ id]]
   (= selected-door id)))

(reg-sub
 ::door-open?
 :<- [::doors]
 (fn [doors [_ id]]
   ((doors id) :open?)))
