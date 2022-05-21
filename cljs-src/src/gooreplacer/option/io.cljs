(ns gooreplacer.option.io
  (:require [goog.dom :as gdom]
            [gooreplacer.common.db :as db]
            [antizer.reagent :as ant]
            [gooreplacer.common.tool :as tool]))

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

(defn import-online-rules! [{:keys [redirect-rules cancel-rules request-headers response-headers rules] :as rs}]
  (reset! db/online-rules [])
  (doseq [r (concat (map #(assoc % :purpose "redirectUrl") redirect-rules)
                    (map #(assoc (normalize-legacy-rule %) :purpose "redirectUrl") rules)
                    (map #(assoc % :purpose "cancel") cancel-rules)
                    (map #(assoc % :purpose "requestHeaders") request-headers)
                    (map #(assoc % :purpose "responseHeaders") response-headers))]
    (db/append-online-rules! r)))

(defn import-rules []
  (let [file-choose (gdom/getElement "jsonChooser")]
    (set! (.-value file-choose) "")
    (set! (.-onchange file-choose) (fn []
                                     (let [file (aget file-choose "files" 0)
                                           reader (js/FileReader.)]
                                       (set! (.-onloadend reader) (fn [resp]
                                                                    (let [goo-rules (js->clj (.parse js/JSON resp.target.result) :keywordize-keys true)
                                                                          {:keys [redirect-rules cancel-rules request-headers response-headers rules] :as goo-rules} goo-rules]
                                                                      (import-legacy-rules! rules)
                                                                      (doseq [r redirect-rules] (db/append-redirect-rules! r))
                                                                      (doseq [r cancel-rules] (db/append-cancel-rules! r))
                                                                      (doseq [r request-headers] (db/append-request-headers! r))
                                                                      (doseq [r response-headers] (db/append-response-headers! r)))))
                                       (.readAsText reader file))))
    (.click file-choose)))

(defn export-rules []
  (let [data {:createBy "http://liujiacai.net/gooreplacer/"
              :version (db/version)
              :createAt (.toLocaleString (js/Date.))
              :redirect-rules (map tool/decode-rule @db/redirect-rules)
              :cancel-rules (map tool/decode-rule @db/cancel-rules)
              :request-headers (map tool/decode-rule @db/request-headers)
              :response-headers (map tool/decode-rule @db/response-headers)}
        content (js/Blob. [(.stringify js/JSON (clj->js data) nil 2)] (clj->js {:type "application/json"}))]
    (.download js/chrome.downloads (clj->js {:url (.createObjectURL js/window.URL content)
                                             :saveAs true
                                             :filename "gooreplacer.json"}))))
