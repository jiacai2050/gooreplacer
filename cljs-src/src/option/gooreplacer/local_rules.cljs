(ns gooreplacer.local-rules
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.db :as db]
            [gooreplacer.table :as table]
            [gooreplacer.tool :as tool]
            [clojure.string :as str]
            [gooreplacer.tool :as tool]
            [cljs.core.match :refer-macros [match]]))

(def kind-select-opts
  (for [k ["wildcard" "regexp"]]
    ^{:key k} [ant/select-option {:value k} k]))

(defn editable-cell [value update-row-fn]
  (r/with-let [editable? (r/atom false)]
    [:div.editable-cell
     (let [this (r/current-component)]
       (if @editable?
         [:div.editable-cell-input-wrapper
          (let [save-handler (fn [evt]
                               (reset! editable? false)
                               (let [v (goog.object/getValueByKeys evt "target" "value")]
                                 (update-row-fn v)))]
            [ant/input {:default-value value
                        :on-blur save-handler
                        :on-press-enter save-handler}])]
         [:div.editable-cell-text-wrapper
          value
          [ant/tooltip {:title "Edit"}
           [ant/icon {:type "edit" :class "pull-right"
                      :on-click #(reset! editable? true)}]]]))]))

(defn gen-editable-column
  [title data-index which-db current-row? & {:keys [display-fn]}]
  {:title title :dataIndex data-index :render #(r/as-element [editable-cell (if display-fn (display-fn %2) %)
                                                              (fn [new-val] (reset! which-db (mapv (fn [rule] (if (current-row? %2 rule)
                                                                                                                (assoc rule (keyword data-index) new-val)
                                                                                                                rule))
                                                                                                   @which-db)))])})

(defn gen-common-columns [which-db current-row?]
  [{:title "Kind" :dataIndex "kind" :render (fn [val record _] (r/as-element [ant/select {:default-value val
                                                                                          :on-change (fn [new-val]
                                                                                                       (reset! which-db
                                                                                                               (mapv (fn [rule] (if (current-row? record rule)
                                                                                                                                  (assoc rule :kind new-val) rule))
                                                                                                                     @which-db))
                                                                                                       (ant/message-success (str "Change to " new-val)))}
                                                                              kind-select-opts]))}
   {:title "Enable" :dataIndex "enable" :render (fn [val record _]
                                                  (r/as-element [ant/checkbox {:default-checked val
                                                                               :on-change #(reset! which-db
                                                                                                   (mapv (fn [rule] (if (current-row? record rule)
                                                                                                                      (update rule :enable not) rule))
                                                                                                         @which-db))}]))}
   {:title "Actions" :render (fn [_ record _] (r/as-element
                                               [ant/popconfirm {:title "Do you really want to delete this?"
                                                                :on-confirm (fn [] (reset! which-db
                                                                                           (remove (fn [rule] (current-row? record rule))
                                                                                                   @which-db)))}
                                                [ant/button {:icon "delete" :type "danger"}]]))}])

(defn gen-rules-table [{:keys [table-title columns add-new-form which-db switch row-key]}]
  (fn []
    (r/with-let [display-new-form? (r/atom false)]
      [ant/card {:title (r/as-element [ant/checkbox {:default-checked (switch @db/goo-conf)
                                                     :on-change #(swap! db/goo-conf update switch not)} table-title])
                 :extra (r/as-element [ant/button {:type "primary" :on-click #(reset! display-new-form? true)} "Add"])}
       [ant/table {:bordered true :dataSource @which-db
                   :columns columns :row-key row-key
                   :pagination table/pagination}]
       [ant/modal {:title (str "New " table-title) :visible @display-new-form? :footer false
                   :on-cancel #(reset! display-new-form? false)}
        [add-new-form display-new-form?]]])))

(defn gen-new-url-form [append-rule-fn! need-dst?]
  (fn [display?]
    (ant/create-form
     (fn []
       (let [new-rule-form (ant/get-form)]
         [ant/form {:layout "vertical"}
          [ant/form-item {:label "Source"}
           (ant/decorate-field new-rule-form "src" {:rules [{:required true}]}
                               [ant/input])]
          [ant/form-item {:label "Kind"}
           (ant/decorate-field new-rule-form "kind" {:initial-value "wildcard"}
                               [ant/select  kind-select-opts])]
          (when need-dst?
            [ant/form-item {:label "Destination"}
             (ant/decorate-field new-rule-form "dst" {:rules [{:required true}]}
                                 [ant/input])])
          [ant/form-item {:label "Enable"}
           (ant/decorate-field new-rule-form "enable" {:value-prop-name "checked" :initial-value true}
                               [ant/checkbox])]
          [:div.text-center
           [ant/button {:type "primary" :size "large" :on-click #(ant/validate-fields new-rule-form {:force true} (fn [err vals]
                                                                                                                    (when-not err
                                                                                                                      (append-rule-fn! (js->clj vals :keywordize-keys true))
                                                                                                                      (ant/reset-fields new-rule-form)
                                                                                                                      (reset! display? false))))} "Submit"]]])))))

(def redirect-rules-table
  (gen-rules-table {:columns (let [current-row? (fn [record rule] (= (aget record "src") (:src rule)))]
                               (into [(gen-editable-column "Source" "src" db/redirect-rules current-row? :display-fn (fn [record]
                                                                                                                       (:src (tool/decode-rule (js->clj record :keywordize-keys true)))))
                                      (gen-editable-column "Destination" "dst" db/redirect-rules current-row?)]
                                     (gen-common-columns db/redirect-rules current-row?)))
                    :table-title "Redirect Rules"
                    :add-new-form (gen-new-url-form db/append-redirect-rules! true)
                    :which-db db/redirect-rules
                    :switch :redirect-enabled?
                    :row-key "src"}))

(def cancel-rules-table
  (gen-rules-table {:columns (let [current-row? (fn [record rule] (= (aget record "src") (:src rule)))]
                               (into [(gen-editable-column "Source" "src" db/cancel-rules current-row? :display-fn (fn [record]
                                                                                                                     (:src (tool/decode-rule (js->clj record :keywordize-keys true)))))]
                                     (gen-common-columns db/cancel-rules current-row?)))
                    :table-title "Cancel Rules"
                    :add-new-form (gen-new-url-form db/append-cancel-rules! false)
                    :which-db  db/cancel-rules
                    :switch :cancel-enabled?
                    :row-key "src"}))


(def op-select-opts
  (for [k ["modify" "cancel"]]
    ^{:key k} [ant/select-option {:value k} k]))

(defn gen-new-header-form [append-rule-fn!]
  (fn [display?]
    (ant/create-form
     (fn []
       (let [new-rule-form (ant/get-form)]
         [ant/form {:layout "vertical"}
          [ant/form-item {:label "Source"}
           (ant/decorate-field new-rule-form "src" {:rules [{:required true}]}
                               [ant/input])]
          [ant/form-item {:label "Kind"}
           (ant/decorate-field new-rule-form "kind" {:initial-value "wildcard"}
                               [ant/select kind-select-opts])]
          [ant/form-item {:label "Operation"}
           (ant/decorate-field new-rule-form "op" {:initial-value "modify"}
                               [ant/select op-select-opts])]
          [ant/form-item {:label "Header name"}
           (ant/decorate-field new-rule-form "name" {:rules [{:required true}]}
                               [ant/input {:placeholder "case-insensitive"}])]
          [ant/form-item {:label "Header value"}
           (ant/decorate-field new-rule-form "value" {:rules [{:required false}]}
                               [ant/input {:placeholder "Ignore this if operation is cancel"}])]
          [ant/form-item {:label "Enable"}
           (ant/decorate-field new-rule-form "enable" {:value-prop-name "checked" :initial-value true}
                               [ant/checkbox])]
          [:div.text-center
           [ant/button {:type "primary" :size "large" :on-click #(ant/validate-fields new-rule-form {:force true} (fn [err vals]
                                                                                                                    (when-not err
                                                                                                                      (append-rule-fn! (js->clj vals :keywordize-keys true))
                                                                                                                      (ant/reset-fields new-rule-form)
                                                                                                                      (reset! display? false))))} "Submit"]]])))))

(defn gen-header-rules-columns [current-row? which-db]
  (into [{:title "Operation" :dataIndex "op" :render (fn [val record _] (r/as-element [ant/select {:default-value val
                                                                                                   :on-change  (fn [new-val]
                                                                                                                 (reset! which-db
                                                                                                                         (mapv (fn [rule] (if (current-row? record rule)
                                                                                                                                            (assoc rule :op new-val) rule))
                                                                                                                               @which-db))
                                                                                                                 (ant/message-success (str "Change to " new-val)))}
                                                                                       op-select-opts]))}
         (gen-editable-column "Header Name" "name" which-db current-row?)
         (gen-editable-column "Header Value" "value" which-db current-row?)
         (gen-editable-column "Source" "src" which-db current-row? :display-fn (fn [record]
                                                                                 (:src (tool/decode-rule (js->clj record :keywordize-keys true)))))]
        (gen-common-columns which-db current-row?)))

(defn gen-headers-table [table-title which-db switch append-rule-fn!]
  (gen-rules-table {:columns (let [current-row? (fn [record rule] (and (= (aget record "src") (:src rule))
                                                                       (= (aget record "name") (:name rule))))]
                               (gen-header-rules-columns current-row? which-db))
                    :table-title table-title
                    :add-new-form (gen-new-header-form append-rule-fn!)
                    :which-db which-db
                    :switch switch
                    :row-key #(str (aget % "src") (aget % "name"))}))

(def request-header-rules-table (gen-headers-table "Request Header Rules " db/request-headers :req-headers-enabled? db/append-request-headers!))
(def response-header-rules-table (gen-headers-table "Response Header Rules" db/response-headers :res-headers-enabled? db/append-response-headers!))

(defn header-rules-table []
  [:div
   [request-header-rules-table]
   [response-header-rules-table]])

(defn- do-test [form]
  (ant/validate-fields
   form
   {:force true}
   (fn [err vals]
     (when-not err
       (let [{:keys [test-url]} (js->clj vals :keywordize-keys true)]
         (.sendMessage js/chrome.runtime
                       #js {"sandbox" test-url}
                       (fn [resp]
                         (match [(js->clj resp :keywordize-keys true)]
                                [{:redirectUrl redirect-url}]
                                (ant/message-success (str "Matched! " test-url " is redirected to " redirect-url))
                                [{:cancel _}]
                                (ant/message-success (str "Matched! " test-url " is blocked."))
                                [nothing] (let [{:keys [global-enabled? online-enabled? redirect-enabled? cancel-enabled?]} @db/goo-conf]
                                            (when-not global-enabled?
                                              (ant/message-warning "Gooreplacer is OFF totally!!"))
                                            (when-not redirect-enabled?
                                              (ant/message-warning "Redirect rules is OFF !!"))
                                            (when-not cancel-enabled?
                                              (ant/message-warning "Cancel rules is OFF !!"))
                                            (when-not online-enabled?
                                              (ant/message-warning "Online rules is OFF !!"))
                                            (ant/message-error "Ooops. No rules matched!"))))))))))

(defn sandbox []
  [ant/card (ant/create-form
             (fn []
               (let [sandbox-form (ant/get-form)
                     test-handler (partial do-test sandbox-form)]
                 [ant/form {:layout "vertical"}
                  [ant/form-item {:label "Test URL" :extra "Test redirect/cancel rules here. Headers rules not supported now."}
                   (ant/decorate-field sandbox-form "test-url" {:rules [{:required true}]}
                                       [ant/input {:placeholder "example.com"
                                                   :on-press-enter test-handler}])]
                  [ant/form-item
                   [ant/button {:type "primary"
                                :on-click test-handler} "Test"]]])))])
