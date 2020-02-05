(ns money.core.transaction-test
  (:require [clojure.test :refer [deftest is are testing run-tests]]
            [clojure.spec.alpha :as s]
            [money.core.account :as a]
            [money.core.currency :as c]
            [money.core.transaction :as t]))

(def split1 {::t/description "abc" ::t/account 1 ::t/amount 10})
(def split2 {::t/description "xyz" ::t/account 2 ::t/amount -10})
(def splits [split1 split2])

(deftest spec-validations
  (testing "Split needs description, account, amount"
    (are [valid split] (= (s/valid? ::t/split split) valid)
         false {}
         false {::t/description "abc" ::t/account 123}
         false {::t/description "abc" ::t/amount 123}
         true {::t/description "abc" ::t/account 123 ::t/amount 123}
         true split1
         true split2))
  (testing "The splits of a transaction need to be a vector of at least two"
    (are [valid splits] (= (s/valid? ::t/splits splits) valid)
         false [split1]
         false '(split1 split2)
         true [split1 split2]))
  (testing "Transaction needs description, data, splits"
    (are [valid transaction] (= (s/valid? ::t/transaction transaction) valid)
         false {}
         false {::t/description "abc" ::t/date 1}
         false {::t/description "abc" ::t/splits splits}
         false {::t/date 1 ::splits splits}
         true {::t/description "abc" ::t/date 1 ::t/splits splits}
         true {::t/description "abc"
               ::t/date 1
               ::t/splits splits
               ::c/exchange-rate [1 2 1.25]})))

(def account1 {::a/name "account 1"
               ::a/currency 1
               ::a/parent nil
               ::a/type :normal})
(def account2 {::a/name "account 2"
               ::a/currency 1
               ::a/parent nil
               ::a/type :normal})
(def accounts {1 account1
               2 account2})

(def currency1 {::c/name "BTC"})
(def currency2 {::c/name "Grams of gold"})
(def currencies {1 currency1 2 currency2})

(defn- transaction [splits]
  {::t/description ""
   ::t/date 1
   ::t/splits splits})

(defn- split [account amount]
  {::t/description ""
   ::t/account account
   ::t/amount amount})

(defn- valid-transaction? [transaction]
  (t/valid-transaction? transaction accounts currencies))

(deftest consistency-validations
  (testing "Splits need to be balanced"
    (are [valid transaction] (= (valid-transaction? transaction) valid)
         true (transaction [(split 1 1.0) (split 2 -1.0)])
         false (transaction [(split 1 1.0) (split 2 2.0)])))
  ; (testing "Accounts all exist"
  ;   (are [valid transaction] (= (valid-transaction? transaction) valid)
  ;        false (transaction [(split 1 1.0) (split 7 -1.0)])))
  )
