(ns event-processing.catalogs.sample-catalog
    (:require [event-processing.functions.sample-functions]))

;;; Catalogs describe each task in a workflow. We use
;;; them for describing input and output sources, injecting parameters,
;;; and adjusting performance settings.

(defn build-catalog [batch-size batch-timeout]
  [{:onyx/name :format-line
    :onyx/fn :event-processing.functions.sample-functions/format-line
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Strips the line of any leading or trailing whitespace"}

   {:onyx/name :identity
    :onyx/fn :clojure.core/identity
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "identity"}

   {:onyx/name :upper-case
    :onyx/fn :event-processing.functions.sample-functions/upper-case
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Capitalizes the first letter of the line"}

   {:onyx/name :extract-heartbeat-info
    :onyx/fn :event-processing.functions.sample-functions/transform-segment-shape
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout
    :onyx/doc "Extracts group-id group-city and category"}

   {:onyx/name :prepare-rows
    :onyx/fn :event-processing.functions.sample-functions/prepare-rows
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout}])
