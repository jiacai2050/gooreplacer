var rules = {
    'ajax.googleapis.com': {dstURL: 'ajax.lug.ustc.edu.cn', enable: true},        
    'fonts.googleapis.com': {dstURL:'fonts.lug.ustc.edu.cn', enable: true},        
    'themes.googleusercontent.com': {dstURL:'google-themes.lug.ustc.edu.cn', enable: true},
    'fonts.gstatic.com': {dstURL:'fonts-gstatic.lug.ustc.edu.cn', enable: true},
    'http.*://platform.twitter.com/widgets.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/widgets.js', enable: true},
    'http.*://apis.google.com/js/api.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/api.js', enable: true},
    'http.*://apis.google.com/js/plusone.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/plusone.js', enable: true}
};
if(!localStorage.getItem("rules")) {
    localStorage.setItem("rules", JSON.stringify(rules));
}
if(!localStorage.getItem("isRedirect")) {
    localStorage.setItem("isRedirect", true);
}
