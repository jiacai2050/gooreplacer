(ns gooreplacer.core
  (:require [goog.dom :as gdom]
            [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.db :as db]
            [gooreplacer.io :as io]
            [gooreplacer.local-rules :as lr]
            [gooreplacer.online-rules :refer [configure-online-form]]))

(defn top-menu []
  [:div
   [ant/row {:type "flex" :justify "center"}
    [ant/col 
     [:h1 "Gooreplacer"]]]
   [ant/row {:type "flex" :justify "center"}
    [ant/col
     [:h3 "Modify, block URLs & Headers"]]]
   [ant/row {:type "flex" :justify "center" :gutter 8}
    [ant/col
     [ant/button {:type "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "http://liujiacai.net/gooreplacer/"})} [ant/icon {:type "home"}] " Home"]]
    [ant/col
     [ant/button {:type "primary" :on-click io/import-rules} [ant/icon {:type "upload"}] " Import"]]
    [ant/col
     [ant/button {:type "primary" :on-click io/export-rules} [ant/icon {:type "download"}] " Export"]]
    [ant/col
     [ant/button {:type "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "https://github.com/jiacai2050/gooreplacer/tree/master/doc/guides.md"})} [ant/icon {:type "question"}] " Help"]]]])


(defn tabs []
  [ant/tabs {:active-key @db/opened-tab :on-change #(reset! db/opened-tab %)}
   (for [[tab-name tab-key tab-ui] [["Redirect URL" "redirect-tab" lr/redirect-rules-table]
                                    ["Cancel URL" "cancel-tab" lr/cancel-rules-table]
                                    ["Request/Response Headers" "header-tab" lr/header-rules-table]
                                    ["Online-rules" "online-tab" configure-online-form]
                                    ["Sandbox" "sanbox-tab" lr/sandbox]]]
     ^{:key tab-key} [ant/tabs-tab-pane {:tab (r/as-element [:h4 tab-name])} [tab-ui]])])

(defn main-body []
  [:div
   [top-menu]
   [ant/row {:type "flex" :justify "center" :style {:margin-top "20px"}}
    [ant/col {:span 20} 
     [tabs]]]])

(r/render
 [main-body]
 (gdom/getElement "app"))
