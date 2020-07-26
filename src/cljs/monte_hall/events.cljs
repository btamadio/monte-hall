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
   (rf/dispatch [::allocate-prize])
   (merge db db/new-game)))

(rf/reg-event-fx
 ::allocate-prize
 [(rf/inject-cofx :random-int 3)]
 (fn [cofx _]
   (let [val (:random-int cofx)
         db (:db cofx)]
     {:db (assoc-in db [:doors val :prize?] true)})))

(rf/reg-event-db
 ::set-selected-door
 (fn [db [_ id]]
   (assoc db :selected-door id)))

; Definitely should have some unit tests for this logic
(defn first-reveal
  [doors selected-door tiebreaker]
  (let [choices (filter #(and (not (:prize? %)) (not= (:id %) selected-door)) doors)
        num-choices (count choices)]
    (if (= num-choices 1)
      (:id  (first choices))
      (:id (nth choices tiebreaker)))))

(rf/reg-event-fx
 ::first-reveal
 [(rf/inject-cofx :random-int 2)]
 (fn [cofx [_ _]]
   (let [db (:db cofx)
         tiebreaker (:random-int cofx)
         door-to-open (first-reveal (:doors db) (:selected-door db) tiebreaker)]
     {:db (-> db
              (assoc-in [:doors door-to-open :open?] true)
              (assoc :stage :first-reveal))})))

(rf/reg-event-db
 ::second-reveal
 (fn [db [_ _]]
   (let [selected-door (:selected-door db)]
     (-> db
         (assoc-in [:doors selected-door :open?] true)
         (assoc :stage :final-reveal)))))

(rf/reg-cofx
 :random-int
 (fn [coeffects val]
   (assoc coeffects :random-int (rand-int val))))
