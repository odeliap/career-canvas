document.addEventListener('DOMContentLoaded', function() {
    const jobsGrid = document.getElementById("jobTiles");
    const jobsList = document.getElementById("jobList");
    const spreadsheet = document.getElementById("spreadsheetRows");
    const allItems = {
        'jobCards': jobsGrid.querySelectorAll(".job-tile"),
        'jobListItems': jobsList.querySelectorAll(".job-list-item"),
        'spreadsheetRows': spreadsheet.querySelectorAll(".spreadsheet-row")
    };

    const lastUpdateFilter = document.getElementById("lastUpdate");
    const companyFilter = document.getElementById("company");
    const locationFilter = document.getElementById("location");
    const showStarredCheckbox = document.getElementById("showStarred");
    const searchInput = document.getElementById("searchBox");

    const tilesViewBtn = document.getElementById("tilesToggleView");
    const listViewBtn = document.getElementById("listToggleView");
    const spreadsheetViewBtn = document.getElementById("spreadsheetToggleView");

    const tilesView = document.getElementById("tilesView");
    const listView = document.getElementById('listView');
    const spreadsheetView = document.getElementById('spreadsheetView');

    const ITEMS_PER_PAGE = 30;

    let currentPage = {
        'jobCards': 1,
        'jobListItems': 1,
        'spreadsheetRows': 1
    };
    let maxPage = {
        'jobCards': 1,
        'jobListItems': 1,
        'spreadsheetRows': 1
    };

    function getActiveGrouping() {
        if (!tilesView.classList.contains("hidden")) {
            return 'jobCards';
        } else if (!listView.classList.contains("hidden")) {
            return 'jobListItems';
        } else if (!spreadsheetView.classList.contains("hidden")) {
            return 'spreadsheetRows';
        }
        return null;
    }

    function filterCard(card) {
        const now = new Date().getTime();
        const hours = parseFloat(lastUpdateFilter.value);
        const timeLimit = now - hours * 60 * 60 * 1000;
        const selectedCompany = companyFilter.value;
        const selectedLocation = locationFilter.value;
        const selectedSearch = searchInput.value.toLowerCase();

        const lastUpdate = parseFloat(card.getAttribute("data-lastupdate"));
        const company = card.getAttribute("data-company");
        const jobTitle = card.getAttribute("data-title");
        const location = card.getAttribute("data-location");
        const starred = card.getAttribute("data-starred") === "true";
        const showStarredOnly = showStarredCheckbox.checked;

        return lastUpdate >= timeLimit &&
            (selectedCompany === "" || selectedCompany === company) &&
            (selectedLocation === "" || selectedLocation === location) &&
            ((company.toLowerCase().includes(selectedSearch) || jobTitle.toLowerCase().includes(selectedSearch)) || selectedSearch === "") &&
            (!showStarredOnly || starred);
    }

    function filterRows(resetPage = true) {
        const currentGrouping = getActiveGrouping()
        if (resetPage) currentPage[currentGrouping] = 1;

        let filteredItems = 0;
        let displayCards = [];

        allItems[currentGrouping].forEach(card => {
            if (filterCard(card)) {
                filteredItems++;
                displayCards.push(card);
                if (card.classList.contains("spreadsheet-row")) {
                    card.style.display = "table-row";
                } else {
                    card.style.display = "block";
                }
            } else {
                card.style.display = "none";
            }
        });

        maxPage[currentGrouping] = Math.ceil(filteredItems / ITEMS_PER_PAGE);

        displayCards.forEach((card, index) => {
            if (index >= (currentPage[currentGrouping] - 1) * ITEMS_PER_PAGE && index < currentPage[currentGrouping] * ITEMS_PER_PAGE) {
                if (card.classList.contains("spreadsheet-row")) {
                    card.style.display = "table-row";
                } else {
                    card.style.display = "block";
                }
            } else {
                card.style.display = "none";
            }
        });

        updatePaginationDisplay();
    }

    function updatePaginationDisplay() {
        const pageNumbersContainer = document.getElementById('pageNumbersContainer');
        pageNumbersContainer.innerHTML = "";

        const currentGrouping = getActiveGrouping();

        for (let i = 1; i <= maxPage[currentGrouping]; i++) {
            const pageElem = document.createElement("span");
            pageElem.textContent = i;
            pageElem.className = "page-number";

            if (i === currentPage[currentGrouping]) {
                pageElem.classList.add("current");
            }

            pageElem.addEventListener("click", function() {
                currentPage[currentGrouping] = i;
                filterRows(false);
            });

            pageNumbersContainer.appendChild(pageElem);
        }

        document.getElementById('prevPage').disabled = currentPage[currentGrouping] === 1;
        document.getElementById('nextPage').disabled = currentPage[currentGrouping] >= maxPage[currentGrouping];
    }

    lastUpdateFilter.addEventListener("change", filterRows);

    companyFilter.addEventListener("change", function() {
        console.log('Company Filter changed:', this.value);
        filterRows();
    });

    locationFilter.addEventListener("change", filterRows);

    searchInput.addEventListener('keydown', function(event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            filterRows();
        }
    });

    searchInput.addEventListener('input', function() {
        filterRows();
      });

    showStarredCheckbox.addEventListener("change", function() {
        filterRows();
        const checkboxLabel = document.getElementById('showStarredLabel');

        if (this.checked) {
            checkboxLabel.style.backgroundImage = 'url("/assets/images/authenticated.user/feed/tile/filledbookmark.png")';
        } else {
            checkboxLabel.style.backgroundImage = 'url("/assets/images/authenticated.user/feed/tile/bookmark.png")';
        }
    });

    document.getElementById('prevPage').addEventListener("click", function() {
        const currentGrouping = getActiveGrouping()
        if (currentPage[currentGrouping] > 1) {
            currentPage[currentGrouping]--;
            filterRows(false);
        }
    });

    document.getElementById('nextPage').addEventListener("click", function() {
        const currentGrouping = getActiveGrouping()
        if (currentPage[currentGrouping] < maxPage[currentGrouping]) {
            currentPage[currentGrouping]++;
            filterRows(false);
        }
    });

    filterRows();

    let currentFilter = null;

    document.querySelectorAll('.status-filter').forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');
            const filterDisplayElementId = `${filter}-count`;
            const filterDisplayElement = document.getElementById(filterDisplayElementId);

            if (currentFilter === filter) {
                currentFilter = null;
                document.querySelectorAll('[data-status]').forEach(job => {
                    if (job.classList.contains("spreadsheet-row")) {
                        job.style.display = "table-row";
                    } else {
                        job.style.display = "block";
                    }
                    filterDisplayElement.classList.remove('highlighted-count-arrow-box');
                });
            } else {
                document.querySelectorAll('[data-status]').forEach(job => {
                    job.style.display = 'none';
                    filterDisplayElement.classList.add('highlighted-count-arrow-box');
                });

                document.querySelectorAll(`[data-status="${filter}"]`).forEach(job => {
                    if (job.classList.contains("spreadsheet-row")) {
                        job.style.display = "table-row";
                    } else {
                        job.style.display = "block";
                    }
                });

                currentFilter = filter;
                lastUpdateFilter.value = '876600';
                companyFilter.value = '';
                locationFilter.value = '';
            }
        });
    });

    function showView(viewId, toggleId) {
        tilesView.classList.add('hidden');
        listView.classList.add('hidden');
        spreadsheetView.classList.add('hidden');

        document.getElementById(viewId).classList.remove('hidden');

        const toggleOpts = document.querySelectorAll('.toggle-view');

        toggleOpts.forEach(toggleOpt => toggleOpt.classList.remove('active-toggle-view'));

        document.getElementById(toggleId).classList.add('active-toggle-view');
        filterRows();
    }

    tilesViewBtn.addEventListener('click', function() {
        showView('tilesView', 'tilesToggleView');
    });

    listViewBtn.addEventListener('click', function() {
        showView('listView', 'listToggleView');
    });

    spreadsheetViewBtn.addEventListener('click', function() {
        showView('spreadsheetView', 'spreadsheetToggleView');
    });
});
