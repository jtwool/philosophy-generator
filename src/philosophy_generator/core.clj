(ns philosophy-generator.core
  (:gen-class)
  (:require
    [clojure.java.io :as io] 
    [clojure.core.reducers :as reducers]
    [clojure.string :as str])
)

(defn clean-and-tokenize
  "Clean remove 'THIRD BOOK' and 'IV.' from text"
  [text]
  (map #(concat
          (concat ["^S" "^S" "^S"] (str/split (str/lower-case %) #"\s"))
        ["^E" "^E" "^E"])
  (str/split
    (str/replace
      (str/replace
        (str/replace text #"([A-Z ]+ BOOK)|([IVX]+.\s)" "")
       #"[,-]" "")
      #"\s+" " ")
  #"[.!?;:]"))
)

(defn token-target
  "Turn tokens into pair"
  [tkns]
  (let [n (count tkns)]
  (str (str/join ","  (take (- n 1) tkns)) "=>" (last tkns)))
)

(defn sent-token-target
  "Turn sentence into token-target pairs"
  [sentence]
  (loop [pairs [] snt sentence]
  (if (zero? (count snt)) pairs
  (recur
    (conj pairs 
    (token-target (take 2 snt))
    (token-target (take 3 snt))
    (token-target (take 4 snt)))
    (rest snt)))))

(defn sents-to-pairs
  "Turn a sequence of tokenized sentences into a count map"
  [sentences]
  (frequencies  (reducers/flatten (map sent-token-target sentences)))
)

(defn write-map
  "Write a map to a file"
  [m fp]
  (with-open [wrtr (io/writer fp)]
    (doseq
     [line  (map #(str/join " " %) (seq m))]
     (.write wrtr (str line "\n")))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
