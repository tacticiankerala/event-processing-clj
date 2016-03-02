(ns event-processing.launcher.launch-prod-peers
  (:require [clojure.core.async :refer [<!! chan]]
            [aero.core :refer [read-config]]
            [taoensso.timbre :as t]
            [onyx.plugin.kafka]
            [onyx.plugin.sql]
            [onyx.plugin.core-async]
            [onyx.plugin.seq]
            [onyx.lifecycle.metrics.timbre]
            [onyx.lifecycle.metrics.metrics]
            [event-processing.functions.sample-functions]
            [event-processing.jobs.sample-submit-job]
            [event-processing.lifecycles.sample-lifecycle])
  (:gen-class))

(defn -main [n & args]
  (let [n-peers (Integer/parseInt n)
        config (read-config (clojure.java.io/resource "config.edn") {:profile :default})
        peer-config (-> (:peer-config config)
                        (assoc :onyx.log/config {:appenders {} :min-level :info}))
        peer-group (onyx.api/start-peer-group peer-config)
        env (onyx.api/start-env (:env-config config))
        peers (onyx.api/start-peers n-peers peer-group)]
    (println "Attempting to connect to to Zookeeper: " (:zookeeper/address peer-config))
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread.
                        (fn []
                          (doseq [v-peer peers]
                            (onyx.api/shutdown-peer v-peer))
                          (onyx.api/shutdown-peer-group peer-group)
                          (shutdown-agents))))
    (println "Started peers. Blocking forever.")
    ;; Block forever.
    (<!! (chan))))
