document.addEventListener('DOMContentLoaded', function() {
  function toggleVisibility(editButton, currentContent, editorSection, isEditing) {
    if (!isEditing) {
      currentContent.classList.add("hidden");
      editorSection.classList.remove("hidden");
    } else {
      editorSection.classList.add("hidden");
      currentContent.classList.remove("hidden");
    }
    return !isEditing;
  }

  const editAboutButton = document.getElementById('editAboutContentButton');
  const currentAbout = document.getElementById('individualJobAboutContent');
  const aboutEditor = document.getElementById('jobAboutEditorSection');

  const editRequirementsButton = document.getElementById('editRequirementsContentButton');
  const currentRequirements = document.getElementById('individualJobRequirementsContent');
  const requirementsEditor = document.getElementById('jobRequirementsEditorSection');

  const editTechStackButton = document.getElementById('editTechStackContentButton');
  const currentTechStack = document.getElementById('individualJobTechStackContent');
  const techStackEditor = document.getElementById('jobTechStackEditorSection');

  const editNotesButton = document.getElementById('editNotesContentButton');
  const currentNotes = document.getElementById('individualJobNotesContent');
  const notesEditor = document.getElementById('jobNotesEditorSection');

  var editingAbout = false;
  var editingRequirements = false;
  var editingTechStack = false;
  var editingNotes = false;

  editAboutButton.addEventListener("click", function() {
    editingAbout = toggleVisibility(editAboutButton, currentAbout, aboutEditor, editingAbout);
  });

  editRequirementsButton.addEventListener("click", function() {
    editingRequirements = toggleVisibility(editRequirementsButton, currentRequirements, requirementsEditor, editingRequirements);
  });

  editTechStackButton.addEventListener("click", function() {
    editingTechStack = toggleVisibility(editTechStackButton, currentTechStack, techStackEditor, editingTechStack);
  });

  editNotesButton.addEventListener("click", function() {
    editingNotes = toggleVisibility(editNotesButton, currentNotes, notesEditor, editingNotes);
  });
});
