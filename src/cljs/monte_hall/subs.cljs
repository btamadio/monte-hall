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

; (subscribe ::door 1) as an example
(reg-sub
 ::door-selected?
 :<- [::doors]
 (fn [doors [_ id]]
   ((first (filter #(= id (:id %)) doors)) :selected?)))
