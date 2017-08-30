(ns core-concepts.server
  (:require [fulcro.easy-server :refer [make-fulcro-server]]
            [core-concepts.people :as ppl]
            [fulcro.server :refer [defquery-root defquery-entity]]
            [fulcro-sql.core :as sql]
            [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

(def schema {::sql/joins      {:person/job (sql/to-one [:person/current_job_id :job/id])
                               :job/duties [:job/id :job_duties/job_id :job_duties/duty_id :duty/id]}
             ::sql/pks        {}
             ::sql/graph->sql {}})

(defrecord Seeder [databases]
  component/Lifecycle
  (start [this]
    (if-let [people-db (sql/get-dbspec databases :people)]
      (jdbc/with-db-transaction [db people-db]
        (let [nrows (jdbc/query db ["SELECT COUNT(*) AS cnt FROM person"] {:row-fn :cnt :result-set-fn first})]
          (when (zero? nrows)
            (timbre/info "People database is empty. Seeding some initial data")
            (sql/seed! people-db schema
              [(sql/seed-row :duty {:id :id/sweep :name "Sweep"})
               (sql/seed-row :duty {:id :id/mop :name "Mop"})
               (sql/seed-row :duty {:id :id/code :name "Code"})
               (sql/seed-row :duty {:id :id/drink-beer :name "Drink Beer"})
               (sql/seed-row :job {:id :id/janitor :title "Janitor"})
               (sql/seed-row :job {:id :id/programmer :title "Programmer"})
               (sql/seed-row :job_duties {:id :join-1 :job_id :id/janitor :duty_id :id/mop})
               (sql/seed-row :job_duties {:id :join-2 :job_id :id/janitor :duty_id :id/sweep})
               (sql/seed-row :job_duties {:id :join-3 :job_id :id/programmer :duty_id :id/drink-beer})
               (sql/seed-row :job_duties {:id :join-4 :job_id :id/programmer :duty_id :id/code})
               (sql/seed-row :person {:id :sally :name "Sally" :age 41 :person/current_job_id :id/janitor})
               (sql/seed-row :person {:id :renee :name "Renee" :age 28 :person/current_job_id :id/programmer})]))))
      (timbre/error "Unable to get injected database :people"))
    this)
  (stop [this] this))

(defn make-system [config]
  (make-fulcro-server
    :config-path config
    :parser-injections #{:databases}
    :components {:seeder    (component/using (map->Seeder {})
                              [:databases])
                 :databases (component/using (sql/build-db-manager {})
                              [:config])}))

(defquery-root :query/all-people
  "Get all the people"
  (value [{:keys [databases query]} params]
    (timbre/info "Subquery " query)
    (let [people-db (sql/get-dbspec databases :people)]
      (jdbc/with-db-transaction [db people-db]
        (let [all-people (jdbc/query db ["SELECT id FROM person"] {:row-fn :id :result-set-fn set})]
          (sql/run-query people-db schema :person/id query all-people))))))

(defquery-entity :JOB/by-id
  (value [{:keys [databases query]} id params]
    (timbre/info "Subquery " query)
    (let [people-db     (sql/get-dbspec databases :people)
          query-with-id (conj query :db/id)]
      (jdbc/with-db-transaction [db people-db]
        ; run-query will return a vector, but this is a to-one client query
        (first (sql/run-query people-db schema :job/id query-with-id #{id}))))))














(comment

  (let [db (-> user/system deref :databases (sql/get-dbspec :people))]
    (jdbc/with-db-connection [c db]
      (jdbc/query c ["SELECT id FROM person"])))

  (let [db (-> user/system deref :databases (sql/get-dbspec :people))]
    (jdbc/with-db-connection [c db]
      (sql/run-query c schema :person/id [:db/id :person/name {:person/job [:db/id :job/title]}] #{1 2}))
    )
  )
