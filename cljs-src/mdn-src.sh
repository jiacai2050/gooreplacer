#!/bin/bash

cd "$(cd `dirname $0`;pwd)"

set -x
git archive --format=zip HEAD -o ~/firefox_gooreplacer_src_`date +%s`.zip
