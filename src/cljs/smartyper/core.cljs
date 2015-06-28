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
  [:div [:h2 "Welcome to smartyper"]
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
  (typing-area/set-text "The Germans have another kind of parenthesis, which they make by splitting a verb in two and putting half of it at the be- ginning of an exciting chapter and the other half at the end of it. Can any one conceive of anything more confusing than that ? These things are called “separable verbs.” The German grammar is blis- tered all over with separable verbs; and the wider the two portions of one of them are spread apart, the better the author of the crime is pleased with his performance. A favorite one is reiste ab,—which means, departed. Here is an example which I culled from a novel and reduced to English: “The trunks being now ready, he DE- af- ter kissing his mother and sisters, and once more pressing to his bosom his adored Gretchen, who, dressed in simple white muslin,with a single tuberose in the ample folds of her rich brown hair had tottered feebly down the stairs, still pale from the terror and excitement of the past evening, but longing to lay her poor aching head yet once again upon the breast of him whom she loved more dearly than life itself, PARTED.”")
  (hook-browser-navigation!)
  (mount-root))
