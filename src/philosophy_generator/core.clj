(ns philosophy-generator.core
  (:gen-class)
  (:require [clojure.string :as str])
)

(defn clean-and-tokenize
  "Clean remove 'THIRD BOOK' and 'IV.' from text"
  [text]
  (map #(concat
          (concat ["^S" "^S" "^S"] (str/split % #"\s"))
        ["^E" "^E" "^E"])
  (str/split
    (str/replace
      (str/replace text #"([A-Z ]+ BOOK)|([IVX]+.\s)" "")
      #"\s+" " ")
  #"[.!?;]"))
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
