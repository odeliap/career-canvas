document.getElementById("save-job").addEventListener("click", function(e) {
    e.preventDefault();
    var jobDescriptionInputDiv = document.querySelector(".job-description-input");
    if (jobDescriptionInputDiv.style.display === "none") {
        jobDescriptionInputDiv.style.display = "block";
        this.style.backgroundColor = '#0077B5';
        this.style.color = 'white';
    } else {
        jobDescriptionInputDiv.style.display = "none";
        this.style.backgroundImage = 'transparent';
        this.style.color = '#333';
    }
});

$(document).ready(function() {
    if (!$("input[name='jobType']:checked").length) {
        $("input[name='jobType'][value='FullTime']").prop("checked", true);
    }
});
