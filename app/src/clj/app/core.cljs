(ns app.core
  (:require [app.android :as a]
            [app.db]
            [app.events]
            [app.store :as st]
            [app.subs]
            [app.test-data :refer [generate-accounts generate-transactions]]
            [re-frame.core :as rf]))

(defn- prevent-exit-with-callback []
  (js/setInterval (fn [] nil) 1000))

(defn- initialize [data]
  (let [transactions (generate-transactions)
        accounts (generate-accounts)]
    (rf/dispatch-sync [:initialize-db {:transactions transactions
                                       :accounts accounts
                                       :currencies []}]))
  (a/send-ready))

(defn- initialize-store []
  (reset! st/store
          (reify st/Store
            (save [_ data] (a/data->store data)))))

(defn -main [& _]
  (prevent-exit-with-callback)
  (initialize-store)
  (a/setup-android-interaction initialize)
  (a/send-waiting-for-db))

(set! *main-cli-fn* -main)
