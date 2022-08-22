(defproject technext "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :min-lein-version "2.9.4"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/datomic-pro "1.0.6202" :exclusions [org.slf4j/log4j-over-slf4j org.slf4j/slf4j-nop]]
                 [clj-time "0.15.2"]
                 [compojure "1.6.2"]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.6.2"]
                 [ring/ring-devel "1.8.1"]
                 [org.clojure/data.csv "1.0.1"]]
  :main ^:skip-aot technext.core
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :username [:gpg :env/datomic_username]
                                   :password [:gpg :env/datomic_password]}}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :uberjar-name "technext-app.jar"
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
