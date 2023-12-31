document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('jobUrlModal');
    const showModalBtn = document.getElementById('addJobButton');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelAddJobUrl');
    var loadedBefore = false;

    if (modal.getAttribute('data-show') === 'true' && !loadedBefore) {
        modal.style.display = "block";
        loadedBefore = true;
    }

    showModalBtn.onclick = function() {
        modal.style.display = "block";
    }

    closeModalBtn.onclick = function() {
        modal.style.display = "none";
    }

    cancelBtn.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
});
