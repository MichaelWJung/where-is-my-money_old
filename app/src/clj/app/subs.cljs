(ns app.subs
  (:require [re-frame.core :as rf]
            [app.db :as db]
            [money.core.transaction :as t]
            [money.screens.transaction :as st]
            [money.presenters.account-presenter :as ap]
            [money.presenters.transaction-presenter :as tp]))

(rf/reg-sub
  :accounts
  (fn [db _]
    (get-in db [:data :accounts])))

(rf/reg-sub
  :transactions
  (fn [db _]
    (get-in db [:data :transactions])))

(rf/reg-sub
  :transaction-screen-state
  (fn [db _]
    (get-in db [::db/screen-states ::st/transaction-screen-state])))

(rf/reg-sub
  :account-transactions
  :<- [:transactions]
  (fn [transactions [_ account-id]]
    (t/get-account-transactions transactions account-id)))

(rf/reg-sub
  :account-names
  :<- [:accounts]
  (fn [accounts _]
    (ap/present-account-names accounts)))

(rf/reg-sub
  :reduced-transactions
  (fn [[_ account-id] _]
    [(rf/subscribe [:account-transactions account-id])
     (rf/subscribe [:accounts])])
  (fn [[transactions accounts] [_ account-id]]
    (ap/reduce-transactions transactions accounts account-id)))

(rf/reg-sub
  :account-overview
  :<- [:reduced-transactions 1]
  :<- [:accounts]
  (fn [[reduced-transactions accounts] _]
    (ap/present-transactions reduced-transactions accounts "en-US")))

(rf/reg-sub
  :current-screen
  (fn [db _]
    (:navigation db)))

(rf/reg-sub
  :transaction-screen
  :<- [:transaction-screen-state]
  :<- [:accounts]
  (fn [[screen-state accounts] _]
    (tp/present-transaction-screen screen-state accounts)))
