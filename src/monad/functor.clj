(ns monad.functor)

;;define the Functor typeclass
(defprotocol Functor
  (fmap [functor f] "(a -> b) -> f a -> f b"))

(defrecord List [value]
  Functor
  (fmap [functor f]
    (List. (map f (:value functor)))))

;;因为clojure中的defprotocol中定义的func第一个参数都类似于oo语言中的this 所以第一个值必须是自身的引用 不能是一个func 所以与haskell中fmap参数顺序有点不一致
;;clojure中的defrecord其实就是一个类似hashmap的结构 可以直接用参数的keyword为键去获取record中对应的值

(:value (let [list-functor (List. [1 2 3])]
          (fmap list-functor #(* 2 %))))

;;functor laws

;;Identity
;;Mapping an identity function over a Functor is the same as applying identity to the Functor itself

(let [list-functor (List. [1 2 3])]
  (= (fmap list-functor identity)
     (identity list-functor)))

;;Composition
;;If you compose the functions f and g and map the resulting function over the Functor, that is the same as first mapping g over the Functor and then mapping f over the resulting Functor.

(let [f #(+ 10 %)
      g #(* 2 %)
      list-functor (List. [1 2 3])]
  (= (fmap list-functor (comp  f g))
     (-> list-functor (fmap g) (fmap f))))

