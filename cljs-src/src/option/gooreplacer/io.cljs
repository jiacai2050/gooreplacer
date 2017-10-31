(ns gooreplacer.io
  (:require [goog.dom :as gdom]
            [gooreplacer.db :as db]
            [antizer.reagent :as ant]
            [gooreplacer.tool :as tool]))

(defn normalize-legacy-rule [rule]
  (let [src (key rule)
        {:keys [dstURL kind enable]
         :or [kind "wildcard"]} (val rule)]
    {:src (subs (str src) 1)
     :dst dstURL
     :kind kind
     :enable enable}))

(defn import-legacy-rules! [rules]
  (doseq [rule rules] 
    (db/append-redirect-rules! (normalize-legacy-rule rule))))

(defn append-rules! [new-rules which-db]
  (when new-rules
    (swap! which-db into (map tool/encode-rule new-rules))))

;; (merge {:src (subs (str (key r)) 1)}
;;        (set/rename-keys (val r) {:dstURL :dst}))

(defn import-online-rules! [{:keys [redirect-rules cancel-rules request-headers response-headers rules] :as goo-rules}]
  (append-rules! (map #(assoc % :purpose "redirectUrl") redirect-rules) db/online-rules)
  (append-rules! (map #(assoc (normalize-legacy-rule %) :purpose "redirectUrl") rules) db/online-rules)
  (append-rules! (map #(assoc % :purpose "cancel") cancel-rules) db/online-rules)
  (append-rules! (map #(assoc % :purpose "cancel") cancel-rules) db/online-rules)
  (append-rules! (map #(assoc % :purpose "requestHeaders") request-headers) db/online-rules)
  (append-rules! (map #(assoc % :purpose "responseHeaders") response-headers) db/online-rules))

(defn import-rules []
  (let [file-choose (gdom/getElement "gsonChooser")]
    (set! (.-value file-choose) "")
    (set! (.-onchange file-choose) (fn []
                                     (let [file (aget file-choose "files" 0)
                                           reader (js/FileReader.)]
                                       (set! (.-onloadend reader) (fn [resp]
                                                                    (let [goo-rules (js->clj (.parse js/JSON resp.target.result) :keywordize-keys true)
                                                                          {:keys [redirect-rules cancel-rules request-headers response-headers rules] :as goo-rules} goo-rules]
                                                                      (import-legacy-rules! rules)
                                                                      (append-rules! redirect-rules db/redirect-rules)
                                                                      (append-rules! cancel-rules db/cancel-rules)
                                                                      (append-rules! request-headers db/request-headers)
                                                                      (append-rules! response-headers db/response-headers))))
                                       (.readAsText reader file))))
    (.click file-choose)))

(defn export-rules []
  (let [data {:createBy "http://liujiacai.net/gooreplacer/"
              :version (db/version)
              :createAt (js/Date)
              :redirect-rules (map tool/decode-rule (db/read-redirect-rules))
              :cancel-rules (map tool/decode-rule (db/read-cancel-rules))
              :request-headers (map tool/decode-rule (db/read-request-headers))
              :response-headers (map tool/decode-rule (db/read-response-headers))}
        js-data (clj->js data)
        content-type "application/json"
        content (js/Blob. [(.stringify js/JSON js-data nil 2)] (clj->js {:type content-type}))
        file-export (gdom/getElement "gsonExport")]
    (set! (.-href file-export) (.createObjectURL js/window.URL content))
    (.click file-export)))
