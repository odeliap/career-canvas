document.addEventListener('DOMContentLoaded', function() {
    const toggleButton = document.getElementById('jobFeedToggleViewButton');
    const tilesView = document.getElementById('tiles-view');
    const listView = document.getElementById('list-view');

    toggleButton.addEventListener('click', function() {
        if (tilesView.style.display === 'none') {
            tilesView.style.display = 'block';
            listView.style.display = 'none';
            toggleButton.innerHTML = 'Show Tiles View';
        } else {
            tilesView.style.display = 'none';
            listView.style.display = 'block';
            toggleButton.innerHTML = 'Show List View';
        }
    });
});
