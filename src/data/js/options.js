function getLabel(status) {
    if (status == "true") {
        return "停用";
    } else {
        return "开启";
    }
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
        var asteriskRE = /\.\*/g;
        if (key.match(asteriskRE)) {
            srcURL = key.replace(asteriskRE, "*");
        };
        rowHTML.push(
            "<td><input class=shiftCheckbox type=checkbox value=" + key +"/>"+srcURL+"</td>",
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

    $('.shiftCheckbox').shiftcheckbox();

    $("#shiftDel").click(function() {
            for (key in rules) {
                if($("input[value='"+key+"/']").get(0).checked){
                    delete rules[key];
                }
            }
            db.setRules(rules);
            reset(rules);
        }
    )


//    liuzheng  20150122 https://editor.datatables.net/examples/api/checkbox.html
    editor = new $.fn.dataTable.Editor( {
        "ajax": "../php/checkbox.php",
        "table": "#example",
        "fields": [ {
                label:     "Active:",
                name:      "active",
                type:      "checkbox",
                separator: "|",
                options:   [
                    { label: '', value: 1 }
                ]
            }, {
                label: "First name:",
                name:  "first_name"
            }, {
                label: "Last name:",
                name:  "last_name"
            }, {
                label: "Phone:",
                name:  "phone"
            }, {
                label: "City:",
                name:  "city"
            }, {
                label: "Zip:",
                name:  "zip"
            }
        ]
    } );

    $('#example').dataTable( {
        dom: "Tfrtip",
        ajax: "../php/checkbox.php",
        columns: [
            { data: "first_name" },
            { data: "last_name" },
            { data: "phone" },
            { data: "city" },
            { data: "zip" },
            {
                data:   "active",
                render: function ( data, type, row ) {
                    if ( type === 'display' ) {
                        return '<input type="checkbox" class="editor-active">';
                    }
                    return data;
                },
                className: "dt-body-center"
            }
        ],
        tableTools: {
            sRowSelect: "os",
            aButtons: [
                { sExtends: "editor_create", editor: editor },
                { sExtends: "editor_edit",   editor: editor },
                { sExtends: "editor_remove", editor: editor }
            ],
            sRowSelector: 'td:not(:last-child)' // no row selection on last column
        },
        rowCallback: function ( row, data ) {
            // Set the checked state of the checkbox in the table
            $('input.editor-active', row).prop( 'checked', data.active == 1 );
        }
    } );

    $('#example').on( 'change', 'input.editor-active', function () {
        editor
            .edit( $(this).closest('tr'), false )
            .set( 'active', $(this).prop( 'checked' ) ? 1 : 0 )
            .submit();
    } );
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
            "<td>----></td>",
            "<td><input type='text' id='dstURL"+total+"'></td>",
            "</tr>"].join("");
        $("#rules").append(rowHTML);

        total+=1;
    }
    $('input[type="text"]').blur(function() {
        var val = this.value.trim();
        var stopwords = [
            "\\(",
            "\\)",
            "\\[",
            "\\]",
            "\\{",
            "\\}",
            "\\?",
            "\\\\",
            "\\+"
        ].join("|");
        var keywordsRE = new RegExp(stopwords, 'g');
        if (val.match(keywordsRE)) {
            alert("URL中不能包含 (, ), [, ], {, }, ?, \\, + 这些特殊字符！");
            this.value = "";
            $(this).focus();
            return false;
        };
    });
}
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