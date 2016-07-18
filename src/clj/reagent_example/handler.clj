(ns reagent-example.handler
  (:require [reagent-example.data :as data]
            [reagent-example.shared-code :as shared]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [reagent-example.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app])

(defn head []
  [:head
   [:title "Reagent Example"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(def loading-page
  (html5
    (head)
    [:body {:class "body-container"}
     [:div
      mount-target
      [:hr]
      [:div (shared/get-message)]]
     (include-js "/js/app.js")]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/frameworks" [] {:body @data/frameworks})

  (resources "/")
  (not-found "Not Found"))

(defn outer-wrap [handler]
  (-> handler
      (wrap-restful-format)))

(def app (outer-wrap (wrap-middleware #'routes)))
