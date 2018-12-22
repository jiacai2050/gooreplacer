(ns gooreplacer.tool-test
  (:require [gooreplacer.tool :as tool]
            [cljs.test :refer-macros [deftest is testing run-tests]]))

(deftest test-try-redirect
  (testing "wildcard.."
    (let [req-url "https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js" 
          r {:src "ajax.googleapis.com"
             :dst "ajax.proxy.ustclug.org"
             :kind "wildcard"
             :enable true}]
      (is (= "https://ajax.proxy.ustclug.org/ajax/libs/jquery/1.8.1/jquery.min.js" (tool/try-redirect req-url r)))))
  (testing "regexp without group.."
    (let [req-url "http://weibo.com/welcome" 
          r {:src "weibo.com"
             :dst "liujiacai.net"
             :kind "regexp"
             :enable true}]
      (is (= "http://liujiacai.net/welcome" (tool/try-redirect req-url r)))))
  (testing "regexp with group.."
    (let [req-url "weibo.com" 
          r {:src "(weibo).com"
             :dst "$1.cn"
             :kind "regexp"
             :enable true}]
      (is (= "weibo.cn" (tool/try-redirect req-url r))))))

(deftest test-try-cancel
  (let [req-url "http://liujiacai.net"
        r {:src "liujiacai.net"
           :kind "wildcard"
           :enable true}]
    (is (tool/try-cancel req-url r))))

(deftest test-encode-decode-rule
  (testing "encode before save"
    (let [rule {:src "abc*com\\*xx?yy" :kind "wildcard"}
          {:keys [src]} (tool/encode-rule rule)]
      (is (= "abc.*com\\*xx.?yy" src)))
    (let [rule {:src "https://www.v*ex.pub/bg.gif?" :kind "wildcard" :enable true}]
      (is (= "https://www.v.*ex.pub/bg.gif.?" (:src (tool/encode-rule rule))))))
  (testing "decode before display"
    (let [rule {:src "abc.*com\\*xx.?yy" :kind "wildcard"}
          {:keys [src]} (tool/decode-rule rule)]
      (is (= "abc*com\\*xx?yy" src)))))

(deftest test-modify-header
  (testing "modify header"
    (let [old-header (clj->js [{:name "content-type" :value "text/html; charset=utf-8"}
                               {:name "content-type2" :value "unchanged"}])
          header-name "Content-Type"
          header-value "application/json"
          op "modify"]
      (is (= [{:name "content-type", :value "application/json"}
              {:name "content-type2" :value "unchanged"}]
             (do
               (tool/try-modify-header! old-header header-name header-value op)
               (js->clj old-header :keywordize-keys true))))))
  (testing "cancel header"
    (let [old-header (clj->js [{:name "content-type" :value "text/html; charset=utf-8"}
                               {:name "content-type2" :value "unchanged"}])
          header-name "Content-Type"
          header-value nil
          op "cancel"]
      (is (= [{:name "content-type2" :value "unchanged"}]
             (do
               (tool/try-modify-header! old-header header-name header-value op)
               (js->clj old-header :keywordize-keys true)))))))
