(ns reagent-example.server
  (:require [mount.core :as mount :refer [defstate]]
            [reagent-example.handler :refer [app]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn create-server []
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty app {:port port :join? false})))

(defstate server
  :start (create-server)
  ;;:stop (.stop server)
  )

(defn -main [& args]
  (mount/start))
