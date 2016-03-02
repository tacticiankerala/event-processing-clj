(defproject event-processing "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.onyxplatform/onyx "0.8.6"]
                 [org.onyxplatform/onyx-sql "0.8.6.0"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [org.onyxplatform/onyx-kafka "0.8.6.0"]
                 [org.onyxplatform/onyx-metrics "0.8.6.0"]
                 [org.onyxplatform/onyx-seq "0.8.6.0"]
                 [cheshire "5.5.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [aero "0.1.3"]]
  :profiles {:uberjar {:aot [event-processing.launcher.aeron-media-driver
                             event-processing.launcher.launch-prod-peers]}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["src"]}})
