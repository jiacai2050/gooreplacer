var popup = new (function() {
    this.init = function() {
        var isRedirect = gooDB.getIsRedirect();
        if(isRedirect) {
            this.enableIcon();
        } else {
            this.disableIcon();
        }    
    };
    this.enableIcon = function() {
        chrome.browserAction.setIcon({
            path: {
                "19": "data/img/19.png",
                "38": "data/img/38.png"
            }
        });  
    };
    this.disableIcon = function() {
        chrome.browserAction.setIcon({
            path: {
                "19": "data/img/19-off.png",
                "38": "data/img/38-off.png"
            }
        });  
    };
});
popup.init();
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        if(request.hasOwnProperty("gosetting")) {
            var isRedirect = request["gosetting"];
            if(!isRedirect) {
                if(confirm("如果想自定义规则，需先开启重定向。点击确定开启，并前往自定义页面")) {
                    chrome.tabs.create({url: "data/options.html"});
                    gooDB.setIsRedirect(true);
                    popup.enableIcon();
                } 
            }
            else {
                chrome.tabs.create({url: "data/options.html"});
            }
        } 
        else if (request.hasOwnProperty("isRedirect")) {
            var isRedirect = request["isRedirect"];
            gooDB.setIsRedirect(isRedirect);
            if(isRedirect) {
                popup.enableIcon();
            } else {
                popup.disableIcon();
            }
        }
    }
);