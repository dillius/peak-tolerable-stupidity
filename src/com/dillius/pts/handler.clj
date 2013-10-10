(ns com.dillius.pts.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [ring.util.response :as resp]
            [clojure.walk :refer :all]
            [cheshire.core :refer :all]))

(defrecord Entry [level])
(def data (ref {}))

(defn upsertEntry
  [user level]
  (dosync
   (alter data assoc-in [user :level] level)))

(defn getEntry
  [user]
  (@data user))

(defn getAll
  []
  @data)


(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

  (POST "/api/entry" {:keys [params]} (upsertEntry (:user params) (:level params)))
  (GET "/api/entry" {:keys [params]} (generate-string (getEntry (:user params))))
  (GET "/api/entry/:user" [user] (generate-string (getEntry user)))

  (GET "/api/entries" {:keys [params]} (generate-string (getAll)))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes)
)
