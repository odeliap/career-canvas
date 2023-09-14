const documentsTab = document.getElementById("documents-tab");
documentsTab.classList.add("highlight-sidebar-section");

function toggleView(viewId) {
    const views = document.querySelectorAll('.view-content');
    const buttons = document.querySelectorAll('.toggle-button');

    views.forEach(view => {
        view.style.display = 'none';
    });

    buttons.forEach(button => {
        button.classList.remove('active');
    });

    document.getElementById(viewId).style.display = 'block';
    if(viewId === 'view1') {
        buttons[0].classList.add('active');
    } else {
        buttons[1].classList.add('active');
    }
}