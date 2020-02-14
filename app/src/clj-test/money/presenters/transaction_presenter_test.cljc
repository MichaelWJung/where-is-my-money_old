(ns money.presenters.transaction-presenter-test
  (:require [clojure.test :refer [deftest is are testing]]
            [money.core.account :as a]
            [money.presenters.transaction-presenter :as tp]
            [money.screens.transaction :as st]))

(defn- account [id name]
  {::a/name name
   ::a/currency 1
   ::a/parent nil
   ::a/type :normal})

(deftest present-transaction-screen
  (testing "New transaction screen is presented correctly"
    (is (=
         (tp/present-transaction-screen
           {::st/description "Movie tickets"
            ::st/date 123
            ::st/account-id 2
            ::st/amount 10.0
            ::st/id 0
            ::st/new? true}
           {0 (account 0 "Cash")
            1 (account 1 "Rent")
            2 (account 2 "Car maintenance")})

         {::tp/screen-title tp/new-transaction-title
          ::tp/ok-button-text tp/create-button-text
          ::tp/description "Movie tickets"
          ::tp/date 123
          ::tp/amount "10"
          ::tp/selected-account 2
          ::tp/accounts ["Cash" "Rent" "Car maintenance"]}))))
