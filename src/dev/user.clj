(ns user
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
    [com.stuartsierra.component :as component]
    core-concepts.server
    [figwheel-sidecar.system :as fig]))

;;FIGWHEEL
(def figwheel (atom nil))

; Usable from a REPL to start one-or-more figwheel builds
(defn start-figwheel
  "Start Figwheel on the given builds, or defaults to build-ids in `figwheel-config`."
  ([]
   (let [figwheel-config (fig/fetch-config)
         props           (System/getProperties)
         all-builds      (->> figwheel-config :data :all-builds (mapv :id))]
     (start-figwheel (keys (select-keys props all-builds)))))
  ([build-ids]
   (let [figwheel-config   (fig/fetch-config)
         default-build-ids (-> figwheel-config :data :build-ids)
         build-ids         (if (empty? build-ids) default-build-ids build-ids)
         preferred-config  (assoc-in figwheel-config [:data :build-ids] build-ids)]
     (reset! figwheel (component/system-map
                        :css-watcher (fig/css-watcher {:watch-paths ["resources/public/css"]})
                        :figwheel-system (fig/figwheel-system preferred-config)))
     (println "STARTING FIGWHEEL ON BUILDS: " build-ids)
     (swap! figwheel component/start)
     (fig/cljs-repl (:figwheel-system @figwheel)))))

;; ==================== SERVER ====================
(set-refresh-dirs "src/dev" "src/main" "src/cards")

(defn started? [sys]
  (-> sys :config :value))

(defonce system (atom nil))

(letfn [(refresh [& args] {:pre [(not @system)]} (apply tools-ns/refresh args))
        (init [path] {:pre [(not (started? @system))]}
          (when-let [new-system (core-concepts.server/make-system "config/dev.edn")]
            (reset! system new-system)))

        (start []
          {:pre [@system (not (started? @system))]}
          (swap! system component/start))

        (stop
          []
          (when (started? @system)
            (swap! system component/stop))
          (reset! system nil))]

  (defn go
    "Initialize the server and start it."
    ([] (go :dev))
    ([path] {:pre [(not @system) (not (started? @system))]}
     (init path)
     (start)))

  (defn restart
    "Stop, refresh, and restart the server."
    []
    (stop)
    (refresh :after 'user/go)))

