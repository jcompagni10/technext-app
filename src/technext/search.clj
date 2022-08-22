(ns technext.search
  (:require [datomic.api :as d]
            [technext.db :refer [*conn*]]
            [technext.util :as u]
            [technext.parser :as parser]))


(defn lookup-keyword [db search-str]
  (let [tokens (parser/string->tokens search-str)
        n-tokens (count tokens)]
    (u/with-time
      (->> (d/q '[:find ?d (count ?tokens)
                  :in $ [?tokens ...]
                  :where
                  [?e :keyword/token ?tokens]
                  [?e :keyword/docs ?d]]
                db tokens)

           (reduce (fn [col [id n-matches]]
                     ;;NOTE: this currently only returns patents where all tokens match
                     ;;this could be tweaked to return different levels of matching
                     (if (= n-matches n-tokens)
                       (cons id col)
                       col)) [])
           ))))

(defn lookup-by-id [db id]
  (-> (d/entity db id)
      (select-keys [:patent/id :patent/text])))
