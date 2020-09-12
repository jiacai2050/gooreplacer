(ns gooreplacer.option.setting
  (:require [antizer.reagent :as ant]
            [gooreplacer.common.db :as db]
            [gooreplacer.common.i18n :as i18n]))

(defn setting-body []
  [ant/form {:layout "vertical"}
   (let [conf @db/goo-conf]
     (for [[title switch] [[i18n/title-global-switch :global-enabled?]
                           [i18n/tab-online-rule :online-enabled?]
                           [i18n/tab-redirect-url :redirect-enabled?]
                           [i18n/tab-cancel-url :cancel-enabled?]
                           [i18n/title-req-headers :req-headers-enabled?]
                           [i18n/title-resp-headers :res-headers-enabled?]]]
       ^{:key title}
       [ant/form-item {:label title}
        [ant/switch {:checked-children "on" :un-checked-children "off"
                     :default-checked (switch conf)
                     :on-change (fn [checked]
                                  (when (= switch :global-enabled?)
                                    (db/config-icon checked))
                                  (swap! db/goo-conf update switch not)
                                  ;; (println title checked)
                                  )}]]
       ))
   [ant/form-item {:label i18n/title-theme}
    [ant/select {:default-value @db/theme
                 :on-change (fn [theme]
                              (db/config-darkmode theme)
                              (reset! db/theme theme))}
     (for [[title theme] [[i18n/title-dark db/dark-key]
                          [i18n/title-light db/light-key]
                          [i18n/title-auto db/auto-key]]]
       ^{:key theme}
       [ant/select-option title])
     ]]
   ;; [ant/form-item {:label "Locale"}
   ;;  [ant/select {:style {:width "150px"} :default-value @db/locale
   ;;               :on-change #(do (reset! db/locale %)
   ;;                               (ant/message-success %))}
   ;;   [ant/select-option {:value "en_US"} "English"]
   ;;   [ant/select-option {:value "zh_CN"} "简体中文"]]]
   ])
