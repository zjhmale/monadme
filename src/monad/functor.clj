(ns monad.functor)

;;define the Functor typeclass
(defprotocol Functor
  (fmap [functor f] "(a -> b) -> f a -> f b"))

(defrecord List [value]
  Functor
  (fmap [functor f]
    (List. (map f (:value functor)))))

;;因为clojure中的defprotocol中定义的func第一个参数都类似于oo语言中的this 所以第一个值必须是自身的引用 不能是一个func 所以与haskell中fmap参数顺序有点不一致

(:value (let [list-functor (List. [1 2 3])]
          (fmap list-functor #(* 2 %))))
