(ns gooreplacer.listener
  (:require [cljs.core.async :refer [<! >! chan]]
            [cljs.core.match :refer-macros [match]])
  (:require-macros [cljs.core.async.macros :refer [go-loop go]]))

(enable-console-print!)

(when-let [web-request (goog.object/getValueByKeys js/window "chrome" "tabs")]
  (let [msg-ch (chan)]
    (.addListener js/chrome.runtime.onMessage
                  (fn [msg sender send-response]
                    (go (>! msg-ch msg))
                    ;; https://developer.chrome.com/extensions/runtime#event-onMessage
                    true))
    (.addListener js/chrome.browserAction.onClicked
                  (fn [_]
                    (go (>! msg-ch #js {"url" "../option/index.html"}))))
    (go-loop []
      (match [(js->clj (<! msg-ch) :keywordize-keys true)]
             [{:url url}] (.create js/chrome.tabs (clj->js {:url url}))
             [msg] (println "Unknown: " msg))

      (recur)))
  (println "listen message done!"))
