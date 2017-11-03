(ns gooreplacer.core
  (:require [gooreplacer.db :as db]
            [gooreplacer.tool :as tool]
            [gooreplacer.listener :as listener]
            [clojure.string :as str]))

(when-let [web-request (goog.object/getValueByKeys js/window "chrome" "webRequest")]
  (.addListener (.-onBeforeRequest web-request)
                (fn [req]
                  (when-let [url (aget req "url")]
                    (let [{:keys [global-enabled? online-enabled? redirect-enabled? cancel-enabled?]} (db/read-goo-conf)]
                      (when global-enabled?
                        (if-let [online-resp (when online-enabled?
                                               (tool/url-match (aget req "url") (filter #(#{"redirectUrl" "cancel"} (:purpose %)) (db/read-online-rules))))]
                          online-resp
                          (if-let [redirect-resp (when redirect-enabled?
                                                   (tool/url-match url (db/read-redirect-rules) "redirectUrl"))]
                            redirect-resp
                            (when cancel-enabled?
                              (tool/url-match url (db/read-cancel-rules) "cancel"))))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking"])
  (.addListener (.-onHeadersReceived web-request)
                (fn [req]
                  (let [{:keys [res-headers-enabled? online-enabled? global-enabled?]} (db/read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (tool/url-match (aget req "url") (filter #(= (:purpose %) "responseHeaders") (db/read-online-rules))))]
                        online-resp
                        (when res-headers-enabled?
                          (tool/headers-match "responseHeaders" (.-url req) (.-responseHeaders req) (db/read-response-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "responseHeaders"])
  (.addListener (.-onBeforeSendHeaders web-request)
                (fn [req]
                  (let [{:keys [req-headers-enabled? global-enabled? online-enabled?]} (db/read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (tool/url-match (aget req "url") (filter #(= (:purpose %) "requestHeaders") (db/read-online-rules))))]
                        online-resp
                        (when req-headers-enabled?
                          (tool/headers-match "requestHeaders" (.-url req) (.-requestHeaders req) (db/read-request-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "requestHeaders"])
  (println "listen request done!"))

