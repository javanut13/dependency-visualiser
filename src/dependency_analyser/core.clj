(ns dependency-analyser.core
  (:gen-class)
  (:require [clojure.data.json :only write-str])
  (:use [dependency-analyser.parse :only [files-with-suffixes create-symbol-map]]
        [org.httpkit.server :only [run-server]]))

(defmacro compile-time-read [path]
  (let [data (slurp path)]
    data))

(def javascript
  {:status 200
   :headers {"Content-Type" "application/javascript"}
   :body (compile-time-read "visualiser/main.js")})

(def main-page
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (compile-time-read "visualiser/index.html")})

(def failed
  {:status 501
   :headers {"Content-Type" "text/plain"}
   :body "not supported"})

(defn as-json-resp [data]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (str
           "window.loaded_data("
           (clojure.data.json/write-str data)
           ");")})

(defn app [data req]
  (println (:request-method req) (:uri req))
  (if (not= :get (:request-method req))
    failed
    (case (:uri req)
      "/" main-page
      "/data.json" (as-json-resp @data)
      "/main.js" javascript
      failed)))

(defn get-data [path suffixes]
  (future
    (create-symbol-map (files-with-suffixes path suffixes))))

(defn -main
  "This is the main func"
  ([] (println "Arguments are <path to source folder> <extensions...>"))
  ([path & suffixes]
  (let [data (get-data path suffixes)]
    (run-server (partial app data) {:port 8000}))
    (println "Started server on port 8000")))
