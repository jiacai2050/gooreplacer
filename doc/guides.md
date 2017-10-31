## 功能介绍

本插件所有的功能依赖于 [webRequest](https://developer.chrome.com/extensions/webRequest) 接口，主要有以下两个功能：

1. 重定向、删除某些 URL
2. 修改、删除某些 http headers

## 匹配方式

为了做到细粒度控制，所有规则需要指定一个作用源，用于限定规则作用范围，支持两种匹配方式：

- 通配符（wildcard），可以使用 `*`表示任意个字符，`?`表示任一字符。如需表示这两个字符自身的含义，需要使用`\`进行转义。

```
www.baidu.com/s\?wd=java   ----通配符--->  www.baidu.com/s?wd=lisp
```

此外，可以使用`^`、`$`表示字符的开始与结尾。例如：

```
baidu.com/$  ----通配符--->  baidu.com/?
```
这样就能够把`baidu.com/`重定向到`baidu.com/?`了，[据说](http://v2ex.com/t/169967#reply2)，这样能防止劫持吆 -:)

- 正则式（regexp），语法同[JS 的 RegExp](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/RegExp)，对于有分组的情况，在目标 URL 中可以使用 `$1`, `$2`... 反引用。例如

```
(weibo|ucloud)\.com  ----正则式--->  $1.cn
```

这样就把`weibo.com`、`ucloud.com`分别重定向到`weibo.cn`与`ucloud.cn`了。


## 内置规则

下面是一份导出规则的样本，展示了目前支持的四类规则：

- `redirect-rules` 重定向 URL 规则
- `cancel-rules` 屏蔽 URL 规则
- `request-headers` http request headers 修改规则
- `response-headers` http response headers 修改规则

```
{
  "createBy": "http://liujiacai.net/gooreplacer/",
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
对于规则中一些字段的枚举值：

- `kind`: `wildcard`表示通配符，`regexp`表示正则式
- `op`: `modify`表示修改 header，`cancel`表示删除 header

## 迁移指南（v1.0-->v2.0）

在 Option 页面的打开 Console，输入下面的代码

```
function decode(src, kind) {
    if (kind == 'wildcard' && src.match(/\.(\*|\?)/g)) {
        return src.replace(/\.(\*|\?)/g, "$1");
    } else {
        return src;
    }
}

var rawRules = JSON.parse(localStorage.getItem("rules"));
var exportRules = {};
for(var k in rawRules) {
    exportRules[decode(k, rawRules[k].kind)] = rawRules[k];
}

console.log(JSON.stringify({"rules": exportRules}, null , 2));
```

回车运行，会打印出本地规则，新建`gooreplacer.gson`文件，保存上面输出的内容，之后在导入时选择这个文件即可恢复原有规则。

PS：如果在迁移过程中有任何问题，可以提 issue，我会尽量协助大家迁移。


