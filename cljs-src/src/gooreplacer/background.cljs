(ns gooreplacer.background
  (:require [gooreplacer.common.tool :as tool]
            [clojure.string :as str]
            [cljs.core.async :refer [<! >! chan]]
            [alandipert.storage-atom]
            [gooreplacer.common.dev]
            [cljs.core.match :refer-macros [match]])
  (:require-macros [gooreplacer.common.macro :refer [init-db-reader!]]
                   [cljs.core.async.macros :refer [go-loop go]]))

(init-db-reader!)

(defn modify-url [url]
  (let [{:keys [global-enabled? online-enabled? redirect-enabled? cancel-enabled?]} (read-goo-conf)]
    (when global-enabled?
      (if-let [redirect-resp (when redirect-enabled?
                               (tool/url-match url (read-redirect-rules) "redirectUrl"))]
        redirect-resp
        (if-let [cancel-resp (when cancel-enabled?
                               (tool/url-match url (read-cancel-rules) "cancel"))]
          cancel-resp
          (when online-enabled?
            (tool/url-match url (filter #(#{"redirectUrl" "cancel"} (:purpose %)) (read-online-rules)))))))))

(when-let [web-request (goog.object/getValueByKeys js/window "chrome" "webRequest")]
  (.addListener (.-onBeforeRequest web-request)
                (fn [req]
                  (when-let [url (aget req "url")]
                    (modify-url url)))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking"])
  (.addListener (.-onHeadersReceived web-request)
                (fn [req]
                  (let [{:keys [res-headers-enabled? online-enabled? global-enabled?]} (read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (tool/url-match (aget req "url") (filter #(= (:purpose %) "responseHeaders") (read-online-rules))))]
                        online-resp
                        (when res-headers-enabled?
                          (tool/headers-match "responseHeaders" (.-url req) (.-responseHeaders req) (read-response-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "responseHeaders"])
  (.addListener (.-onBeforeSendHeaders web-request)
                (fn [req]
                  (let [{:keys [req-headers-enabled? global-enabled? online-enabled?]} (read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (tool/url-match (aget req "url") (filter #(= (:purpose %) "requestHeaders") (read-online-rules))))]
                        online-resp
                        (when req-headers-enabled?
                          (tool/headers-match "requestHeaders" (.-url req) (.-requestHeaders req) (read-request-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "requestHeaders"])
  (println "listen request done!")
  (.addListener js/chrome.runtime.onMessage
                (fn [msg sender send-response]
                  (match [(js->clj msg :keywordize-keys true)]
                         [{:sandbox test-url}] (send-response (modify-url test-url))
                         [{:url goto-url}] (.create js/chrome.tabs (clj->js {:url goto-url}))
                         [msg] (println "Unknown: " msg))
                  ;; https://developer.chrome.com/extensions/runtime#event-onMessage
                  true))
  (.addListener js/chrome.browserAction.onClicked
                #(.create js/chrome.tabs (clj->js {:url "../option/index.html"})))
  (println "listen message done!"))
