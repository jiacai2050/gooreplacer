#!/bin/bash

cd "$(cd `dirname $0`;pwd)"

set -x

rm resources/release/ui/option.js \
   resources/release/ui/popup.js \
   resources/release/background/main.js


lein with-profile release do clean, cljsbuild once option
lein with-profile release do clean, cljsbuild once background
lein with-profile release do clean, cljsbuild once popup
# clean compiled js before packaging
lein with-profile release clean

cd resources/release
zip -x *.DS_Store -r ~/firefox_gooreplacer_`date +%s`.zip *
