#!/bin/bash

cd "$(cd `dirname $0`;pwd)"

set -x
rm resources/release/option/main.js*
rm resources/release/background/main.js*

lein with-profile release,ui-deps do clean, cljsbuild once option
lein with-profile release,bg-deps do clean, cljsbuild once background
lein with-profile release clean

cd resources/release
zip -x *.DS_Store -r ~/firefox_gooreplacer_`date +%s`.zip *
