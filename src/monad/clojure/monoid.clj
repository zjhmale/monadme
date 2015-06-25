(ns monad.monoid)

;;Simply put, Monoids describe types containing a binary function and an identity value
;;monoid有一个不变元(或者说幺元) 任何这个类型的monoid和这个不变元进行mappend操作都不会改变自身的值
;;比如+ *都是mappend, 0 1就是不变元

(def addmempty (+))                                         ;; 0
(def addappend +)
(defn addconcat [ms]
  (reduce addappend addmempty ms))

(addappend 3 4)                                             ;; 7
(addconcat [2 3 4])                                         ;; 9

(defn plus-monoid
  ([]
   0)
  ([a b]
   (+ a b)))

(plus-monoid)                                               ;; 0 - same as mempty
(plus-monoid 3 4)                                           ;; 7 - same as mappend
(reduce plus-monoid [2 3 4])                                ;; 9 - when working with monoids, reduce is the same as mconcat

(defn list-monoid
  ([]
   '())
  ([a b]
   (concat a b)))

(list-monoid)                                               ;; () - remember, same as mempty
(list-monoid [1 2 3] [4 5 6])                               ;; (1 2 3 4 5 6) - remember, same as mappend
(reduce list-monoid [[1 2 3] [4 5 6] [7 8 9]])              ;; (1 2 3 4 5 6 7 8 9) - mconcat in action

;;实现的更像haskell一点

(defprotocol Monoid
  (mempty [m] "mempty :: m")
  (mappend [m n] "mappend :: m -> m -> m"))

(defrecord List [value]
  Monoid
  (mempty [m] (List. (list)))
  (mappend [m n]
    (List. (apply concat (map :value [m n])))))

(defn mconcat [m nest]
  "mconcat :: [m] -> m"
  (reduce mappend (mempty m) nest))

(:value (mempty (List. '())))

(:value (let [m (List. (list 1 2 3))
              n (List. (list 4 5 6))]
          (mappend m n)))

(mappend (mappend (mempty (List. '())) (List. (list 1 2 3))) (List. (list 4 5 6)))

(:value (let [nest (map ->List [(list 1 2 3) (list 4 5 6)])]
          (mconcat (->List '()) nest)))

(defrecord Product [value]
  Monoid
  (mempty [m] (Product. 1))
  (mappend [m n]
    (Product. (apply * (map :value [m n])))))

(:value (mempty (Product. nil)))

(:value (let [m (Product. 3)
              n (Product. 9)]
          (mappend m n)))

(:value (let [nest (map ->Product [1 3 9])]
          (mconcat (->Product 1) nest)))

;;or is a macro and a macro is not a value just like fn so can not use apply here
(defrecord Any [value]
  Monoid
  (mempty [m] (Any. false))
  (mappend [m n]
    (Any. (or (:value m) (:value n)))))

(:value (mempty (->Any false)))

(:value (let [m (->Any true)
              n (->Any false)]
          (mappend m n)))

(:value (let [nest (map ->Any [false false true])]
          (mconcat (->Any false) nest)))

;;monoid laws

;;Identity
;;Applying mappend to mempty and a monoid x should be the same as the original x monoid

(let [x (->List (list 1 2 3))]
  (= (mappend (mempty (->List '())) x)
     (mappend x (mempty (->List '())))
     x))

;;Associativity结合律
;;Applying mappend to a monoid x and the result of applying mappend to the monoids y and z should be the same as first applying mappend to the monoids x and y and then applying mappend to the resulting monoid and the monoid z

(let [x (->List (list 1 2 3))
      y (->List (list 4 5 6))
      z (->List (list 7 8 9))]
  (= (mappend x (mappend y z))
     (mappend (mappend x y) z)))