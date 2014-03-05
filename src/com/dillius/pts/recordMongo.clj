(ns com.dillius.pts.recordMongo
  (:require [clojure.walk :refer :all]
            [cheshire.core :refer :all]
            [cronj.core :refer :all]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all])
  (:import [com.mongodb MongoOptions ServerAddress]))

(mg/connect!)
(mg/set-db! (mg/get-db "pts"))

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
  (generate-string (dissoc (mc/find-one-as-map "levels" {:name user}) :_id)))

(defn upsert-entry
  [user change]
  (let [current (or (:level (mc/find-one-as-map "levels" {:name user})) 0)
        updated (validate-level (modify-level current (clojure.string/replace (str change) #"[%]" "")))]
    (if (clojure.string/blank? user)
      {:status 406 :body "Invalid Username!"}
      (do
        (mc/update "levels" {:name user} {:name user :level updated} :upsert true)
       (get-entry user)))))


(defn get-all
  []
  (generate-string (map #(dissoc % :_id) (reverse (sort-by :level (mc/find-maps "levels"))))))

(defn clear-data
  ([]
     (mc/remove "levels"))
  ([t opts] (clear-data))
  )

(defn zero-data
  ([]
     (mc/update "levels" {} {"$set" {:level 0}} :multi true))
  ([t opts] (zero-data)))
