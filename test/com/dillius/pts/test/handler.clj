(ns com.dillius.pts.test.handler
  (:use clojure.test
        ring.mock.request
        clojure.walk
        com.dillius.pts.handler
        cronj.core)
  (:require [cheshire.core :refer :all]))

(deftest test-app

  (testing "api entry POST"
    (let [response (app (request :post "/api/entry" {:user "dillius" :level "99"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry POST 2 with int"
    (let [response (app (request :post "/api/entry" {:user "mike" :level 78}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry GET"
    (let [response (app (request :get "/api/entry" {:user "dillius"}))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 99 (responseFields "level")))
        )
      )
    )

  (testing "api entry GET url"
    (let [response (app (request :get "/api/entry/mike"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 78 (responseFields "level")))
        )
      )
    )

  (testing "api entry POST 3"
    (let [response (app (request :post "/api/entry" {:user "mike" :level "40"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entries GET"
    (let [response (app (request :get "/api/entries"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 99 ((responseFields "dillius") "level")))
        (is (= 40 ((responseFields "mike") "level")))
        )
      )
    )

  ;;(clear-data) ; Clear the data out

  (def T1 (local-time 2013 10 01 23 59 58))
  (def T2 (local-time 2013 10 02 0 0 2))
  (simulate cj T1 T2)

  (testing "api entry POST 4"
    (let [response (app (request :post "/api/entry" {:user "derp" :level "15"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entries GET after-clear"
    (let [response (app (request :get "/api/entries"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 15 ((responseFields "derp") "level")))
        (is (nil? (responseFields "mike")))
        (is (nil? (responseFields "dillius")))
        )
      )
    )

  (testing "api entry POST mod positive"
    (let [response (app (request :post "/api/entry" {:user "derp" :level "+5"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry GET mod positive"
    (let [response (app (request :get "/api/entry/derp"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 20 (responseFields "level")))
        )
      )
    )

  (testing "api entry POST mod negative"
    (let [response (app (request :post "/api/entry" {:user "derp" :level "-8"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry GET mod negative"
    (let [response (app (request :get "/api/entry/derp"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 12 (responseFields "level")))
        )
      )
    )


  (testing "api entry POST mod junk"
    (let [response (app (request :post "/api/entry" {:user "derp" :level "d7t"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry GET mod junk"
    (let [response (app (request :get "/api/entry/derp"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= 12 (responseFields "level")))
        )
      )
    )


  )
