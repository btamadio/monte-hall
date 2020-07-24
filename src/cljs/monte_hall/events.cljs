(ns monte-hall.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [monte-hall.db :as db]))

(reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(defn select-door
  [id]
  (fn
    [door]
    (assoc door :selected? (= id (:id door)))))

(reg-event-db
 ::select-door
 (fn [db [_ id]]
   (update-in db [:doors] #(map (select-door id) %))))
