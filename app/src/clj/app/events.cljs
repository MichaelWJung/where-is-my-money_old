(ns app.events
  (:require [app.db :refer [default-db]]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize-db
  (fn [_ [_ stored]]
    (assoc default-db :data stored)))
