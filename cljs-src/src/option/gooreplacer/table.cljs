(ns gooreplacer.table
  (:require [gooreplacer.i18n :as i18n]))

(def pagination {:show-size-changer true
                 :page-size-options ["5" "10" "20"]
                 :show-total i18n/tmpl-pagination})
