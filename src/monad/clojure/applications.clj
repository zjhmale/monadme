(ns monad.clojure.applications
  (:use [monad.monad]))

;;more applications use monad to make it more elegant and concise

(defn ask [] identity)
(defn asks [f]
  (fn [env]
    (f env)))

;;just like the reader monad in haskell
(def reader-m {:return (fn [a]
                         (fn [_] a))
               :bind   (fn [m k]
                         (fn [r]
                           ((k (m r)) r)))})

(defn connect-to-db [env]
  (let [db-uri (:db-uri env)]
    (prn (format "Connected to db at %s" db-uri))))

(defn connect-to-api [env]
  (let [api-key (:api-key env)
        env (ask)]
    (prn (format "Connected to api with key %s" api-key))))

(defn connect-to-db []
  (do-m reader-m
        [db-uri (asks :db-uri)]
        (prn (format "Connected to db at %s" db-uri))))

(macroexpand '(do-m reader-m
                    [db-uri (asks :db-uri)]
                    (prn (format "Connected to db at %s" db-uri))))

((:bind reader-m)
  (asks :db-uri)
  (clojure.core/fn [db-uri]
    ((:return reader-m) (prn (format "Connected to db at %s" db-uri)))))

(defn connect-to-api []
  (do-m reader-m
        [api-key (asks :api-key)
         env (ask)]
        (prn (format "Connected to api with key %s" api-key))))
