// Plik: src/main/resources/static/js/admin_panel.js
document.addEventListener("DOMContentLoaded", function() {
    const adminPanelModal = document.getElementById('adminPanelModal');
    const adminPanelMsg = document.getElementById('adminPanelMsg');
    const addCategoryForm = document.getElementById('addCategoryForm');
    const addTagForm = document.getElementById('addTagForm');
    const categoryName = document.getElementById('categoryName');
    const tagName = document.getElementById('tagName');

    // Bootstrap modal instance
    let modalInstance = null;
    if (adminPanelModal) {
        modalInstance = bootstrap.Modal.getOrCreateInstance(adminPanelModal);

        // Czyść formularze i komunikaty po zamknięciu
        adminPanelModal.addEventListener('hidden.bs.modal', function () {
            addCategoryForm.reset();
            addTagForm.reset();
            adminPanelMsg.textContent = "";
        });
    }

    function showMsg(msg, success = true) {
        adminPanelMsg.textContent = msg;
        adminPanelMsg.className = "mt-2 " + (success ? "text-success" : "text-danger");
    }

    addCategoryForm.onsubmit = async function(e) {
        e.preventDefault();
        const name = categoryName.value.trim();
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            showMsg("Musisz być zalogowany!", false);
            return;
        }
        try {
            const res = await fetch('/api/categories/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                body: JSON.stringify({ name })
            });
            if (res.ok) {
                showMsg("Dodano kategorię!");
                addCategoryForm.reset();
            } else {
                const err = await res.json().catch(() => ({}));
                showMsg(err.message || "Błąd dodawania kategorii", false);
            }
        } catch {
            showMsg("Błąd sieci", false);
        }
    };

    addTagForm.onsubmit = async function(e) {
        e.preventDefault();
        const name = tagName.value.trim();
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            showMsg("Musisz być zalogowany!", false);
            return;
        }
        try {
            const res = await fetch('/api/tags/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
                body: JSON.stringify({ name })
            });
            if (res.ok) {
                showMsg("Dodano tag!");
                addTagForm.reset();
            } else {
                const err = await res.json().catch(() => ({}));
                showMsg(err.message || "Błąd dodawania tagu", false);
            }
        } catch {
            showMsg("Błąd sieci", false);
        }
    };
});