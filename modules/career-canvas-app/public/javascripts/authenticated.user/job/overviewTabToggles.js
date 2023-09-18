document.addEventListener('DOMContentLoaded', function() {
  const toggleTabs = [
    { tab: "individualJobAboutToggleBtn", content: "individualJobAboutContent" },
    { tab: "individualJobRequirementsToggleBtn", content: "individualJobRequirementsContent" },
    { tab: "individualJobTechStackToggleBtn", content: "individualJobTechStackContent" },
    { tab: "individualJobNotesToggleBtn", content: "individualJobNotesContent" }
  ];

  toggleTabs.forEach(({ tab, content }) => {
    const toggleTab = document.getElementById(tab);
    const contentElement = document.getElementById(content);

    toggleTab.addEventListener("click", function() {
      if (toggleTab.classList.contains("show-toggle-image")) {
        toggleTab.classList.remove("show-toggle-image");
        toggleTab.classList.add("hide-toggle-image");
        contentElement.classList.add("hidden");
      } else {
        toggleTab.classList.remove("hide-toggle-image");
        toggleTab.classList.add("show-toggle-image");
        contentElement.classList.remove("hidden");
      }
    });
  });
});
