(ns smartyper.typing-area
  (:require [smartyper.keypress :as keypress]))

(defn keypress-watcher [key a old-val new-val] (println new-val))

(defn init-typing-area! []
  (keypress/hook-keypress-detection!)
  (add-watch keypress/last-keypress :watch-change keypress-watcher))

(defn component [text]
  [:div#typing-area
   [:span text]])

