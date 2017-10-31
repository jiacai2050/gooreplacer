(ns gooreplacer.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [gooreplacer.tool-test]))

(doo-tests 'gooreplacer.tool-test)
