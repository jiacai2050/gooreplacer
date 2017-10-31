(ns gooreplacer.core
  (:require [goog.dom :as gdom]
            [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.db :as db]
            [gooreplacer.io :as io]
            [gooreplacer.bootstrap :as bs]
            [gooreplacer.local-rules :as lr]
            [gooreplacer.online-rules :refer [configure-online-form]]))

(defn top-menu []
  [bs/grid {:class "text-center" :style {:margin-bottom 10}}
   [bs/row
    [bs/col {:sm 4 :sm-offset 4}
     [:h2 "Gooreplacer"]]]
   [bs/row
    [bs/col {:sm 8 :sm-offset 2}
     [:h4 "Modify, block URLs & Headers"]]]
   [bs/row
    [bs/button-toolbar {:style {:display "flex" :justify-content "center"}}
     [bs/button {:bs-style "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "http://liujiacai.net/gooreplacer/"})} [bs/glyphicon {:glyph "home"}] " Home"]
     [bs/button {:bs-style "primary" :on-click io/import-rules} [bs/glyphicon {:glyph "import"}] " Import"]
     [bs/button {:bs-style "primary" :on-click io/export-rules} [bs/glyphicon {:glyph "export"}] " Export"]
     [bs/button {:bs-style "primary" :on-click #(.sendMessage js/chrome.runtime #js {"url" "https://github.com/jiacai2050/gooreplacer4chrome/tree/master/doc/guides.md"})} [bs/glyphicon {:glyph "question-sign"}] " Help"]]]])

(defn tabs []
  (r/with-let [which-active (r/atom 1)]
    [bs/tabs {:active-key @which-active :on-select #(reset! which-active %) :id "nav"}
     [bs/tab {:title "Redirect URL" :event-key 1} [lr/redirect-rules-table]]
     [bs/tab {:title "Cancel URL" :event-key 2} [lr/cancel-rules-table]]
     [bs/tab {:title "Request/Response Headers" :event-key 3} [lr/header-rules-table]]]))

(defn main-body []
  [:div.container
   [top-menu]
   [configure-online-form]
   [tabs]])

(r/render
 [main-body]
 (gdom/getElement "app"))
