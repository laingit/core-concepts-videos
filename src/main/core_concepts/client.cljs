(ns core-concepts.client
  (:require-macros [core-concepts.client :refer [defident]])
  (:require
    [om.next :as om :refer [defui]]
    [om.dom :as dom]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.ui.bootstrap3 :as b]
    [fulcro.client.core :as fc]
    [fulcro.client.data-fetch :as df]))

(defident person-ident [person-or-id] :PERSON/by-id)
(defident job-ident [job-or-id] :JOB/by-id)
(defident duty-ident [job-or-id] :DUTY/by-id)

(defui ^:once Duty
  static om/IQuery
  (query [this] [:db/id :duty/name])
  static om/Ident
  (ident [this props] (duty-ident props))
  Object
  (render [this]
    (let [{:keys [db/id duty/name]} (om/props this)]
      (dom/span #js {:style #js {:borderBottom "1px solid black" :marginLeft "4pt"}} name))))

(def ui-duty (om/factory Duty {:keyfn :db/id}))

(defui ^:once Job
  static om/IQuery
  (query [this] [:db/id :job/title {:job/duties (om/get-query Duty)}])
  static om/Ident
  (ident [this props] (job-ident props))
  Object
  (render [this]
    (let [{:keys [db/id job/title job/duties] :as p} (om/props this)]
      (dom/span nil
        title ": " (map ui-duty duties)))))

(def ui-job (om/factory Job {:keyfn :db/id}))

(defui ^:once Person
  static om/IQuery
  (query [this] [:ui/fetch-state :db/id :person/name :person/age {:person/job (om/get-query Job)}])
  static om/Ident
  (ident [this props] (person-ident props))
  Object
  (render [this]
    (let [{:keys [db/id person/name person/age person/job]} (om/props this)]
      (b/row {}
        (b/col {:xs 3} (dom/span nil name))
        (b/col {:xs 3} (dom/span nil age))
        (when job
          (b/col {:xs 6}
            (ui-job job)))))))

(def ui-person (om/factory Person {:keyfn :db/id}))

(defui ^:once PersonList
  static fc/InitialAppState
  (initial-state [c params]
    {:people []})
  static om/IQuery
  (query [this] [{:people (om/get-query Person)}])
  static om/Ident
  (ident [this props] [:PERSON-LIST :singleton])
  Object
  (render [this]
    (let [{:keys [people]} (om/props this)]
      (b/panel {}
        (b/panel-heading {}
          (b/panel-title {} "People"))
        (b/panel-body {}
          (b/container-fluid nil
            (b/row {}
              (b/col {:xs 3} (dom/b nil "Name"))
              (b/col {:xs 3} (dom/b nil "Age"))
              (b/col {:xs 3} (dom/b nil "Job")))
            (df/lazily-loaded
              #(map ui-person %)
              people)))))))

(def ui-person-list (om/factory PersonList))

(defui ^:once Root
  static fc/InitialAppState
  (initial-state [c params] {:person-list (fc/get-initial-state PersonList nil)})
  static om/IQuery
  (query [this] [:ui/react-key {:person-list (om/get-query PersonList)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key person-list]} (om/props this)]
      (dom/div #js {:key react-key}
        (ui-person-list person-list)))))

(defonce app (atom (fc/new-fulcro-client)))
