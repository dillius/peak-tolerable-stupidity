(ns com.dillius.pts.handler
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [ring.util.response :as resp]
            [clojure.walk :refer :all]
            [cheshire.core :refer :all]
            [cronj.core :refer :all]))

(defrecord Entry [level])
(def data (ref {}))

(defn upsert-entry
  [user level]
  (dosync
   (alter data assoc-in [user :level] level)))

(defn get-entry
  [user]
  (@data user))

(defn get-all
  []
  @data)

(defn clear-data
  ([] (dosync (alter data empty)))
  ([t opts] (clear-data))
  )

(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

  (POST "/api/entry" {:keys [params]} (upsert-entry (:user params) (:level params)))
  (GET "/api/entry" {:keys [params]} (generate-string (get-entry (:user params))))
  (GET "/api/entry/:user" [user] (generate-string (get-entry user)))

  (GET "/api/entries" {:keys [params]} (generate-string (get-all)))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(def clear-task
  {:id "clear-task"
   :handler clear-data
   :schedule "0 0 0 * * * *"
   })

(def cj (cronj :entries [clear-task]))
