(ns gooreplacer.popup
  (:require [goog.dom :as gdom]
            [gooreplacer.common.db :as db]
            [gooreplacer.common.i18n :as i18n]
            [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.option.setting :refer [setting-body]]))

(defn main-body []
  (db/config-darkmode @db/theme)
  (db/config-icon (:global-enabled? @db/goo-conf))
  [ant/locale-provider {:locale (ant/locales db/locale)}
   [ant/card
    [:center
     [ant/button {:type "primary" :on-click #(.openOptionsPage js/chrome.runtime)} i18n/title-config-rule]]
    [:hr]
    [setting-body]]])

(r/render
 [main-body]
 (gdom/getElement "popup-div"))
