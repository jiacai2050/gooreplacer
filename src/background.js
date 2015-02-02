chrome.webRequest.onBeforeRequest.addListener(
    function(request) {
        var requestUrl = request.url;
        var isRedirect= JSON.parse(localStorage.getItem("isRedirect"));
        //var redirectRules = JSON.parse(localStorage.getItem('rules'));
        if(isRedirect) {
            //for(var key in redirectRules) {
            //    var redirectRE = new RegExp(key);
            //    if( redirectRules[key].enable && requestUrl.match(redirectRE)) {
            //        newUrl = requestUrl.replace(redirectRE, redirectRules[key].dstURL);    
            //        return {redirectUrl: newUrl};
            //    }
            //}
            L=0;
			for(i in window.localStorage){
				if (localStorage[i].match('dst')){
					//console.log(i);
					var reg = new  RegExp(i);
					//console.log(reg);
					matchURL=reg.exec(requestUrl);
					if(matchURL){
						//console.log('match!!');
						var LL=matchURL[0].length;
						if(L<LL){
							L=LL;
							var M=i;
							//console.log("M",M);
							R=reg;
						}
						//return
					}
				}
			}
			//console.log(url.replace(R,JSON.parse(localStorage[M])['dstURL']));
            //alert(requestUrl.replace(R,JSON.parse(localStorage[M])['dstURL']))
			return requestUrl.replace(R,JSON.parse(localStorage[M])['dstURL'])
        }
    },
    // filters
    {
        urls: ["<all_urls>"]
    },
    // extraInfoSpec
    ["blocking"]
);
