(ns app.db
  (:require [cljs.spec.alpha :as s]
            [money.core.account :as a]
            [money.core.transaction :as t]
            [re-frame.core :as rf]))

(s/def ::currencies (fn [_] true))
(s/def ::data (s/keys :req-un [::t/transactions ::a/accounts ::currencies]))
(s/def ::db (s/keys :req-un [::data]))

(def default-db
  {:data {:transactions []
          :accounts {}
          :currencies []}})
