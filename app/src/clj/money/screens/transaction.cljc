(ns money.screens.transaction
  (:require [cljs.spec.alpha :as s]))

(s/def ::description string?)
(s/def ::date int?)
(s/def ::account-id int?)
(s/def ::amount number?)
(s/def ::id int?)
(s/def ::new? boolean?)

(s/def ::transaction-screen-state
  (s/keys :req [::description ::date ::account-id ::amount ::id ::new?]))
