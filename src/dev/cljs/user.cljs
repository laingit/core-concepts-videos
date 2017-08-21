(ns cljs.user
  (:require
    [om.next :as om]
    [cljs.pprint :refer [pprint]]
    [fulcro.client.logging :as log]
    [core-concepts.client :as core]
    [fulcro.client.core :as fc]))

(enable-console-print!)
(log/set-level :all)

(defn mount []
  (reset! core/app (fc/mount @core/app core/Root "app")) )

(mount)
