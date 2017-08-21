(ns core-concepts.client
  (:require
    [om.next :as om :refer [defui]]
    [om.dom :as dom]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.core :as fc]))

(defn person-ident [person-or-id]
  (if (map? person-or-id)
    [:PERSON/by-id (:db/id person-or-id)]
    [:PERSON/by-id person-or-id]))

(defn set-liked*
  [state-map id yes-no]
  (update-in state-map (person-ident id) assoc :tony-likes? yes-no))

(defn increment-age*
  [state-map id]
  (update-in state-map (conj (person-ident id) :person/age) inc))

(defmutation set-liked
  [{:keys [person-id yes-no]}]
  (action [{:keys [state]}]
    (swap! state (fn [s]
                   (-> s
                     (increment-age* person-id)
                     (set-liked* person-id yes-no))))))

(defui ^:once Person
  static fc/InitialAppState
  (initial-state [c {:keys [id name age]}] {:db/id id :person/name name :person/age age :tony-likes? false})
  static om/IQuery
  (query [this] [:db/id :person/name :person/age :tony-likes?])
  static om/Ident
  (ident [this props] (person-ident props))
  Object
  (render [this]
    (let [{:keys [db/id person/name person/age tony-likes?]} (om/props this)]
      (dom/div nil
        (dom/p nil (str "Name: " name))
        (dom/p nil (str "Age: " age))
        (dom/span nil
          "Liked by Tony?")
        (dom/input #js {:type    "checkbox"
                        :onClick (fn [e] (om/transact! this `[(set-liked ~{:person-id id :yes-no (not tony-likes?)})]))
                        :checked tony-likes?})))))

(def ui-person (om/factory Person {:keyfn :db/id}))

(defui ^:once PersonList
  static fc/InitialAppState
  (initial-state [c params] {:people [(fc/get-initial-state Person {:id 1 :name "Job" :age 22})
                                      (fc/get-initial-state Person {:id 2 :name "Sally" :age 62})
                                      (fc/get-initial-state Person {:id 3 :name "Alex" :age 32})]})
  static om/IQuery
  (query [this] [{:people (om/get-query Person)}])
  static om/Ident
  (ident [this props] [:PERSON-LIST :singleton])
  Object
  (render [this]
    (let [{:keys [people]} (om/props this)]
      (dom/div nil
        (map ui-person people)))))

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
