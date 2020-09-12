(ns gooreplacer.option.core
  (:require [goog.dom :as gdom]
            [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.common.db :as db]
            [gooreplacer.option.io :as io]
            [gooreplacer.common.i18n :as i18n]
            [gooreplacer.option.local-rules :as lr]
            [gooreplacer.option.setting :refer [setting-body]]
            [gooreplacer.option.online-rules :refer [configure-online-form]]))

(defn top-menu []
  [:div
   [ant/row {:type "flex" :justify "center"}
    [ant/col
     [:h1 i18n/app-name]]]
   [ant/row {:type "flex" :justify "center"}
    [ant/col
     [:h3 i18n/app-desc]]]
   [ant/row {:type "flex" :justify "center" :gutter 8}
    [ant/col
     [ant/button {:type "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "http://liujiacai.net/gooreplacer/"})} [ant/icon {:type "home"}] i18n/btn-home]]
    [ant/col
     [ant/button {:type "primary" :on-click io/import-rules} [ant/icon {:type "upload"}] i18n/btn-import]]
    [ant/col
     [ant/button {:type "primary" :on-click io/export-rules} [ant/icon {:type "download"}] i18n/btn-export]]
    [ant/col
     [ant/button {:type "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "https://github.com/jiacai2050/gooreplacer/tree/master/doc/guides.md"})} [ant/icon {:type "question"}] i18n/btn-help]]]])

(defn setting-tab []
  [ant/card [setting-body]])

(defn tabs []
  [ant/tabs {:active-key @db/opened-tab :on-change #(reset! db/opened-tab %)}
   (for [[tab-name tab-key tab-ui] [[i18n/tab-redirect-url "redirect-tab" lr/redirect-rules-table]
                                    [i18n/tab-cancel-url "cancel-tab" lr/cancel-rules-table]
                                    [i18n/tab-headers "header-tab" lr/header-rules-table]
                                    [i18n/tab-online-rule "online-tab" configure-online-form]
                                    [i18n/tab-sandbox "sanbox-tab" lr/sandbox]
                                    [i18n/tab-setting "setting-tab" setting-tab]]]
     ^{:key tab-key} [ant/tabs-tab-pane {:tab (r/as-element [:h4 tab-name])} [tab-ui]])])


(defn main-body []
  (db/config-darkmode @db/theme)
  (db/config-icon (:global-enabled? @db/goo-conf))
  [:div
   [top-menu]
   [ant/locale-provider {:locale (ant/locales db/locale)}
    [ant/row {:type "flex" :justify "center" :style {:margin-top "20px"}}
     [ant/col {:span 20}
      [tabs]]]]])

(r/render
 [main-body]
 (gdom/getElement "app"))
