(ns app.db
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

(def default-db
  {:transactions {:message "blub"}})
