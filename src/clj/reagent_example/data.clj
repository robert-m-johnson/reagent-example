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

(def frameworks
  (apply vector (map #(hash-map :name %) names)))
