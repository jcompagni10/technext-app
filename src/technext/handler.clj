(ns technext.handler
  (:require
   [compojure.core :refer :all]
   [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
   [hiccup.page :refer [html5]]
   [ring.middleware.reload :refer [wrap-reload]]
   [technext.search :as search]
   [datomic.api :as d]
   [technext.db :refer [*conn*]]))


(defn page [& body]
  (html5
    [:head
     [:title "TechNext"]]
    [:body
     [:div body]
     ]))

(def search-page (page
                   [:h3 "Search Form"]
                   [:form {:action "/results" :method "get"}
                    [:input {:type "text" :name "search"}]
                    [:br]
                    [:input {:type "submit" :label "Search"} ]]))

(defn results-page [results]
  (page
    [:p (str "Search Time: " (:time results) " msecs")]
    [:p (str (count (:result results))) " Matches"]
    [:div (->> (:result results)
               (map (fn [patent-id]
                      [:div [:a {:href (str "/patent/" patent-id)} patent-id]])))]))

(defn patent-page [patent]
  (page
    [:h2 (str "Patent ID: " (:patent/id patent))]
    [:h3 "Patent Text"]
    [:p (:patent/text patent)]))

(defroutes app-routes
  (GET "/" []
       search-page)

  (GET "/results" [:as req]
       (let [search (get-in req [:query-params "search"])
             results (search/lookup-keyword (d/db *conn*) search)]
         (results-page results)))

  (GET "/patent/:id" [id]
       (let [patent (search/lookup-by-id (d/db *conn*) (clojure.edn/read-string id))]
         (patent-page patent))))



(def app (-> #'app-routes
                 (wrap-defaults (merge site-defaults {:security {:anti-forgery false}}))
                  wrap-reload))
