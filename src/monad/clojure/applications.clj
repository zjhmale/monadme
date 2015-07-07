(ns monad.clojure.applications
  (:use [monad.clojure.monad]
        [monad.clojure.repo]))

;;more applications use monad to make it more elegant and concise

(defn ask [] identity)
(defn asks [f]
  (fn [env]
    (f env)))

;;just like the reader monad in haskell
(def reader-m {:return (fn [a]
                         (fn [_] a))
               :>>=   (fn [m k]
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
((connect-to-db-m) {:db-uri  "user:passwd@host/dbname"
                    :api-key "AF167"})

(((:>>= reader-m)
   (asks :db-uri)
   (clojure.core/fn [db-uri]
     ((:return reader-m) (prn (format "Connected to db at %s" db-uri)))))
  {:db-uri  "user:passwd@host/dbname"
   :api-key "AF167"})

((:>>= reader-m)
  (asks :db-uri)
  (clojure.core/fn [db-uri]
    ((:return reader-m) (prn (format "Connected to db at %s" db-uri)))))

((fn [m k]
   (fn [r]
     ((k (m r)) r)))
  (asks :db-uri)
  (clojure.core/fn [db-uri]
    ((:return reader-m) (prn (format "Connected to db at %s" db-uri)))))

(let [m (fn [env]
          (:db-uri env))
      k (fn [db-uri]
          ((:return reader-m) (prn (format "Connected to db at %s" db-uri))))]
  (fn [r]
    ((k (m r)) r)))

((let [m (fn [env]
           (:db-uri env))
       k (fn [db-uri]
           ((:return reader-m) (prn (format "Connected to db at %s" db-uri))))]
   (fn [r]
     ((k (m r)) r)))
  {:db-uri  "user:passwd@host/dbname"
   :api-key "AF167"})

(let [m (fn [env]
          (:db-uri env))
      k (fn [db-uri]
          (fn [_] (prn (format "Connected to db at %s" db-uri))))
      r {:db-uri  "user:passwd@host/dbname"
         :api-key "AF167"}]
  ((k (m r)) r))

(((fn [db-uri]
    (fn [_] (prn (format "Connected to db at %s" db-uri)))) "user:passwd@host/dbname")
  {:db-uri  "user:passwd@host/dbname"
   :api-key "AF167"})

((fn [_] (prn (format "Connected to db at %s" "user:passwd@host/dbname")))
  {:db-uri  "user:passwd@host/dbname"
   :api-key "AF167"})

(prn (format "Connected to db at %s" "user:passwd@host/dbname"))

;;展开过程还是有点绕

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

(macroexpand '(do-m reader-m
                    [_ (connect-to-db-m)
                     _ (connect-to-api-m)]
                    (prn "Done.")))

((:>>= reader-m)
  (connect-to-db-m)
  (clojure.core/fn [_]
    (clojure.core/->
      (connect-to-api-m)
      ((:>>= reader-m)
        (clojure.core/fn [_]
          ((:return reader-m)
            (prn "Done.")))))))

((:>>= reader-m)
  (connect-to-db-m)
  (fn [_]
    ((:>>= reader-m)
      (connect-to-api-m)
      (fn [_]
        ((:return reader-m)
          (prn "Done."))))))

((run-app-m) {:db-uri  "user:passwd@host/dbname"
              :api-key "AF167"})

;;感觉屌飞了 黑魔法啊 reader monad扔出来的就是一个单参函数接受传入的配置 并且可以用ask获取到传入的配置值
;;有了reader monad 就不需要把配置手动显式的传给每一个依赖配置的函数 可以让内部的业务代码更加整洁 当然团队内部平均水平肯定要都在一条线上

;;用reader monad解决依赖注入问题

(defn compute-clone
  [_]
  (prn "Compute clone for user"))

;;type hint
;;clone! :: Number -> String -> Reader UserRepository Number
(defn clone! [id user]
  (do-m reader-m
        [repo (ask)]
        (let [old-user (fetch-by-id! repo id)
              cloned-user (create! repo (compute-clone old-user) user)
              updated-user (assoc old-user :clone-id (:id cloned-user))]
          (update! repo old-user updated-user user))))

(defn run-clone []
  (do-m reader-m
        [_ (clone! 10 "leo")]
        (prn "Cloned.")))

((run-clone) (mk-user-repo))

;;上面这个例子就可以使用require引入另一个namespace下的内容防止namespace污染 易于测试
;;这里实现依赖注入的意义在于 如果不用reader monad那么就需要用namespace/function来使用另一个名字空间的函数 这样就会变得很冗余
;;每次都要加上namespace前缀很繁琐 用reader monad就可以只写一次 后面所有依赖另一个namespace中函数的位置都不用再显示生命namespace前缀了
;;但是貌似defprotocol和reify的组合本身就可以这样弄 就是repo那里使用let绑定为mk-user-repo的位置 用reader monad会让代码描述性更强 repo就是单独传入的一个module一样的意思
;;如果不用reader monad那么用mk-user-repo函数生成的repo值还需要显示的被传入clone!函数 现在只要在最外层显式地写一次就好了
;;其实这样看来 上面传递env的那个例子也算是一种依赖注入 依赖就是env这个上下文中保存的信息
