$(document).ready(function() {
    if (!$("input[name='jobType']:checked").length) {
        $("input[name='jobType'][value='FullTime']").prop("checked", true);
    }
});
