(ns gooreplacer.common.macro-test
  (:require [gooreplacer.common.macro :as m]
            [clojure.test :refer [deftest is are]]))

(deftest test-camelcase->kebab
  (are [input expected] (= (#'m/camelcase->kebab input) expected)
    "someKey" "some-key"))
