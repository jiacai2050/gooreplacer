官方网站
===

http://liujiacai.net/gooreplacer

gooreplacer For Chrome
===

A replacer for google fonts/api/themes.... to load page faster!

一个用于替换网页中Google Fonts,API,themes等的Chrome插件，让你快速打开这些页面！

[Firefox版本](https://github.com/jiacai2050/gooreplacer)

INSTALL
===

如果你有梯(fan)子(qiang)，可以去[Chrome WebStore](https://chrome.google.com/webstore/detail/gooreplacer/jlmmdfhaddlgkgcigccmlfhapliiacoh)下载。如果没有，那就按照下面的方式安装：

对于Windows用户：

从2014年5月开始，[Google为了照顾你们的安全](http://chrome.blogspot.com/2014/05/protecting-chrome-users-from-malicious.html)，不再允许直接安装本地扩展，但直接可以采用注册表安装，下载[force_install_for_windows.reg](https://github.com/jiacai2050/gooreplacer4chrome/raw/master/force_install_for_windows.reg)，双击安装，之后重启Chrome就可以了。

如果想卸载，用[remove_install_for_windows.reg](https://github.com/jiacai2050/gooreplacer4chrome/raw/master/remove_install_for_windows.reg)文件清除注册表即可。

对于Linux/Mac用户：

1. 先在Chrome中打开chrome://extensions/, 打开开发者模式，如下图<img src="http://img01.taobaocdn.com/imgextra/i1/581166664/TB2gof_apXXXXbCXpXXXXXXXXXX_!!581166664.png" alt=" develop"/>
2. 然后下载[gooreplacer.crx](https://github.com/jiacai2050/gooreplacer4chrome/blob/master/gooreplacer.crx?raw=true)
3. 最后把gooreplacer.crx文件拖到Chrome上就可以安装了。<img src="http://img03.taobaocdn.com/imgextra/i3/581166664/TB2rBMEapXXXXb1XpXXXXXXXXXX_!!581166664.jpg" alt=" chrome-drag"/>

That's all! Enjoy!

TEST
===

安装本插件后，可以在Chrome中输入下面的链接检查是否起作用:

1. https://fonts.googleapis.com/css?family=Open+Sans
2. https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js
3. http://fonts.googleapis.com/css?family=Open+Sans
4. http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js

如果能转到lug.ustc.edu.cn相应的资源即说明跳转成功。

Development
===

- 使用[Chrome extension webRequest](https://developer.chrome.com/extensions/webRequest)模块开发
- 使用[科大公共库](https://servers.ustclug.org/2014/07/ustc-blog-force-google-fonts-proxy/)开替换Google资源,之前曾使用360公共库，但是[360并不支持https访问](https://servers.ustclug.org/2014/06/blog-googlefonts-speedup/)，所以最终选择了科大。

PS：如果你的工作依赖Google Apps,可以试试[CubeBackup](http://www.cubebackup.com/).

如果你发现有任何问题，请与我联系。谢谢！
