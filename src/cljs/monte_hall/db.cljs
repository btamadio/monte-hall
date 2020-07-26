(ns monte-hall.db)

(def new-game
  {:doors
   [{:id 0 :open? false :prize? false}
    {:id 1 :open? false :prize? false}
    {:id 2 :open? false :prize? false}]
   :selected-door nil
   :stage :new-game})

(def default-db
  (merge new-game {:history []}))
