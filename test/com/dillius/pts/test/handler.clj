(ns com.dillius.pts.test.handler
  (:use clojure.test
        ring.mock.request
        clojure.walk
        com.dillius.pts.handler)
  (:require [cheshire.core :refer :all]))

(deftest test-app

  (testing "api entry POST"
    (let [response (app (request :post "/api/entry" {:user "dillius" :level "99"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry POST 2"
    (let [response (app (request :post "/api/entry" {:user "mike" :level "78"}))]
      (is (= (:status response) 200))
      )
    )

  (testing "api entry GET"
    (let [response (app (request :get "/api/entry" {:user "dillius"}))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= "99" (responseFields "level")))
        )
      )
    )

  (testing "api entry GET url"
    (let [response (app (request :get "/api/entry/mike"))]
      (is (= (:status response) 200))
      (let [responseFields (parse-string (:body response))]
        (is (not (nil? responseFields)))
        (is (= "78" (responseFields "level")))
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
        (is (= "99" ((responseFields "dillius") "level")))
        (is (= "40" ((responseFields "mike") "level")))
        )
      )
    )
  )
