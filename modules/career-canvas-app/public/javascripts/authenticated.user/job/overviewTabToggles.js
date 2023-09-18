document.addEventListener('DOMContentLoaded', function() {
  const toggleTabs = [
    { tab: "individualJobAboutToggleBtn", content: "individualJobAboutContent", editBtn: "editAboutContentButton", editContent: "jobAboutEditorSection" },
    { tab: "individualJobRequirementsToggleBtn", content: "individualJobRequirementsContent", editBtn: "editRequirementsContentButton", editContent: "jobRequirementsEditorSection" },
    { tab: "individualJobTechStackToggleBtn", content: "individualJobTechStackContent", editBtn: "editTechStackContentButton", editContent: "jobTechStackEditorSection" },
    { tab: "individualJobNotesToggleBtn", content: "individualJobNotesContent", editBtn: "editNotesContentButton", editContent: "jobNotesEditorSection" }
  ];

  toggleTabs.forEach(({ tab, content, editBtn, editContent }) => {
    const toggleTab = document.getElementById(tab);
    const contentElement = document.getElementById(content);
    const editButton = document.getElementById(editBtn);
    const textEditor = document.getElementById(editContent);

    toggleTab.addEventListener("click", function() {
      if (toggleTab.classList.contains("show-toggle-image")) {
        toggleTab.classList.remove("show-toggle-image");
        toggleTab.classList.add("hide-toggle-image");
        contentElement.classList.add("hidden");
        editButton.classList.add("hidden");
        textEditor.classList.add("hidden");
      } else {
        toggleTab.classList.remove("hide-toggle-image");
        toggleTab.classList.add("show-toggle-image");
        contentElement.classList.remove("hidden");
        editButton.classList.remove("hidden");
      }
    });
  });
});
