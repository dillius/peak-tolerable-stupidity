(ns com.dillius.pts.main
  (:use ring.adapter.jetty)
  (:require
   [com.dillius.pts.handler :as handler]
   [com.dillius.pts.recordMongo :as rec]
   [cronj.core :refer :all])
  (:gen-class))

(def clear-task
  {:id "clear-task"
   :handler rec/zero-data
   :schedule "0 0 0 * * * *"
   })

(def cronJobs (cronj :entries [clear-task]))

(defn -main [& [port]]
  (let [port (Integer. (or port (System/getenv "PORT") 3011))]
    (start! cronJobs)
    (run-jetty
     #'handler/app
     {:host "127.0.0.1" :port port :join? false})))
