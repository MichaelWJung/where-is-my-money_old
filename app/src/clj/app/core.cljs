(ns app.core
  (:require [app.android :as a]
            ; [app.db]
            ; [app.events]
            [app.store :as st]
            ; [app.subs]
            [re-frame.core :as rf]))


(defn- prevent-exit-with-callback []
  (js/setInterval (fn [] nil) 1000))

(defn- initialize [data]
  (rf/dispatch-sync [:initialize-db data])
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
