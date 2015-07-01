(ns smartyper.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [smartyper.typing-area :as typing-area]
              [smartyper.timer :as timer]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; My code

;; -------------------------
;; Views

(defn home-page []
  [:div 
   (timer/component)
   (typing-area/component)])

(defn about-page []
  [:div [:h2 "About smartyper"]
    [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (typing-area/set-text "Never knew before what eternity was made for. It is to give some of us a chance to learn German.")
  (hook-browser-navigation!)
  (mount-root))
