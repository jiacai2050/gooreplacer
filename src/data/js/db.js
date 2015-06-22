var GooRule = function(srcURL, dstObj) {
    var WILDCARD = "wildcard",
        REGEXP   = "regexp";

    this.srcURL = srcURL;
    this.kind   = dstObj.kind || WILDCARD; //规则默认为WILDCARD类型
    this.dstURL = dstObj.dstURL;
    this.enable = dstObj.hasOwnProperty("enable")? dstObj.enable : true;   //规则默认开启
    
    this.toJson = function() {
        var json = {};
        json[this.getKey()] = this.getValue();
        return json;
    }
    this.getKey = function() {
        return this.srcURL;
    }
    this.getValue = function() {
        return {
            dstURL : this.dstURL,
            kind: this.kind,
            enable : this.enable    
        }; 
    }
    this.getKindLabel = function() {
        if (this.kind === REGEXP) {
            return "正则式";
        } else {
            return "通配符";
        }
    }
    this.getSrcURLLabel = function() {
        var replaceWildcard = function(url) {
            //js不支持look-behind，所以这里采用将字符串倒转，之后采用look-ahead方式
            //这里需要将*与?替换为.*与.?，而\*与\?保留不变
            var reverse = function(str) {
                return str.split("").reverse().join("");
            };
            var reversedUrl = reverse(url);
            return reverse(reversedUrl.replace(/([\*|\?])(?!\\)/g,"$1."));
        };
        if (this.kind == "wildcard" && this.srcURL.match(/\.(\*|\?)/g)) {
            return this.srcURL.replace(/\.(\*|\?)/g, "$1");
        } else {
            return this.srcURL;
        }
    }
}

var gooDB = new (function () {
    var RULES_KEY      = "rules",
        ISREDIRECT_KEY = "isRedirect",
        rules          = {
            'ajax.googleapis.com': {dstURL: 'ajax.lug.ustc.edu.cn', enable: true},        
            'fonts.googleapis.com': {dstURL:'fonts.lug.ustc.edu.cn', enable: true},        
            'themes.googleusercontent.com': {dstURL:'google-themes.lug.ustc.edu.cn', enable: true},
            'fonts.gstatic.com': {dstURL:'fonts-gstatic.lug.ustc.edu.cn', enable: true},
            'http.*://platform.twitter.com/widgets.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/widgets.js', enable: true},
            'http.*://apis.google.com/js/api.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/api.js', enable: true},
            'http.*://apis.google.com/js/plusone.js': {dstURL: 'https://raw.githubusercontent.com/jiacai2050/gooreplacer/gh-pages/proxy/plusone.js', enable: true}
        };
    this.init = function() {
        if(!localStorage.getItem(RULES_KEY)) {
            localStorage.setItem(RULES_KEY, JSON.stringify(rules));
        }
        if(!localStorage.getItem(ISREDIRECT_KEY)) {
            localStorage.setItem(ISREDIRECT_KEY, true);
        }
    }
    this.getRules = function() {
        return JSON.parse(localStorage.getItem(RULES_KEY));
    }
    this.setRules = function(rules) {
        return localStorage.setItem(RULES_KEY, JSON.stringify(rules));
    }
    this.addRule = function(rule) {
        var rules = this.getRules();
        rules[rule.getKey()] = rule.getValue();
        return localStorage.setItem(RULES_KEY, JSON.stringify(rules));
    }
    this.deleteRule = function(ruleKey) {
        var rules = this.getRules();
        delete rules[ruleKey];
        this.setRules(rules);
    }
    this.updateRule = function(srcURL, rule) {
        var rules = this.getRules();
        delete rules[srcURL];
        rules[rule.getKey()] = rule.getValue();
        this.setRules(rules);
    }
    this.toggleRule = function(ruleKey) {
        var rules = this.getRules();
        rules[ruleKey]["enable"] = ! rules[ruleKey]["enable"];
        this.setRules(rules);
        return rules[ruleKey]["enable"];
    }
    this.getIsRedirect = function() {
        return JSON.parse(localStorage.getItem(ISREDIRECT_KEY));
    }
    this.setIsRedirect = function(isRedirect) {
        return localStorage.setItem(ISREDIRECT_KEY, isRedirect);
    }
});