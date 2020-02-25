(ns app.core
  (:require [app.android :as a]
            [app.db :as db]
            [app.events]
            [app.store :as st]
            [app.subs]
            [app.test-data :refer [generate-accounts generate-transactions]]
            [clojure.spec.alpha :as s]
            [cognitect.transit :as t]
            [re-frame.core :as rf]))

(def fs (js/require "fs"))

(defn- file-exists [path]
  (.existsSync fs path))

(defn read-transit [path f]
  (.readFile fs path "utf8"
             (fn [err data]
               (let [reader (t/reader :json)
                     parsed (t/read reader data)]
                 (f parsed)))))

(defn write-transit [path data]
  (let [writer (t/writer :json)
        serialized (t/write writer data)]
    (.writeFile fs path serialized
                (fn [err] (println "The file was saved!")))))

(defn- prevent-exit-with-callback []
  (js/setInterval (fn [] nil) 1000))

(def db-file "/home/local/db")

(defn- initialize-without-db-file []
  (let [transactions (generate-transactions)
        accounts (generate-accounts)
        db {:data {:transactions transactions
                   :accounts accounts
                   :currencies []}}]
    (write-transit db-file db)
    (rf/dispatch-sync [:initialize-db db])
    (a/send-ready)))

(defn- initialize-from-db-file []
  (read-transit db-file
                (fn [content]
                  (if (s/valid? ::db/db content)
                    (do (rf/dispatch-sync [:initialize-db content])
                        (a/send-ready))
                    (initialize-without-db-file)))))

(defn- initialize []
  (if (file-exists db-file)
    (initialize-from-db-file)
    (initialize-without-db-file)))

(defn- initialize-store []
  (reset! st/store
          (reify st/Store
            (save [_ db] (write-transit db-file db)))))

(defn -main [& _]
  (s/check-asserts true)
  (prevent-exit-with-callback)
  (initialize-store)
  (a/setup-android-interaction)
  (initialize))

(set! *main-cli-fn* -main)
