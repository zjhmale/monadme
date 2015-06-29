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
    (do
      (prn env)
      (prn (format "Connected to api with key %s" api-key)))))

(defn run-app [env]
  (do
    (connect-to-db env)
    (connect-to-api env)
    (prn "Done!")))

(run-app {:db-uri  "user:passwd@host/dbname"
          :api-key "AF167"})

(defn connect-to-db-m []
  (do-m reader-m
        [db-uri (asks :db-uri)]
        (prn (format "Connected to db at %s" db-uri))))

(macroexpand '(do-m reader-m
                    [db-uri (asks :db-uri)]
                    (prn (format "Connected to db at %s" db-uri))))

;;expand to this in runtime
((:bind reader-m)
  (asks :db-uri)
  (clojure.core/fn [db-uri]
    ((:return reader-m) (prn (format "Connected to db at %s" db-uri)))))

(defn connect-to-api-m []
  (do-m reader-m
        [api-key (asks :api-key)
         env (ask)]
        (do
          ;;we just ask the value from reader monad and we get it!
          (prn env)
          (prn (format "Connected to api with key %s" api-key)))))

(defn run-app-m []
  (do-m reader-m
        [_ (connect-to-db-m)
         _ (connect-to-api-m)]
        (prn "Done.")))

((run-app-m) {:db-uri  "user:passwd@host/dbname"
              :api-key "AF167"})

;;感觉屌飞了 黑魔法啊 reader monad扔出来的就是一个单参函数接受传入的配置 并且可以用ask获取到传入的配置值
;;有了reader monad 就不需要把配置手动显式的传给每一个依赖配置的函数 可以让内部的业务代码更加整洁 当然团队内部平均水平肯定要都在一条线上

