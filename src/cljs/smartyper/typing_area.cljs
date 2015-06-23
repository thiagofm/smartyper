(ns smartyper.typing-area
  (:require [smartyper.keypress :as keypress]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]))

(def typing-area-text (atom nil))

(defn characters-data [text]
  (vec (map-indexed
         (fn[i c] { :character c :typed false :id i}) text)))

(defn set-text [text]
  (swap! typing-area-text #(characters-data text)))

(defn characters-component []
  (map (fn [c]
         [:span
          {:style {:color (if (= (:typed c) false) "red" "black")}}
          (:character c)])
      @typing-area-text))

(defn load-component []
  (into
    [:div#typing-area]
    (characters-component )))

(defn current-needed-character-id [data]
  (:id (first (filter #(= (:typed %) false) data))))

(defn update-typed [id]
  (let [x (map (fn[d] (if (= id (:id d)) (assoc d :typed true) d)) @typing-area-text)]
    (swap! typing-area-text (fn[n] (vec x)))))

(defn keypress-watcher [key a old-val new-val]
  (let [cx (get @typing-area-text (current-needed-character-id @typing-area-text))]
  (if (= new-val (:character cx))
    (update-typed (current-needed-character-id @typing-area-text))
    nil)))

(defn init-typing-area! []
  (keypress/hook-keypress-detection!)
  (add-watch keypress/last-keypress :watch-change keypress-watcher))


