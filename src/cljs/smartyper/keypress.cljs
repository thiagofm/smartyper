(ns smartyper.keypress)

(defonce last-keypress (atom 0))

(defn- keypress-handler [e]
  (let [key-code (.-keyCode e)]
    (swap! last-keypress (fn[] key-code))))

(defn- keydown-handler [e]
  (let [key-code (.-keyCode e)
        character (.fromCharCode js/String key-code)]
    (when (= key-code 8)
      (swap! last-keypress (fn[] key-code))
      (.preventDefault e))))

(defn key-code-to-character [key-code]
  (.fromCharCode js/String key-code))

(defn- charcode-to-char [charcode])

(defn hook-keypress-detection! []
  (.addEventListener js/document "keydown" keydown-handler false)
  (.addEventListener js/document "keypress" keypress-handler false))
