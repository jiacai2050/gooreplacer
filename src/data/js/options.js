var fileUtil = new (function() {
    this.exportRules = function() {
        var rules = gooDB.getRules();
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
                var newRules = $.extend(gooDB.getRules(), res.rules);
                gooDB.setRules(newRules);
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
            var par = $(e.target).parent().parent(); //tr
            var tdSrcURL  = par.children("td:nth-child(1)");
            var ruleKey = tdSrcURL.children("input[type=hidden]").val();
            gooDB.deleteRule(ruleKey);
            par.remove();
        }
    };
    this.toggle = function(e) {
        var toggleBtn = $(e.target);
        var par = toggleBtn.parent().parent(); //tr
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
        var td = $(e.target).parent(); //td
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
            dstURLVal = tdDstURLOldHtml;

        tdSrcURL.html("<input type='text' size='" + srcURLVal.length + "' value='" + srcURLVal + "'/>");
        var select = "<select><option value='wildcard' selected>通配符</option><option value='regexp'>正则式</option></select>";
        if (kindVal === "regexp") {
            select = "<select><option value='wildcard'>通配符</option><option value='regexp' selected>正则式</option></select>";
        };
        tdKind.html(select);
        tdDstURL.html("<input type='text' size='" + dstURLVal.length + "' value='" + dstURLVal + "'/>");

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
        var par = $(e.target).parent().parent(); //tr
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
        tdDstURL.html(gooRule.dstURL);
        tdButtons.html(imageUtil.rule_disable +  imageUtil.edit + imageUtil["delete"]);

        imageUtil.bindClick("rule_enable");
        imageUtil.bindClick("edit");
        imageUtil.bindClick("delete");
    }
});

var imageUtil = new (function() {
    var assets = {
        edit: {
            src: "img/edit.png",
            title: "编辑",
            "class": "btnEdit",
            onclick: gooRuleDAO.edit
        },
        save: {
            src: "img/save.png",
            title: "保存",    
            "class": "btnSave",
            onclick: gooRuleDAO.save
        },
        undo: {
            src: "img/undo.png",
            title: "取消",
            "class": "btnUndo"
        },
        "delete": {
            src: "img/delete.png",
            title: "删除",
            "class": "btnDelete",
            onclick: gooRuleDAO["delete"]
        },
        rule_enable: {
            src: "img/rule_enable.png",
            title: "开启",
            "class": "btnToggle",
            onclick: gooRuleDAO.toggle
        },
        rule_disable: {
            src: "img/rule_disable.png",
            title: "禁用",
            "class": "btnToggle",
            onclick: gooRuleDAO.toggle
        }
    };
    var getImageElementByName = function(name) {
        var res = assets[name];
        return "<img class='" + res["class"] + "' src='" + res.src + "' title='" + res.title + "' style='cursor: pointer;'/>";   
    }
    this.edit = getImageElementByName("edit");
    this.save = getImageElementByName("save");
    this["delete"] = getImageElementByName("delete");
    this.undo = getImageElementByName("undo");
    this.rule_enable = getImageElementByName("rule_enable");
    this.rule_disable = getImageElementByName("rule_disable");
    this.bindClick = function(name, cb) {
        var cls = $("." + assets[name]["class"]);
        cb = cb || assets[name].onclick || function() {};
        // http://stackoverflow.com/questions/203198/event-binding-on-dynamically-created-elements
        cls.unbind("click");
        cls.bind("click", cb);
    }
});
var addEnterListener = function() {
    $("table.gridtable input[type=text]").keyup(function(e){
        if(e.keyCode == 13){  //Enter键
            var par = $(e.target).parent().parent(); //tr
            var tdButtons = par.children("td:nth-child(4)");
            tdButtons.children("img[class=btnSave]").click();
        }
    });
}
var addRow = function() {
    var rowHTML = ["<tr>",
        "<td><input type='text'/></td>",
        "<td><select><option value='wildcard'>通配符</option><option value='regexp'>正则式</option></td>",
        "<td><input type='text'/></td>",
        "<td>" + imageUtil.save + "</td>",
        "</tr>"].join("");
    $("#rules tbody").append(rowHTML);
    imageUtil.bindClick("save");
    addEnterListener();
}

$(function() {
    $("#homepage").click(function() {
        window.open("http://liujiacai.net/gooreplacer/");
    });
    $("#import").click(function() {
        fileUtil.importRules();
    });
    $("#export").click(function() {
        fileUtil.exportRules();
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
    $("#add").click(addRow);
    initRules();
});
function initRules() {
    rules = gooDB.getRules();
    var html = [];
    for(key in rules) {
        var gooRule = new GooRule(key, rules[key]);
        var srcURL      = gooRule.srcURL,
            srcURLLabel = gooRule.getSrcURLLabel(),
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
            "<td><span>" + srcURLLabel + "</span><input type='hidden' value='" + gooRule.getKey() + "'/></td>",
            "<td><span>" + kindLabel + "</span><input type='hidden' value='" + gooRule.kind + "'/></td>",
            "<td>" + dstURL + "</td>",
            "<td>" + imageUtil[ruleStatus] + imageUtil.edit + imageUtil["delete"] + "</td>",
            "</tr>");
        $("#rules tbody").append(rowHTML.join(""));
        
        imageUtil.bindClick(ruleStatus);
        imageUtil.bindClick("edit");
        imageUtil.bindClick("delete");
    }
};
