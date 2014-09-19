chrome.webRequest.onBeforeRequest.addListener(
  function(info) {
    var requestUrl = info.url;
    var redirectMap = {
        'googleapis.com': 'lug.ustc.edu.cn',        
        'themes.googleusercontent.com': 'google-themes.lug.ustc.edu.cn',
        'fonts.gstatic.com': 'fonts-gstatic.lug.ustc.edu.cn'   
    };
    for(var key in redirectMap) {
        var redirectReg = new RegExp(key);
        if( requestUrl.match(redirectReg) ) {
            var newUrl = requestUrl.replace(redirectReg, redirectMap[key]);    

            return {redirectUrl: newUrl};
         }
    }

  },
  // filters
  {
    urls: [
      "*://*.googleapis.com/*",
      "*://themes.googleusercontent.com/*",
      "*://fonts.gstatic.com/*"
    ],
  },
  // extraInfoSpec
  ["blocking"]);
