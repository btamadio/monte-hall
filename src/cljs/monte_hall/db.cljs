(ns monte-hall.db)

(def default-db
  {:doors [{:id 0 :open? false :prize? false}
           {:id 1 :open? false :prize? false}
           {:id 2 :open? false :prize? false}]
   :selected-door nil
   :stage :uninitialized})
