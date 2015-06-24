(ns monad.monad
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
  (prn (str "total price: $" (:total order))))

(defn place-order
  [order]
  (let [shipping-rate (calculate-shipping-rate (:address order))
        discount (lookup-discount-code (:discount-code order))]
    (-> order
        (apply-shipping-costs shipping-rate)
        (apply-discount-code discount)
        (place))))

(def order {
            :items         [{:name "Jalapeño sauce" :price 20.0}]
            :address       {:country "Australia"}
            :discount-code "XMAS2012"
            :total         20.0
            })

(def another-order {
                    :items         [{:name "Jalapeño sauce" :price 20.0}]
                    :address       {:country "Brazil"}
                    :discount-code "HACKERZ"
                    :total         20.0
                    })

(place-order order)
(comment
  (place-order another-order) "CompilerException java.lang.NullPointerException")

(defn apply-shipping-costs [order shipping-rate]
  (if shipping-rate
    (assoc order :total (+ (:total order) shipping-rate))
    order))

(defn apply-discount-code [order discount]
  (if discount
    (assoc order :total (- (:total order) discount))
    order))

;;如果要避免空指针导致整个系统崩溃 只能是用无休止的判断来避免
;;而且照理来说要把货物运往巴西由于没有标明运费是无法上架的 但是现在的系统并没有对这一点进行相应的处理
;;虽然有方法可以让这个系统更加健壮正确 但是那就需要更多无谓的if判断来做到
(place-order another-order)

;;now use monad to fix it

;;use algo.monads
