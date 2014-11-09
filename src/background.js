
chrome.webRequest.onBeforeRequest.addListener(
    function(request) {
        var requestUrl = request.url;
        chrome.storage.sync.get('rules', function(pref) {

            var redirectRules = pref;

            for(var key in redirectRules) {
                var redirectRE = new RegExp(key);
                requestUrl.match(redirectRE);
                if( redirectRules[key].enable && requestUrl.match(redirectRE)) {
                    var redirectUrl = requestUrl.replace(redirectRE, redirectRules[key].dstURL);    
                    return {redirectUrl: newUrl};
                }
            }
        });
    },
  // filters
  {
     urls : ["<all_urls>"]
  },
  // extraInfoSpec
  ["blocking"]);

chrome.storage.onChanged.addListener(function(changes, namespace) {
    for (key in changes) {
        if (key == 'rules') {
            var storageChange = changes[key];
            console.log('Storage key "%s" in namespace "%s" changed. ' +
                  'Old value was "%s", new value is "%s".',
                  key,
                  namespace,
                  storageChange.oldValue,
                  storageChange.newValue);    

            break;
        };
        
    }
    alert("ok");

});
    console.log(hello);