(ns event-processing.tasks.rabbitmq
  (:require [schema.core :as s]
            [onyx.plugin.rabbitmq-input]))


(s/defn add-rabbitmq-input
  "Adds a rabbitmq queue as input task to a job"
  [job task opts]
  (-> job
      (update :catalog conj (merge {:onyx/name task
                                    :onyx/plugin :onyx.plugin.rabbitmq-input/input
                                    :onyx/type :input
                                    :onyx/medium :rabbitmq
                                    ;:onyx/batch-size batch-size
                                    ;:onyx/max-peers 1
                                    }
                                   opts))
      (update :lifecycles conj {:lifecycle/task task
                                   :lifecycle/calls :onyx.plugin.rabbitmq-input/reader-calls})))

