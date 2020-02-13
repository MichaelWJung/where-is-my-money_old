(ns app.events
  (:require [app.db :refer [default-db]]
            [clojure.spec.alpha :as s]
            [money.core.transaction :as t]
            [re-frame.core :as rf]))

(defn check-and-throw
  "Throws an exception if `db` doesn’t match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (println "db: " db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw :app.db/db)))

(def transaction-interceptors [check-spec-interceptor
                               (rf/path :data :transactions)])

(def navigation-interceptors [check-spec-interceptor
                              (rf/path :navigation)])

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

(rf/reg-event-db
  :open-new-transaction-screen
  navigation-interceptors
  (fn [_ _] :transaction))

(rf/reg-event-db
  :close-transaction-screen
  navigation-interceptors
  (fn [_ _] :account))
