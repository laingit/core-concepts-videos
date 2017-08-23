(ns core-concepts.people)

(def software-eng {:db/id      2
                   :job/title  "Software Engineer"
                   :job/duties [{:db/id     3
                                 :duty/name "Write Code"}]})

(def sally {:db/id       1
            :person/name "Sally "
            :person/age  25
            :person/job  {:db/id      1
                          :job/title  "CEO"
                          :job/duties [{:db/id     1
                                        :duty/name "Impress Clients"}
                                       {:db/id     2
                                        :duty/name "Annoy Developers"}]}})

; NOTE: software-eng will be exact same db entity for both of these:
(def mary {:db/id       2
           :person/name "Mary"
           :person/age  32
           :person/job  software-eng})

(def kyle {:db/id       3
           :person/name "Kyle"
           :person/age  55
           :person/job  software-eng})

(def people [sally mary kyle])
