(ns com.dillius.pts.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [ring.util.response :as resp]
            [clojure.walk :refer :all]
            [cheshire.core :refer :all]))



(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes)
)
