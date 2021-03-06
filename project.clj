(defproject peak-tolerable-stupidity "0.1.2"
  :description "Peak Tolerable Stupidity Tracker"
  :url "http://www.peaktolerablestupidity.com"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [cheshire "5.0.2"]
                 [clj-time "0.6.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [im.chit/cronj "0.9.6"]
                 [com.novemberain/monger "1.7.0"]
                 [lein-less-npm "0.1.0"]]
  :aot [com.dillius.pts.main]
  :main com.dillius.pts.main
  :plugins [[lein-ring "0.8.2"]
            [lein-less-npm "0.1.0-SNAPSHOT"]]
  :ring {:handler com.dillius.pts.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}}
  :uberjar-name "peak-tolerable-stupidity.jar"
  :keep-non-project-classes true
  :license {:name "Apache License Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :min-lein-version "2.0.0"
  :less-in "resources/public/css/less/application.less"
  :less-out "resources/public/css/application.css"
  )
