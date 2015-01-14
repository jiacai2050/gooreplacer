chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    console.log(sender.tab ?
            "from a content script:" + sender.tab.url :
            "from the extension");
    if (request.action == "export") {
        exportRules();
        sendResponse({farewell: "goodbye"});
    } else if (request.action == "import") {
        importRules();
        sendResponse({farewell: "goodbye"});
    };
});
function exportRules() {
    chrome.fileSystem.chooseEntry({type:"openFile"}, function(fileEntry, entries) {
        chrome.fileSystem.getDisplayPath(fileEntry, function(path) {
            alert(path);
        });

    });

}
function importRules() {

}
