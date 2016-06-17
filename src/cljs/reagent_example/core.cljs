(ns reagent-example.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [ajax.core :refer [GET POST]]
              [ajax.edn :refer [edn-response-format]]
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

(defn framework-table-row [{name :name :as framework}]
  [:tr [:td name]])

(defn frameworks-table []
  [:table
   [:tbody
    (let [fks @frameworks]
      (for [framework fks]
        ^{:key (:name framework)} [framework-table-row framework]))]])

(defn layout []
  [:div
   [:div [:h1 "Reagent example"]]
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
                  (.log js/console (str response))
                  (reset! frameworks response)
                  )
       }))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root)
  (fetch-data)
  )
