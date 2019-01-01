(ns linreg.learn
  (:require [linreg.common :refer :all]))

(defn- converged? [theta0 theta1]
  (and
    (> 0.000001 (abs theta0))
    (> 0.000001 (abs theta1))))

(defn- sum-theta
  ([data theta0 theta1]
   (sum-theta data theta0 theta1 false))
  ([data theta0 theta1 include-mileage?]
   (reduce (fn [sum [mileage price]]
             (->> (if include-mileage? mileage 1)
                  (* (- (estimate-price theta0 theta1 mileage)
                        price))
                  (+ sum)))
           0
           data)))

(defn learn
  ([data]
   (learn data 0 0))
  ([data theta0 theta1]
   (learn data theta0 theta1 0.0001))
  ([data theta0 theta1 rate]
   (let [m (count data)]
     (loop [theta0 theta0
            theta1 theta1]
       (let [temp-theta0 (* (/ rate m)
                            (sum-theta data theta0 theta1))
             temp-theta1 (* (/ rate m)
                            (sum-theta data theta0 theta1 true))]
         (if (converged? temp-theta0 temp-theta1)
           [(* theta0 1000) theta1]
           (recur (- theta0 temp-theta0)
                  (- theta1 temp-theta1))))))))
