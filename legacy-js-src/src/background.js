gooDB.init();

function findRedirectUrl(requestURL, gooRules) {
    for (var i = 0; i < gooRules.length; i++) {
        var gooRule = gooRules[i];
        var redirectRE = new RegExp(gooRule.srcURL);
        if (gooRule.enable) {
            var redirectMatch = redirectRE.exec(requestURL);
            if (redirectMatch) {
                var redirectURL = "";
                if (gooRule.isWildcard()) { // kind 默认为wildcard
                    redirectURL = requestURL.replace(redirectRE, gooRule.dstURL);
                } else {
                    redirectURL = requestURL.replace(redirectMatch[0], gooRule.dstURL);
                    redirectMatch = redirectMatch.splice(1);
                    for (var i = 0; i < redirectMatch.length; i++) {
                        redirectURL = redirectURL.replace("$" + (i + 1), redirectMatch[i]);
                    }
                }
                return redirectURL;
            }
        }
    }
}
chrome.webRequest.onBeforeRequest.addListener(
    function(request) {
        var requestURL = request.url,
            isRedirect = gooDB.getIsRedirect();
        if (isRedirect) {
            // 1. 检查本地自定义规则
            var gooRules = gooDB.getRules();
            var redirectURL = findRedirectUrl(requestURL, gooRules);
            if (redirectURL) {
                return {
                    redirectUrl: redirectURL
                }
            }
            var onlineURL = gooDB.getOnlineURL();
            // 2. 检查在线URL所定义规则
            if (onlineURL.enable) {
                var gooRules = gooDB.getRules("onlineRules");
                var redirectURL = findRedirectUrl(requestURL, gooRules);
                if (redirectURL) {
                    return {
                        redirectUrl: redirectURL
                    }
                }
            }
        }
    },
    // filters
    {
        urls: ["<all_urls>"]
    },
    // extraInfoSpec
    ["blocking"]
);
