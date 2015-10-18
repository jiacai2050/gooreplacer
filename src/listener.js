chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        if (request.hasOwnProperty("gosetting")) {
            var isRedirect = request["gosetting"];
            if (!isRedirect) {
                if (confirm("如果想自定义规则，需先开启重定向。点击确定开启，并前往自定义页面")) {
                    chrome.tabs.create({
                        url: "data/options.html"
                    });
                    gooDB.setIsRedirect(true);
                    popup.enableIcon();
                }
            } else {
                chrome.tabs.create({
                    url: "data/options.html"
                });
            }
        } else if (request.hasOwnProperty("isRedirect")) {
            var isRedirect = request["isRedirect"];
            gooDB.setIsRedirect(isRedirect);
            if (isRedirect) {
                popup.enableIcon();
            } else {
                popup.disableIcon();
            }
        } else if (request.hasOwnProperty("onlineUpdate")) {
            fetchRules(function(ret) {
                sendResponse(ret);
            });
            // 返回 true，意味着 sendResponse 会被异步调用
            // https://developer.chrome.com/extensions/runtime#event-onMessage
            return true;
        } else if (request.hasOwnProperty("onlineSave")) {
            chrome.alarms.clear(alarmName, function(wasCleared) {
                if (wasCleared) {
                    chrome.alarms.create(alarmName, {
                        when: Date.now() + 5000,
                        periodInMinutes: parseInt(request["onlineSave"])
                    });
                    sendResponse({
                        msg: "设置成功！更新频率为：" + request["onlineSave"] + "分钟"
                    });
                } else {
                    chrome.alarms.get(alarmName, function(alarm) {
                        sendResponse({
                            msg: "设置失败！更新频率为：" + alarm.periodInMinutes + "分钟"
                        });
                    });
                }
            });
            return true;
        }
    });
