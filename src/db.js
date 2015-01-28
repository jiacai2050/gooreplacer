var rules = {
    'ajax.googleapis.com': {dstURL: 'ajax.lug.ustc.edu.cn', enable: true},        
    'fonts.googleapis.com': {dstURL:'fonts.lug.ustc.edu.cn', enable: true},        
    'themes.googleusercontent.com': {dstURL:'google-themes.lug.ustc.edu.cn', enable: true},
    'fonts.gstatic.com': {dstURL:'fonts-gstatic.lug.ustc.edu.cn', enable: true},
    'http.*://platform.twitter.com/widgets.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/widgets.js', enable: true},
    'http.*://apis.google.com/js/api.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/api.js', enable: true},
    'http.*://apis.google.com/js/plusone.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/plusone.js', enable: true}
};
if(!localStorage.getItem("rules")) {
	for(url in rules){
		localStorage.setItem(url,JSON.stringify(rules[url]))
	}
    localStorage.setItem("rules", JSON.stringify(rules));
}
if(!localStorage.getItem("isRedirect")) {
    localStorage.setItem("isRedirect", true);
}
