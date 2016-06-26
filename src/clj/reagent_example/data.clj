(ns reagent-example.data
  (:require [clojure.core.async :as async :refer
             [alt! go-loop timeout <! chan]]
            [mount.core :refer [defstate]]))

(def ^:private real-names
  ["Angular" "Babylon.js" "Backbone" "Batman.js" "Brink.js" "Cappucino" "Crafty" "Ember"
   "Enyo" "Hummingbird" "Jaggery" "jQuery" "Kango" "Lodash" "Mithril" "Potato" "React"
   "RequireJS" "Snack" "truckJS" "Underscore" "vanilla" "Zepto"])

(def ^:private nouns
  ["Snow" "Cherry" "Boat" "Lettuce" "Leather" "Desk" "Chicken" "poison" "sense" "cushion"
   "Machine" "pizza" "tramp" "Trousers" "Silver" "daddy" "Flag" "finger" "Pocket" "Silk"
   "Limit" "zoo" "fear" "egg" "baboon" "js" "things" "Fireman" "Regret"])

(defn- append-js [s] (str s ".js"))

(def names
  (let [fake-names (map append-js nouns)]
    (->>
     (concat real-names fake-names)
     (into (sorted-set-by String/CASE_INSENSITIVE_ORDER)))))

(defn name->framework [name]
  {:name name
   :stars (rand-int 500)
   :forks (rand-int 50)})

(defn create-frameworks
  ([] (create-frameworks names))
  ([names]
   (mapv name->framework names)))

(def frameworks
  (atom (create-frameworks)))

(defn unique-rands
  "Generates a set of n random numbers between 0 and l-1"
  [n l]
  (if (>= n l)
    (into #{} (range l))
    (loop [acc #{}]
      (if (= (count acc) n)
        acc
        (let [r (rand-int l)]
          (if (acc r)
            (recur acc)
            (recur (conj acc r))))))))

(defn update-some-frameworks [frameworks]
  (let [c (count frameworks)
        indices (range c)
        sample-size (rand-int (quot c 2))
        some-indices (unique-rands sample-size c)]
    (mapv
     (fn [index]
       (let [framework (frameworks index)]
         (if (some-indices index)
           (name->framework (:name framework))
           framework)))
     indices)))

(defn create-looper []
  (let [stop-chan (chan)]
    (go-loop []
      (when (alt!
              stop-chan false
              :default :keep-alive)
        (<! (timeout 3000))
        (swap! frameworks update-some-frameworks)
        (recur)))
    stop-chan))

(defstate looper
  :start (create-looper)
  :stop (async/close! looper))

