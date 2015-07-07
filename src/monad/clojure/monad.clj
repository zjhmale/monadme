(ns monad.clojure.monad
  (:use [clojure.algo.monads]))

;;in a world without monad

;;Say you have an e-commerce system. When placing an order, a few things need to get done:
;;gather information about the order;
;;calculate shipping rates;
;;apply discount codes, if any, and;
;;finally place the order.

(defn calculate-shipping-rate [address]
  (if (= (:country address) "Australia")
    10.0
    nil))

(defn apply-shipping-costs [order shipping-rate]
  (assoc order :total (+ (:total order) shipping-rate)))

(defn lookup-discount-code [code]
  (if (= code "XMAS2012")
    5.0
    nil))

(defn apply-discount-code [order discount]
  (assoc order :total (- (:total order) discount)))

(defn place [order]
  (str "total price: $" (:total order)))

(defn place-order
  [order]
  (let [shipping-rate (calculate-shipping-rate (:address order))
        discount (lookup-discount-code (:discount-code order))]
    (-> order
        (apply-shipping-costs shipping-rate)
        (apply-discount-code discount)
        (place))))

(def order {:items         [{:name "Jalapeño sauce" :price 20.0}]
            :address       {:country "Australia"}
            :discount-code "XMAS2012"
            :total         20.0
            })

(def another-order {:items         [{:name "Jalapeño sauce" :price 20.0}]
                    :address       {:country "Brazil"}
                    :discount-code "HACKERZ"
                    :total         20.0
                    })

(place-order order)
(comment
  (place-order another-order) "CompilerException java.lang.NullPointerException")

;;如果要避免空指针导致整个系统崩溃 只能是用无休止的判断来避免
;;而且照理来说要把货物运往巴西由于没有标明运费是无法上架的 但是现在的系统并没有对这一点进行相应的处理
;;虽然有方法可以让这个系统更加健壮正确 但是那就需要更多无谓的if判断来做到

(comment
  (defn apply-shipping-costs [order shipping-rate]
    (if shipping-rate
      (assoc order :total (+ (:total order) shipping-rate))
      order))

  (defn apply-discount-code [order discount]
    (if discount
      (assoc order :total (- (:total order) discount))
      order))

  (place-order another-order))

;;now use monad to fix it

;;use algo.monads
;;看algo.monads源码可以看到maybe-m是内建的maybe monad 这个monad表示一个可能计算失败的上下文

(defn place-order-use-monad [order]
  (domonad maybe-m
    [order order
     shipping-rate (calculate-shipping-rate (:address order))
     discount (lookup-discount-code (:discount-code order))]
    (-> order
        (apply-shipping-costs shipping-rate)
        (apply-discount-code discount)
        (place))))

;;使用了maybe monad之后我们就可以大胆放心的使用空指针了 因为我们不需要担心空指针会使我们的系统在运行的某一步中出错导致崩溃 因为只要某一步出现了nil 那么maybe monad会让这个nil保存在上下文中 知道返回出去为止
;;不需要用大量的if去判断nil的情况 因为只要一个地方出现了nil 那么整个系统返回的就是一个nil 并且不会崩溃 只要判断返回值即可
;;就像haskell中的maybe monad 如果一个地方出现了Nothing 那么之后所有操作返回的都是Nothing 但是并不会导致系统崩溃 最终用guard或者pattern match来处理最终的结果值是Nothing还是(Just a)即可
;;domonad这个宏就是haskell中的do notation用来避免monad的嵌套
(place-order-use-monad order)
(place-order-use-monad another-order)

;;implement a simple maybe monad

(def maybe-monad
  {:return (fn [v] v)
   :bind   (fn [mv f]
             (if mv
               (f mv)
               nil))})

(-> another-order
    ((:bind maybe-monad)
      (fn [order]
        (-> (calculate-shipping-rate (:address order))
            ((:bind maybe-monad)
              (fn [shipping-rate]
                (-> (lookup-discount-code (:discount-code order))
                    ((:bind maybe-monad)
                      (fn [discount]
                        ((:return maybe-monad)
                          (-> order
                              (apply-shipping-costs shipping-rate)
                              (apply-discount-code discount)
                              (place))))))))))))

;;可以看到加入有一个值是nil 那么整个表达式就直接返回nil了不会再继续执行嵌入的fn了

;;implement it more like haskell

(defprotocol Monad
  (return [context value] "return :: a -> ma")
  (>>= [m f] ">>= :: m a -> (a -> m b) -> m b")
  (>> [ma mb] ">> :: m a -> m b -> m b just ignore the first value"))

(defrecord Maybe [value]
  Monad
  (return [context v] (->Maybe v))
  (>>= [m f]
    (let [v (:value m)]
      (if v (f v) m)))
  (>> [ma mb]
    (>>= ma (fn [_] mb))))

(:value (return (->Maybe nil) nil))

(:value (>>= (->Maybe 9) (fn [x] (return (->Maybe nil) (* x 10)))))
(:value (>>= (->Maybe nil) (fn [x] (return (->Maybe nil) (* x 10)))))

;;使用bind操作符 也就是使用monad就可以消除一大堆的if操作 但是如果不用do notation 一大堆的bind嵌套也蛮乱的 但是相对于一坨一坨的if nil? 要美观不少
;;monad就是将上下午的操作放入内部 外部代码可以简洁 一致 比如maybe monad就是把处理计算失败出现空指针这个上下文放入到了bind函数内部来处理 而不是把这些细节暴露到外部

(defn place-order-use-bind
  [order]
  (:value (>>= (->Maybe order)
               (fn [order]
                 (>>= (->Maybe (calculate-shipping-rate (:address order)))
                      (fn [shipping-rate]
                        (>>= (->Maybe (lookup-discount-code (:discount-code order)))
                             (fn [discount]
                               (return (->Maybe nil)
                                       (-> order
                                           (apply-shipping-costs shipping-rate)
                                           (apply-discount-code discount)
                                           (place)))))))))))

(place-order-use-bind order)
(place-order-use-bind another-order)

;;list monad

;;just read the algo.monads source code
;;algo.monads提供了自定义monad的能力 只要实现m-result m-bind m-zero m-plus四个函数就可以利用domonad来使用monad的威力
(def list-monad {:m-result (fn [v] [v])
                 :m-bind   (fn [mv f]
                             (if (seq mv)
                               (apply concat (map f mv)) []))})

;;just list comprehension again
;;由于clojure缺乏type inference的能力(强类型动态语言硬伤)所以在monad的时候必须显式指名monad的类型 而无法通过类型系统自动判断使用哪一个monad
(domonad list-monad
  [a [1 2]
   b [a (- a)]]
  (* 3 b))

(domonad list-monad
  [a [1 2]
   b []]
  (* 3 b))

(defrecord List [value]
  Monad
  (return [context v] (->List (list v)))
  (>>= [m f]
    (let [v (:value m)]
      (if (seq v)
        (->List (apply concat (map (comp :value f) v)))
        v)))
  (>> [ma mb]
    (>>= ma (fn [_] mb))))

(:value (return (->List nil) 3))
;;list comprehension is just a syntax suger of list monad

(:value (>>= (->List (list 1 2))
             (fn [a]
               (>>= (->List (list a (- a)))
                    (fn [b]
                      (return (->List nil) (* 3 b)))))))

;;monad laws

;;Right unit
;;Binding a monadic value m to return should be equal to m itself

(let [m (->List (list 1 2 3))]
  (= m (>>= m
            (fn [v]
              (return (->List nil) v)))))

;;Left unit
;;Applying return to x and then applying >>= to the resulting value and fshould be the same as applying f directly to x

(let [m 3
      f (fn [x] (->List (list x (- x))))]
  (= (f m)
     (>>= (return (->List nil) m) f)))

;;Associativity 结合律
;;Binding m to f and then applying >>= to the result and g should be the same as applying >>= to m and a function of argument x that first applies f to x and then binds it to g.
;;详细的证明可以使用LC进行推导证明

(let [m (return (->List nil) 3)
      f (fn [x] (->List (list x (- x))))
      g (fn [x] (->List (list (inc x) (dec x))))]
  (= (>>= (>>= m f) g)
     (>>= m
          (fn [x]
            (>>= (f x) g)))))

;;monad motivation in real world

(defn half-double [n]
  [(/ n 2) (* 2 n)])

(half-double 10)                                            ;;[5 20]

(defn inc-int [n]
  [(+ 5 n) (+ 10 n)])

(inc-int 3)                                                 ;;[8 13]

(defn do-both [n]
  (apply concat
         (map inc-int
              (half-double n))))

(do-both 8)                                                 ;;'(9 14 21 26)

;;just use monad bind to compose these things
;;bind in list monad is just do the list comprehension

(defn bind [lst fn]
  (apply concat
         (map fn lst)))

(defn do-bind [lst]
  (bind lst
        (fn [elem]
          (bind (half-double elem)
                (fn [halfdouble]
                  (inc-int halfdouble))))))

(do-bind [8 10])                                            ;;'(9 14 21 26 10 15 25 30)

;;and without monad

(apply concat (for [a [8 10]
                    b (half-double a)]
                (inc-int b)))

;;make youself a do notation

(def list-m {:return (fn [v] (list v))
             :bind   (fn [mv f]
                       (mapcat f mv))})

(let [bind (:bind list-m)
      return (:return list-m)]
  (bind [1 2 3]
        (fn [a]
          (bind [4 5]
                (fn [b]
                  (return [a b]))))))

(defn m-steps [m [name val & bindings] body]
  ;;(println (str name val (vec bindings)))
  (if (seq bindings)
    `(-> ~val
         ((:bind ~m) (fn [~name]
                       ~(m-steps m (vec bindings) body))))
    `(-> ~val
         ((:bind ~m) (fn [~name]
                       ((:return ~m) ~body))))))

(defmacro do-m [m bindings body]
  (m-steps m bindings body))

(defn combinations [ma mb]
  (do-m list-m
        [a ma
         b mb]
        [a b]))

(combinations [1 2 3] [4 5])

;;just list comprehension nothing new here

(for [a [1 2 3]
      b [4 5]]
  [a b])

;;maybe monad with the do notation macro

(defn add [a b]
  (+ a b))

(add 1 2)
;;(add 1 nil)                                                 ;;CompilerException java.lang.NullPointerException

(def monad-maybe {:return (fn [v] v)
                  :bind   (fn [mv f]
                            (when mv
                              (f mv)))})

(defn m-add [ma mb]
  (do-m monad-maybe [a ma
                     b mb]
        (+ a b)))

(m-add 1 2)
(m-add 1 nil)