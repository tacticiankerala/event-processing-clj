(ns event-processing.functions.sample-functions
    (:require [clojure
               [string :refer [capitalize trim]]]
              [taoensso.timbre :refer [info]]
              [cheshire.core :refer [parse-string]]
              [clj-time.coerce :as time-coerce]))

;;; Defines functions to be used by the peers. These are located
;;; with fully qualified namespaced keywords, such as
;;; event-processing.functions.sample-functions/format-line

(defn format-line [segment]
  (update-in segment [:line] trim))

(defn upper-case [{:keys [line] :as segment}]
  (if (seq line)
    (let [upper-cased (apply str (capitalize (first line)) (rest line))]
      (assoc-in segment [:line] upper-cased))
    segment))

(defn transform-segment-shape
  "Recursively restructures a segment {:new-key [paths...]}"
  [segment]
  (update-in segment [:createdAt] #(time-coerce/to-timestamp (time-coerce/from-long (Long/parseLong %)))))

(defn get-in-segment [keypath segment]
  (get-in segment keypath))

(defn prepare-rows [segment]
  (info (str "******>>> " segment))
  {:rows [segment]})

(defn rabbitmq-deserializer
  [^bytes payload]
  (parse-string (String. payload "UTF-8") true))
