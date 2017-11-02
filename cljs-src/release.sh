#!/bin/bash

cd "$(cd `dirname $0`;pwd)"

set -x
rm resources/release/option/main.js*
rm resources/release/background/main.js*

lein with-profile release-bg do clean, cljsbuild once
lein with-profile release-option do clean, cljsbuild once

rm -rf resources/release/option/js
rm -rf resources/release/background/js

cd resources/release
zip -x *.DS_Store -r ~/gooreplacer_`date +%s`.zip *
