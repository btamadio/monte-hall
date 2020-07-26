(ns monte-hall.events
  (:require
   [re-frame.core :as rf]
   [monte-hall.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   (rf/dispatch [::allocate-prize])
   db/default-db))

(rf/reg-event-db
 ::new-game
 (fn [db _]
   (merge db/new-game db)))

(rf/reg-event-fx
 ::allocate-prize
 [(rf/inject-cofx :random-int 3)]
 (fn [cofx _]
   (let [val (:random-int cofx)
         db (:db cofx)]
     {:db (assoc-in db [:doors val :prize?] true)})))

(rf/reg-event-db
 ::select-door
 (fn [db [_ id]]
   (assoc db :selected-door id)))

(rf/reg-cofx
 :random-int
 (fn [coeffects val]
   (assoc coeffects :random-int (rand-int val))))
