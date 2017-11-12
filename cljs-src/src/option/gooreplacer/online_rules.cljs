(ns gooreplacer.online-rules
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.bootstrap :as bs]
            [gooreplacer.db :as db]
            [gooreplacer.tool :as tool]
            [gooreplacer.table :as table]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.set :as set]
            [gooreplacer.io :as io])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn edit-online-url []
  (r/with-let [online-editable? (r/atom false)
               edit-or-ok-label (r/atom "Edit")]
    (let [!online-url (atom nil)
          save-fn (fn []
                    (swap! online-editable? not)
                    (if (= "OK" @edit-or-ok-label)
                      (let [url (.. @!online-url -refs -input -value)]
                        (swap! db/goo-conf assoc :url url)
                        (reset! edit-or-ok-label "Edit"))
                      (reset! edit-or-ok-label "OK")))]
      [bs/form-group {:control-id "online-url"}
       [bs/col {:sm 2 :class "text-right"}
        [bs/control-label "Rule List URL"]]
       [bs/col {:sm 8}
        [ant/input {:default-value (:url @db/goo-conf) :disabled (not @online-editable?) :ref #(reset! !online-url %)
                    :on-press-enter save-fn
                    :addon-after (r/as-element [ant/icon {:type "right" :style {:cursor "pointer"}
                                                          :on-click #(.sendMessage js/chrome.runtime #js {"url" (:url @db/goo-conf)})}])}]]
       [bs/col {:sm 2}
        [ant/button {:on-click save-fn} @edit-or-ok-label]]])))

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
    [bs/form-group
     [bs/col {:sm 2 :class "text-right"}
      [bs/control-label "Last  update"]]
     [bs/col {:sm 4} (:online-update-time @db/goo-conf)]
     [bs/col {:sm 3}
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
                                          (ant/message-error (str "Connection Error! " error-text))))))} (if @loading? "Loading..." "Update Now")]
      [ant/button {:on-click #(reset! display-rules? true)} "View"]
      [ant/modal {:visible @display-rules? :footer nil :title "Online rules" :width "60%"
                  :on-cancel #(reset! display-rules? false)} [online-rules-table display-rules?]]]]))

(defn configure-online-form []
  [bs/panel {:header (r/as-element [ant/checkbox {:default-checked (:online-enabled? @db/goo-conf)
                                                  :on-change #(swap! db/goo-conf update :online-enabled? not)} "Online Rule List"])
             :bs-style "info"}
   [bs/form {:class "form-horizontal" :on-submit #(.preventDefault %)}
    [edit-online-url]
    [update-url]]])
