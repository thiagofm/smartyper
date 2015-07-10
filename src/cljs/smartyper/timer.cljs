(ns smartyper.timer
  (:require [reagent.core :as reagent :refer [atom]]
            [smartyper.typing-area :as typing-area]))

(defonce current-time (atom 0))
(defonce start-time (atom 0))
(defonce amount-of-typed-characters (atom 0))
(defonce amount-of-errors (atom 0))
(defonce done (atom false))

(defn current-time-in-seconds []
  (.getTime (js/Date.)))

(defn update-done [_ _ _ character-maps]
  (when (every? #(:typed %) character-maps) (swap! done (fn[] true))))

(defn update-current-time []
  (when (not @done) (swap! current-time #(current-time-in-seconds))))

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
  (add-watch typing-area/character-maps :watch-change update-done)
  [:ul {:class "typing-info"}
   [:li
    [:div {:class "title"} "Elapsed time"]
    [:div {:class "data"} (if (not= @start-time 0) (int (/ (- @current-time @start-time) 1000)) 0)]]
   [:li
    [:div {:class "title"} "Gross WPM"]
    [:div {:class "data"} (if (not= @start-time 0) (int (* (/ (/ @amount-of-typed-characters 5) (/ (- @current-time @start-time) 1000)) 60)) 0)]]
   [:li
    [:div {:class "title"} "Net WPM"]
    [:div {:class "data"} (if (not= @start-time 0)
         (-
           (int (* (/ (/ @amount-of-typed-characters 5) (/ (- @current-time @start-time) 1000)) 60))
           (int (* (/ (/ @amount-of-errors 5) (/ (- @current-time @start-time) 1000)) 60)))
         0)]]])
