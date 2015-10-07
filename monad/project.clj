(defproject monad "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/algo.monads "0.1.5"]
                 [acolfut "0.3.3"]]
  :plugins [[lein-colortest "0.3.0"]]
  :main ^:skip-aot monad.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
