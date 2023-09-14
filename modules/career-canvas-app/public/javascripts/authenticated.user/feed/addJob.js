document.getElementById("add-job").addEventListener("click", function(e) {
    e.preventDefault();
    var jobUrlInputDiv = document.querySelector(".job-url-input");
    if (jobUrlInputDiv.style.display === "none") {
        jobUrlInputDiv.style.display = "block";
        this.style.backgroundColor = '#0077B5';
        this.style.color = 'white';
    } else {
        jobUrlInputDiv.style.display = "none";
        this.style.backgroundImage = 'transparent';
        this.style.color = '#333';
    }
});