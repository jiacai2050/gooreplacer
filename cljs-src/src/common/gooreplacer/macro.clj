(ns gooreplacer.macro
  (:require [clojure.string :as str]))

(defn- get-module-path [module-name]
  (str/split module-name #"\."))

(defn- module-name->kebab-case
  "Converts module and sub module names from camel case to kebab case 
   eg: DatePicker to date-picker or Menu.Item to menu-item"
  [input]
  (->> (re-seq #"\w[a-z0-9]*" input)
       (map str/lower-case)
       (str/join "-")))

(defn- def-component [comp]
  (let [comp-name (name comp)]
    `(def ~(symbol (module-name->kebab-case comp-name))
       (let [react-comp# (apply goog.object/getValueByKeys js/ReactBootstrap ~(get-module-path comp-name))]
         (reagent.core/adapt-react-class react-comp#)))))

(defmacro bootstrap-comp->reagent [comps]
  `(do ~@(map def-component comps)))

(defmacro def-database [db-name init & {:keys [no-append?]}]
  (let [db-name-keyword (keyword db-name)]
    `(do
       (def ~db-name
         (alandipert.storage-atom/local-storage (reagent.core/atom ~init) ~db-name-keyword))
       (defn ~(symbol (str "read-" db-name)) []
         (alandipert.storage-atom/load-local-storage ~db-name-keyword))
       (when-not ~no-append?
         (defn ~(symbol (str "append-" db-name "!")) [~'rule]
           (swap! ~db-name conj (gooreplacer.tool/encode-rule ~'rule)))))))

;; (def-database goo-conf [{:a 1}] :no-append? true)
