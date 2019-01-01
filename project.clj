(defproject linreg "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [com.hypirion/clj-xchart "0.2.0"]]
  :main ^:skip-aot linreg.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
