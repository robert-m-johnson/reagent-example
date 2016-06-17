(ns reagent-example.data)

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

(def frameworks
  (apply vector (map name->framework names)))
