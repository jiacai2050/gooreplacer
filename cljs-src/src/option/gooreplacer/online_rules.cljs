(ns gooreplacer.online-rules
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [gooreplacer.bootstrap :as bs]
            [gooreplacer.db :as db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [clojure.set :as set]
            [gooreplacer.io :as io])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn edit-online-url []
  (r/with-let [online-editable? (r/atom false)
               edit-or-ok-label (r/atom "Edit")]
    (let [!online-url (atom nil)]
      [bs/form-group {:control-id "online-url"}
       [bs/col {:sm 2 :class "text-right"}
        [bs/control-label "Rule List URL"]]
       [bs/col {:sm 8}
        [bs/input-group
         [bs/form-control {:type "text" :default-value (:url @db/goo-conf) :disabled (not @online-editable?) :input-ref #(reset! !online-url %)}]
         [bs/input-group-addon 
          [bs/glyphicon {:glyph "chevron-right"
                         :on-click #(.sendMessage js/chrome.runtime #js {"url" (.-value @!online-url)})}]]]]
       [bs/col {:sm 2}
        [bs/button {:bs-style "primary" :on-click (fn []
                                                    (swap! online-editable? not)
                                                    (if (= "OK" @edit-or-ok-label)
                                                      (let [url (.-value @!online-url)]
                                                        (swap! db/goo-conf assoc :url url)
                                                        (reset! edit-or-ok-label "Edit"))
                                                      (reset! edit-or-ok-label "OK")))} @edit-or-ok-label]]])))

(defn update-url [loading?]
  (r/with-let [loading? (r/atom false)]
    [bs/form-group
     [bs/col {:sm 2 :class "text-right"}
      [bs/control-label "Last  update"]]
     [bs/col {:sm 4} (:online-update-time @db/goo-conf)]
     [bs/col {:sm 3}
      [bs/button-toolbar
       [bs/button {:bs-style "primary"
                   :on-click #(do (reset! loading? true)
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
       [bs/button {:bs-style "primary"
                   :on-click #(ant/modal-confirm {:title "Do you really want to clear online rules?"
                                                  :on-ok (fn []
                                                           (reset! db/online-rules [])
                                                           (swap! db/goo-conf assoc :online-update-time 0)
                                                           (ant/message-success "Online rules is empty now.")
                                                           (.resolve js/Promise 0))})} "Clear"]]]]))

(defn configure-online-form []
  [bs/panel {:header (r/as-element [ant/checkbox {:default-checked (:online-enabled? @db/goo-conf)
                                                  :on-change #(swap! db/goo-conf update :online-enabled? not)} "Online Rule List"])
             :bs-style "info"}
   [bs/form {:class "form-horizontal" :on-submit #(.preventDefault %)}
    [edit-online-url]
    [update-url]]])
