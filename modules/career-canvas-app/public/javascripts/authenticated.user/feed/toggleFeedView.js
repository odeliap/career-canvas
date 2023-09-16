function showView(viewId, toggleId) {
    document.getElementById('tiles-view').classList.add('hidden');
    document.getElementById('list-view').classList.add('hidden');
    document.getElementById('spreadsheet-view').classList.add('hidden');

    document.getElementById(viewId).classList.remove('hidden');

    const toggleOpts = document.querySelectorAll('.toggle-view');

    toggleOpts.forEach(toggleOpt => toggleOpt.classList.remove('active-toggle-view'));

    document.getElementById(toggleId).classList.add('active-toggle-view');
}