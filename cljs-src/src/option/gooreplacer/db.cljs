(ns gooreplacer.db
  (:require [reagent.core :as r]
            [gooreplacer.i18n :as i18n]
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

(defonce locale (let [language (js/chrome.i18n.getMessage "@@ui_locale")]
                  ;; https://developer.chrome.com/webstore/i18n#localeTable
                  ;; https://ant.design/docs/react/i18n#LocaleProvider
                  (case language
                    ("zh_CN" "zh_TW") language
                    "en_US")))

(println "locale" locale "version" (version))

(def label-mapping
  {
   ;; kind
   "wildcard" i18n/kind-wildcard
   "regexp" i18n/kind-regexp

   ;; operation headers
   "modify" i18n/op-modify
   "cancel" i18n/op-cancel

   ;; rule type
   "redirectUrl" i18n/tab-redirect-url
   ;; "cancel" i18n/tab-cancel-url
   "requestHeaders" i18n/title-req-headers
   "responseHeaders" i18n/title-resp-headers
   })
