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