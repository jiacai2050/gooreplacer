chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        if(request.hasOwnProperty("gosetting")) {
            var isRedirect = request["gosetting"];
            if(!isRedirect) {
                if(confirm("如果想自定义规则，需先开启重定向。点击确定开启，并前往自定义页面")) {
                    chrome.tabs.create({url: "data/options.html"});
                    localStorage.setItem("isRedirect", true);
                    enableIcon();
                    chrome.runtime.sendMessage({init: true});
                } 
            }
            else {
                chrome.tabs.create({url: "data/options.html"});
            }
        } 
        else if (request.hasOwnProperty("isRedirect")) {
            var isRedirect = request["isRedirect"];
            if(isRedirect) {
                enableIcon();
            } else {
                disableIcon();
            }
        }
    }
);
var isRedirect = JSON.parse(localStorage.getItem("isRedirect"));
if(isRedirect) {
    enableIcon();
} else {
    disableIcon();
}
function enableIcon() {
    chrome.browserAction.setIcon({
        path: {
            "19": "data/img/19.png",
            "38": "data/img/38.png"
        }
    });  
}
function disableIcon() {
    chrome.browserAction.setIcon({
        path: {
            "19": "data/img/19-off.png",
            "38": "data/img/38-off.png"
        }
    });  
}
