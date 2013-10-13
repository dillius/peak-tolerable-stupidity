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

(defn parse-int [s]
  (try
    (Integer/parseInt (re-find #"\A-?\d+" s))
    (catch NumberFormatException e nil)))

(defn modify-level
  [current val]
  (if (nil? (parse-int (subs val 0 1)))
    (if (= (subs val 0 1) "+")
      (+ current (parse-int (subs val 1)))
      (if (= (subs val 0 1) "-")
        (- current (parse-int (subs val 1)))
        current))
    (parse-int val)
    ))

(defn validate-level
  [val]
  (if (< val 0)
    0
    (if (> val 100)
      100
      val)))

(defn get-entry
  [user]
  (@data user))

(defn upsert-entry
  [user change]
  (let [current (or (:level (get-entry user)) 0)
        updated (validate-level (modify-level current (clojure.string/replace (str change) #"[%]" "")))]
    (dosync
     (alter data assoc-in [user :level] updated))
    (get-entry user)))


(defn consolidate
  [entry]
  (assoc (second entry) :name (name (first entry))))

(defn get-all
  []
  (reverse (sort-by :level (map consolidate @data))))

(defn clear-data
  ([] (dosync (alter data empty)))
  ([t opts] (clear-data))
  )

(defroutes app-routes
  (GET "/" [] (resp/resource-response "index.html" {:root "public"}))

  (POST "/api/entry" {:keys [params]} (generate-string (upsert-entry (:user params) (:level params))))
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
