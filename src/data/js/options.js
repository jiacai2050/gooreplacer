function getLabel(status) {
    if (status == "true") {
        return "停用";
    } else {
        return "开启";
    }
}
$(function(){
	var debug = false;
	if (debug){
		$("#debug").show()
	}else{
		$("#debug").hide()
	}
	$('#testButton').click(function (){
		console.log($('#test').val())
		var url = $('#test').val()
		var L = 0;
		for(i in window.localStorage){
			if (localStorage[i].match('dst')){
				//console.log(i);
				var reg = new  RegExp(i);
				//console.log(reg);
				matchURL=reg.exec(url);
				if(matchURL){
					console.log('match!!');
					var LL=matchURL[0].length;
					if(L<LL){
						L=LL;
						var M=i;
						console.log("M",M);
						R=reg;
					}
					//return
				}
			}
		}
		//console.log(JSON.parse(localStorage[M])['dstURL']);
		//console.log(url.replace(R,JSON.parse(localStorage[M])['dstURL']))
		console.log(url.replace(R,JSON.parse(localStorage[M])['dstURL']));
		return url.replace(R,JSON.parse(localStorage[M])['dstURL'])
	});
	$('#restoreDB').click(function (){
		localStorage.clear();
		var rules = {
    'ajax.googleapis.com': {dstURL: 'ajax.lug.ustc.edu.cn', enable: true},        
    'fonts.googleapis.com': {dstURL:'fonts.lug.ustc.edu.cn', enable: true},        
    'themes.googleusercontent.com': {dstURL:'google-themes.lug.ustc.edu.cn', enable: true},
    'fonts.gstatic.com': {dstURL:'fonts-gstatic.lug.ustc.edu.cn', enable: true},
    'http.*://platform.twitter.com/widgets.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/widgets.js', enable: true},
    'http.*://apis.google.com/js/api.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/api.js', enable: true},
    'http.*://apis.google.com/js/plusone.js': {dstURL: 'http://liujiacai.net/gooreplacer/proxy/plusone.js', enable: true}
};
if(!localStorage.getItem("rules")) {
	for(url in rules){
		localStorage.setItem(url,JSON.stringify(rules[url]))
	}
    localStorage.setItem("rules", JSON.stringify(rules));
}
if(!localStorage.getItem("isRedirect")) {
    localStorage.setItem("isRedirect", true);
}
	})
})
var db = new DB();
function reset(rules) {
    $("#list").html("<thead><td style='width: 40%'>源地址</td><td style='width: 40%'>转发地址</td><td style='width: 6%'>Button</td></thead><tbody>");
    var html = [];
    for (key in rules) {
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
        }
        rowHTML.push(
            "<td><input class=shiftCheckbox type=checkbox value=" + key + "/>" + srcURL + "</td>",
//            "<td>----></td>",
            "<td>" + rules[key].dstURL + "</td>",
            "<td><input type=button id=change" + key + " value=" + rules[key].enable + "></td>",
            "<td><input type=button id=del" + key + " value='删除'></td>",
            "</tr>");

        $("#list").append(rowHTML.join(""));
    }
    $("#list").append("</tbody>");
    $("input[id^=del]").each(function () {
        var key = this.id.substring(3);
        this.onclick = function () {
            if (confirm("确定要删除这条规则吗？")) {
                var rules = db.getRules();
                delete rules[key];
				localStorage.removeItem(key);
                db.setRules(rules);
                reset(rules);
            }
        }
    });
    $("input[id^=change]").each(function () {
        var key = this.id.substring(6);
        this.value = getLabel(this.value);
        this.onclick = function () {
            var rules = db.getRules();
            rules[key]["enable"] = !rules[key]["enable"];
            db.setRules(rules);
			localStorage[key]=JSON.stringify(rules[key]);
            reset(rules);
        };
    });

    $('.shiftCheckbox').shiftcheckbox();

    $("#shiftDel").click(function () {
            for (key in rules) {
                if ($("input[value='" + key + "/']").get(0).checked) {
                    delete rules[key];
                }
            }
            db.setRules(rules);
            reset(rules);
        }
    )
}


$(function () {
    $("#homepage").click(function () {
        window.open("http://liujiacai.net/gooreplacer/");
    });
    $("#import").click(function () {
        importRules();
    });
    $("#export").click(function () {
        exportRules();
    });
    $("#help").click(function () {
        jQuery.fn.center = function () {
            return this;
        };
        $('#config').show();
    });
    $('#close').click(function () {
        $('#config').hide();
    });

    $("#ok").click(function () {
        var rules = {};
        var number = 0;
        $("input[id^=srcURL]").each(function () {
            if (this.value.trim() !== "") {
                var dstURL = this.id.replace("srcURL", "dstURL");
                var value = $("#" + dstURL).val();
                rules[this.value] = {
                    dstURL: value,
                    enable: true
                };
				localStorage.setItem(this.value, JSON.stringify(rules[this.value])); 
                this.value = "";
                $("#" + dstURL).val("");
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
    $("#more").click(function () {
        addRows();
    });
    addRows();
});
var total = 0;
var addRows = function () {
    var addLimit = 5 + total;
    while (total < addLimit) {

        var rowHTML = ["<tr>",
            "<td><input type='text' id='srcURL" + total + "'></td>",
            "<td>----></td>",
            "<td><input type='text' id='dstURL" + total + "'></td>",
            "</tr>"].join("");
        $("#rules").append(rowHTML);

        total += 1;
    }
   // $('input[type="text"]').blur(function () {
   //     var val = this.value.trim();
   //     var stopwords = [
   //         "\\(",
   //         "\\)",
   //         "\\[",
   //         "\\]",
   //         "\\{",
   //         "\\}",
   //         "\\?",
   //         "\\\\",
   //         "\\+"
   //     ].join("|");
   //     var keywordsRE = new RegExp(stopwords, 'g');
   //     if (val.match(keywordsRE)) {
   //         alert("URL中不能包含 (, ), [, ], {, }, ?, \\, + 这些特殊字符！");
   //         this.value = "";
   //         $(this).focus();
   //         return false;
   //     }
   // });
};
function exportRules() {
    var rules = db.getRules();
    var gson = {
        createBy: "http://liujiacai.net/gooreplacer/",
        createAt: new Date().toString(),
        rules: rules
    };
    var contentType = 'application/json';
    var content = new Blob([JSON.stringify(gson)], {type: contentType});

    var gsonExport = document.getElementById("gsonExport");
    gsonExport.href = window.URL.createObjectURL(content);
    gsonExport.click();
}
function importRules() {
    var gsonChooser = document.getElementById("gsonChooser");
    gsonChooser.value = "";
    gsonChooser.onchange = function () {
        var files = this.files;
        var gsonFile = files[0];
        var reader = new FileReader();
        reader.onloadend = function (response) {
			
	for(url in response.target.result){
		localStorage.setItem(url,JSON.stringify(response.target.result[url]))
	}
	
            var res = JSON.parse(response.target.result);
            var newRules = $.extend(db.getRules(), res.rules);
            db.setRules(newRules);
            reset(newRules);
        };
        reader.readAsText(gsonFile);
    };
    gsonChooser.click();
}