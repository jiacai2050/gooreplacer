(ns gooreplacer.core
  (:require [gooreplacer.tool :as tool]
            [gooreplacer.listener :as listener]
            [clojure.string :as str]
            [alandipert.storage-atom])
  (:require-macros [gooreplacer.macro :refer [init-db-reader!]]))

(when-let [web-request (goog.object/getValueByKeys js/window "chrome" "webRequest")]
  (init-db-reader!)
  (.addListener (.-onBeforeRequest web-request)
                (fn [req]
                  (when-let [url (aget req "url")]
                    (let [{:keys [global-enabled? online-enabled? redirect-enabled? cancel-enabled?]} (read-goo-conf)]
                      (when global-enabled?
                        (if-let [redirect-resp (when redirect-enabled?
                                                 (tool/url-match url (read-redirect-rules) "redirectUrl"))]
                          redirect-resp
                          (if-let [cancel-resp (when cancel-enabled?
                                                 (tool/url-match url (read-cancel-rules) "cancel"))]
                            cancel-resp
                            (when online-enabled?
                              (tool/url-match (aget req "url") (filter #(#{"redirectUrl" "cancel"} (:purpose %)) (read-online-rules))))))))))
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
  (println "listen request done!"))

