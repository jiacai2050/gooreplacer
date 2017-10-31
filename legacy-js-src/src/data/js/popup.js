$(function() {
    var toggle = $("#toggle");
    var gosetting = $("#gosetting");
    var header = $("header");

    toggle.attr("checked", gooDB.getIsRedirect());

    gosetting.click(function() {
        chrome.runtime.sendMessage({gosetting: gooDB.getIsRedirect()});
    });
    toggle.click(function() {
        chrome.runtime.sendMessage({isRedirect: this.checked});
    });
    header.click(function() {
        open("http://liujiacai.net/gooreplacer/");
    });
});
