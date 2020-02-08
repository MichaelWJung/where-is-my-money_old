(ns app.subs
  (:require [re-frame.core :as rf]
            [money.presenters.account-presenter :refer
             [reduce-transactions present-transactions]]))

(rf/reg-sub
  :accounts
  (fn [db _]
    (get-in db [:data :accounts])))

(rf/reg-sub
  :transactions
  (fn [db _]
    (get-in db [:data :transactions])))

(rf/reg-sub
  :reduced-transactions
  :<- [:transactions]
  :<- [:accounts]
  (fn [[transactions accounts] [_ account-id]]
    (reduce-transactions transactions accounts account-id)))

(rf/reg-sub
  :account-overview
  :<- [:reduced-transactions 0]
  :<- [:accounts]
  (fn [[reduced-transactions accounts] _]
    (present-transactions reduced-transactions accounts "en-US")))
