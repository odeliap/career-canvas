document.addEventListener('DOMContentLoaded', function() {
    const jobsGrid = document.getElementById("job-tiles");
    const jobsList = document.getElementById("job-list");
    const spreadsheet = document.getElementById("spreadsheet-rows");
    const allItems = {
        'jobCards': jobsGrid.querySelectorAll(".job-tile"),
        'jobListItems': jobsList.querySelectorAll(".job-list-item"),
        'spreadsheetRows': spreadsheet.querySelectorAll(".spreadsheet-row")
    };

    const lastUpdateFilter = document.getElementById("lastUpdate");
    const companyFilter = document.getElementById("company");
    const locationFilter = document.getElementById("location");
    const showStarredCheckbox = document.getElementById("showStarred");

    const ITEMS_PER_PAGE = 30;
    let currentPage = 1;
    let maxPage = 1;

    function filterCard(card) {
        const now = new Date().getTime();
        const hours = parseFloat(lastUpdateFilter.value);
        const timeLimit = now - hours * 60 * 60 * 1000;
        const selectedCompany = companyFilter.value;
        const selectedLocation = locationFilter.value;
        const lastUpdate = parseFloat(card.getAttribute("data-lastupdate"));
        const company = card.getAttribute("data-company");
        const location = card.getAttribute("data-location");
        const starred = card.getAttribute("data-starred") === "true";
        const showStarredOnly = showStarredCheckbox.checked;

        return lastUpdate >= timeLimit &&
            (selectedCompany === "" || selectedCompany === company) &&
            (selectedLocation === "" || selectedLocation === location) &&
            (!showStarredOnly || starred);
    }

    function filterRows(resetPage = true) {
        if (resetPage) currentPage = 1;

        let filteredItems = 0;
        let displayCards = [];

        for (let itemType in allItems) {
            allItems[itemType].forEach(card => {
                if (filterCard(card)) {
                    filteredItems++;
                    displayCards.push(card);
                } else {
                    card.style.display = "none";
                }
            });
        }

        maxPage = Math.ceil(filteredItems / ITEMS_PER_PAGE);

        displayCards.forEach((card, index) => {
            if (index >= (currentPage - 1) * ITEMS_PER_PAGE && index < currentPage * ITEMS_PER_PAGE) {
                card.style.display = "block";
            } else {
                card.style.display = "none";
            }
        });

        updatePaginationDisplay();
    }

    function updatePaginationDisplay() {
        const pageNumbersContainer = document.getElementById('pageNumbersContainer');
        pageNumbersContainer.innerHTML = "";

        for (let i = 1; i <= maxPage; i++) {
            const pageElem = document.createElement("span");
            pageElem.textContent = i;
            pageElem.className = "page-number";

            if (i === currentPage) {
                pageElem.classList.add("current");
            }

            pageElem.addEventListener("click", function() {
                currentPage = i;
                filterRows(false);
            });

            pageNumbersContainer.appendChild(pageElem);
        }

        document.getElementById('prevPage').disabled = currentPage === 1;
        document.getElementById('nextPage').disabled = currentPage >= maxPage;
    }

    lastUpdateFilter.addEventListener("change", filterRows);
    companyFilter.addEventListener("change", filterRows);
    locationFilter.addEventListener("change", filterRows);

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
        if (currentPage > 1) {
            currentPage--;
            filterRows(false);
        }
    });

    document.getElementById('nextPage').addEventListener("click", function() {
        if (currentPage < maxPage) {
            currentPage++;
            filterRows(false);
        }
    });

    filterRows();

    function updateCountForStatus(status) {
        const statusElements = document.querySelectorAll(`[data-status="${status}"]`);

        const count = statusElements.length/3;

        const countDisplayElementId = `${status}-count`;
        const countDisplayElement = document.getElementById(countDisplayElementId);
        if (countDisplayElement) {
            if (count > 0) {
                countDisplayElement.innerHTML = `<div><p>${count}</p> <p class="count-display">${status.toUpperCase()}</p></div>`;
            }
        } else {
            console.warn(`No element found with the ID: ${countDisplayElementId}`);
        }

        if (count > 0) {
            countDisplayElement.classList.add('count-arrow-box');
        } else {
            countDisplayElement.classList.remove('count-arrow-box');
        }
    }

    updateCountForStatus('Bookmarked');
    updateCountForStatus('Applying');
    updateCountForStatus('Applied');
    updateCountForStatus('Interviewing');
    updateCountForStatus('Offer');
    updateCountForStatus('Rejected');

    let currentFilter = null;

    document.querySelectorAll('.status-filter').forEach(button => {
        button.addEventListener('click', function() {
            const filter = this.getAttribute('data-filter');
            const filterDisplayElementId = `${filter}-count`;
            const filterDisplayElement = document.getElementById(filterDisplayElementId);

            if (currentFilter === filter) {
                currentFilter = null;
                document.querySelectorAll('[data-status]').forEach(job => {
                    job.style.display = 'block';
                    filterDisplayElement.classList.remove('highlighted-count-arrow-box');
                });
            } else {
                document.querySelectorAll('[data-status]').forEach(job => {
                    job.style.display = 'none';
                    filterDisplayElement.classList.add('highlighted-count-arrow-box');
                });

                document.querySelectorAll(`[data-status="${filter}"]`).forEach(job => {
                    job.style.display = 'block';
                });

                currentFilter = filter;
                lastUpdateFilter.value = '876600';
                companyFilter.value = '';
                locationFilter.value = '';
            }
        });
    });
});
