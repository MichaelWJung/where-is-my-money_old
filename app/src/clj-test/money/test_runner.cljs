(ns money.test-runner
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [run-all-tests]]))

(defn main[]
  (s/check-asserts true)
  (run-all-tests #"^money\..*"))
