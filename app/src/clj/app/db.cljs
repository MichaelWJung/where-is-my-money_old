(ns app.db
  (:require [cljs.spec.alpha :as s]
            [money.core.account :as a]
            [money.core.transaction :as t]
            [money.screens.account :as sa]
            [money.screens.transaction :as st]
            [re-frame.core :as rf]))

(s/def ::currencies (constantly true))
(s/def ::data (s/keys :req-un [::t/transactions ::a/accounts ::currencies]))

(s/def ::screen-states (s/keys :req [::sa/account-screen-state]
                               :opt [::st/transaction-screen-state]))

(s/def ::navigation #{:transaction :account})

(s/def ::db (s/keys :req-un [::data ::navigation]))

(def default-db
  {:data {:transactions {}
          :accounts {}
          :currencies []}
   ::screen-states {::sa/account-screen-state
                    {::sa/account-id 4}}
   :navigation :account
   :highest-ids {:transaction 1}})
