(ns technext.db
  (:require [datomic.api :as d]
            [technext.schema :as schema]
            [clojure.data.csv :as csv]
            [technext.util :as u]
            [technext.parser :as parser]
            [clojure.java.io :as io]
            [clojure.string :as s]))




(declare ^:dynamic *conn*)

(def db-uri "datomic:dev://ec2-35-92-158-109.us-west-2.compute.amazonaws.com:4334/technext?password=password")

(defn create-schema [conn]
  (d/transact *conn* schema/norms))



(defn load-edn-to-db [path]
  (println "Loading DB: " path)
  (with-open [r (io/reader path)]
    (->> (line-seq r)
         (map clojure.edn/read-string)
         (partition-all 5000)
         (pmap (fn [chunk]
                 (println "start chunk size:" (count chunk))
                 (time @(d/transact *conn* chunk))
                 (println "chunk")
                 ))
         (doall)
         ))


  (println "DB Load complete"))



(defn init-db [& [db-uri*]]
  (d/create-database db-uri)
  ;; (create-schema *conn*)
  ;; (load-edn-to-db "./resources/private/patents.edn")
  ;; (load-edn-to-db "./resources/private/keywords.edn")
  (def ^:dynamic *conn* (d/connect db-uri)))

(defn tokens->tx [tokens id]
  (->> tokens
       (map (fn [t]
              {:keyword/token t
               :keyword/docs [[:patent/id id]]
               :keyword/doc-ids [id]}))))

(defn index-patent-tx [{:keys [patent_text patent_id]}]
  (let [tokens (parser/string->tokens patent_text)]
    {:tokens (tokens->tx tokens patent_id)
     :patent {:patent/id patent_id
              :patent/text patent_text}}))


(defn merge-tokens [tokens]
  (->> tokens
       (group-by :keyword/token)
       (map (fn [[kw tokens]]
              {:keyword/token kw
               :keyword/docs [:keyword/docs tokens]
               :keyword/doc-ids [:keyword/doc-ids tokens]}))))

;;NOTE: This is not used in the production version, instead we store the resulting transactions and load them upon startup
(defn data->txes [path]
  (let [data (->> (u/csv->map path))
        parsed-data (->> data
                         (pmap (fn [patent] (index-patent-tx patent))))
        token-txes (->> parsed-data
                        (mapcat :tokens))
        patent-txes (->> parsed-data
                         (map :patent))]

    (spit "./resources/private/patents.edn" (s/join "\n" patent-txes))
    (spit "./resources/private/keywords.edn" (s/join "\n" token-txes))
    ))
