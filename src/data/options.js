function getLabel(status) {
    if (status == "true") {
        return "停用";
    } else {
        return "开启";
    }
}
function reset(rules) {
    $("#list").html("");
    var html = [];
    for(key in rules) {
        var srcURL = key;
        var rowHTML = [];
        if (rules[key].enable) {
            rowHTML.push("<tr>");
        } else {
            rowHTML.push("<tr class='disable'>");
        }
        var asteriskRE = /\.\*/g;
        if (key.match(asteriskRE)) {
            srcURL = key.replace(asteriskRE, "*");
        };
        rowHTML.push(
            "<td>"+srcURL+"</td>",
            "<td>----></td>",
            "<td>"+rules[key].dstURL+"</td>",
            "<td><input type=button id=change"+key+" value="+rules[key].enable+"></td>",
            "<td><input type=button id=del"+key+" value='删除'></td>",
            "</tr>");

        $("#list").append(rowHTML.join(""));
    }

    $("input[id^=del]").each(function() {
        var key = this.id.substring(3);
        this.onclick = function() {
            if (confirm("确定要删除这条规则吗？")) {
                chrome.storage.sync.get("rules", function(prefs) {
                    var rules = prefs.rules;
                    delete rules[key];
                    chrome.storage.sync.set({"rules": rules}, function() {
                        localStorage.setItem('rules',JSON.stringify(rules));
                        reset(rules); 
                    });
                }); 
            };
        }
    });    
    $("input[id^=change]").each(function() {
        var key = this.id.substring(6);
        this.value = getLabel(this.value);
        this.onclick = function() {
            chrome.storage.sync.get("rules", function(prefs) {
                var rules = prefs.rules;
                rules[key]["enable"] = !rules[key]["enable"];
                chrome.storage.sync.set({"rules": rules}, function() {
                    localStorage.setItem('rules',JSON.stringify(rules));
                    reset(rules); 
                });
            }); 
        }
    });
}


$(function() {

    $("#ok").click(function() {
        var rules = {};
        var number = 0;
        $("input[id^=srcURL]").each(function() {
            if(this.value.trim() !== "") {
                var dstURL = this.id.replace("srcURL", "dstURL");
                var value = $("#" + dstURL).val();
                rules[this.value] = {
                    dstURL : value,
                    enable : true
                };

                this.value = "";
                $("#" + dstURL).val("");
                number += 1;
            }
        });
        chrome.storage.sync.get('rules', function(prefs) {
            var newRules = $.extend(prefs.rules, rules);
            chrome.storage.sync.set({
                rules: newRules 
            }, function() {
                alert("成功添加" + number + "个规则");    
                localStorage.setItem('rules',JSON.stringify(newRules));
                reset(newRules);
            }); 

        });

        
    });  
    chrome.storage.sync.get("rules", function(prefs) {
        reset(prefs.rules); 
    }); 
    $("#more").click(function() {
        addRows();
    }); 
    addRows();     
    $("#export").click(function() {
    //https://developer.chrome.com/apps/app_storage
    });
});
var total=0;
var addRows = function() {
    var addLimit = 5 + total;
    while(total < addLimit) {
        
        var rowHTML = ["<tr>",
            "<td><input id='srcURL"+total+"'></td>",
            "<td>----></td>",
            "<td><input id='dstURL"+total+"'></td>",
            "</tr>"].join("");
        $("#rules").append(rowHTML);

        total+=1;
    } 
}

