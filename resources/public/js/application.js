$(document).ready(function() {
    function showInput() {
        $('#input').show();
    };

    function hideInput() {
        $('#input').hide();
        $('#name').val('');
        $('#level').val('');
    };

    function toggleInput() {
        if($('#input').is(':visible')) {
            hideInput();
        } else {
            showInput();
        }
    };

    function loadData() {
        $.getJSON("/api/entries",
            function(json) {
                $("#resultsBody tr").remove();
                var tr;
                for(var i = 0; i < json.length; i++) {
                    tr = $('<tr/>');
                    if(json[i].level > 80) {
                        tr.addClass("danger");
                    } else if(json[i].level > 50) {
                        tr.addClass("warning");
                    }
                    tr.append("<td>" + json[i].name + "</td>");
                    td = $("<td>" + json[i].level + "%</td>");
                    td.addClass("end");
                    tr.append(td);
                    $('#resultsBody').append(tr);
                }
            }
        );
    };

    function submitUpdate() {
        $.ajax({
            url: "/api/entry",
            type: "POST",
            dataType: "json",
            data: {user: $('#name').val(),
                   level: $('#level').val()},
            success: function(data, status, xhr) {
                hideInput();
                loadData();
            },
            error: function (xhr, status, thrownError) {
                hideInput();
                console.log(xhr);
                console.log(status);
                console.log(thrownError);
                alert(xhr.status);
                alert(thrownError);
            }
        });
    };

    $('#addUpdate').on('click', toggleInput);
    $('#submit').on('click', submitUpdate);
    loadData();
    setInterval(loadData, 10000);
});
