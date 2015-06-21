(ns smartyper.keypress)

(def last-keypress (atom 0))

(defn- keydown-handler [e]
  (swap! last-keypress #(.fromCharCode js/String (.-keyCode e))))

(defn- charcode-to-char [charcode])

(defn hook-keypress-detection! []
  (.addEventListener js/document "keypress" keydown-handler false))
