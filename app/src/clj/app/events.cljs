(ns app.events
  (:require [app.db :as db]
            [clojure.spec.alpha :as s]
            [money.adapters.account :as aa]
            [money.core.transaction :as t]
            [money.screens.account :as sa]
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

(rf/reg-cofx
  :now
  (fn [cofx _data]
    (assoc cofx :now (.now js/Date))))

(rf/reg-event-db
  :initialize-db
  [check-spec-interceptor]
  (fn [_ [_ stored]]
    (assoc db/default-db :data stored)))

(rf/reg-event-db
  :set-account
  [check-spec-interceptor]
  (fn [db [_ account-idx]]
    (let [accounts (get-in db [:data :accounts])]
      (assoc-in db
                [::db/screen-states ::sa/account-screen-state ::sa/account-id]
                (aa/account-idx->id accounts account-idx)))))

(rf/reg-event-db
  :remove-transaction
  transaction-interceptors
  (fn [transactions [_ id-to-remove]]
    (t/remove-transaction-by-id transactions id-to-remove)))

(rf/reg-event-db
  :update-transaction-data
  [check-spec-interceptor]
  (fn [db [_ transaction-data]]
    (let [accounts (get-in db [:data :accounts])]
      (update-in db
                 [::db/screen-states ::st/transaction-screen-state]
                 #(st/update-screen % accounts transaction-data)))))

(rf/reg-event-db
  :save-transaction
  [check-spec-interceptor]
  (fn [db _]
    (let [screen-data
          (get-in db [::db/screen-states ::st/transaction-screen-state])

          current-account
          (get-in db [::db/screen-states
                      ::sa/account-screen-state ::sa/account-id])

          new-transaction
          (st/screen-data->transaction current-account screen-data)]
      (-> db
          (update-in [:data :transactions]
                     #(t/add-transaction % new-transaction))
          (assoc :navigation :account)))))

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
    (let [transaction (get-in db [:data :transactions id])
          current-account (get-in db [::db/screen-states
                                      ::sa/account-screen-state
                                      ::sa/account-id])]
      (-> db
          (assoc-in [::db/screen-states ::st/transaction-screen-state]
                    (st/transaction->screen-data current-account transaction))
          (assoc :navigation :transaction)))))

(rf/reg-event-fx
  :new-transaction
  [check-spec-interceptor
   (rf/inject-cofx :now)]
  (fn [cofx _]
    (let [db (:db cofx)
          now (:now cofx)
          id (inc (get-in db [:highest-ids :transaction]))]
      {:db (-> db
               (assoc-in [::db/screen-states ::st/transaction-screen-state]
                         (st/new-transaction id now 0))
               (assoc-in [:highest-ids :transaction] id)
               (assoc :navigation :transaction))})))

(rf/reg-event-db
  :close-transaction-screen
  [check-spec-interceptor]
  (fn [db _]
    (-> db
        (update-in [::db/screen-states] dissoc ::st/transaction-screen-state)
        (assoc :navigation :account))))
