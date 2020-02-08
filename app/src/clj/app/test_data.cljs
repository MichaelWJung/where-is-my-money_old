(ns app.test-data
  (:require [money.core.account :as a]
            [money.core.transaction :as t]))

(defn- index-to-amount [i]
  (mod (* i 123) 129))

(defn- get-date [i]
  (+ 1578830400000 (* i 86400000)))

(defn- generate-splits
  [[account1 account2] amount]
  [{::t/description ""
    ::t/amount amount
    ::t/account account1}
   {::t/description ""
    ::t/amount (- amount)
    ::t/account account2}])

(defn- generate-transaction [i]
  {::t/id i
   ::t/description (str "Transaktion " i)
   ::t/splits (generate-splits [0 (+ 1 (mod i 5))] (index-to-amount i))
   ::t/date (get-date i)})

(defn generate-transactions []
  (mapv generate-transaction (range 200)))

(defn- generate-account [i]
  [i {::a/name (str "Account " i)
      ::a/currency 0
      ::a/parent nil
      ::a/type :normal}])

(defn generate-accounts []
  (into {} (map generate-account (range 6))))
