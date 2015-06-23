(ns monad.applicativefunctor
  (:use [monad.functor])
  (:require [monad.functor])
  (:import [monad.functor List]))

(defmulti pure (fn [f _] f))
(defmethod pure List [_ v]
  ;;wrap a value in a minimal context
  (List. [v]))

;;clojure中的for就是对给定的范围做笛卡尔积

(defmulti <*> (fn [fs _] (class fs)))
(defmethod <*> List [fs xs]
  ;;unwraps the functions in fs, applies them to the Functors in xs, wrapping the result in a minimal context
  (List. (for [f (:value fs)
               x (:value xs)]
           (f x))))

;;<$> :: (Functor f) => (a -> b) -> f a -> f b
;;f <$> x = fmap f x

(defmulti <$> (fn [f _] f))
(defmethod <$> List [f x]
  (fmap x s))