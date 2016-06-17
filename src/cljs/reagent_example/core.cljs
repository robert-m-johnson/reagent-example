(ns reagent-example.core
  (:require-macros [cljs.core.async.macros :refer [alt! go go-loop]])
  (:require [cljs.core.async :as async :refer [timeout <! chan]]
            [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [ajax.core :refer [GET POST]]
            [ajax.edn :refer [edn-response-format]]
            [mount.core :as mount :include-macros true]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;; -------------------------
;; Views

;;(defn home-page []
;;  [:div [:h2 "Welcome to the about page"]
;;   [:div [:a {:href "/about"} "go to about page"]]])

;;(defn about-page []
;;  [:div [:h2 "About reagent-example"]
;;   [:div [:a {:href "/"} "go to the home page"]]])

;;(defn current-page []
;;  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

;;(secretary/defroute "/" []
;;  (session/put! :current-page #'home-page))

;;(secretary/defroute "/about" []
;;  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defonce frameworks (atom [{:name "Regeant"}]))

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
                  ;;(.log js/console (str response))
                  (reset! frameworks response)
                  )
       }))

(defn create-looper []
  (let [stop-chan (chan)]
    (go-loop []
      (when (alt!
              stop-chan false
              :default :keep-alive)
        (<! (timeout 3000))
        (fetch-data)
        (recur)))
    stop-chan))

;; (defstate looper
;;   :start (create-looper)
;;   :stop (async/close! looper))

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
  (create-looper)
  )
