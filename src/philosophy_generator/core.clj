(ns philosophy-generator.core
  (:gen-class)
  (:require 
    [clojure.core.reducers :as reducers]
    [clojure.string :as str])
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

(defn token-target
  "Turn tokens into pair"
  [tkns]
  (let [n (count tkns)]
  (str (str/join ", "  (take (- n 1) tkns)) "=>" (last tkns)))
)

(defn sent-token-target
  "Turn sentence into token-target pairs"
  [sentence]
  (loop [pairs [] snt sentence]
  (if (zero? (count snt)) pairs
  (let [new-pairs (conj pairs 
    (token-target (take 2 snt))
    (token-target (take 3 snt))
    (token-target (take 4 snt)))]
    (println pairs)
    (recur new-pairs (rest snt))))))

(defn tokens-to-probs
  "Turn a sequence of tokenized sentences into a count map"
  [sentences]
  (identity sentences)
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
