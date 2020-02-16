(ns app.events
  (:require [app.db :as db]
            [clojure.spec.alpha :as s]
            [money.core.transaction :as t]
            [money.screens.transaction :as st]
            [re-frame.core :as rf]))

(defn check-and-throw
  "Throws an exception if `db` doesn’t match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (println "db: " db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw :app.db/db)))

(def navigation-interceptors [check-spec-interceptor
                              (rf/path :navigation)])
(def screen-states-interceptors [check-spec-interceptor
                                 (rf/path ::db/screen-states)])
(def transaction-interceptors [check-spec-interceptor
                               (rf/path :data :transactions)])

(rf/reg-event-db
  :initialize-db
  [check-spec-interceptor]
  (fn [_ [_ stored]]
    (assoc db/default-db :data stored)))

(rf/reg-event-db
  :remove-transaction
  transaction-interceptors
  (fn [transactions [_ id-to-remove]]
    (t/remove-transaction-by-id transactions id-to-remove)))

(rf/reg-event-db
  :update-transaction-data
  screen-states-interceptors
  (fn [screens [_ transaction-data]]
    (update screens
            ::st/transaction-screen-state
            #(st/update-screen % transaction-data))))

(rf/reg-event-db
  :save-transaction
  [check-spec-interceptor]
  (fn [db _]
    (let [screen-data
          (get-in db [::db/screen-states ::st/transaction-screen-state])

          new-transaction
          (st/screen-data->transaction 1 screen-data)]
      (update-in db [:data :transactions]
                 #(t/add-transaction % new-transaction)))))

; (rf/reg-event-db
;   :save-transaction
;   [check-spec-interceptor]
;   (fn [db [_ transaction]]
;     (-> db
;         (assoc :navigation :account)
;         (update-in [:data :transactions]
;                    #(t/add-simple-transaction % transaction)))))

(rf/reg-event-db
  :edit-transaction
  [check-spec-interceptor]
  (fn [db [_ id]]
    (let [transaction (get-in db [:data :transactions id])]
      (-> db
          (assoc-in [::db/screen-states ::st/transaction-screen-state]
                    (st/transaction->screen-data 1 transaction))
          (assoc :navigation :transaction)))))

(rf/reg-event-db
  :new-transaction
  [check-spec-interceptor]
  (fn [db _]
    (let [id (inc (get-in db [:highest-ids :transaction]))]
      (-> db
          (assoc-in [::db/screen-states ::st/transaction-screen-state]
                    (st/new-transaction id 100000000 0))
          (assoc-in [:highest-ids :transaction] id)
          (assoc :navigation :transaction)))))

(rf/reg-event-db
  :close-transaction-screen
  navigation-interceptors
  (fn [_ _] :account))
