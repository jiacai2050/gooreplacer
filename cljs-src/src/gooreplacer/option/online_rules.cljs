(ns gooreplacer.option.online-rules
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.common.db :as db]
            [gooreplacer.common.tool :as tool]
            [gooreplacer.option.table :as table]
            [gooreplacer.common.i18n :as i18n]
            [gooreplacer.option.io :as io]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.set :as set]
            [goog.dom :as gdom])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn edit-online-url []
  (r/with-let [online-editable? (r/atom false)
               edit-or-ok-label (r/atom i18n/tip-edit)]
    (let [online-url-id "online-url"
          save-fn (fn []
                    (swap! online-editable? not)
                    (if (= i18n/tip-ok @edit-or-ok-label)
                      (let [url (-> (gdom/getElement online-url-id)
                                    (.-value)
                                    clojure.string/trim)]
                        (swap! db/goo-conf assoc :url url)
                        (reset! edit-or-ok-label i18n/tip-edit))
                      (reset! edit-or-ok-label i18n/tip-ok)))]
      [ant/form-item
       [ant/row {:gutter 10}
        [ant/col {:span 20}
         [ant/input {:default-value (:url @db/goo-conf) :disabled (not @online-editable?) :id online-url-id
                     :on-press-enter save-fn
                     :addon-after (r/as-element [ant/icon {:type "right" :style {:cursor "pointer"}
                                                           :on-click #(.sendMessage js/chrome.runtime #js {"url" (:url @db/goo-conf)})}])}]]
        [ant/col {:span 4}
         [ant/button {:type "primary" :on-click save-fn} @edit-or-ok-label]]]])))

(def common-columns
  [{:title i18n/col-purpose :dataIndex "purpose"}
   {:title i18n/col-src :dataIndex "src"}
   {:title i18n/col-kind :dataIndex "kind"}
   {:title i18n/col-enable :dataIndex "enable" :render #(if % "true" "false")}])

(defn- decorate-rule
  "translate rule field for human"
  [rule]
  (-> rule
      (update :purpose db/label-mapping)
      (update :kind db/label-mapping)))

(defn online-rules-table [display?]
  [:div
   [ant/table {:bordered true :dataSource (->> @db/online-rules
                                               (filter (fn [rule] (#{"redirectUrl" "cancel"} (:purpose rule))))
                                               (map (comp decorate-rule tool/decode-rule)))
               :row-key "src" :title #(r/as-element [:div.text-center [:h5 i18n/tab-url]])
               :columns (conj common-columns
                              {:title i18n/col-dst :dataIndex "dst"}) :pagination table/pagination}]
   [ant/table {:bordered true :dataSource (->> @db/online-rules
                                               (filter (fn [rule] (#{"responseHeaders" "requestHeaders"} (:purpose rule))))
                                               (map (comp decorate-rule tool/decode-rule)))
               :row-key  #(str (aget % "src") (aget % "purpose") (aget % "name")) :title #(r/as-element [:div.text-center [:h5 i18n/tab-headers]])
               :columns (conj common-columns
                              {:title i18n/col-operation :dataIndex "op"}
                              {:title i18n/col-header-name :dataIndex "name"}
                              {:title i18n/col-header-value :dataIndex "value"}) :pagination table/pagination}]
   [ant/popconfirm {:title i18n/cfm-delete
                    :on-confirm (fn []
                                  (reset! display? false)
                                  (reset! db/online-rules [])
                                  (swap! db/goo-conf assoc :online-update-time 0)
                                  (ant/message-success (i18n/tmpl-delete-ok i18n/tab-online-rule)))}
    [:div.text-center [ant/button {:type "danger" :size "large"} i18n/btn-clear-all]]]])

(defn update-url [loading?]
  (r/with-let [loading? (r/atom false)
               display-rules? (r/atom false)]
    [:div
     [ant/form-item {:label i18n/txt-last-update}
      (:online-update-time @db/goo-conf)]
     [ant/row {:type "flex" :justify "center" :gutter 10}
      [ant/col
       [ant/button {:on-click #(do (reset! loading? true)
                                   (go (let [url (:url @db/goo-conf)
                                             {:keys [success error-text body] :as resp} (<! (http/get url {:with-credentials? false}))]
                                         (reset! loading? false)
                                         (if success
                                           (try
                                             (let [online-rules (if (map? body) body (js->clj (.parse js/JSON body) :keywordize-keys true))]
                                               (io/import-online-rules! online-rules)
                                               (swap! db/goo-conf assoc :online-update-time (.toLocaleString (js/Date.)))
                                               (ant/message-success (i18n/tmpl-update-ok i18n/tab-online-rule)))
                                             (catch js/Error e (ant/message-error (i18n/tmpl-update-fail i18n/tab-online-rule (.stringify js/JSON e)))))
                                           (ant/message-error (i18n/tmpl-update-fail i18n/tab-online-rule error-text))))))
                    :type "primary"}
        (if @loading? i18n/txt-loading i18n/btn-update-now)]]
      [ant/col
       [ant/button {:on-click #(reset! display-rules? true) :type "primary"} i18n/btn-view]]
      [ant/col
       [ant/modal {:visible @display-rules? :footer nil :title i18n/tab-online-rule :width "80%"
                   :on-cancel #(reset! display-rules? false)} [online-rules-table display-rules?]]]]]))

(defn configure-online-form []
  [ant/card {:title (r/as-element [ant/checkbox {:default-checked (:online-enabled? @db/goo-conf)
                                                 :on-change #(swap! db/goo-conf update :online-enabled? not)} i18n/tab-online-rule])}
   [ant/form {:on-submit #(.preventDefault %)}
    [edit-online-url]
    [update-url]]])
