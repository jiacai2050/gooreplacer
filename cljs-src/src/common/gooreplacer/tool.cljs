(ns gooreplacer.tool
  (:require [clojure.string :as str]))

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

(defn try-modify-header [old-headers header-name header-value op]
  (case op
    "modify" (conj (remove #(= (str/lower-case header-name) (str/lower-case (:name %)))
                           old-headers)
                   {:name header-name :value header-value})
    "cancel" (remove #(= (str/lower-case header-name) (str/lower-case (:name %)))
                     old-headers)))

;; mainly used in background script
(def supported-handler {"redirectUrl" try-redirect
                        "cancel" try-cancel
                        "responseHeaders" try-modify-header
                        "requestHeaders" try-modify-header})

(defn url-match [url rules & [purpose]]
  ;; purpose priority
  ;; purpose in one rule > purpose param > default purpose "redirectUrl"
  (loop [[{:keys [purpose-in-rule] :as rule} & rest] rules]
    (when (and url rule)
      (let [purpose (or purpose-in-rule purpose "redirectUrl")]
        (when-let [handler (supported-handler purpose)]
          (if-let [resp (handler url rule)]
            (clj->js {purpose resp})
            (recur rest)))))))


(defn headers-match [purpose url raw-headers rules]
  (loop [[{:keys [enable src kind op name value] :as rule} & rest] rules
         headers (js->clj raw-headers :keywordize-keys true)]
    (if rule
      (if (and enable (re-find (re-pattern src) url))
        (let [handler (supported-handler purpose)
              new-headers (handler headers name value op)]
          (recur rest new-headers))
        (recur rest headers))
      (clj->js {purpose headers}))))
