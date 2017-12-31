(ns gooreplacer.macro
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn- load-db-conf []
  (edn/read-string (slurp (io/resource "config/db.edn"))))

(defmacro init-database!
  "Used in option page script, which requires React"
  []
  (let [entries (load-db-conf)]
    `(do
       ~@(mapcat (fn [{:keys [name init-value no-append?]}]
                   (let [str-name (clojure.core/name name)
                         sym-name (symbol str-name)]
                     [`(def ~sym-name
                         (alandipert.storage-atom/local-storage 
                          (reagent.core/atom ~init-value)
                          ~(keyword str-name)))
                      (when-not no-append?
                        `(defn ~(symbol (str "append-" str-name "!")) [~'rule]
                           (swap! ~sym-name conj (gooreplacer.tool/encode-rule ~'rule))))]))
          entries))))

(defmacro init-db-reader!
  "Used in background script, which doesn't require React"
  []
  (let [entries (load-db-conf)]
    `(do
       ~@(map (fn [{:keys [name]}]
                (let [str-name (clojure.core/name name)]
                  `(defn ~(symbol (str "read-" str-name)) []
                       (alandipert.storage-atom/load-local-storage ~(keyword str-name)))))
          entries))))

;; (init-database!)
