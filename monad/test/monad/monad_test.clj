(ns monad.monad-test
  (:require [acolfut.sweet :refer :all]
            [monad.clojure.functor :refer :all])
  (:import [monad.clojure.functor List]))

(deftest functor-test
  (testing "list functor"
    (is= (:value (let [list-functor (List. [1 2 3])]
                   (fmap list-functor #(* 2 %))))
         '(2 4 6))))
