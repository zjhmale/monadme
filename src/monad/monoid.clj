(ns monad.monoid)

(defprotocol Monoid
  (mempty [m] "mempty :: m")
  (mappend [m n] "mappend :: m -> m -> m")
  (mconcat [nest] "mconcat :: [m] -> m"))

(defrecord List [value]
  Monoid
  (mempty [m] (List. (list)))
  (mappend [m n]
    (List. (concat (:value m) (:value n))))
  (mconcat [nest]
    (reduce mappend (mempty nest) (:value nest))))

(:value (mempty (List. '())))

(:value (let [m (List. (list 1 2 3))
              n (List. (list 4 5 6))]
          (mappend m n)))

(mappend (mappend (mempty (List. '())) (List. (list 1 2 3))) (List. (list 4 5 6)))

(:value (let [m (List. (list 1 2 3))
              n (List. (list 4 5 6))
              nest (List. (list m n))]
          (mconcat nest)))
