(ns linreg.core
  (:gen-class)
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as str]
            [com.hypirion.clj-xchart :as c]
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
                   :default "resources/data.csv"]
                  ["-v" "--visualize" "visualize data or not"
                   :default false]])

(defn -main
  [& args]
  (let [{errors :errors
         {mode :mode
          file :file
          visualize? :visualize} :options} (cli/parse-opts args cli-options)]
    (if errors
      (doseq [err errors] (println err))
      (condp = mode
        "learn"
        (try
          (println visualize?)
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
                 (tap (fn [data]
                        (when visualize?
                          (c/view
                           (c/xy-chart
                            {"Maxime" {:x (mapv first data)
                                       :y (mapv second data)}}
                            {:title "Price of cars over mileage"
                             :x-axis {:title "Mileage"}
                             :y-axis {:title "Price"
                                      :decimal-pattern "$##.##k"}
                             :theme :matlab
                             :render-style :scatter})))
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
