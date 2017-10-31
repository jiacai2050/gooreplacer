
## gooreplacer

> Redirect, block Requests; Modify, cancel request/response Headers

Gooreplacer is first created for redirecting Google Ajax/API to other CDN, since Google is blocked in my home.

Besides redirects, more features have been added to gooreplacer, mainly:

1. block some request based on some pattern
2. modify request/response http header based on some pattern
3. export/import user-defined rules

## Install

[Chrome](https://chrome.google.com/webstore/detail/gooreplacer/jnlkjeecojckkigmchmfoigphmgkgbip) / [Firefox](https://addons.mozilla.org/firefox/addon/gooreplacer/)

## Guides

Currently gooreplacer support 4 functions. Take one exported rules as example:

```
{
  "createBy": "https://github.com/jiacai2050/gooreplacer4chrome",
  "version": "2.0",
  "createAt": "Sun Oct 29 2017 19:55:32 GMT+0800 (CST)",
  "redirect-rules": [
    {
      "src": "ajax.googleapis.com",
      "dst": "ajax.proxy.ustclug.org",
      "kind": "wildcard",
      "enable": true
    }
  ],
  "cancel-rules": [
    {
      "src": "zhihu.com",
      "kind": "wildcard",
      "enable": true
    }
  ],
  "request-headers": [
    {
      "src": "v2ex.pub",
      "kind": "wildcard",
      "name": "Cookie",
      "value": "ljc=very good",
      "op": "modify",
      "enable": true
    }
  ],
  "response-headers": [
    {
      "src": "v2ex.pub",
      "kind": "wildcard",
      "name": "content-type",
      "op": "cancel",
      "enable": true
    }
  ]
}
```

- `redirect-rules` used for redirects
- `cancel-rules` used for blocks
- `request-headers` used for modify/cancel http request headers
- `response-headers` used for modify/cancel http response headers

In each rule, 

- `src` limit which request it will work on, and it has two types: wildcard and regexp, which is defined in `kind` key.
- `dst` only required in redirects, means destination
- `enable` turn on/off
- `op` required in `request-headers` `response-headers`, has two enum value: `cancel` or `modify`
- `name`, `value` required in `request-headers` `response-headers`, means header name(case-insensitive), header value

> NOTE: In order to be compatiable with old versions, `redirect-rules` key in JSON-rules file can be just `rules` and have following format:

```
"rules": {
  "ajax.googleapis.com": {
    "dstURL": "ajax.proxy.ustclug.org",
    "enable": true,
    "kind": "wildcard"
  }
}
```

According to [Google Extension document](https://developer.chrome.com/extensions/webRequest)，headers below can't be modified:

- Authorization
- Cache-Control
- Connection
- Content-Length
- Host
- If-Modified-Since
- If-None-Match
- If-Range
- Partial-Data
- Pragma
- Proxy-Authorization
- Proxy-Connection
- Transfer-Encoding


## Development

Gooreplacer is written in pure JavaScript before v1.0, you can check this version at [legacy-js-src](legacy-js-src).
After v2.0, gooreplacer get a big rewrite in [ClojureScript](https://github.com/clojure/clojurescript) + [Reagent](https://github.com/reagent-project/reagent) + [Antd](https://ant.design/) + [React-Bootstrap](https://react-bootstrap.github.io/)，which is resident in [cljs-src](cljs-src)。


## License

[MIT License](http://liujiacai.net/license/MIT.html?year=2015) © Jiacai Liu
