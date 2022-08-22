(ns technext.core
  (:require [technext.handler :refer [app]]
            [ring.adapter.jetty :as jetty]
            [technext.db :as db])
  (:gen-class))




(defn -main []
  (db/init-db)
  (let [port (Integer. (or (System/getenv "PORT")
                           3000))]

    (jetty/run-jetty #'app {:port  port
                            :join? false}))
  (println "server started"))



