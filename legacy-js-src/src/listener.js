chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
		// 直接返回 true，意味着 sendResponse 会被异步调用
		// https://developer.chrome.com/extensions/runtime#event-onMessage
        if (request.hasOwnProperty("gosetting")) {
            chrome.tabs.create({
                url: "data/options.html"
            });
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
				if (ret.code === 0) {
					sendResponse({
                        isOK: true,
                        updateTime: ret.updateTime,
                        msg: ret.msg
                    });
				} else {
					sendResponse({isOK: false, errCode: ret.code, msg: ret.msg});
				}
			});
            return true;
        } else if (request.hasOwnProperty("onlineSave")) {
            chrome.alarms.clear(alarmName, function(wasCleared) {
                var updateInterval = parseInt(request["onlineSave"]);
                if (updateInterval === 0) {
                    sendResponse({
                        msg: "关闭自动更新！"
                    });
                } else {
                    chrome.alarms.create(alarmName, {
                        when: Date.now() + 5000,
                        periodInMinutes: parseInt(request["onlineSave"])
                    });
                    sendResponse({
                        msg: "设置成功！更新频率为：" + request["onlineSave"] + "分钟"
                    });
                }

            });
            return true;
        }
    });
