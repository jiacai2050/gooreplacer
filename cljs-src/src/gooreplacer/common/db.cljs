(ns gooreplacer.common.db
  (:require [reagent.core :as r]
            [gooreplacer.common.i18n :as i18n]
            [gooreplacer.common.tool]
            [alandipert.storage-atom :refer [local-storage] :as st])
  (:require-macros [gooreplacer.common.macro :refer [init-database!]]))

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

(def dark-key "dark")
(def light-key "light")
(def auto-key "auto")

(defn config-darkmode [theme]
  (condp = theme
    dark-key (.enable js/DarkReader)
    light-key (.disable js/DarkReader)
    auto-key (.auto js/DarkReader)))

;; https://github.com/darkreader/darkreader/issues/1802
(defonce _init (.setFetchMethod js/DarkReader js/window.fetch))

(defn config-icon [enable]
  (if enable
    (.setIcon js/chrome.browserAction
              (clj->js {:path {"32" "img/32.png"
                               "16" "img/16.png"
                               "48" "img/48.png"}}))
      (.setIcon js/chrome.browserAction
                (clj->js {:path {"32" "img/32-off.png"
                                 "16" "img/16-off.png"
                                 "48" "img/48-off.png"}}))))
