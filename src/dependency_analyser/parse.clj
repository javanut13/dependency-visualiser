(ns dependency-analyser.parse
  (:import java.io.File)
  (:require [clojure.string :only [ends-with? replace]]
            [clojure.set :only difference]))

(defn recusive-files [path]
  (->> path
       File.
       file-seq
       (filter #(.isFile %))))

(defn files-with-suffixes [path suffixes]
  (->> (recusive-files path)
       (filter (fn [file]
                 (let [path (.getAbsolutePath file)]
                   (some #(clojure.string/ends-with? path %) suffixes))))))

(defn symbols-in [content]
  (re-seq #"\b[A-Z][A-Za-z]+\b" content))

(defn get-comments [content]
  (re-seq #"(?s)/\*.*?\*/|//.*?\n" content))

(defn remove-comments [content]
  (clojure.string/replace content #"(?s)/\*.*?\*/|//.*?\n" ""))

(defn get-class-name [content]
  (let [[_ t n] (re-find #"(interface|object|class|enum)\s+([A-Z]\w+)" content)]
    (if n
      [t n]
      nil)))

(defn get-symbols-for [file]
  (let [content (slurp file)
        [typ class-name] (get-class-name content)
        symbols (-> content
                    remove-comments
                    symbols-in
                    set)]
    (if class-name
      [class-name {:symbols symbols :type typ}]
      nil)))

(defn create-symbol-map [files]
  (->> files
       (map get-symbols-for)
       (remove nil?)
       (into {})))

