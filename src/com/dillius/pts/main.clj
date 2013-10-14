(ns com.dillius.pts.main
  (:use ring.adapter.jetty)
  (:require
   [com.dillius.pts.handler :as handler]
   [cronj.core :refer :all])
  (:gen-class))

(defn -main [& [port]]
  (let [port (Integer. (or port (System/getenv "PORT") 3011))]
    (start! handler/cj)
    (run-jetty
     #'handler/app
     {:host "127.0.0.1" :port port :join? false})))
