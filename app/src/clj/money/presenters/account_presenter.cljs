(ns money.presenters.account-presenter
  (:require [clojure.spec.alpha :as s]
            [money.core.account :as a]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [money.core.transaction :as t]))

(s/def ::id int?)
(s/def ::description string?)
(s/def ::other-account int?)
(s/def ::amount number?)
(s/def ::date int?)
(s/def ::reduced-transaction
  (s/keys :req [::id ::description ::other-account ::amount ::date]))
(s/def ::reduced-transactions (s/coll-of ::reduced-transaction))
(s/def ::balance number?)
(s/def ::balances (s/coll-of ::balance))

(defn- get-account-split [account-id splits]
  ;TODO: Handle transactions with more than one split per account
  (first (filter #(= account-id (::t/account %)) splits)))

(defn- get-amount [account-id splits]
  (let [account-split (get-account-split account-id splits)]
    (::t/amount account-split)))

(defn- get-other-account-id [this-account-id splits]
  (first (remove #(= this-account-id %) (map ::t/account splits))))

(defn reduce-transaction [transaction accounts account-id]
  (s/assert ::t/transaction transaction)
  (s/assert ::a/accounts accounts)
  (let [splits (::t/splits transaction)]
    {::id (::t/id transaction)
     ::description (::t/description transaction)
     ::amount (get-amount account-id splits)
     ::date (::t/date transaction)
     ::other-account (get-other-account-id account-id splits)}))

(defn- get-amounts [reduced-transactions]
  (map ::amount reduced-transactions))

(defn- last-or-zero [v]
  (if (empty? v)
    0
    (last v)))

(defn reduce-transactions [transactions accounts account-id]
  (s/assert ::t/transactions transactions)
  (s/assert ::a/accounts accounts)
  (let [reduced-transactions
        (map #(reduce-transaction % accounts account-id) transactions)]
    {::reduced-transactions reduced-transactions
     ::balances (reductions + (get-amounts reduced-transactions))}))


(defn convert-date [unix-time locale]
  (.toLocaleDateString (js/Date. unix-time)
                       locale
                       #js {:year "numeric" :month "short" :day "numeric"}))

(defn- get-account-name [id accounts]
  (::a/name (get accounts id)))

(defn present-transaction [transaction balance accounts locale]
  (s/assert ::reduced-transaction transaction)
  (s/assert ::a/accounts accounts)
  {:id (::id transaction)
   :description (::description transaction)
   :amount (str (::amount transaction))
   :date (convert-date (::date transaction) locale)
   :account (get-account-name (::other-account transaction) accounts)
   :balance (str balance)})

(defn present-transactions [transactions-and-balances accounts locale]
  (s/assert ::a/accounts accounts)
  (let [transactions (::reduced-transactions transactions-and-balances)
        balances (::balances transactions-and-balances)]
    (s/assert ::reduced-transactions transactions)
    (map #(present-transaction %1 %2 accounts locale) transactions balances)))
