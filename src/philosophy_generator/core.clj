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

(defn read-counts
  "Read a counts file"
  [fp]
  (with-open [rdr (io/reader fp)]
  (reduce (fn [a b]
            (let [parts (str/split b #"(=>)|\s")
                  tkns (first parts)
                  trgt (nth parts 1)
                  cnt  (nth parts 2)]
                (assoc a tkns (conj (get a tkns [])
                   {:tkns tkns 
                    :trgt trgt
                    :cnt (Integer. cnt)}))))
          {}
          (line-seq rdr))
))

(defn word-options
  "Generates word options from counts and input tokens"
  [cntmap tkns]
  (let [n (count tkns)]
  (reduce (fn [a b] (concat a (repeat (* n (b :cnt)) (b :trgt))))
          []
          (cntmap (str/join "," tkns)))))

(defn next-word
  "Randomly selects next word from counts and input tokens"
  [cntmap tkns]
  (rand-nth
    (filter (fn [a] (not (some #{a} tkns)))
    (loop [opts [] ts tkns]
      (let [n (count ts)]
      (if (zero? n) opts
      (recur  (concat opts (word-options cntmap ts))
              (rest ts))))))))

(defn complete-sentence
  "Finishes a sentence given a starting sequence"
  [cntmap tkns]
  (loop [ts tkns snt tkns]
    (let [nw (next-word cntmap (take-last 3 ts))] 
      (if (= nw "^E") (str/join " " snt)
      (if (> (count snt) 12) (str/join " " snt)
      (recur (take-last 3 (conj ts nw)) (conj snt nw)))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (write-map
    (sents-to-pairs
      (clean-and-tokenize
        (slurp (nth args 0))))
    "./counts.tmp")
  (let [ph-probs (read-counts "./counts.tmp")]
    (loop [a nil b 0]
      (if (= b 1) (println a))
      (let [x (do (print ">> ") (flush) (read-line))
            start-tkns (str/split (str/lower-case x) #"\s+")] 
        (if (= ["quit"] start-tkns) nil
        (recur 
           (complete-sentence ph-probs start-tkns)
           1)))))
  (io/delete-file "./resources/counts.tmp")
)
