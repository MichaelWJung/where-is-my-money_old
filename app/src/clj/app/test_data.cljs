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
  [i {::t/id i
      ::t/description (str "Transaktion " i)
      ::t/splits (generate-splits [0 (+ 1 (mod i 5))] (index-to-amount i))
      ::t/date (get-date i)}])

(defn generate-transactions []
  (into {} (map generate-transaction (range 0))))

(defn- get-account-name [i]
  (case i
    0 "Bargeld"
    1 "Girokonto"
    2 "Lebensmittel"
    3 "Kraftstoff"
    4 "Versicherungen"
    5 "Forderungen"
    (str "Konto " i)))

(defn- generate-account [i]
  [(* 2 i) {::a/name (get-account-name i)
            ::a/currency 0
            ::a/parent nil
            ::a/type :normal}])

(defn generate-accounts []
  (into {} (map generate-account (range 6))))
