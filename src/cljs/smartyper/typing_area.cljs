(ns smartyper.typing-area
  (:require [smartyper.keypress :as keypress]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]))

(def character-maps (atom []))
(def typed-characters (atom []))
(def wrong-characters-stack (atom []))
(defrecord CharacterMap [id character typed])
(defrecord TypedCharacter [character correct])

(defn set-text [text]
  "Sets the text for the component"
  (swap! character-maps
         #(vec (map-indexed (fn[i c] (CharacterMap. i c false))
                            text))))

(defn- next-character-id-to-type [characters]
  "Finds the next CharacterMap needed to be typed"
  (first (filter #(= (:typed %) false) characters)))

(defn- update-latest-character-map-as-typed! []
  "Updates the latest CharacterMap as typed"
  (let [characters @character-maps
        character-map-id (:id (next-character-id-to-type characters))]
    (swap!
      character-maps
      #(assoc-in characters [character-map-id :typed] true))))

(defn- update-latest-character-map-as-not-typed! []
  "Updates the latest CharacterMap as typed"
  (let [characters @character-maps
        character-map-id (dec (:id (next-character-id-to-type characters)))]
    (when (>= character-map-id 0)
      (swap! character-maps #(assoc-in characters [character-map-id :typed] false))
      )))

(defn- add-wrong-character-to-stack [character]
  (swap! wrong-characters-stack #(conj @wrong-characters-stack character)))

(defn- remove-last-characters-stack! []
  (swap! wrong-characters-stack #(pop @wrong-characters-stack)))

(defn- clear-wrong-characters-stack! []
  (swap! wrong-characters-stack (fn[n] [])))

(defn- add-typed-character [character correct]
  (swap! typed-characters #(conj @typed-characters (TypedCharacter. character correct))))

(defn- keypress-watcher [_ _ _ key-code]
  (let [character (keypress/key-code-to-character key-code)
        cm @character-maps
        character-id (:id (next-character-id-to-type cm))
        character-map (get cm character-id)
        wrong-characters-stack-size (count @wrong-characters-stack)]
    (if (= key-code 8) ; backspace handling
      (let [stack-size (count @wrong-characters-stack)]
        (if (> stack-size 0)
          (remove-last-characters-stack!)
          (update-latest-character-map-as-not-typed!)))
      (let [correct (and (= character (:character character-map)) (= wrong-characters-stack-size 0))]
        (when correct
          (update-latest-character-map-as-typed!)
          (clear-wrong-characters-stack!))
        (when (not correct)
          (add-wrong-character-to-stack character))
        (add-typed-character character correct)))))

(defn- setup! []
  (keypress/hook-keypress-detection!)
  (add-watch keypress/last-keypress :watch-change keypress-watcher))

(defn component []
  (setup!)
  (into
    [:div#typing-area]
    (conj
          (map (fn [c] (when (not (:typed c)) [:span {:style {:color "black"}} (:character c)])) @character-maps)
          (map (fn [c] [:span {:style {:color "red"}} c]) @wrong-characters-stack)
          (map (fn [c] (when (:typed c) [:span {:style {:color "blue"}} (:character c)])) @character-maps))))
