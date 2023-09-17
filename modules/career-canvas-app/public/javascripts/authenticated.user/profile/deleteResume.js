document.addEventListener('DOMContentLoaded', function() {
    var deleteButtons = document.querySelectorAll('.delete-resume-button');

    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.preventDefault();

            var isConfirmed = window.confirm('Are you sure you want to delete this resume?');

            if (isConfirmed) {
                event.target.closest('#deleteResumeForm').submit();
            }
        });
    });
});