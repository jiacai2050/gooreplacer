gooDB.init();
chrome.webRequest.onBeforeRequest.addListener(
    function(request) {
        var requestUrl = request.url;
        var isRedirect= gooDB.getIsRedirect();
        var gooRules = gooDB.getRules();
        if(isRedirect) {
            for(var srcURL in gooRules) {
                var gooRule = gooRules[srcURL];
                var redirectRE = new RegExp(srcURL);
                if( gooRule.enable) {
                    var redirectMatch = redirectRE.exec(requestURL);
                    if (redirectMatch) {
                        var redirectURL = "";
                        if (gooRule.kind === "regexp") { // kind 默认为wildcard
                            redirectURL = requestURL.replace(redirectMatch[0], gooRule.dstURL);
                            redirectMatch = redirectMatch.splice(1);
                            for (var i = 0; i < redirectMatch.length; i++) {
                                redirectURL = redirectURL.replace("$" + (i+1), redirectMatch[i]);
                            };
                        } else {
                            redirectURL = requestURL.replace(redirectRE, gooRule.dstURL);
                        }
                        return {redirectUrl: redirectURL};
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