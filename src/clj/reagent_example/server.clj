(ns reagent-example.server
  (:require [mount.core :as mount :refer [defstate]]
            [reagent-example.handler :refer [app]]
            [config.core :refer [env]]
            [immutant.web])
  (:gen-class))

(defn create-server []
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (immutant.web/run app {:port port :path "/"})))

;;(defstate server
;;  :start (create-server)
;;  :stop (.stop server)
;;  )

(defn -main [& args]
  (def server (create-server))
  (mount/start))
