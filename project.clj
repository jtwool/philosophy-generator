(defproject philosophy-generator "0.5"
  :description "Simple sentence completer based on philosophical texts."
  :url "https://github.com/jtwool/philosophy-generator"
  :license {:name "Mozilla Public License v2.0"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot philosophy-generator.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
