function getClubs(div, cb){
    $.get("/api/clubs", function (data){
        $(div).empty();
        $(div).append("<option value='all'>All</option>")
        if(data.CLUB){
            for(var i=0; i < data.CLUB.length; i++){
                $(div).append("<option value='" + data.CLUB[i].NAME + "'>" + data.CLUB[i].NAME + "</option>");
            }
        }
    })
    .always(function(){
        cb();
    });
}

function getCountries(div, cb){
    $.get("/api/countries", function (data){
        $(div).empty();
        $(div).append("<option value='all'>All</option>")
        if(data.COUNTRY){
            for(var i=0; i < data.COUNTRY.length; i++){
                $(div).append("<option value='" + data.COUNTRY[i].NAME + "'>" + data.COUNTRY[i].NAME + "</option>");
            }
        }
    })
    .always(function(){
        cb();
    });
}