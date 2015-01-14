chrome.webRequest.onBeforeRequest.addListener(
    function(request) {
        var requestUrl = request.url;
        var newUrl = "";
        var found = false;
        var redirectRules = JSON.parse(localStorage.getItem('rules'));
        for(var key in redirectRules) {
            var redirectRE = new RegExp(key);
            if( redirectRules[key].enable && requestUrl.match(redirectRE)) {
                newUrl = requestUrl.replace(redirectRE, redirectRules[key].dstURL);    
                return {redirectUrl: newUrl};
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
