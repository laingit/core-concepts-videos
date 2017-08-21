(defproject core-concepts "0.1.0-SNAPSHOT"
  :min-lein-version "2.7.0"
  :plugins [[lein-cljsbuild "1.1.7"]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [com.stuartsierra/component "0.3.2"]
                 [fulcrologic/fulcro "1.0.0-beta8" :exclusions [commons-codec]]]

  :profiles {:dev {:source-paths ["src/dev" "src/main" "src/cards"]
                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [devcards "0.2.3"]]}}

  :source-paths ["src/main"]
  :clean-targets ^{:protect false} ["target" "resources/public/js"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :cljsbuild {:builds
              [{:id           "dev"
                :figwheel     {:on-jsload "cljs.user/mount"}
                :source-paths ["src/dev" "src/main"]
                :compiler     {:asset-path           "js/dev"
                               :main                 cljs.user
                               :optimizations        :none
                               :output-dir           "resources/public/js/dev"
                               :output-to            "resources/public/js/core_concepts.js"
                               :preloads             [devtools.preload]
                               :source-map-timestamp true}}
               {:id           "cards"
                :figwheel     {:devcards true}
                :source-paths ["src/main" "src/cards"]
                :compiler     {:asset-path           "js/cards"
                               :main                 core-concepts.cards
                               :optimizations        :none
                               :output-dir           "resources/public/js/cards"
                               :output-to            "resources/public/js/cards.js"
                               :preloads             [devtools.preload]
                               :source-map-timestamp true}}]})
