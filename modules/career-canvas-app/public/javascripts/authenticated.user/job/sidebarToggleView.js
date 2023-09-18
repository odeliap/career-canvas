document.addEventListener('DOMContentLoaded', function() {
    const sidebarToggleButton = document.getElementById('sidebarToggle');
    const expandedContent = document.querySelectorAll('.extended-sidebar-item');
    const compressedContent = document.querySelectorAll('.compressed-sidebar-item');
    const sidebar = document.getElementById('jobInfoSidebar');

    var expanded = true;

    sidebarToggleButton.addEventListener("click", function() {
      if (expanded) {
        expandedContent.forEach(item => {
            item.classList.add("hidden");
        });
        compressedContent.forEach(item => {
            item.classList.remove("hidden");
        });
        sidebar.classList.remove('extended-sidebar');
        sidebar.classList.add('compressed-sidebar');
        sidebarToggleButton.classList.add('compressed-sidebar-toggle-button-icon');
        expanded = false;
      } else if (!expanded) {
        compressedContent.forEach(item => {
            item.classList.add("hidden");
        });
        expandedContent.forEach(item => {
            item.classList.remove("hidden");
        });
        sidebar.classList.remove('compressed-sidebar');
        sidebar.classList.add('extended-sidebar');
        sidebarToggleButton.classList.remove('compressed-sidebar-toggle-button-icon');
        expanded = true;
      }
    });
});
