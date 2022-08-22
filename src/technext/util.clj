(ns technext.util
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn csv->map [filename]
  (let [r (io/reader filename)
        [cols & rest] (csv/read-csv r)
        cols (->> cols
                  (map keyword))]
    (->> rest
         (map (fn [vals]
                (->> vals
                     (zipmap cols)))))))


(defmacro with-time
  [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     {:result ret#
      :time (/ (double (- (. System (nanoTime)) start#)) 1000000.0)}))


