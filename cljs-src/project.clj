(defproject gooreplacer "3.13.0"
  :description "Modify, block URLs & Headers"
  :url "https://github.com/jiacai2050/gooreplacer"
  :license {:name "MIT"
            :url "http://liujiacai.net/license/MIT.html?year=2015"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.773"]
                 [figwheel-sidecar "0.5.14"]
                 [org.clojure/core.match "1.0.0"]
                 [org.clojure/data.json "1.0.0"]
                 [alandipert/storage-atom "2.0.1"]

                 ;; ui
                 [antizer "0.3.0"]
                 [cljs-http "0.1.46"]
                 [org.clojure/core.async "1.3.610"]
                 [reagent "0.10.0"]]
  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.8"]
            [lein-doo "0.1.8"]]
  :profiles {
             ;; https://docs.cider.mx/cider/0.23/basics/clojurescript.html#_piggieback
             ;; cider will jack in piggieback automatically
             ;; :dev {:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :dev-option {:clean-targets ^{:protect false} [:target-path "resources/dev/ui/compiled-js/option"]
                          :cljsbuild {:builds [{:id "dev"
                                                :figwheel true
                                                :source-paths ["src"]
                                                :compiler {:output-dir "resources/dev/ui/compiled-js/option"
                                                           :source-map true
                                                           :asset-path "js"
                                                           :output-to "resources/dev/ui/js/option.js"
                                                           :optimizations :none
                                                           :main gooreplacer.option.core
                                                           :verbose true}}]}
                          :figwheel {:server-port 8080
                                     :http-server-root "dev/ui"
                                     :css-dirs ["resources/dev/ui/css"]
                                     :server-logfile ".figwheel_option.log"
                                     :repl true}}
             :dev-popup {:clean-targets ^{:protect false} [:target-path "resources/dev/ui/compiled-js/popup"]
                         :cljsbuild {:builds [{:id "dev"
                                               :figwheel true
                                               :source-paths ["src"]
                                               :compiler {:output-dir "resources/dev/ui/compiled-js/popup"
                                                          :source-map true
                                                          :asset-path "js"
                                                          :output-to "resources/dev/ui/js/pop.js"
                                                          :optimizations :none
                                                          :main gooreplacer.popup
                                                          :verbose true}}]}
                         :figwheel {:server-port 8090
                                    :http-server-root "dev/ui"
                                    :css-dirs ["resources/dev/ui/css"]
                                    :server-logfile ".figwheel_popup.log"
                                    :repl true}}
             :dev-bg {:clean-targets ^{:protect false} [:target-path "resources/dev/background/js"]
                      :figwheel {:server-port 8095
                                 :http-server-root "dev/background"
                                 :server-logfile ".figwheel_bg.log"
                                 :repl true}
                      :cljsbuild {:builds [{:id "dev"
                                            :source-paths ["src"]
                                            :figwheel true
                                            :compiler {:output-to "resources/dev/background/js/main.js"
                                                       :source-map true
                                                       :output-dir "resources/dev/background/js"
                                                       :asset-path "js"
                                                       :main gooreplacer.background
                                                       :optimizations :none
                                                       :verbose true}}]}}
             :release {:clean-targets ^{:protect false} [:target-path
                                                         "resources/release/ui/option-js"
                                                         "resources/release/ui/popup-js"
                                                         "resources/release/background/js"]
                       :cljsbuild {:builds [{:id "option"
                                             :source-paths ["src"]
                                             :compiler {:output-to "resources/release/ui/option.js"
                                                        :output-dir "resources/release/ui/option-js"
                                                        :externs ["externs/chrome_extensions.js" "externs/chrome.js" "externs/darkreader.js"]
                                                        :optimizations :advanced
                                                        :main gooreplacer.option.core}}
                                            {:id "popup"
                                             :source-paths ["src"]
                                             :compiler {:output-to "resources/release/ui/popup.js"
                                                        :output-dir "resources/release/ui/popup-js"
                                                        :externs ["externs/chrome_extensions.js" "externs/chrome.js" "externs/darkreader.js"]
                                                        :optimizations :advanced
                                                        :main gooreplacer.popup}}
                                            {:id "background"
                                             :source-paths ["src"]
                                             :compiler {:output-to "resources/release/background/main.js"
                                                        :output-dir "resources/release/background/js"
                                                        :externs ["externs/chrome_extensions.js" "externs/chrome.js"]
                                                        :optimizations :advanced
                                                        :main gooreplacer.background}}]}}
             :test {:cljsbuild {:builds [{:id "test"
                                          :source-paths ["src" "test"]
                                          :compiler {:output-to "out/main.js"
                                                     :main gooreplacer.runner
                                                     :optimizations :none}}]}
                    :doo {:build "test"}}}

  :aliases {"option"   ["with-profile" "dev-option" "do"
                        ["clean"]
                        ["figwheel" "dev"]]
            "popup"["with-profile" "dev-popup" "do"
                    ["clean"]
                    ["figwheel" "dev"]]
            "bg"       ["with-profile" "dev-bg" "do"
                        ["clean"]
                        ["figwheel" "dev"]]
            "test" ["with-profile" "test" "do"
                    ["clean"]
                    ;; First install phantom
                    ["doo" "phantom"]]})
