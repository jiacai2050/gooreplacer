(ns gooreplacer.db
  (:require [reagent.core :as r]
            [gooreplacer.tool]
            [alandipert.storage-atom :refer [local-storage] :as st])
  (:require-macros [gooreplacer.macro :refer [def-database]]))

(def default-conf {:url ""
                   :online-update-time 0
                   :global-enabled? true
                   :online-enabled? true
                   :redirect-enabled? true
                   :cancel-enabled? true
                   :req-headers-enabled? true
                   :res-headers-enabled? true})

(def-database goo-conf default-conf :no-append? true)

(def-database online-rules [])

(def-database redirect-rules [])

(def-database cancel-rules [])

(def-database request-headers [])

(def-database response-headers [])

(defn version []
  (if-let [manifest (.getManifest js/chrome.runtime)]
    (.-version manifest)
    "0.1"))
