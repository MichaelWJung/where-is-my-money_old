(ns money.presenters.account-presenter
  (:require [clojure.spec.alpha :as s]
            [money.core.account :as a]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [money.core.transaction :as t]))

(s/def ::description string?)
(s/def ::other-account int?)
(s/def ::amount number?)
(s/def ::date int?)
(s/def ::reduced-transaction
  (s/keys :req [::description ::other-account ::amount ::date]))
(s/def ::reduced-transactions (s/coll-of ::reduced-transaction))
(s/def ::balance number?)
(s/def ::balances (s/coll-of ::balance))

(defn- get-account-split [account-id splits]
  ;TODO: Handle transactions with more than one split per account
  (first (filter #(= account-id (::t/account %)) splits)))

(defn- get-amount [account-id splits]
  (let [account-split (get-account-split account-id splits)]
    (::t/amount account-split)))

(defn- get-other-account-id [this-account-id splits]
  (first (remove #(= this-account-id %) (map ::t/account splits))))

(defn reduce-transaction [transaction accounts account-id]
  (let [splits (::t/splits transaction)]
    {::description (::t/description transaction)
     ::amount (get-amount account-id splits)
     ::date (::t/date transaction)
     ::other-account (get-other-account-id account-id splits)}))

(defn- get-amounts [reduced-transactions]
  (map ::amount reduced-transactions))

(defn- last-or-zero [v]
  (if (empty? v)
    0
    (last v)))

(defn reduce-transactions [transactions accounts account-id]
  (let [reduced-transactions
        (map #(reduce-transaction % accounts account-id) transactions)]
    {::reduced-transactions reduced-transactions
     ::balances (reductions + (get-amounts reduced-transactions))}))


(defn convert-date [unix-time locale]
  (.toLocaleDateString (js/Date. unix-time)
                       locale
                       #js {:year "numeric" :month "short" :day "numeric"}))

(defn- get-account-name [id accounts]
  (::a/name (get accounts id)))

(defn present-transaction [transaction balance accounts locale]
  {:description (::description transaction)
   :amount (str (::amount transaction))
   :date (convert-date (::date transaction) locale)
   :account (get-account-name (::other-account transaction) accounts)
   :balance (str balance)})

(defn present-transactions [transactions accounts locale]
  (map #(present-transaction %1 %2 accounts locale)
       (::reduced-transactions transactions)
       (::balances transactions)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Android-Test-Funktionen
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- index-to-amount [i]
  (mod (* i 123) 129))

(defn- calc-balance [i]
  (apply + (map index-to-amount (range (+ i 1)))))

(defn- get-account [i]
  (let [x (mod i 5)]
    (cond
      (= x 0) "Lebensmittel"
      (= x 1) "Tanken"
      (= x 2) "Restaurant"
      (= x 3) "Kind"
      :else "Geschenke")))

(defn- generate-transaction [i]
  {:description (str "Transaktion " i)
   :amount (index-to-amount i)
   :balance (calc-balance i)
   :date (str (+ 1 (mod i 28)) ".1.2020")
   :account (get-account i)})

(defn clj->json
  [ds]
  (.stringify js/JSON (clj->js ds)))

(defn ^:export generate-transactions []
  (clj->json (map generate-transaction (range 200))))

; (defn- -main [& args]
;   (rf/reg-event-db              ;; sets up initial application state
;     :initialize                 ;; usage:  (dispatch [:initialize])
;     (fn [_ _]                   ;; the two parameters are not important here, so use _
;       {:time (js/Date.)         ;; What it returns becomes the new application state
;        :time-color "#f88"}))
;   ; (js/setInterval (fn [] nil) 1000)
;   ; (.on js/LiquidCore
;   ;      "ping"
;   ;      (fn []
;   ;        (.emit js/LiquidCore "pong" #js {:message "Hallo Welt!"})
;   ;        (.exit js/process 0)))
;   ; (.emit js/LiquidCore "ready")
;   )

; (set! *main-cli-fn* -main)
