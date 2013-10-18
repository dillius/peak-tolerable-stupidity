(ns com.dillius.pts.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [ring.util.response :as resp]
            [clojure.walk :refer :all]
            [cheshire.core :refer :all]
            [cronj.core :refer :all]
            [com.dillius.pts.record :as rec]))


(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

  (POST "/api/entry" {:keys [params]} (generate-string (rec/upsert-entry (:user params) (:level params))))
  (GET "/api/entry" {:keys [params]} (generate-string (rec/get-entry (:user params))))
  (GET "/api/entry/:user" [user] (generate-string (rec/get-entry user)))

  (GET "/api/entries" {:keys [params]} (generate-string (rec/get-all)))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(def clear-task
  {:id "clear-task"
   :handler rec/clear-data
   :schedule "0 0 0 * * * *"
   })

(def cj (cronj :entries [clear-task]))
