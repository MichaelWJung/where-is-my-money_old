(ns money.presenters.account-presenter-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is are testing run-tests]]
            [money.presenters.account-presenter :as ap]
            [money.core.account :as a]
            [money.core.transaction :as t]))

(def account1 {::a/name "Checking Account"
               ::a/currency 1
               ::a/parent nil
               ::a/type :normal})
(def account2 {::a/name "Travel"
               ::a/currency 1
               ::a/parent nil
               ::a/type :normal})
(def account3 {::a/name "Entertainment"
               ::a/currency 1
               ::a/parent nil
               ::a/type :normal})
(def accounts {1 account1
               2 account2
               3 account3})

(defn- split [account amount]
  {::t/description ""
   ::t/account account
   ::t/amount amount})

(defn- transaction [description date amount account_id]
  {::t/description description
   ::t/date date
   ::t/splits [(split 1 amount) (split account_id (- amount))]})

(deftest date-conversion
  (is (= (ap/convert-date 1578830400000 "de-DE") "12. Jan. 2020"))
  (is (= (ap/convert-date 1578830400000 "en-US") "Jan 12, 2020"))
  (is (= (ap/convert-date 1578830400000 "ru-RU") "12 янв. 2020 г.")))

(deftest reduce-transaction
  (are [transaction expected]
       (let [reduced-transaction (ap/reduce-transaction transaction accounts 1)]
         (and (= reduced-transaction expected)
              (s/valid? ::ap/reduced-transaction reduced-transaction)))

       (transaction "Hotel" 987654 123 2)
       {::ap/description "Hotel"
        ::ap/amount 123
        ::ap/date 987654
        ::ap/other-account 2}))

(deftest reduce-transactions
  (is (= (ap/reduce-transactions
           [(transaction "Hotel" 987654 123 2)
            (transaction "Movie theater" 555666 444 3)
            (transaction "Taxi" 11111 22 2)]
           accounts
           1)
         {::ap/reduced-transactions [{::ap/description "Hotel"
                                      ::ap/amount 123
                                      ::ap/date 987654
                                      ::ap/other-account 2}
                                     {::ap/description "Movie theater"
                                      ::ap/amount 444
                                      ::ap/date 555666
                                      ::ap/other-account 3}
                                     {::ap/description "Taxi"
                                      ::ap/amount 22
                                      ::ap/date 11111
                                      ::ap/other-account 2}]
          ::ap/balances [123 567 589]})))

(deftest presenter
  (testing "Transactions are correctly converted (without balance)"
    (are [transaction balance locale expected]
         (= (ap/present-transaction transaction balance accounts locale)
            expected)

         {::ap/description "Hotel"
          ::ap/amount 123
          ::ap/date 1578830400000
          ::ap/other-account 2}
         123
         "de-DE"
         {:description "Hotel"
          :amount "123"
          :date "12. Jan. 2020"
          :account "Travel"
          :balance "123"}

         {::ap/description "Taxi"
          ::ap/amount 42
          ::ap/date 1578830400000
          ::ap/other-account 2}
         456
         "en-US"
         {:description "Taxi"
          :amount "42"
          :date "Jan 12, 2020"
          :account "Travel"
          :balance "456"}
         )))

(deftest present-transactions
  (is (= (ap/present-transactions
           {::ap/reduced-transactions [{::ap/description "Hotel"
                                        ::ap/amount 123
                                        ::ap/date 1578830400000
                                        ::ap/other-account 2}
                                       {::ap/description "Taxi"
                                        ::ap/amount 42
                                        ::ap/date 1578916800000
                                        ::ap/other-account 2}]
            ::ap/balances [123 165]}
           accounts
           "de-DE")

         [{:description "Hotel"
           :amount "123"
           :date "12. Jan. 2020"
           :account "Travel"
           :balance "123"}
          {:description "Taxi"
           :amount "42"
           :date "13. Jan. 2020"
           :account "Travel"
           :balance "165"}])))
