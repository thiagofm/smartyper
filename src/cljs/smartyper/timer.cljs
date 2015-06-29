(ns smartyper.timer
  (:require [reagent.core :as reagent :refer [atom]]
            [smartyper.typing-area :as typing-area]))

(def current-time (atom 0))
(def start-time (atom 0))
(def amount-of-typed-characters (atom 0))
(def amount-of-errors (atom 0))

(defn current-time-in-seconds []
  (.getTime (js/Date.)))

(defn update-current-time []
  (swap! current-time #(current-time-in-seconds)))

(defn compute-amount-of-errors [typed-characters]
  (count (filter #(= (:correct %) false) typed-characters)))

(defn- characters-being-typed [_ _ _ typed-characters]
  (swap! amount-of-typed-characters #(count typed-characters))
  (swap! amount-of-errors #(compute-amount-of-errors typed-characters))
  (when (= 0 @start-time)
    (swap! start-time #(current-time-in-seconds))))

(defn component []
  (update-current-time)
  (js/setInterval update-current-time 1000)
  (add-watch typing-area/typed-characters :watch-change characters-being-typed)
  [:div
   [:p (if (not= @start-time 0) (int (/ (- @current-time @start-time) 1000)) 0)]
   [:p (if (not= @start-time 0)
         (int (* (/ (/ @amount-of-typed-characters 5) (/ (- @current-time @start-time) 1000)) 60))
         0)]
   [:p (if (not= @start-time 0)
         (-
           (int (* (/ (/ @amount-of-typed-characters 5) (/ (- @current-time @start-time) 1000)) 60))
           (int (* (/ (/ @amount-of-errors 5) (/ (- @current-time @start-time) 1000)) 60)))
         0)]])