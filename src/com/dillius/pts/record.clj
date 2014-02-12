(ns com.dillius.pts.record
  (:require [clojure.walk :refer :all]
            [cheshire.core :refer :all]
            [cronj.core :refer :all]))

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
  (generate-string (@data user)))

(comment (defn asyncUpdate
           [user change]
           (fn [old-val]
             (assoc-in old-val [user :level] (validate-level (modify-level (or (:level (old-val user)) 0) change))))))

(comment (defn upsert-entry
           [user change]
           (send data (asyncUpdate user change))
           (str "Queued")))

(defn upsert-entry
  [user change]
  (let [current (or (:level (@data user)) 0)
        updated (validate-level (modify-level current (clojure.string/replace (str change) #"[%]" "")))]
    (if (clojure.string/blank? user)
      {:status 406 :body "Invalid Username!"}
      (dosync
       (alter data assoc-in [user :level] updated)
       (get-entry user)))))


(defn consolidate
  [entry]
  (assoc (second entry) :name (name (first entry))))

(defn get-all
  []
  (generate-string (reverse (sort-by :level (map consolidate @data)))))

(defn clear-data
  ([]
     (dosync (alter data empty))
     (comment (send data empty)))
  ([t opts] (clear-data))
  )

(defn zero-map
  [m]
  (into {} (for [[k v] m] [k 0])))

(defn zero-levels
  [m]
  (into {} (for [[k v] m] [k (assoc v :level 0)])))

(defn zero-data
  ([]
     (dosync (alter data zero-levels)))
  ([t opts] (zero-data)))
