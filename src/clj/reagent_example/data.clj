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
   "JQuery"
   "Kango"
   "Mithril"
   "Potato"
   "React"
   "RequireJS"
   "Snack"
   "truckJS"
   "Zepto"
   ])

(defn name->framework [name]
  {:name name
   :stars (inc (rand-int 500))})

(def frameworks
  (apply vector (map name->framework names)))
