
## [gooreplacer](http://liujiacai.net/gooreplacer)  [![Chrome Web Store](https://img.shields.io/chrome-web-store/v/jnlkjeecojckkigmchmfoigphmgkgbip.svg?style=plastic)](https://chrome.google.com/webstore/detail/gooreplacer/jnlkjeecojckkigmchmfoigphmgkgbip) [![Mozilla Add-on](https://img.shields.io/amo/v/gooreplacer.svg?style=plastic)](https://addons.mozilla.org/firefox/addon/gooreplacer/)

> A Firefox/Chrome extension to modify HTTP requests :-)

At first, Gooreplacer is created for redirecting Google Ajaxs/APIs/Fonts to other CDN to bypass [Great Firewall](https://en.wikipedia.org/wiki/Great_Firewall), since pages referring those are slow as molasses.

Nowadays, more features have been added to gooreplacer, mainly:

1. Block request
2. Modify headers
3. Export/Import rules, sync between Firefox and Chrome.
4. Live Test

Most users prefer gooreplacer than other similar extensions, [HTTPS Everywhere](https://www.eff.org/https-everywhere)/[Redirector](http://einaregilsson.com/redirector/), for ease-of-use. Why not give it a try?

## Install

[Chrome](https://chrome.google.com/webstore/detail/gooreplacer/jnlkjeecojckkigmchmfoigphmgkgbip) / [Firefox](https://addons.mozilla.org/firefox/addon/gooreplacer/)

## Guides

Currently gooreplacer support 4 functions. Take one exported rules as example:

```
{
  "createBy": "https://github.com/jiacai2050/gooreplacer",
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
      "src": "facebook.com",
      "kind": "wildcard",
      "enable": true
    }
  ],
  "request-headers": [
    {
      "src": "google.com",
      "kind": "wildcard",
      "name": "user-agent",
      "value": "gooreplacer",
      "op": "modify",
      "enable": true
    }
  ],
  "response-headers": [
    {
      "src": "google.com",
      "kind": "wildcard",
      "name": "Cookie",
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

In redirects, you can use `$1` `$2` ... to backreference captured groups in case of `regexp` kind. For example:

```
{
  "src": "https://search.yahoo.com/search\?p=([a-z]+)",
  "dst": "https://www.google.com/search?q=$1",
  "kind": "regexp",
  "enable": true
}
```

Then https://search.yahoo.com/search?p=gooreplacer will redirect to https://www.google.com/search?q=gooreplacer

If your rules don't work as expected, use sandbox to test, it can tell what's missing.

## NOTE

- In order to be compatiable with old versions, `redirect-rules` key in JSON-rules file can be just `rules` and have following format:

  ```
  "rules": {
    "ajax.googleapis.com": {
      "dstURL": "ajax.proxy.ustclug.org",
      "enable": true,
      "kind": "wildcard"
    }
  }
  ```

- According to [Google Extension document](https://developer.chrome.com/extensions/webRequest)，headers below can't be modified:

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
