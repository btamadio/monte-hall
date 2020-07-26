(ns monte-hall.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx reg-cofx]]
   [monte-hall.db :as db]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-fx
 ::allocate-prize
 [(inject-cofx :random-int 3)]
 (fn [cofx _]
   (let [val (:random-int cofx)
         db (:db cofx)]
     {:db (assoc-in db [:doors val :prize?] true)})))

(reg-event-db
 ::select-door
 (fn [db [_ id]]
   (println db)
   (assoc db :selected-door id)))

(reg-cofx
 :random-int
 (fn [coeffects val]
   (assoc coeffects :random-int (rand-int val))))
