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
                    {::st/description "asdf"
                     ::st/date 12345678910
                     ::st/account-id 2
                     ::st/amount 10.0
                     ::st/id 31337
                     ::st/new? true}}
   :navigation :account})
