(ns reagent-example.handler
  (:require [reagent-example.data :as data]
            [reagent-example.shared-code :as shared]
            [ring.middleware.format :refer [wrap-restful-format]]
            [compojure.core :refer [GET POST defroutes routes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [reagent-example.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.immutant :refer [get-sch-adapter]]
            [clojure.tools.logging :as log]
            ))

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

(defn create-ws []
  (let [{:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]}
        (sente/make-channel-socket! (get-sch-adapter) {})]
    (def ring-ajax-post                ajax-post-fn)
    (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
    (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
    (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
    (def connected-uids                connected-uids) ; Watchable, read-only atom

    (add-watch data/frameworks :connected-uids
               (fn [_ _ old new]
                 (when (not= old new)
                   (log/info "Sending framework updates...")
                   (doseq [uid (:any @connected-uids)]
                     (chsk-send!
                      uid
                      [:some/request-id new])
                     (log/info "Sent update to user" uid)))))))

(defroutes http-routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/frameworks" [] {:body @data/frameworks})

  (resources "/")
  (not-found "Not Found"))

(defn http-wrapper [handler]
  (-> handler
      wrap-restful-format
      wrap-middleware))

(defroutes ws-routes
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))

(defn ws-wrapper [handler]
  (-> handler
      ring.middleware.keyword-params/wrap-keyword-params
      ring.middleware.params/wrap-params))

(def app-routes
  (routes
   (ws-wrapper ws-routes)
   (http-wrapper http-routes)
   ))

(def app  #'app-routes)
