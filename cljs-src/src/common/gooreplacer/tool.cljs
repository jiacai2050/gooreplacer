(ns gooreplacer.tool
  (:require [clojure.string :as str]
            [goog.array :as garr]
            [goog.object :as gobj]))

(defn encode-rule [{:keys [kind] :as rule}]
  (if (= "wildcard" kind)
    (update rule :src (fn [src]
                        (let [rev-s (str/reverse src)]
                          (str/reverse (str/replace rev-s #"([\*|\?])(?!\\)" "$1.")))))
    rule))

(defn decode-rule [rule]
  (if (= "wildcard" (:kind rule))
    (update rule :src #(str/replace % #"\.([\*|\?])" "$1"))
    rule))

(defn try-redirect [url {:keys [src dst kind enable] :as rule}]
  (when enable
    (let [src-re (re-pattern src)]
      (when-let [matched (re-find src-re url)]
        (case kind
          "wildcard" (str/replace-first url src-re dst)
          "regexp" (if (string? matched)
                     (str/replace-first url src-re dst)
                     (let [[full-match & parenthesized-matches] matched
                           redirect-url (str/replace-first url full-match dst)]
                       (loop [[m & rest] parenthesized-matches
                              redirect-url redirect-url
                              i 1]
                         (if m
                           (recur rest
                                  (str/replace redirect-url (str "$" i) m)
                                  (inc i))
                           redirect-url)))))))))

(defn try-cancel [url {:keys [src enable kind]}]
  (some? (when enable
           (re-find (re-pattern src) url))))

(defn try-modify-header!
  "raw-headers is a js object for in-place update"
  [raw-headers header-name header-value op]
  (let [header-name-lower-case (str/lower-case header-name)
        headers-length (alength raw-headers)]
    (loop [i 0]
      (when (< i headers-length)
        (let [curr-header (aget raw-headers i)]
          (if (= header-name-lower-case (str/lower-case (.-name curr-header)))
            (case op
              "modify" (set! (.-value curr-header) header-value)
              "cancel" (garr/removeAt raw-headers i))
            (recur (inc i))))))))

;; mainly used in background script
(def supported-handler {"redirectUrl" try-redirect
                        "cancel" try-cancel
                        "responseHeaders" try-modify-header!
                        "requestHeaders" try-modify-header!})

(defn url-match [url rules & [explict-purpose]]
  ;; purpose priority
  ;; purpose in one rule > purpose param > default purpose "redirectUrl"
  (loop [[{:keys [purpose] :as rule} & rest] rules]
    (when (and url rule)
      (let [purpose (or purpose explict-purpose "redirectUrl")]
        (when-let [handler (supported-handler purpose)]
          (if-let [resp (handler url rule)]
            (clj->js {purpose resp})
            (recur rest)))))))


(defn headers-match [purpose url raw-headers rules]
  (let [headers raw-headers]
    (loop [[{:keys [enable src kind op name value] :as rule} & rest] rules]
      (if rule
        (if (and enable (re-find (re-pattern src) url))
          (let [handler! (supported-handler purpose)]
             (handler! headers name value op)
            (recur rest))
          (recur rest))
        (gobj/create purpose headers)))))
