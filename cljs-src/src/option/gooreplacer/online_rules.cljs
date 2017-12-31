(ns gooreplacer.online-rules
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.db :as db]
            [gooreplacer.tool :as tool]
            [gooreplacer.table :as table]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.set :as set]
            [gooreplacer.io :as io]
            [goog.dom :as gdom])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn edit-online-url []
  (r/with-let [online-editable? (r/atom false)
               edit-or-ok-label (r/atom "Edit")]
    (let [online-url-id "online-url"
          save-fn (fn []
                    (swap! online-editable? not)
                    (if (= "OK" @edit-or-ok-label)
                      (let [url (-> (gdom/getElement online-url-id)
                                    (.-value)
                                    clojure.string/trim)]
                        (swap! db/goo-conf assoc :url url)
                        (reset! edit-or-ok-label "Edit"))
                      (reset! edit-or-ok-label "OK")))]
        [ant/form-item {:label "Rule List URL"}
         [ant/row {:gutter 10}
          [ant/col {:span 20}
           [ant/input {:default-value (:url @db/goo-conf) :disabled (not @online-editable?) :id online-url-id
                       :on-press-enter save-fn
                       :addon-after (r/as-element [ant/icon {:type "right" :style {:cursor "pointer"}
                                                             :on-click #(.sendMessage js/chrome.runtime #js {"url" (:url @db/goo-conf)})}])}]]
       [ant/col {:span 4}
        [ant/button {:type "primary" :on-click save-fn} @edit-or-ok-label]]]])))

(def common-columns
  [{:title "Purpose" :dataIndex "purpose"}
   {:title "Source" :dataIndex "src"}
   {:title "Kind" :dataIndex "kind"}
   {:title "Enable" :dataIndex "enable" :render #(if % "true" "false")}])

(defn online-rules-table [display?]
  [:div
   [ant/table {:bordered true :dataSource (->> @db/online-rules
                                               (filter (fn [rule] (#{"redirectUrl" "cancel"} (:purpose rule))))
                                               (map tool/decode-rule))
               :row-key "src" :title #(r/as-element [:div.text-center [:h5 "Redirect/Cancel Rules"]])
               :columns (conj common-columns
                              {:title "Destination" :dataIndex "dst"}) :pagination table/pagination}]
   [ant/table {:bordered true :dataSource (->> @db/online-rules
                                               (filter (fn [rule] (#{"responseHeaders" "requestHeaders"} (:purpose rule))))
                                               (map tool/decode-rule))
               :row-key  #(str (aget % "src") (aget % "purpose") (aget % "name")) :title #(r/as-element [:div.text-center [:h5 "Request/Response Headers Rules"]])
               :columns (conj common-columns
                              {:title "Operation" :dataIndex "op"}
                              {:title "name" :dataIndex "name"}
                              {:title "value" :dataIndex "value"}) :pagination table/pagination}]
   [ant/popconfirm {:title "Do you really want to clear online rules?"
                    :on-confirm (fn []
                                  (reset! display? false)
                                  (reset! db/online-rules [])
                                  (swap! db/goo-conf assoc :online-update-time 0)
                                  (ant/message-success "Online rules is empty now."))}
    [:div.text-center [ant/button {:type "danger" :size "large"} "Clear ALL online rules"]]]])

(defn update-url [loading?]
  (r/with-let [loading? (r/atom false)
               display-rules? (r/atom false)]
    [:div
     [ant/form-item {:label "Last update time"}
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
                                               (swap! db/goo-conf assoc :online-update-time (js/Date))
                                               (ant/message-success "Update done."))
                                             (catch js/Error e (ant/message-error (str "Parse rules error! " (.stringify js/JSON e)))))
                                           (ant/message-error (str "Connection Error! " error-text))))))
                    :type "primary"}
        (if @loading? "Loading..." "Update Now")]]
      [ant/col
       [ant/button {:on-click #(reset! display-rules? true) :type "primary"} "View"]]
      [ant/col
       [ant/modal {:visible @display-rules? :footer nil :title "Online rules" :width "80%"
                   :on-cancel #(reset! display-rules? false)} [online-rules-table display-rules?]]]]]))

(defn configure-online-form []
  [ant/card {:title (r/as-element [ant/checkbox {:default-checked (:online-enabled? @db/goo-conf)
                                                  :on-change #(swap! db/goo-conf update :online-enabled? not)} "Online Rule List"])}
   [ant/form {:on-submit #(.preventDefault %)}
    [edit-online-url]
    [update-url]]])
