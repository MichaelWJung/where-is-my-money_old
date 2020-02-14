(ns money.presenters.transaction-presenter
  (:require [clojure.spec.alpha :as s]
            [money.core.account :as a]
            [money.screens.transaction :as st]))

(def new-transaction-title "New transaction")
(def edit-transaction-title "Edit transaction")

(def create-button-text "Create")
(def save-button-text "Save")

(s/def ::screen-title #{new-transaction-title edit-transaction-title})
(s/def ::ok-button-text #{create-button-text save-button-text})
(s/def ::description string?)
(s/def ::date int?)
(s/def ::amount string?)
(s/def ::selected-account int?)
(s/def ::account string?)
(s/def ::accounts (s/coll-of ::account :kind vector?))

(s/def ::transaction
  (s/keys :req [::screen-title
                ::ok-button-text
                ::description
                ::date
                ::amount
                ::selected-account
                ::accounts]))

(defn- present-account [account]
  (s/assert ::a/account account)
  (::a/name account))

(defn present-transaction-screen [screen-state accounts]
  (s/assert ::st/transaction-screen-state screen-state)
  (s/assert ::a/accounts accounts)
  (let [new? (::st/new? screen-state)]
    {::screen-title (if new? new-transaction-title edit-transaction-title)
     ::ok-button-text (if new? create-button-text save-button-text)
     ::description (::st/description screen-state)
     ::date (::st/date screen-state)
     ::amount (str (::st/amount screen-state))
     ::selected-account (::st/account-id screen-state)
     ::accounts (mapv present-account (vals accounts))}))
