(ns core-concepts.server
  (:require [fulcro.easy-server :refer [make-fulcro-server]]
            [core-concepts.people :as ppl]
            [fulcro.server :refer [defquery-root defquery-entity]]
            [taoensso.timbre :as timbre]))

(defn make-system [config]
  (make-fulcro-server
    :config-path config))

(defquery-root :query/all-people
  "Get all the people"
  (value [env params]
    (Thread/sleep 500)
    (timbre/info "Query params" params)
    ppl/people))
