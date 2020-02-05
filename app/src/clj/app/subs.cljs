(ns app.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :message
  (fn [db _]
    (get-in db [:transactions :message])))
