(ns gooreplacer.option.table
  (:require [gooreplacer.common.i18n :as i18n]))

(def pagination {:show-size-changer true
                 :page-size-options ["5" "10" "20"]
                 :show-total i18n/tmpl-pagination})
