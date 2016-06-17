(ns reagent-example.data
  (:require [clojure.core.async :as async :refer
             [alt! go-loop timeout <! chan]]
            [mount.core :refer [defstate]]))

(def names
  [
   "Angular"
   "Babylon.js"
   "Backbone"
   "Batman.js"
   "Brink.js"
   "Cappucino"
   "Crafty"
   "Ember"
   "Enyo"
   "Hummingbird"
   "Jaggery"
   "jQuery"
   "Kango"
   "Lodash"
   "Mithril"
   "Potato"
   "React"
   "RequireJS"
   "Snack"
   "truckJS"
   "Underscore"
   "Zepto"
   ])

(defn name->framework [name]
  {:name name
   :stars (rand-int 500)
   :forks (rand-int 50)})

(defn create-frameworks []
  (apply vector (map name->framework names)))

(def frameworks
  (atom (create-frameworks)))

(defn create-looper []
  (let [stop-chan (chan)]
  (go-loop []
    (when (alt!
            stop-chan false
            :default :keep-alive)
      (<! (timeout 3000))
      (reset! frameworks (create-frameworks))
      (recur)))
  stop-chan))

(defstate looper
  :start (create-looper)
  :stop (async/close! looper))

