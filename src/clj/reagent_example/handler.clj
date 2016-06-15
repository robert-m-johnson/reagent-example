(ns reagent-example.handler
  (:require [reagent-example.frameworks :as frameworks]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [reagent-example.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(def loading-page
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/frameworks" [] {:body ["foo" "bar"]})

  (resources "/")
  (not-found "Not Found"))

(defn outer-wrap [handler]
  (-> handler
      (wrap-restful-format)))

(def app (outer-wrap (wrap-middleware #'routes)))
