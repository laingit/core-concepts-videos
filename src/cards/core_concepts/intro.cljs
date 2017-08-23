(ns fulcro-template.intro
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [om.next :as om :refer-macros [defui]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.core :as fc]
            [core-concepts.client :as client]
            [core-concepts.people :as ppl]
            [om.next.protocols :as omp]
            [om.dom :as dom]
            [fulcro.client.data-fetch :as df]))

(defcard-fulcro root-card
  client/Root
  {}
  {:inspect-data true
   :fulcro       {:started-callback
                  (fn [app]
                    (df/load app :query/all-people client/Person
                      {:target [:PERSON-LIST :singleton :people]})
                    )}})




(comment


  ; defcard-fulcro puts the application in the atom cardname-fulcro-app
  root-card-fulcro-app

  ; define a tree of data that matches our data graph
  (def new-person {:db/id       6
                   :person/name "New Person"
                   :person/age  45
                   :person/job  {:db/id      88
                                 :job/title  "Software Engineer"
                                 :job/duties [{:db/id     32
                                               :duty/name "Write Code"}]}})

  ; see that the query graph follows that shape
  (om/get-query client/Person)

  (def app @root-card-fulcro-app)

  ; The two combined can be used to place new data into our graph db. This is how
  ; you'd do it, say, with server push. But it is essentially what all remote
  ; interactions boil down to:
  (fc/merge-state! app
    client/Person
    new-person
    :append [:PERSON-LIST :singleton :people])

  ; Or you could take it a level higher.
  (fc/merge-state! app
    client/PersonList
    {:people ppl/people}
    :replace [:person-list])

  (om/force-root-render! (:reconciler app))

  )


