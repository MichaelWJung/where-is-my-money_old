(ns money.test-runner
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [run-all-tests]]
            [money.core.account-test]
            [money.core.currency-test]
            [money.core.transaction-test]
            [money.presenters.account-presenter-test]
            ))

(defn main[]
  (s/check-asserts true)
  (run-all-tests #"^money\..*"))
