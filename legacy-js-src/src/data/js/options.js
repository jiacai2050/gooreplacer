var fileUtil = new (function() {
    this.exportRules = function() {
        var gooRules = gooDB.getRules();
        var jsonRules = {};
        for (var i = 0; i < gooRules.length; i++) {
            var gooRule = gooRules[i];
            jsonRules[gooRule.getSrcURLLabel()] = gooRule.getValue();
        }
        var gson = {
            createBy: "http://liujiacai.net/gooreplacer/",
            createAt: new Date().toString(),
            rules: jsonRules
        };
        var contentType = 'application/json';
        var content = new Blob([JSON.stringify(gson, null, 4)], {type: contentType});

        var gsonExport = document.getElementById("gsonExport");
        gsonExport.href = window.URL.createObjectURL(content);
        gsonExport.click();
    };
    this.importRules = function() {
        var gsonChooser = document.getElementById("gsonChooser");
        gsonChooser.value = "";
        gsonChooser.onchange = function() {
            var files = this.files;
            var gsonFile = files[0];
            var reader = new FileReader();
            reader.onloadend = function(response) {
                var res = JSON.parse(response.target.result);
                var jsonRules = res.rules;
                for(var key in jsonRules) {
                    gooDB.addRule(new GooRule(key, jsonRules[key]));
                }
                initRules();
            };
            reader.readAsText(gsonFile);
        }
        gsonChooser.click();
    }
});

var gooRuleDAO = new(function() {
    var dao = this;

    this["delete"] = function(e) {
       if (confirm("确定要删除这条规则吗？")) {
            var par = $(e.target).parents("tr"); //tr
            var tdSrcURL  = par.children("td:nth-child(1)");
            var ruleKey = tdSrcURL.children("input[type=hidden]").val();
            gooDB.deleteRule(ruleKey);
            par.remove();
        }
    };
    this.toggle = function(e) {
        var toggleBtn = $(e.target);
        var par = toggleBtn.parents("tr"); //tr
        var tdSrcURL  = par.children("td:nth-child(1)");
        var ruleKey   = tdSrcURL.children("input[type=hidden]").val();
        var enable = gooDB.toggleRule(ruleKey);
        par.toggleClass("disable", ! enable);
        if (enable) {
            toggleBtn.after(imageUtil.rule_disable);
        } else {
            toggleBtn.after(imageUtil.rule_enable);
        }
        toggleBtn.remove();
        imageUtil.bindClick("rule_enable");
    }
    this.edit = function(e) {
        var td = $(e.target).parents("td"); //td
        var par = td.parent(); //tr
        var tdSrcURL  = par.children("td:nth-child(1)"),
            tdKind    = par.children("td:nth-child(2)"),
            tdDstURL  = par.children("td:nth-child(3)"),
            tdButtons = par.children("td:nth-child(4)");
        var tdSrcURLOldHtml = tdSrcURL.html(),
            tdKindOldHtml   = tdKind.html(),
            tdDstURLOldHtml = tdDstURL.html(),
            tdButtonsOldHtml= tdButtons.html();
        var srcURLVal = tdSrcURL.children("span").html(),
            ruleKey   = tdSrcURL.children("input[type=hidden]").val(),
            kindVal   = tdKind.children("input[type=hidden]").val(),
            dstURLVal = tdDstURL.children("span").html();

        tdSrcURL.html("<input class='form-control' spellcheck='false' type='text' size='" + srcURLVal.length + "' value='" + srcURLVal + "'/>");
        var select = "<select class='form-control'><option value='wildcard' selected>通配符</option><option value='regexp'>正则式</option></select>";
        if (kindVal === "regexp") {
            select = "<select class='form-control'><option value='wildcard'>通配符</option><option value='regexp' selected>正则式</option></select>";
        };
        tdKind.html(select);
        tdDstURL.html("<input class='form-control' spellcheck='false' type='text' size='" + dstURLVal.length + "' value='" + dstURLVal + "'/>");

        td.html(imageUtil.save + imageUtil.undo);
        imageUtil.bindClick("save", function(e) {
            gooDB.deleteRule(ruleKey);
            gooRuleDAO.save(e);
        });
        imageUtil.bindClick("undo", function() {
            tdSrcURL.html(tdSrcURLOldHtml);
            tdKind.html(tdKindOldHtml);
            tdDstURL.html(tdDstURLOldHtml);
            tdButtons.html(tdButtonsOldHtml);

            imageUtil.bindClick("rule_enable");
            imageUtil.bindClick("edit");
            imageUtil.bindClick("delete");
        });
        addEnterListener();
    }
    this.save = function(e) {
        var par = $(e.target).parents("tr"); //tr
        var tdSrcURL  = par.children("td:nth-child(1)"),
            tdKind    = par.children("td:nth-child(2)"),
            tdDstURL  = par.children("td:nth-child(3)"),
            tdButtons = par.children("td:nth-child(4)");
        var srcURLVal = tdSrcURL.children("input[type=text]").val(),
            kindVal   = tdKind.children("select").val(),
            dstURLVal = tdDstURL.children("input[type=text]").val();

        if (srcURLVal.trim() === "") {
            alert("原始URL不能为空！");
            tdSrcURL.children("input[type=text]").val("");
            return false;
        };
        if (dstURLVal.trim() === "") {
            alert("目的URL不能为空！");
            tdDstURL.children("input[type=text]").val("");
            return false;
        };
        var gooRule = new GooRule(srcURLVal, {
            dstURL: dstURLVal,
            kind  : kindVal,
            enable: true
        });
        gooDB.addRule(gooRule);
        tdSrcURL.html("<span>" + gooRule.getSrcURLLabel() + "</span><input type='hidden' value='" + gooRule.getKey() + "'/>");
        tdKind.html("<span>" + gooRule.getKindLabel() + "</span><input type='hidden' value='" + gooRule.kind + "'/>");
        tdDstURL.html("<span>" + gooRule.dstURL + "</span>");
        tdButtons.html("<span>" + imageUtil.rule_disable +  imageUtil.edit + imageUtil["delete"] + "</span>");

        imageUtil.bindClick("rule_enable");
        imageUtil.bindClick("edit");
        imageUtil.bindClick("delete");
    }
});

var imageUtil = new (function() {
    var assets = {
        edit: {
            src: "img/edit.png",
               glyphicon:"glyphicon-pencil",
               color:"gold",
            title: "编辑",
            "class": "btnEdit",
            onclick: gooRuleDAO.edit
        },
        save: {
            src: "img/save.png",
               glyphicon:"glyphicon-floppy-disk",
               color:"purple",
            title: "保存",
            "class": "btnSave",
            onclick: gooRuleDAO.save
        },
        undo: {
            src: "img/undo.png",
               glyphicon:"glyphicon-share-alt",
               color:"royalblue",
            title: "取消",
            "class": "btnUndo"
        },
        "delete": {
            src: "img/delete.png",
               glyphicon:"glyphicon-trash",
            title: "删除",
            "class": "btnDelete",
            onclick: gooRuleDAO["delete"]
        },
        rule_enable: {
            src: "img/rule_enable.png",
               glyphicon:"glyphicon-ok",
               color:"green",
            title: "开启",
            "class": "btnToggle",
            onclick: gooRuleDAO.toggle
        },
        rule_disable: {
            src: "img/rule_disable.png",
               glyphicon:"glyphicon-ban-circle",
               color:"red",
            title: "禁用",
            "class": "btnToggle",
            onclick: gooRuleDAO.toggle
        }
    };
    var getGlyphiconByName = function(name) {
        var res = assets[name];
          return '<span title="'+res.title+'" class="'+res["class"]+' glyphicon '+res.glyphicon+'" style="font-size:1.5em;text-shadow:0 0 2px gray;color:'+(res.color?res.color:"black")+'"></span>'
        //return "<img class='" + res["class"] + "' src='" + res.src + "' title='" + res.title + "' style='cursor: pointer;'/>";
    }
    this.edit = getGlyphiconByName("edit");
    this.save = getGlyphiconByName("save");
    this["delete"] = getGlyphiconByName("delete");
    this.undo = getGlyphiconByName("undo");
    this.rule_enable = getGlyphiconByName("rule_enable");
    this.rule_disable = getGlyphiconByName("rule_disable");
    this.bindClick = function(name, cb) {
        var cls = $("." + assets[name]["class"]);
        cb = cb || assets[name].onclick || function() {};
        // http://stackoverflow.com/questions/203198/event-binding-on-dynamically-created-elements
        cls.unbind("click");
        cls.bind("click", cb);
    }
});
var addEnterListener = function() {
    $("table#rules input[type=text]").keyup(function(e){
        if(e.keyCode == 13){  //Enter键
            var par = $(e.target).parents("tr"); //tr
            var tdButtons = par.children("td:nth-child(4)");
            tdButtons.children("span").click();
        }
    });
}
var addRow = function() {
    var rowHTML = ["<tr>",
        "<td><input class='form-control' spellcheck='false' type='text'/></td>",
        "<td><select class='form-control'><option value='wildcard'>通配符</option><option value='regexp'>正则式</option></td>",
        "<td><input class='form-control' spellcheck='false' type='text'/></td>",
        "<td>" + imageUtil.save + "</td>",
        "</tr>"].join("");
    $("#rules tbody").append(rowHTML);
    imageUtil.bindClick("save");
    addEnterListener();
     scrollTo(0,document.body.scrollHeight);
}
$(function() {
    $("#homepage").click(function() {
        window.open("http://liujiacai.net/gooreplacer/");
    });
     $("#openURL").click(function() {
          open($("#onlineURL").val());
     });
     $("#reset").click(function() {
          if(confirm("确定重置在线规则吗？")){
               gooDB.resetOnline();
               readOnline();
          }
     });
    $("#import").click(function() {
        fileUtil.importRules();
    });
    $("#export").click(function() {
        fileUtil.exportRules();
    });
    $('#close').click(function() {
        $('#config').hide();
    });
    $("#add").click(addRow);
     $("#removeAll").click(function(){
          if(confirm("确定要删除所有本地规则吗？")){
               gooDB.deleteRule();
               location.reload();
          }
     });

    readOnline();
    $("#onlineSave").click(function() {
        var url = $("#onlineURL").val();
        var interval = $("#onlineInterval").val();
        var enable = document.getElementById("onlineEnable").checked;
        gooDB.setOnlineURL(new GooOnlineURL(url, interval, enable));
        chrome.runtime.sendMessage({onlineSave: interval}, function(response) {
            alert(response.msg);
        });
    });
    $("#onlineUpdate").click(function() {
        $("#loadingScreen").dialog('option', 'title', '规则加载中，请稍等.....');
        $("#loadingScreen").dialog('open');
        chrome.runtime.sendMessage({onlineUpdate: "update"}, function(response) {
            if (response.isOK) {
                var updateTime = new Date(response.updateTime).toLocaleString();
                $("#lastUpdateTime").html(updateTime);
            }
            $("#loadingScreen").dialog('close');
            alert(response.msg);
        });
    });

    initRules();

    // create the loading window and set autoOpen to false
    $("#loadingScreen").dialog({
        autoOpen: false,    // set this to false so we can manually open it
        dialogClass: "loadingScreenWindow",
        closeOnEscape: false,
        draggable: false,
        minHeight: 150,
        modal: true,
        buttons: {},
        resizable: false,
        open: function() {
            // scrollbar fix for IE
            $('body').css('overflow','hidden');
        },
        close: function() {
            // reset overflow
            $('body').css('overflow','auto');
        }
    }); // end of dialog
});
function readOnline(){
    var onlineURL = gooDB.getOnlineURL();
    $("#onlineURL").val(onlineURL.url);
    $("#onlineInterval").val(onlineURL.interval);
    document.getElementById("onlineEnable").checked = onlineURL.enable;
    var d = new Date(gooDB.getLastUpdateTime());
    $("#lastUpdateTime").html(d.toLocaleString());
}
function initRules() {
    $("#rules tbody").html("");
    var gooRules = gooDB.getRules();
    for (var i = 0; i < gooRules.length; i++) {
        var gooRule = gooRules[i];
        var srcURLLabel = gooRule.getSrcURLLabel(),
            enable      = gooRule.enable,
            kindLabel   = gooRule.getKindLabel(),
            dstURL      = gooRule.dstURL;

        var rowHTML = [];
        var ruleStatus = "";
        if (gooRule.enable) {
            rowHTML.push("<tr>");
            ruleStatus = "rule_disable";
        } else {
            rowHTML.push("<tr class='disable'>");
            ruleStatus = "rule_enable";
        }
        rowHTML.push(
            "<td><span>" + srcURLLabel + "</span><input type='hidden' value='" + srcURLLabel + "'/></td>",
            "<td><span>" + kindLabel + "</span><input type='hidden' value='" + gooRule.kind + "'/></td>",
            "<td><span>" + dstURL + "</span></td>",
            "<td><span>" + imageUtil[ruleStatus] + imageUtil.edit + imageUtil["delete"] + "</span></td>",
            "</tr>");
        $("#rules tbody").append(rowHTML.join(""));

        imageUtil.bindClick(ruleStatus);
        imageUtil.bindClick("edit");
        imageUtil.bindClick("delete");
    }
};
