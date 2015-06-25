(ns smartyper.typing-area
  (:require [smartyper.keypress :as keypress]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]))

(def wrong-character-maps (atom []))
(def character-maps (atom []))
(defrecord CharacterMap [id character typed])

(defn set-text [text]
  "Sets the text for the component"
  (swap! character-maps
         #(vec (map-indexed (fn[i c] (CharacterMap. i c false))
                            text))))

(defn- next-character-id-to-type [characters]
  "Finds the next CharacterMap needed to be typed"
  (first (filter #(= (:typed %) false) characters)))

(defn- update-wrong-typed-characters [])

(defn- update-latest-character-map-as-typed []
  "Updates the latest CharacterMap as typed"
  (let [characters @character-maps
        character-map-id (:id (next-character-id-to-type characters))]
    (swap!
      character-maps
      #(assoc-in characters [character-map-id :typed] true))))

(defn- keypress-watcher [key a old-val new-val]
  (let [cm @character-maps
        character-id (:id (next-character-id-to-type cm))
        character-map (get cm character-id)]
    (if (= new-val (:character character-map)) ; Value is typed right?
      update-latest-character-map-as-typed
      update-wrong-typed-characters)))

(defn- setup! []
  (keypress/hook-keypress-detection!)
  (add-watch keypress/last-keypress :watch-change keypress-watcher))

(defn component []
  (setup!)
  (into
    [:div#typing-area]
    (map (fn [c]
         [:span
          {:style {:color (if (= (:typed c) false) "red" "black")}}
          (:character c)])
      @character-maps)))
