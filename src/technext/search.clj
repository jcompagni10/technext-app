(ns technext.search
  (:require [datomic.api :as d]
            [technext.db :refer [*conn*]]
            [technext.util :as u]
            [technext.parser :as parser]))


(defn lookup-keyword [db search-str]
  (let [tokens (parser/string->tokens search-str)
        n-tokens (count tokens)]
    (u/with-time
      (->> (d/q '[:find ?patent-id (count ?tokens)
                  :with ?e
                  :in $ [?tokens ...]
                  :where
                  [?e :keyword/token ?tokens]
                  [?e :keyword/doc-ids ?patent-id]]
                db tokens)

           (reduce (fn [col [patent-id  n-matches]]
                     ;;NOTE: this currently only returns patents where all tokens match
                     ;;this could be tweaked to return different levels of matching
                     (if (= n-matches n-tokens)
                       (cons patent-id col)
                       col)) [])))))

(defn get-patent-texts [db patent-ids]
  (d/q '[:find [(pull ?e [:patent/id :patent/text]) ...]
         :in $ [?patent-ids ...]
         :where [?e  :patent/id ?patent-ids ]] db patent-ids))

(defn get-sample-docs [db {:keys [result] :as data}]
  (let [random-ids (->> (shuffle result)
                         (take 5))
        samples (get-patent-texts db random-ids)]


    (assoc data :samples samples)))

(defn lookup-by-id [db id]
  (-> (d/entity db [:patent/id id])
      (select-keys [:patent/id :patent/text])))
