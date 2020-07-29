(ns monte-hall.db)

(def new-game
  {:doors
   [{:id 0 :open? false :prize? false :selected? false}
    {:id 1 :open? false :prize? false :selected? false}
    {:id 2 :open? false :prize? false :selected? false}]
   :first-selection nil
   :second-selection nil})

(def default-db
  (merge new-game {:mode :play :history []}))

