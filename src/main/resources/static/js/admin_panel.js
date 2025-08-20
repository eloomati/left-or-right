// Plik: src/main/resources/static/js/admin_panel.js
document.addEventListener("DOMContentLoaded", function() {
    const adminPanelModal = document.getElementById('adminPanelModal');
    const adminPanelMsg = document.getElementById('adminPanelMsg');
    const addCategoryForm = document.getElementById('addCategoryForm');
    const addTagForm = document.getElementById('addTagForm');
    const categoryName = document.getElementById('categoryName');
    const tagName = document.getElementById('tagName');
    const showCategoriesBtn = document.getElementById('showCategoriesBtn');
    const categoriesList = document.getElementById('categoriesList');
    const showTagsBtn = document.getElementById('showTagsBtn');
    const tagsList = document.getElementById('tagsList');

    // Bootstrap modal instance
    let modalInstance = null;
    if (adminPanelModal) {
        modalInstance = bootstrap.Modal.getOrCreateInstance(adminPanelModal);

        adminPanelModal.addEventListener('hidden.bs.modal', function () {
            addCategoryForm.reset();
            addTagForm.reset();
            adminPanelMsg.textContent = "";
            categoriesList.style.display = "none";
            tagsList.style.display = "none";
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
                if (categoriesList.style.display !== "none") loadCategories();
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
                if (tagsList.style.display !== "none") loadTags();
            } else {
                const err = await res.json().catch(() => ({}));
                showMsg(err.message || "Błąd dodawania tagu", false);
            }
        } catch {
            showMsg("Błąd sieci", false);
        }
    };

    async function loadCategories() {
        categoriesList.innerHTML = '<li class="list-group-item text-muted">Ładowanie...</li>';
        categoriesList.style.display = "block";
        const token = localStorage.getItem('jwtToken');
        try {
            const res = await fetch('/api/categories', {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                const data = await res.json();
                if (!data.length) {
                    categoriesList.innerHTML = '<li class="list-group-item text-muted">Brak kategorii.</li>';
                } else {
                    categoriesList.innerHTML = data.map(cat =>
                        `<li class="list-group-item d-flex justify-content-between align-items-center">
                            ${cat.name}
                            <button class="btn btn-danger btn-sm ms-2" onclick="deleteCategory(${cat.id})">X</button>
                        </li>`
                    ).join('');
                }
            } else {
                categoriesList.innerHTML = '<li class="list-group-item text-danger">Błąd ładowania kategorii</li>';
            }
        } catch {
            categoriesList.innerHTML = '<li class="list-group-item text-danger">Błąd sieci</li>';
        }
    }

    async function loadTags() {
        tagsList.innerHTML = '<li class="list-group-item text-muted">Ładowanie...</li>';
        tagsList.style.display = "block";
        const token = localStorage.getItem('jwtToken');
        try {
            const res = await fetch('/api/tags', {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                const data = await res.json();
                if (!data.length) {
                    tagsList.innerHTML = '<li class="list-group-item text-muted">Brak tagów.</li>';
                } else {
                    tagsList.innerHTML = data.map(t =>
                        `<li class="list-group-item d-flex justify-content-between align-items-center">
                            <span>${t.name}</span>
                            <button class="btn btn-sm btn-danger" onclick="deleteTag(${t.id})" title="Usuń">
                                <i class="bi bi-x"></i>
                            </button>
                        </li>`
                    ).join('');
                }
            } else {
                tagsList.innerHTML = '<li class="list-group-item text-danger">Błąd ładowania tagów</li>';
            }
        } catch {
            tagsList.innerHTML = '<li class="list-group-item text-danger">Błąd sieci</li>';
        }
    }

    window.deleteCategory = async function(id) {
        if (!confirm("Na pewno usunąć kategorię?")) return;
        const token = localStorage.getItem('jwtToken');
        try {
            const res = await fetch(`/api/categories/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                showMsg("Usunięto kategorię!");
                loadCategories();
            } else {
                showMsg("Błąd usuwania kategorii", false);
            }
        } catch {
            showMsg("Błąd sieci", false);
        }
    };

    window.deleteTag = async function(id) {
        if (!confirm("Na pewno usunąć tag?")) return;
        const token = localStorage.getItem('jwtToken');
        try {
            const res = await fetch(`/api/tags/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                showMsg("Usunięto tag!");
                loadTags();
            } else {
                showMsg("Błąd usuwania tagu", false);
            }
        } catch {
            showMsg("Błąd sieci", false);
        }
    };

    showCategoriesBtn.onclick = function() {
        if (categoriesList.style.display === "none") {
            loadCategories();
        } else {
            categoriesList.style.display = "none";
        }
    };

    showTagsBtn.onclick = function() {
        if (tagsList.style.display === "none") {
            loadTags();
        } else {
            tagsList.style.display = "none";
        }
    };

    categoriesList.classList.add('scrollable-list');
    tagsList.classList.add('scrollable-list');
});