document.addEventListener('DOMContentLoaded', function() {
    var deleteJobButton = document.getElementById('deleteJobButton');

    deleteJobButton.addEventListener('click', function(event) {
        event.preventDefault();

        var isConfirmed = window.confirm('Are you sure you want to delete this job?');

        if (isConfirmed) {
            event.target.closest('#deleteJobForm').submit();
        }
    });
});