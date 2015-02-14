function getLabel(status) {
    if (status == "true") {
        return "停用";
    } else {
        return "开启";
    }
}
var getKind = function(kind) {
    if (kind == "wildcard") {
        return "通配符";
    } else {
        return "正则式";
    }
}
var replaceWildcard = function(url) {
    //js不支持look-behind，所以这里采用将字符串倒转，之后采用look-ahead方式
    //这里需要将*与?替换为.*与.?，而\*与\?保留不变
    var reverse = function(str) {
        return str.split("").reverse().join("");
    };
    var reversedUrl = reverse(url);
 
    return reverse(reversedUrl.replace(/([\*|\?])(?!\\)/g,"$1."));
}
var db = new DB();
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
        var srcURL = key;
        var kind = rules[key].kind || "wildcard";
        
        if (kind == "wildcard" && key.match(/\.(\*|\?)/g)) {
            srcURL = key.replace(/\.(\*|\?)/g, "$1");
        };
        rowHTML.push(
            "<td >"+srcURL+"</td>",
            "<td>---->"+getKind(kind)+"---></td>",
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
                var rules = db.getRules();
                delete rules[key];
                db.setRules(rules);
                reset(rules); 
            }; 
        }
    });    
    $("input[id^=change]").each(function() {
        var key = this.id.substring(6);
        this.value = getLabel(this.value);
        this.onclick = function() {
            var rules = db.getRules();
            rules[key]["enable"] = !rules[key]["enable"];
            db.setRules(rules);
            reset(rules); 
        }; 
    });
}


$(function() {
    $("#homepage").click(function() {
        window.open("http://liujiacai.net/gooreplacer/");
    });
    $("#import").click(function() {
        importRules();
    });
    $("#export").click(function() {
        exportRules();
    });
    $("#help").click(function() {
        jQuery.fn.center = function () {
            this.css("position","absolute");
            this.css("top", (($(window).height() - this.outerHeight()) / 2) + $(window).scrollTop() + "px");
            this.css("left", (($(window).width() - this.outerWidth()) / 2) + $(window).scrollLeft() + "px");
            return this;
        }
        $('#config').center().css('top', '-=40px').show();
    });
    $('#close').click(function() {
        $('#config').hide();
    });
    
    $("#ok").click(function() {
        var rules = {};
        var number = 0;
        $("input[id^=srcURL]").each(function() {
            var srcUrlValue = this.value.trim();
            if(srcUrlValue !== "") {
                var dstUrlTag = this.id.replace("srcURL", "dstURL");
                var dstUrlValue = $("#" + dstUrlTag).val().trim();

                var kindTag = this.id.replace("srcURL", "kind");
                var kindValue = $("#" + kindTag).val();

                if (kindValue == "wildcard") {
                    srcUrlValue = replaceWildcard(srcUrlValue);
                };

                rules[srcUrlValue] = {
                    dstURL : dstUrlValue,
                    kind: kindValue,
                    enable : true
                };

                this.value = "";
                $("#" + kindTag).attr("value",'wildcard');
                $("#" + dstUrlTag).val("");
                number += 1;
            }
        });
        var oldRules = db.getRules();
        var newRules = $.extend(oldRules, rules);
        db.setRules(newRules);
        alert("成功添加" + number + "个规则");    
        reset(newRules);
    });  
    reset(db.getRules()); 
    $("#more").click(function() {
        addRows();
    }); 
    addRows();     
});
var total=0;
var addRows = function() {
    var addLimit = 5 + total;
    while(total < addLimit) {
        
        var rowHTML = ["<tr>",
            "<td><input type='text' id='srcURL"+total+"'></td>",
            "<td><select id='kind"+total+"'><option value='wildcard'>通配符</option><option value='regexp'>正则式</option></td>",
            "<td><input type='text' id='dstURL"+total+"'></td>",

            "</tr>"].join("");
        $("#rules").append(rowHTML);

        total+=1;
    }  
}
function exportRules() {
    var rules = db.getRules();
    var gson = {
        createBy: "http://liujiacai.net/gooreplacer/",
        createAt: new Date().toString(),
        rules: rules
    };
    var contentType = 'application/json';
    var content = new Blob([JSON.stringify(gson, null, 4)], {type: contentType});

    var gsonExport = document.getElementById("gsonExport");
    gsonExport.href = window.URL.createObjectURL(content);
    gsonExport.click();
}
function importRules() {
    var gsonChooser = document.getElementById("gsonChooser");
    gsonChooser.value = "";
    gsonChooser.onchange = function() {
        var files = this.files;
        var gsonFile = files[0];
        var reader = new FileReader();
        reader.onloadend = function(response) {
            var res = JSON.parse(response.target.result);
            var newRules = $.extend(db.getRules(), res.rules);
            db.setRules(newRules);
            reset(newRules);
        };
        reader.readAsText(gsonFile);
    }
    gsonChooser.click();
}
