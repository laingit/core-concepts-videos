(ns fulcro-template.intro
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [om.next :as om :refer-macros [defui]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [core-concepts.client :as client]
            [om.dom :as dom]))

(defcard-fulcro root-card
  client/Root
  {}
  {:inspect-data true})


