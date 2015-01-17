
$(function() {
    var toggle = $("#toggle");
    var gosetting = $("#gosetting");
    
    var db = new DB();

    init(db.getIsRedirect());

    gosetting.click(function() {
        chrome.runtime.sendMessage({gosetting: db.getIsRedirect()});
    });
    toggle.click(function() {
        db.setIsRedirect(this.checked);
        chrome.runtime.sendMessage({isRedirect: this.checked});
    });

});

function init(isRedirect) {
    $("#toggle").attr("checked", isRedirect);
}
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        if(request.hasOwnProperty("init")) {
            init(request["init"]);
        } 
    }
);
