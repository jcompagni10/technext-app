(ns technext.parser
  (:require [clojure.string :as s]))

(def stop-words (-> (slurp "./resources/private/stopwords.txt")
                    (s/split #"\n")
                    set))

(defn clean-string [str]
  (-> (s/lower-case str)
      (s/replace  #"[^a-z0-9\- ]" "")))

(defn remove-stop-words [col]
  (->> col
       (remove (fn [token] (contains? stop-words token)))))

(defn string->tokens [str]
  (-> (clean-string str)
      (s/split #" +")
      remove-stop-words))
