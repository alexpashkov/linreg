(ns linreg.core
  (:gen-class)
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as str]
            [linreg.common :refer [tap
                                   estimate-price
                                   save-thetas
                                   get-thetas]]
            [linreg.learn :refer [learn]]))

(def cli-options [["-m" "--mode MODE" "mode of execution"
                   :validate (let [modes #{"learn" "predict"}]
                               [modes (str "Unknown mode. Use one of "
                                           (str/join ", " modes))])
                   :default "learn"]
                  ["-f" "--file FILE" "data file"
                   :default "resources/data.csv"]])

(defn -main
  [& args]
  (let [{errors :errors
         {mode :mode
          file :file} :options} (cli/parse-opts args cli-options)]
    (if errors
      (doseq [err errors] (println err))
      (condp = mode
        "learn"
        (try
          (println "Reading data from" file)
          (with-open [rdr (clojure.java.io/reader file)]
            (->> (line-seq rdr)
                 (drop 1)
                 (reduce (fn [data line]
                           (->>
                            (str/split line #",")
                            (mapv #(/ (read-string %) 1000))
                            (conj data)))
                         [])
                 (tap (fn [_]
                        (println "Calculating...")))
                 (learn)
                 (tap (fn [[theta0 theta1]]
                        (println "Final theta values are:"
                                 theta0 theta1)
                        (println "Saving to the file...")))
                 (save-thetas)))

          (catch Exception _ (println "Invalid input")))
        "predict"
        (do
          (println "Enter mileage:")
          (let [[theta0 theta1] (get-thetas)]
            (doseq [line (line-seq (java.io.BufferedReader. *in*))]
              (try (println "Estimated price is:"
                            (estimate-price
                             theta0
                             theta1
                             (read-string line)))
                   (catch Exception _ (println "Invalid price"))))))))))
