gooreplacer For Chrome
===

A replacer for google fonts/api/themes.... to load page faster!

一个用于替换网页中Google Fonts,API,themes等的Chrome插件，让你快速打开这些页面！

[Firefox版本](https://github.com/jiacai2050/gooreplacer)

INSTALL
===
首先说明一下，这个插件我没上传到Chome Webstore上面，原因是在我上传时遇到了如下图的情景：
<img src="http://img01.taobaocdn.com/imgextra/i1/581166664/TB2jSskapXXXXXJXXXXXXXXXXXX_!!581166664.png" alt=" paid-for-google-develop"/>

这$5不算什么，主要是咱没有美国的信用卡或者paypal这东西呀，没办法了 ╮(╯▽╰)╭


想安装方式也很简单，先在Chrome中打开[chrome://extensions/][]，然后下载gooreplacer.crx，之后把这个crx拖到Chrome上就可以安装了。

That's all! Enjoy!

Development
===

- 使用[Chrome extension webRequest](https://developer.chrome.com/extensions/webRequest)模块开发
- 使用[科大公共库](https://servers.ustclug.org/2014/07/ustc-blog-force-google-fonts-proxy/)开替换Google资源,之前曾使用360公共库，但是[360并不支持https访问](https://servers.ustclug.org/2014/06/blog-googlefonts-speedup/)，所以最终选择了科大。

PS：吐槽下，用Chrome的开发extension比Firefox的方便好多！！

TEST
===

按照本插件后，可以Chrome中输入下面的链接检查是否起作用:

1. https://fonts.googleapis.com/css?family=Open+Sans
2. https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js
3. http://fonts.googleapis.com/css?family=Open+Sans
4. http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js

如果能转到lug.ustc.edu.cn相应的资源即说明跳转成功。

如果你发现有任何问题，请与我联系。谢谢！