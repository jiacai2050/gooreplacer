# gooreplacer

> Redirect, block Requests; Modify, cancel request/response Headers.


## Develop

This addon is written in ClojureScript, so [lein](https://leiningen.org/) is required.

In development period, `optimizations` is set to `none`, and mainly consists of two commands:

- `lein option`, setup a figwheel server for option page
- `lein bg`, setup a figwheel server for background script.

In release period, `optimizations` is set to `advanced`, and just run `./release.sh` to get the finally optimized version in your home dir.
