(ns monte-hall.events
  (:require
   [re-frame.core :as rf]
   [monte-hall.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(rf/reg-event-fx
 ::new-game
 [(rf/inject-cofx :random-int 3)]
 (fn [cofx _]
   (let [db (:db cofx)
         random-door (:random-int cofx)
         game-result (dissoc db :history :mode)]
     {:db (-> db
              (update :history conj game-result)
              (merge db/new-game)
              (assoc-in [:doors random-door :prize?] true))})))

(defn deselect-all
  [doors]
  (into [] (map #(assoc % :selected? false) doors)))

(rf/reg-event-db
 ::set-selected-door
 (fn [db [_ id]]
   (-> db
       (assoc :doors (deselect-all (:doors db)))
       (assoc-in [:doors id :selected?] true))))

(defn eligible?
  [door]
  (and (not (door :prize?)) (not (door :selected?))))

(defn first-reveal
  [doors tiebreaker]
  (let [eligible (filter eligible? doors)
        num-eligible (count eligible)]
    (if (= num-eligible 1)
      (:id (first eligible))
      (:id (nth eligible tiebreaker)))))

(rf/reg-event-fx
 ::first-reveal
 [(rf/inject-cofx :random-int 2)]
 (fn [cofx [_ _]]
   (let [db (:db cofx)
         tiebreaker (:random-int cofx)
         door-to-open (first-reveal (:doors db) tiebreaker)]
     {:db (-> db
              (assoc-in [:doors door-to-open :open?] true)
              (assoc :first-selection door-to-open))})))

(rf/reg-event-db
 ::final-reveal
 (fn [db [_ _]]
   (let [selected-door (:id (first (filter :selected? (:doors db))))]
     (-> db
         (assoc-in [:doors selected-door :open?] true)
         (assoc :second-selection selected-door)))))

(rf/reg-cofx
 :random-int
 (fn [coeffects val]
   (assoc coeffects :random-int (rand-int val))))
