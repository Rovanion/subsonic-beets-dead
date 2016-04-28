(defproject webrtclojure "0.0.1"
  :description "Real time JSON data communication between browsers."
  :url "https://github.com/Rovanion/WebRTClojure"
  :license {:name "AGPLv3"
            :url "http://www.gnu.org/licenses/agpl-3.0.html"}

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [org.clojure/tools.reader "0.10.0"] ; Dependency issue between sente and ring.
   [ring-server "0.4.0"]               ; Library for starting a web server.
   [ring/ring-core "1.4.0"]            ; HTTP abstraction library.
   [ring/ring-defaults "0.2.0"]        ; Middleware collection.
   [yogthos/config "0.8"]              ; Managing environment configuration.
   [compojure "1.5.0"]                 ; Routing.
   [http-kit "2.1.19"]                 ; Our web server.

   [org.clojure/clojurescript "1.8.40"
    :scope "provided"]
   [reagent "0.5.1"                    ; React abstraction.
    :exclusions [org.clojure/tools.reader]]
   [reagent-forms "0.5.22"]
   [reagent-utils "0.1.7"]
   [secretary "1.2.3"]                 ; Client side routing.
   [venantius/accountant "0.1.7"]      ; Managing the URL bar in the browser.
   [com.taoensso/sente "1.8.1"]        ; WebSockets manager.

   [cljs-ajax "0.5.4"]                 ; Testing purposes only.
   ]
  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :ring {:handler      webrtclojure.handler/app
         :uberwar-name "webrtclojure.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "webrtclojure.jar"

  :main webrtclojure.server

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                        :output-dir "target/cljsbuild/public/js/out"
                                        :asset-path   "/js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}


  :profiles {:dev {:repl-options {:init-ns webrtclojure.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring/ring-mock "0.3.0"]
                                  [ring/ring-devel "1.4.0"]
                                  [prone "1.1.1"]
                                  [lein-figwheel "0.5.2"
                                   :exclusions [org.clojure/core.memoize
                                                ring/ring-core
                                                org.clojure/clojure
                                                org.ow2.asm/asm-all
                                                org.clojure/data.priority-map
                                                org.clojure/tools.reader
                                                org.clojure/clojurescript
                                                org.clojure/core.async
                                                org.clojure/tools.analyzer.jvm]]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  ;[figwheel-sidecar "0.5.0-2"]
                                  [pjstadig/humane-test-output "0.8.0"]
                                  ]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.2"
                              :exclusions [org.clojure/core.memoize
                                           ring/ring-core
                                           org.clojure/clojure
                                           org.ow2.asm/asm-all
                                           org.clojure/data.priority-map
                                           org.clojure/tools.reader
                                           org.clojure/clojurescript
                                           org.clojure/core.async
                                           org.clojure/tools.analyzer.jvm]]
                             ]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :nrepl-port 7002
                              :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"
                                                 ]
                              :css-dirs ["resources/public/css"]
                              :ring-handler webrtclojure.handler/app}

                   :env {:dev true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler
                                              {:main "webrtclojure.dev"
                                               :optimizations :none
                                               :source-map true}}



                                        }
                               }}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
