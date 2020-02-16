(ns app.db
  (:require [cljs.spec.alpha :as s]
            [money.core.account :as a]
            [money.core.transaction :as t]
            [money.screens.transaction :as st]
            [re-frame.core :as rf]))

(s/def ::currencies (constantly true))
(s/def ::data (s/keys :req-un [::t/transactions ::a/accounts ::currencies]))

(s/def ::screen-states (s/keys :req [::st/transaction-screen-state]))

(s/def ::navigation #{:transaction :account})

(s/def ::db (s/keys :req-un [::data ::navigation]))

(def default-db
  {:data {:transactions {}
          :accounts {}
          :currencies []}
   ::screen-states {::st/transaction-screen-state
                    {::st/description ""
                     ::st/date 0
                     ::st/account-id 0
                     ::st/amount 0.0
                     ::st/id 1
                     ::st/new? true}}
   :navigation :account
   :highest-ids {:transaction 1}})
