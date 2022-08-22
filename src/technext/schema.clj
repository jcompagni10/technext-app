(ns technext.schema)

(def norms [ ;;Patent Schema
            {:db/ident :patent/text
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "Text of a patent"}
            {:db/ident :patent/id
             :db/unique :db.unique/identity
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "ID for patent"}

            ;;Inverted Index Schema
            {:db/ident :keyword/token
             :db/unique :db.unique/identity
             :db/index true
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/doc "Keyword token"}

            {:db/ident :keyword/docs
             :db/valueType :db.type/ref
             :db/cardinality :db.cardinality/many
             :db/doc "Keyword token"}])
