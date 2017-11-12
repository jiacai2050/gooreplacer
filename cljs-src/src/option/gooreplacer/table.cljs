(ns gooreplacer.table)

(def pagination {:show-size-changer true
                 :page-size-options ["5" "10" "20"]
                 :show-total #(str "Total: " % " rules")})
