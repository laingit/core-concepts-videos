(ns core-concepts.client)

(defmacro defident [sym arglist table-name]
  (let [argname (first arglist)]
    `(defn ~sym [~argname]
       (if (map? ~argname)
         [~table-name (:db/id ~argname)]
         [~table-name ~argname]))))

(comment
  (macroexpand-1 '(defident person-ident [person-or-id] :PERSON/by-id) ))

