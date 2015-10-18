var alarmName = "updateOnlineRules",
       online = gooDB.getOnlineURL();
    onlineURL = online.url;
     interval = online.interval;
chrome.alarms.create(alarmName, {
    when: Date.now() + 5000,
    periodInMinutes: parseInt(interval)
});
function fetchRules(cb) {
    cb = cb || function () {};
    if (onlineURL.trim() !== "") {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", onlineURL, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    var jsonRules = JSON.parse(xhr.responseText).rules;
                    var db = "onlineRules";
                    gooDB.deleteRule(null, db);
                    for(var key in jsonRules) {
                        gooDB.addRule(new GooRule(key, jsonRules[key]), db);
                    }
                    var now = Date.now();
                    gooDB.setLastUpdateTime(now);    
                    cb({code: 0, msg: "更新成功！", updateTime: now});
                } else {
                    cb({code: xhr.status, msg: "更新失败！Status Code: " + xhr.status});
                }
            }
        }
        xhr.send();    
    };
}
chrome.alarms.onAlarm.addListener(function (alarm) {
    if(alarmName === alarm.name) {     
        fetchRules();
    }
});