(ns monad.clojure.applicative
  (:use [monad.clojure.functor :only [Functor fmap]]))

(defprotocol Applicative
  (pure [functor f])
  (<*> [fs xs])
  (<$> [xs f]))

(defrecord List [value]
  Applicative
  (pure [_ f]
    (->List [f]))
  (<*> [fs xs]
    (->List (for [f (:value fs)
                  x (:value xs)]
              (f x))))
  (<$> [xs f]
    (fmap xs f)))

(extend-type List
  Functor
  (fmap [functor f]
    (->List (map f (:value functor)))))

(let [list-functor (->List [1 2 3])]
  (= (fmap list-functor #(* 2 %))
     (<$> list-functor #(* 2 %))))

(:value (let [fs (->List [#(* 2 %) #(+ 10 %)])
              xs (->List [1 2 3])]
          (<*> fs xs)))

(:value (let [g (pure (->List nil) #(* 50 %))
              xs (->List [1 2 3])]
          (<*> g xs)))

;;applicative functor laws

;;Identity
(let [f #(+ 2 %)
      v (->List [10])]
  (= (<*> (pure (->List nil) f) v)
     (fmap v f)))

;;Composition
(let [u (->List [#(* 2 %)])
      v (->List [#(+ 10 %)])
      w (->List [1 2 3])]
  (= (-> (pure (->List nil) (fn [x] (partial comp x)))
         (<*> u)
         (<*> v)
         (<*> w))
     (<*> u (<*> v w))))

;;Homomorphism 同质性
(let [f #(* 2 %)
      x 10]
  (= (-> (pure (->List nil) f)
         (<*> (pure (->List nil) x)))
     (pure (->List nil) (f x))))

;;Interchange
(let [u (pure (->List nil) #(+ 10 %))
      y 50
      $ (fn [y] #(% y))]
  (= (<*> u (pure (->List nil) y))
     (<*> (pure (->List nil) ($ y)) u)))
