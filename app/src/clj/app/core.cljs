(ns app.core
  (:require [app.android :as a]
            [app.db]
            [app.events]
            [app.store :as st]
            [app.subs]
            [app.test-data :refer [generate-accounts generate-transactions]]
            [clojure.spec.alpha :as s]
            [cognitect.transit :as t]
            [re-frame.core :as rf]))

(def fs (js/require "fs"))

(defn read-transit [path f]
  (.readFile fs path "utf8"
             (fn [err data]
               (let [reader (t/reader :json)
                     parsed (t/read reader data)]
                 (f parsed)))))

(defn write-transit [path data]
  (println "Beginning serialization.")
  (let [writer (t/writer :json)
        serialized (t/write writer data)]
    (println "Serialized. Beginning saving.")
    (.writeFile fs path serialized
                (fn [err] (println "The file was saved!")))))

(defn- prevent-exit-with-callback []
  (js/setInterval (fn [] nil) 1000))

(def db-file "/home/local/db")

(defn- initialize [data]
  ; (println "Start reading file")
  ; (read-transit db-file
  ;               (fn [content]
  ;                 (println "Finished reading file")
  ;                 (rf/dispatch-sync [:initialize-db content])
  ;                 (a/send-ready))))
  (println "Starting generation")
  (let [transactions (generate-transactions)
        accounts (generate-accounts)
        db {:transactions transactions
            :accounts accounts
            :currencies []}]
    ; (write-transit db-file db)
    (rf/dispatch-sync [:initialize-db db]))
  (a/send-ready))

(defn- initialize-store []
  (reset! st/store
          (reify st/Store
            (save [_ data] (a/data->store data)))))

(defn -main [& _]
  (s/check-asserts true)
  (prevent-exit-with-callback)
  (initialize-store)
  (a/setup-android-interaction initialize)
  (a/send-waiting-for-db))

(set! *main-cli-fn* -main)
