$(document).ready(function() {
    function showInput() {
        $('#input').show();
    };

    function hideInput() {
        $('#input').hide();
        $('#name').val('');
        $('#level').val('');
    };

    function loadData() {
        $.getJSON("/api/entries",
            function(json) {
                var tr;
                for(var i = 0; i < json.length; i++) {

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

    $('#addUpdate').on('click', showInput);
    $('#submit').on('click', submitUpdate);
    loadData();
});
