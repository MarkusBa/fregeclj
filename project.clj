(defproject fregeclj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj"]
  :main fregeclj.FregeClj
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.frege-lang/frege "3.22.524-gcc99d7e"]
                 ;[org.frege-lang/frege-interpreter-core "1.1.1"]
                 ;[org.frege-lang/frege-interpreter-distribution "1.1.1"]
                 ;[org.frege-lang/frege-interpreter-java-support "1.1.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ch.qos.logback/logback-classic "1.1.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [yesql "0.4.1"]]
  :plugins [[lein-fregec "3.22.524"]]
  :aot :all
  :frege-source-paths ["src/frege"]
  ;note the custom uberjar task under tasks
  )
