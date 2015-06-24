(ns monad.applicativefunctor
  (:use [monad.functor])
  (:require [monad.functor])
  (:import [monad.functor List]))

(defmulti pure (fn [f _] f))
(defmethod pure List [_ v]
  ;;wrap a value in a minimal context
  (List. [v]))

;;clojure中的for就是对给定的范围做笛卡尔积

;;从定义中可以看到<*>其实就是list comprehension
(defmulti <*> (fn [fs _] (class fs)))
(defmethod <*> List [fs xs]
  ;;unwraps the functions in fs, applies them to the Functors in xs, wrapping the result in a minimal context
  (List. (for [f (:value fs)
               x (:value xs)]
           (f x))))

;;<$> :: (Functor f) => (a -> b) -> f a -> f b
;;f <$> x = fmap f x

(defmulti <$> (fn [_ xs] (class xs)))
(defmethod <$> List [f xs]
  (fmap xs f))

(:value (let [fs (List. [#(* 2 %) #(+ 10 %)])
              xs (List. [1 2 3])]
          (<*> fs xs)))

(:value (let [g (pure List #(* 50 %))
              xs (List. [1 2 3])]
          (<*> g xs)))

(let [f #(* 50 %)
      xs (List. [1 2 3])]
  (<$> f xs))

;;applicative functor law

;;Identity
;;Feeding a function f to pure and applying the resulting Applicative to the Functor v should be the same as directly mapping f over the Functor v

(let [f #(+ 2 %)
      v (List. [10])]
  (= (<*> (pure List f) v)
     (fmap v f)))

;;Composition
;;The result of applying an Applicative Functor that yields the function composition operator to the Applicative u, then apply the resulting Functor to v and finally applying that result to the final Applicative w should be the same as applying v to w and then applying u to the resulting Applicative

(let [u (List. [#(* 2 %)])
      v (List. [#(+ 10 %)])
      w (List. [1 2 3])]
  (= (-> (pure List (fn [x] (partial comp x)))
         (<*> u)
         (<*> v)
         (<*> w))
     (<*> u (<*> v w))))

;;clojure的partial就是就是认为制造一个等待一个参数的curry 因为curry在clojure不是默认行为 而在pure fp中curry却和高阶函数一样随处可见
((((fn [x] (partial comp x)) #(* 2 %)) #(+ 10 %)) 10)

;;clojure的comp就是haskell中的dot 将组合的函数从右到左分别apply到给定的参数上
((comp #(* 2 %) #(+ 10 %) #(- % 3)) 10)

;;Homomorphism 同质性
;;The result of applying the pure value of f to the pure value of x should be the same as applying f directly to x and then feeding that into pure.

(let [f #(* 2 %)
      x 10]
  (= (-> (pure List f)
         (<*> (pure List x)))
     (pure List (f x))))

;;Interchange
;;The result of applying an Applicative Functor u to the pure value of y should be the same as taking the Applicative obtained by calling pure with a function that applies its argument to y and then applying that to u

(let [u (pure List #(+ 10 %))
      y 50
      $ (fn [y] #(% y))]
  (= (<*> u (pure List y))
     (<*> (pure List ($ y)) u)))

