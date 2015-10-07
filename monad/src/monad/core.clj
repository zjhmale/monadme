(ns monad.core
  (:use [monad.clojure.applications])
  (:require [monad.clojure.repo :as repo])
  (:gen-class))

(defn -main
  [& args]
  ((run-clone) (repo/mk-user-repo))
  (println "monad"))
