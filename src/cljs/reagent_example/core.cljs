(ns reagent-example.core
  (:require-macros [reagent-example.async-util-cljs :refer [go-looper]]
                   [reagent-example.components :refer [table]])
  (:require [cljs.core.async :as async :refer [timeout <! chan]]
            [reagent-example.shared-code :as shared]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :refer [GET POST]]
            [ajax.edn :refer [edn-response-format]]
            [mount.core :as mount :include-macros true]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

(defonce frameworks (atom []))

;; (defn frameworks-table []
;;   [:table
;;    [:thead
;;     [:tr
;;      [:th "Name"]
;;      [:th "Stars"]
;;      [:th "Forks"]]]
;;    [:tbody
;;     (let [fks @frameworks]
;;       (for [framework fks]
;;         (with-meta
;;           [:tr
;;            [:td (:name framework)]
;;            [:td (:stars framework)]
;;            [:td (:forks framework)]]
;;           {:key (:name framework)})
;;         ))]])

(defn frameworks-table []
  (table
   frameworks
   :name
   [["Name" :name]
    ["Stars" :stars]
    ["Forks" :forks]]))

(defn layout []
  [:div
   [:div [:h1 "Reagent Example"]]
   [frameworks-table]
   [:hr]
   [:div (shared/get-message)]])

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
      (fetch-data))))
