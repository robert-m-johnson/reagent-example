(ns reagent-example.core
  (:require-macros [reagent-example.async-util-cljs :refer [go-looper]])
  (:require [cljs.core.async :as async :refer [timeout <! chan]]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :refer [GET POST]]
            [ajax.edn :refer [edn-response-format]]
            [mount.core :as mount :include-macros true]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defonce frameworks (atom []))

(defn framework-table-row
  [{:keys [name stars forks]
    :as framework}]
  [:tr
   [:td name]
   [:td stars]
   [:td forks]])

(defn frameworks-table []
  [:table
   [:thead
    [:tr
     [:th "Name"]
     [:th "Stars"]
     [:th "Forks"]]]
   [:tbody
    (let [fks @frameworks]
      (for [framework fks]
        ^{:key (:name framework)} [framework-table-row framework]))]])

(defn layout []
  [:div
   [:div [:h1 "Reagent Example"]]
   [frameworks-table]])

(defn mount-root []
  (reagent/render [layout] (.getElementById js/document "app")))

;; Ajax
(defn handler [response]
  (.log js/console (str response)))

(defn fetch-data []
  (GET "/frameworks"
      {:response-format (ajax.edn/edn-response-format)
       :handler (fn [response]
                  (reset! frameworks response))}))

(defn init! []
  (fetch-data)
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root)
   (go-looper
    (do
      (<! (timeout 3000))
      (fetch-data)))
  )
