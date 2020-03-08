(ns app.subs
  (:require [re-frame.core :as rf]
            [app.db :as db]
            [money.core.transaction :as t]
            [money.screens.account :as sa]
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
  :current-account
  (fn [db _]
    (get-in db [::db/screen-states ::sa/account-screen-state ::sa/account-id])))

(rf/reg-sub
  :transaction-screen-state
  (fn [db _]
    (get-in db [::db/screen-states ::st/transaction-screen-state])))

(rf/reg-sub
  :current-account-transactions
  :<- [:transactions]
  :<- [:current-account]
  (fn [[transactions current-account] _]
    (t/get-account-transactions transactions current-account)))

(rf/reg-sub
  :account-names
  :<- [:current-account]
  :<- [:accounts]
  (fn [[current-account accounts] _]
    (ap/present-account-list accounts current-account)))

(rf/reg-sub
  :current-account-reduced-transactions
  :<- [:current-account]
  :<- [:current-account-transactions]
  :<- [:accounts]
  (fn [[current-account transactions accounts] _]
    (ap/reduce-transactions transactions accounts current-account)))

(rf/reg-sub
  :account-overview
  :<- [:current-account-reduced-transactions]
  :<- [:accounts]
  (fn [[reduced-transactions accounts] _]
    (prn "rt a" reduced-transactions accounts)
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
    (if (nil? screen-state)
      nil
      (tp/present-transaction-screen screen-state accounts))))
