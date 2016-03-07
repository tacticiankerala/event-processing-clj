(ns event-processing.jobs.sample-submit-job
    (:require [event-processing.catalogs.sample-catalog :refer [build-catalog]]
              [event-processing.tasks.core-async :refer [add-core-async-input add-core-async-output]]
              [event-processing.tasks.sql :refer [add-sql-partition-input add-sql-insert-output]]
              [event-processing.tasks.file-input :refer [add-seq-file-input]]
              [event-processing.lifecycles.sample-lifecycle :refer [build-lifecycles]]
              [event-processing.lifecycles.metrics :refer [add-metrics]]
              [event-processing.lifecycles.logging :refer [add-logging]]
              [event-processing.workflows.sample-workflow :refer [build-workflow]]
              [event-processing.tasks.rabbitmq :refer [add-rabbitmq-input]]
              [aero.core :refer [read-config]]
              [onyx.api]
              [onyx.lifecycle.metrics.websocket]
              [onyx.lifecycle.metrics.metrics]
              [onyx.plugin.rabbitmq-input]))

;;;;
;; Lets build a job
;; Depending on the mode, the job is built up in a different way
;; When :dev mode, onyx-seq will be used as an input, with the meetup data being
;; included in the onyx-seq lifecycle for easy access
;; core.async is then added as an output task
;;
;; When using :prod mode, kafka is added as an input, and onyx-sql is used as the output

(defn build-job [mode]
  (let [batch-size 1
        batch-timeout 1000
        base-job {:catalog (build-catalog batch-size batch-timeout)
                  :lifecycles (build-lifecycles {:mode mode})
                  :workflow (build-workflow {:mode mode})
                  :task-scheduler :onyx.task-scheduler/balanced}]
    (cond-> base-job
      (= :dev mode) (add-sql-insert-output :write-lines {:onyx/batch-size batch-size
                                                          :sql/classname "org.postgresql.Driver"
                                                          :sql/subprotocol "postgresql"
                                                          :sql/subname "//0.0.0.0:5432/onemdm"
                                                          :sql/user "postgres"
                                                          :sql/password "postgres"
                                                          :sql/table :heartbeats})
      (= :dev mode) (add-rabbitmq-input :read-lines {:onyx/batch-size batch-size
                                                      :rabbit/queue-name "heartbeats"
                                                      :rabbit/host "0.0.0.0"
                                                      :rabbit/port 5672
                                                      :rabbit/deserializer :event-processing.functions.sample-functions/rabbitmq-deserializer
                                                      :onyx/doc "Read segments from rabbitmq queue"
                                                      })
      (= :prod mode) (add-rabbitmq-input :read-lines {:onyx/batch-size batch-size
                                                      :rabbit/queue-name "heartbeats"
                                                      :rabbit/host "rabbitmq"
                                                      :rabbit/port 5672
                                                      :rabbit/deserializer :event-processing.functions.sample-functions/rabbitmq-deserializer
                                                      :onyx/doc "Read segments from rabbitmq queue"
                                                      })
      (= :prod mode) (add-sql-insert-output :write-lines {:onyx/batch-size batch-size
                                                          :sql/classname "org.postgresql.Driver"
                                                          :sql/subprotocol "postgresql"
                                                          :sql/subname "//db:5432/onemdm"
                                                          :sql/user "postgres"
                                                          :sql/password "postgres"
                                                          :sql/table :heartbeats})
      true (add-metrics :all {:metrics/buffer-capacity 10000
                              :metrics/sender-fn :onyx.lifecycle.metrics.websocket/websocket-sender
                              :websocket/address "ws://dashboard:3000/metrics"
                              :metrics/workflow-name "meetup-workflow"})
      true (add-logging :write-lines))))

(defn -main [& args]
  (let [config (read-config (clojure.java.io/resource "config.edn") {:profile :dev})
        peer-config (get config :peer-config)
        job (build-job :prod)]
    (let [{:keys [job-id]} (onyx.api/submit-job peer-config job)
          ]
      (println "Submitted job: " job-id))))
