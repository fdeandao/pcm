var def = {
    count: 23
};

var callManager = {
    calls: new Array(),
    addCall: (function(cb, params) {
        this.calls.push({cb: cb, params: params});
    }),
    execCall: (function() {
        if (this.calls.length > 0) {
            var caller = this.calls.shift();
            caller.cb(caller.params, this.execCall());
        }
    })
};

function getClubs(div, cb) {
    $.get("/api/clubs", function(data) {
        $(div).empty();
        $(div).append("<option value='all'>All</option>");
        if (data.club) {
            for (var i = 0; i < data.club.length; i++) {
                $(div).append("<option value='" + data.club[i].name.replace(/à|á|ä|â|è|é|ë|ê|ì|í|ï|î|ò|ó|ö|ô|ù|ú|ü|û|À|Á|Ä|Â|È|É|Ë|Ê|Ì|Í|Ï|Î|Ò|Ó|Ö|Ô|Ù|Ú|Ü|Û/, "%25") + "'>" + data.club[i].name + "</option>");
            }
        }
    })
            .always(function() {
        if (cb)
            cb();
    });
}

function getCountries(div, cb) {
    $.get("/api/countries", function(data) {
        $(div).empty();
        $(div).append("<option value='all'>All</option>");
        if (data.country) {
            for (var i = 0; i < data.country.length; i++) {
                $(div).append("<option value='" + data.country[i].name + "'>" + data.country[i].name + "</option>");
            }
        }
    })
            .always(function() {
        if (cb)
            cb();
    });
}

function datToSpan(data, skip) {
    var ret = "";
    for (var dat in data) {
        if (skip && dat in skip)
            continue;
        ret += "<div style='display:block;'><b>" + dat + "</b>: " + data[dat] + "</div>";
    }
    return ret;
}



/*function getFileToPlayer(idfile, tbresp, depp, dialog, divhead, iddata) {
 if (divhead && iddata) {
 $("#" + divhead).empty()
 .append(datToSpan(JSON.parse($("#" + iddata + "_hid").val())));
 }
 $.ajax({
 type: "GET",
 url: "/api/filestoplayer/" + idfile + "/" + (depp || false),
 dataType: "JSON"
 }).done(function(data) {
 jsonToTbody({
 "table": tbresp,
 "data": data,
 "prop": "filestoplayer",
 "prefix": "files_2_player",
 "id": "id"
 });
 $("#" + dialog).dialog("open");
 })
 .fail(function() {
 $("#" + tbresp + ">thead").empty();
 $("#" + tbresp + ">tbody").empty();
 alert("error loading data");
 });
 }*/

function jsonToTbody(pData) {
    var thead = $("#" + pData.table + ">thead").empty();
    var tbody = $("#" + pData.table + ">tbody").empty();
    if (pData.data[pData.prop]) {
        if (pData.data[pData.prop].length > 0) {
            var hrow = $("<tr>");
            hrow.append("<th>N.</th>");//Number Column
            if (window[pData.addFisrtColumn]) {
                hrow.append("<th></th>");
            }
            for (var colname in pData.data[pData.prop][0]) {
                if (typeof pData.data[pData.prop][0][colname] === 'string') {
                    hrow.append("<th>" + colname + "</th>");
                }
            }
            if (window[pData.addLastColumn]) {
                hrow.append("<th></th>");
            }
            var id = new Date().getTime();
            thead.append(hrow);
            for (var i = 0; i < pData.data[pData.prop].length; i++) {
                var currData = pData.data[pData.prop][i];
                var row = $("<tr id='" + (pData.prefix || "") + id + "_tr'>");
                row.append("<input type='hidden' id='" + (pData.prefix || "") + id + "_hid' value='" + JSON.stringify(currData) + "' />");
                row.append("<td>" + (parseInt(pData.start) + i) + "</td>");
                if (window[pData.addFisrtColumn]) {
                    row.append("<td>" + window[pData.addFisrtColumn](currData, (pData.prefix || "") + id) + "</td>");
                }
                for (var dat in currData) {
                    if (typeof currData[dat] == 'string') {
                        row.append("<td>" + currData[dat] + "</td>");
                    }
                }
                if (window[pData.addLastColumn]) {
                    row.append("<td>" + window[pData.addLastColumn](currData, (pData.prefix || "") + id) + "</td>");
                }
                tbody.append(row);
                id = id + 1;
            }
        }
    }
}

function showProgress() {
    $('body').append('<div id="progress" class="modal-overlay"><img src="img/ajax-loader.gif" alt="" width="350" height="350" /> Loading...</div>');
    $('#progress').center();
}

function hideProgress() {
    $('#progress').remove();
}

jQuery.fn.center = function() {
    this.css("position", "absolute");
    this.css("top", ($(window).height() - this.height()) / 2 + $(window).scrollTop() + "px");
    this.css("left", ($(window).width() - this.width()) / 2 + $(window).scrollLeft() + "px");
    return this;
};

function getAjaxParams(jsondata) {
    var options = JSON.parse($("#" + jsondata).val());
    return {
        params: {
            url: $("#" + (options.prefix) + "_url").val(),
            start: $("#" + (options.prefix) + "_start_p").val(),
            count: $("#" + (options.prefix) + "_count_p").val(),
            type: $("#" + (options.prefix) + "_type").val(),
            dataType: $("#" + (options.prefix) + "_dataType").val()
        },
        table: (options.prefix) + "_table",
        datajson: options.dataJson,
        id: options.id,
        resp: options.prefix + "_resp_p",
        prefix: options.prefix,
        div: options.div,
        addLastColumn: options.extends.addLastColumn,
        addFirstColumn: options.extends.addFirstColumn,
        preDone: options.extends.preDone,
        postDone: options.extends.postDone,
        fail: options.extends.fail
    };
}

function createTableMaster(options) {
    var div = $("#" + options.div);
    div.empty();
    div.append($("<div id='" + options.prefix + "_head'>" + (options.headJSON ? datToSpan(JSON.parse($("#" + options.headJSON).val())) : options.head) + "</div>"));
    //div.append($("<div id='" + options.prefix + "_head'>" + (options.head) + "</div>"));
    /* ADD PARAMS DATA */
    if (options.params) {
        var divParam = $("<div id='" + options.prefix + "_params_master'>");
        for (var par = 0; par < options.params.length; par++) {
            var input;
            var label = $("<label for='" + (options.prefix) + (options.params[par].name) + "_p'>" + (options.params[par].label || options.params[par].name) + "</label>");
            if (options.params[par].type == "select") {
                input = $("<select id='" + (options.prefix) + (options.params[par].name) + "_p'></select>");
                if (options.params[par].data == "club") {
                    callManager.addCall(function(params, cb) {
                        getClubs(params, cb);
                    }, "#" + (options.prefix) + (options.params[par].name) + "_p");
                }
                if (options.params[par].data == "country") {
                    callManager.addCall(function(params, cb) {
                        getCountries(params, cb);
                    }, "#" + (options.prefix) + (options.params[par].name) + "_p");
                }
            } else if (options.params[par].type == "checkbox") {
                input = $("<input type='checkbox' id='" + (options.prefix) + (options.params[par].name) + "_p' />");
            } else if (options.params[par].type == "text") {
                input = $("<input type='text' id='" + (options.prefix) + (options.params[par].name) + "_p' value='" + (options.params[par].text || "") + "' />");
            } else {
                continue;
            }
            if (options.params[par].onchange) {
                input.change(options.params[par].onchange);
            }
            divParam.append(label);
            divParam.append(input);
        }
        var btnStart = $("<input id='" + (options.prefix) + "_start_p' type='text' value='0'/>");
        var btnCount = $("<input id='" + (options.prefix) + "_count_p' type='text' value='" + def.count + "'/>");
        var btnNext = $("<input id='" + (options.prefix) + "_next_p' type='button' value='Next'/>");
        var btnBack = $("<input id='" + (options.prefix) + "_back_p' type='button' value='Back'/>");
        var btnSearch = $("<input id='" + (options.prefix) + "_search_p' type='button' value='Search'/>");

        btnStart.change(function() {
            loadAjax(getAjaxParams((options.prefix) + "_json_stringify"));
        });

        btnCount.change(function() {
            $("#" + (options.prefix) + "_start_p").val(0);
            loadAjax(getAjaxParams((options.prefix) + "_json_stringify"));
        });

        btnNext.click(function() {
            if (isNaN(parseInt($("#" + (options.prefix) + "_start_p").val())) || isNaN(parseInt($("#" + (options.prefix) + "_count_p").val()))) {
                $("#" + (options.prefix) + "_start_p").val(0);
                $("#" + (options.prefix) + "_count_p").val(def.count);
            } else {
                $("#" + (options.prefix) + "_start_p").val(parseInt($("#" + (options.prefix) + "_start_p").val()) + parseInt($("#" + (options.prefix) + "_count_p").val()));
            }
            loadAjax(getAjaxParams((options.prefix) + "_json_stringify"));
        });

        btnBack.click(function() {
            if (isNaN(parseInt($("#" + (options.prefix) + "_start_p").val())) || isNaN(parseInt($("#" + (options.prefix) + "_count_p").val()))) {
                $("#" + (options.prefix) + "_start_p").val(0);
                $("#" + (options.prefix) + "_count_p").val(def.count);
            } else {
                var idx = parseInt($("#" + (options.prefix) + "_start_p").val()) - parseInt($("#" + (options.prefix) + "_count_p").val());
                $("#" + (options.prefix) + "_start_p").val(idx < 0 ? 0 : idx);
            }
            loadAjax(getAjaxParams((options.prefix) + "_json_stringify"));
        });

        btnSearch.click(function() {
            loadAjax(getAjaxParams((options.prefix) + "_json_stringify"));
        });

        var resp = $("<span id='" + (options.prefix) + "_resp_p'></span>");

        divParam.append($("<label for='" + (options.prefix) + "_start_p'>Start</label>"))
                .append(btnStart)
                .append($("<label for='" + (options.prefix) + "_count_p'>Count</label>"))
                .append(btnCount)
                .append(btnBack)
                .append(btnNext)
                .append(btnSearch)
                .append(resp)
                .append($("<input type='hidden' id='" + (options.prefix) + "_url' value='" + (options.ajax.url) + "' />"))
                .append($("<input type='hidden' id='" + (options.prefix) + "_type' value='" + (options.ajax.type) + "' />"))
                .append($("<input type='hidden' id='" + (options.prefix) + "_dataType' value='" + (options.ajax.dataType || "JSON") + "' />"))
                .append($("<input type='hidden' id='" + (options.prefix) + "_json_stringify' value='" + (JSON.stringify(options)) + "' />"));
        div.append(divParam);
        /* END ADD PARAMS */
    }
    var tdata = $("<table id='" + (options.prefix) + "_table' style='height:300px; overflow:scroll; display:block;'><thead id='" + (options.prefix) + "_thead'></thead><tbody id='" + (options.prefix) + "_tbody'></tbody></table>");
    div.append(tdata);
    callManager.addCall(function(params, cb) {
        loadAjax(params, cb);
    }, getAjaxParams((options.prefix) + "_json_stringify"));
    callManager.execCall();
}

function loadAjax(options, cb) {
    var url = options.params.url;
    var start = options.params.start || 0;
    var count = options.params.count || def.count;
    $.ajax({
        type: options.params.type,
        url: url,
        dataType: options.params.dataType,
        data: {count: count, start: start}
    }).done(function(data) {
        $("#" + options.resp).html("");
        if (window[options.preDone]) {
            window[options.preDone](options, data);
        }
        jsonToTbody({
            "table": options.table,
            "data": data,
            "prop": options.datajson,
            "id": options.id,
            "start": start,
            "prefix": options.prefix,
            "addLastColumn": options.addLastColumn,
            "addFirstColumn": options.addFirstColumn
        });
        if (data.count) {
            $("#" + options.resp).html("Results: " + data.count);
        }
        if (data.error) {
            $("#" + options.resp).html(data.error);
        }
        if (window[options.postDone]) {
            window[options.postDone](options, data);
        }
    })
            .fail(function() {
        if (window[options.fail]) {
            window[options.fail](options);
        }
    }
    )
            .always(function() {
        if (cb) {
            cb();
        }
    }
    );
}


function replaceArr(data, newData, idx) {
    arr = data.split("/");
    arr[idx] = newData;
    return arr.join("/");
}

function loadImagesAjax(btn, id, face, hair) {
    var div = $("#" + id + "_pictures");
    if (!div.html()) {
        addImages(div, id, face, hair);
    }
    div.toggle();
    btn.val(div.is(":visible") === true ? "Hide Pictures" : "Show Pictures")
}

function addImages(div, id, face, hair) {
    div.append($("<input style='display:block;' type='button' value='Reload Images' onclick='reloadImg(\"" + id + "\", \"" + face.replace(/\\/g, "\\\\") + "\", \"" + hair.replace(/\\/g, "\\\\") + "\");'>"));
    var imgFace = $("<img width='200' height='200' id='face_" + id + "'>")
            .attr("alt", face || "No face found");
    var imgHair = $("<img width='200' height='200' id='hair_" + id + "'>")
            .attr("alt", hair || "No hair found");
    imgFace.one("load", function() {
        div.append(imgFace);
        imgHair.one("load", function() {
            div.append(imgHair);
        }).attr("src", hair ? "/api/img/" + hair : "/img/hair.jpg");
    }).attr("src", face ? "/api/img/" + face : "/img/face.jpg");
}

function reloadImg(id, face, hair) {
    $("#face_" + id).one("load", function() {
        $("#hair_" + id).one("load").attr("src", hair ? "/api/img/" + hair : "/img/hair.jpg");
    }).attr("src", face ? "/api/img/" + face : "/img/face.jpg");
}