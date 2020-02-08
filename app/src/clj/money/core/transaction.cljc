(ns money.core.transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.set]
            [money.core.currency :as c]))

(s/def ::id int?)
(s/def ::description string?)
(s/def ::account int?)
(s/def ::amount number?)
(s/def ::date int?)

(s/def ::split (s/keys :req [::description ::account ::amount]))
(s/def ::splits (s/coll-of ::split :kind vector? :min-count 2))

(s/def ::transaction (s/keys :req [::id ::description ::date ::splits]
                             :opt [::c/exchange-rate]))
(s/def ::transactions (s/map-of ::id ::transaction))

(defn- get-accounts [splits]
  (into #{} (map ::account splits)))

(defn- accounts-exist [account-ids-to-check account-map]
  (let [account-ids (into #{} (keys account-map))]
    (empty? (clojure.set/difference account-ids-to-check account-ids))))

(defn- sum-amounts [splits]
  (apply + (map ::amount splits)))

(defn valid-transaction? [transaction accounts currencies]
  (let [{:keys [::splits]} transaction]
    (and (s/valid? ::transaction transaction)
         (zero? (sum-amounts splits)))))

(defn- account-in-splits? [account-id splits]
  (s/assert ::splits splits)
  (some #(= (::account %) account-id) splits))

(defn- account-in-transaction? [account-id transaction]
  (s/assert ::transaction transaction)
  (account-in-splits? account-id (::splits transaction)))

(defn get-account-transactions [transactions-map account-id]
  (let [transactions (vals transactions-map)]
    (filterv (partial account-in-transaction? account-id) transactions)))
