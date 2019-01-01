(ns linreg.common
  (:require [clojure.string :as str]))

(defn abs [x]
  (if (pos? x) x (- x)))

(defn tap [f x]
  (f x) x)

(defn estimate-price [theta0 theta1 mileage]
  (+ theta0 (* theta1 mileage)))

(def ^:private thetas_file
  "./resources/thetas.txt")

(defn save-thetas [thetas]
  (try
    (spit thetas_file
          (str/join "\n" thetas))
    (catch Exception _
      (println "Failed to save thetas to file"))))

(defn get-thetas []
  (try
    (->> (str/split (slurp thetas_file) #"\n")
         (mapv read-string)
         (take 2))
    (catch Exception _ [0 0])))
