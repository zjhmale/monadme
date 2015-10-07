(ns monad.clojure.mpc)

;;three primitive combinator

(defn result
  [v]
  (fn [inp]
    '([v inp])))

(def zero
  (fn [inp]
    '()))

(defn item
  (fn [inp]
    (if (empty? inp)
      inp
      '([(first inp) (rest inp)]))))