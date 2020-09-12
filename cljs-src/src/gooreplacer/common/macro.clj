(ns gooreplacer.common.macro
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
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
                           (swap! ~sym-name conj (gooreplacer.common.tool/encode-rule ~'rule))))]))
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

(defn- camelcase->kebab [s]
  (->> (str/split s #"(?=[A-Z])")
       (map str/lower-case)
       (str/join "-")))

(defmacro init-i18n!
  "Define vars extracted from locales messages"
  []
  (let [msgs (json/read-str (slurp (io/resource "_locales/en/messages.json")))]
    `(do
       ~@(map (fn [[name {:strs [placeholders]}]]
                (let [sname (symbol (camelcase->kebab name))]
                  ;; can't print here, output will go to dev/option/js/gooreplacer/i18n.js file
                  ;; (println sname)
                  ;; https://developer.chrome.com/extensions/i18n-messages#placeholders
                  (if placeholders
                    `(defn ~sname [& ~'args]
                       (js/chrome.i18n.getMessage ~name (~'clj->js ~'args)))
                    `(def ~sname
                       (js/chrome.i18n.getMessage ~name)))))
              msgs))))

;; (init-database!)
