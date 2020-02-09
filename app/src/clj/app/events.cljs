(ns app.events
  (:require [app.db :refer [default-db]]
            [clojure.spec.alpha :as s]
            [money.core.transaction :as t]
            [re-frame.core :as rf]))

(defn check-and-throw
  "Throws an exception if `db` doesn’t match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw :app.db/db)))

(def transaction-interceptors [check-spec-interceptor
                               (rf/path :data :transactions) ])

(rf/reg-event-db
  :initialize-db
  [check-spec-interceptor]
  (fn [_ [_ stored]]
    (assoc default-db :data stored)))

(rf/reg-event-db
  :remove-transaction
  transaction-interceptors
  (fn [transactions [_ id-to-remove]]
    (t/remove-transaction-by-id transactions id-to-remove)))
