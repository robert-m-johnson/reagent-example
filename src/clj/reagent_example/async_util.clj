(ns reagent-example.async-util
  (:require [clojure.core.async :refer [alt! go-loop chan]]))

(defmacro go-looper [body]
  `(let [stop-chan# (chan)]
     (go-loop []
       (when (alt!
               stop-chan# false
               :default :keep-alive)
         ~body
         (recur)))
     stop-chan#))
