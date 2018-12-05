
## [gooreplacer](http://liujiacai.net/gooreplacer)  [![Build Status](https://travis-ci.org/jiacai2050/gooreplacer.svg?branch=master)](https://travis-ci.org/jiacai2050/gooreplacer) [![Chrome Web Store](https://img.shields.io/chrome-web-store/v/jnlkjeecojckkigmchmfoigphmgkgbip.svg?style=plastic)](https://chrome.google.com/webstore/detail/gooreplacer/jnlkjeecojckkigmchmfoigphmgkgbip) [![Mozilla Add-on](https://img.shields.io/amo/v/gooreplacer.svg?style=plastic)](https://addons.mozilla.org/firefox/addon/gooreplacer/)

> 重定向/屏蔽 URL，修改/删除 headers

gooreplacer 最初为解决国内无法访问 Google 资源（Ajax、API等）导致页面加载速度巨慢而生，新版在此基础上，增加了更多实用功能，可以方便用户屏蔽某些请求，修改 HTTP 请求/响应 的 headers。

[English Version](README-en.md)

## 安装

首推在浏览器的插件商店去下载：

[Chrome](https://chrome.google.com/webstore/detail/gooreplacer/jnlkjeecojckkigmchmfoigphmgkgbip) / [Firefox](https://addons.mozilla.org/zh-CN/firefox/addon/gooreplacer/)

此外，也可以直接下载本仓库的 [crx 文件](gooreplacer.crx)。

## 特性

- 支持在线规则，主要用于重定向 Google 资源，用户可更改
- 本地规则
- 导入导出规则，方便用户备份、同步（规则可在 Chrome/Firefox 种通用）
- 支持测试

对于只想重定向 Google 资源的同学，可以在`Online Rule List`处填上

> https://raw.githubusercontent.com/jiacai2050/gooreplacer/master/gooreplacer.gson

然后点击更新就好了。如果想自定义规则，可参考 [guides.md](doc/guides.md)

## 实现

在 v1.0 之前采用纯 JavaScript 实现，代码在 [legacy-js-src](legacy-js-src) 目录，在 v2.0 之后采用 [ClojureScript](https://github.com/clojure/clojurescript) + [Reagent](https://github.com/reagent-project/reagent) + [Antd](https://ant.design/) + [React-Bootstrap](https://react-bootstrap.github.io/)，在 [cljs-src](cljs-src) 目录。


## License

[MIT License](http://liujiacai.net/license/MIT.html?year=2015) © Jiacai Liu
