(ns smartyper.prod
  (:require [smartyper.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
