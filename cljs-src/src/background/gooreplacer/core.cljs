(ns gooreplacer.core
  (:require [gooreplacer.db :as db]
            [gooreplacer.tool :as tool]
            [gooreplacer.listener :as listener]
            [clojure.string :as str]))

(def supported-handler {"redirectUrl" tool/try-redirect
                        "cancel" tool/try-cancel
                        "responseHeaders" tool/try-modify-header
                        "requestHeaders" tool/try-modify-header})

(defn- url-match [url redirect-rules & [purpose]]
  ;; purpose priority
  ;; purpose in one rule > purpose param > default purpose "redirectUrl"
  (loop [[{:keys [purpose-in-rule] :as rule} & rest] redirect-rules]
    (when (and url rule)
      (let [purpose (or purpose-in-rule purpose "redirectUrl")]
        (when-let [handler (supported-handler purpose)]
          (if-let [resp (handler url rule)]
            (clj->js {purpose resp})
            (recur rest)))))))

(defn- headers-match [purpose url raw-headers rules]
  (loop [[{:keys [enable src kind dst op] :as rule} & rest] rules]
    (when rule
      (if (and enable (re-find (re-pattern src) url))
        (let [header-name kind
              header-value dst
              handler (supported-handler purpose)
              new-headers (handler raw-headers header-name header-value op)]
          (clj->js {purpose new-headers}))
        (recur rest)))))

(when-let [web-request (goog.object/getValueByKeys js/window "chrome" "webRequest")]
  (.addListener (.-onBeforeRequest web-request)
                (fn [req]
                  (when-let [url (aget req "url")]
                    (let [{:keys [global-enabled? online-enabled? redirect-enabled? cancel-enabled?]} (db/read-goo-conf)]
                      (when global-enabled?
                        (if-let [online-resp (when online-enabled?
                                               (url-match (aget req "url") (db/read-online-rules)))]
                          online-resp
                          (if-let [redirect-resp (when redirect-enabled?
                                                   (url-match url (db/read-redirect-rules) "redirectUrl"))]
                            redirect-resp
                            (when cancel-enabled?
                              (url-match url (db/read-cancel-rules) "cancel"))))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking"])
  (.addListener (.-onHeadersReceived web-request)
                (fn [req]
                  (let [{:keys [res-headers-enabled? online-enabled? global-enabled?]} (db/read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (url-match (aget req "url") (db/read-online-rules)))]
                        online-resp
                        (when res-headers-enabled?
                          (headers-match "responseHeaders" (.-url req) (.-responseHeaders req) (db/read-response-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "responseHeaders"])
  (.addListener (.-onBeforeSendHeaders web-request)
                (fn [req]
                  (let [{:keys [req-headers-enabled? global-enabled? online-enabled?]} (db/read-goo-conf)]
                    (when global-enabled?
                      (if-let [online-resp (when online-enabled?
                                             (url-match (aget req "url") (db/read-online-rules)))]
                        online-resp
                        (when req-headers-enabled?
                          (headers-match "requestHeaders" (.-url req) (.-requestHeaders req) (db/read-request-headers)))))))
                (clj->js {"urls" ["<all_urls>"]})
                #js ["blocking" "requestHeaders"])
  (println "listen request done!"))

