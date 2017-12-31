(ns gooreplacer.db
  (:require [reagent.core :as r]
            [gooreplacer.tool]
            [alandipert.storage-atom :refer [local-storage] :as st])
  (:require-macros [gooreplacer.macro :refer [init-database!]]))

(init-database!)

(defn version []
  (if-let [manifest (.getManifest js/chrome.runtime)]
    (.-version manifest)
    "0.1"))

(def opened-tab (local-storage (r/atom "redirect-tab")
                               :opened-tab))
