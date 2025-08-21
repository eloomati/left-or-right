document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("proposeTopicForm");
    const categorySelect = document.getElementById('topicCategory');
    const selectedCategoriesList = document.getElementById('selectedCategories');
    const categoriesInput = document.getElementById('categoriesInput');
    let allCategories = [];
    let selectedCategories = [];

    // Wypełnianie selecta kategoriami
// Wypełnianie selecta kategoriami / tagami / itd.
    async function fillSelect(url, selectId, labelKey = "name", valueKey = "id") {
        const select = document.getElementById(selectId);
        if (!select) return;
        try {
            const res = await fetch(url);
            if (!res.ok) return;
            const data = await res.json();

            // zapisz dane do odpowiedniej zmiennej globalnej
            if (selectId === "topicCategory") {
                allCategories = data;
            } else if (selectId === "topicTags") {
                allTags = data;
            } else if (selectId === "topicCountries") {
                allCountries = data;
            } else if (selectId === "topicContinents") {
                allContinents = data;
            }

            // wypełnij select opcjami
            select.innerHTML = data.map(item =>
                `<option value="${item[valueKey]}">${item[labelKey]}</option>`
            ).join('');
        } catch (err) {
            console.error("Błąd w fillSelect:", err);
        }
    }

    fillSelect('/api/categories', 'topicCategory');
    fillSelect('/api/tags', 'topicTags');
    fillSelect('/api/countries', 'topicCountry');
    fillSelect('/api/continents', 'topicContinent');

    // Dwuklik na kategorii - dodaj do wybranych (jeśli nie ma)
    categorySelect?.addEventListener('dblclick', function() {
        Array.from(categorySelect.selectedOptions).forEach(option => {
            const id = option.value;
            if (!selectedCategories.includes(id)) {
                selectedCategories.push(id);
            }
        });
        updateSelectedCategories();
    });

    // Usuwanie z wybranych po kliknięciu na liście
    selectedCategoriesList?.addEventListener('click', function(e) {
        const li = e.target.closest('li');
        if (!li) return;
        const id = li.getAttribute('data-id');
        selectedCategories = selectedCategories.filter(catId => catId !== id);
        updateSelectedCategories();
    });

    function updateSelectedCategories() {
        selectedCategoriesList.innerHTML = selectedCategories.map(id => {
            const cat = allCategories.find(c => String(c.id) === String(id));
            return `<li class="list-group-item py-1" data-id="${id}" style="cursor:pointer;">
                ${cat ? cat.name : id} <span class="text-danger ms-2">&times;</span>
            </li>`;
        }).join('');
        categoriesInput.value = JSON.stringify(selectedCategories);
    }

    // Obsługa formularza
    if (form) {
        form.onsubmit = async function (e) {
            e.preventDefault();
            const msg = document.getElementById('proposeTopicMsg');
            msg.textContent = "";
            msg.className = "";
            // Dodaj wybrane kategorie do wysyłki
            categoriesInput.value = JSON.stringify(selectedCategories);

            tagsInput.value = JSON.stringify(selectedTags);

            const formData = new FormData(form);
            const data = {};
            for (const [key, value] of formData.entries()) {
                if (key === "categories") {
                    data[key] = JSON.parse(value);
                } else {
                    data[key] = value;
                }
            }

            for (const [key, value] of formData.entries()) {
                if (key === "categories" || key === "tags") {
                    data[key] = JSON.parse(value);
                } else {
                    data[key] = value;
                }
            }

            try {
                const res = await fetch('/api/topics/propose', {
                    method: "POST",
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(data)
                });
                if (res.ok) {
                    msg.textContent = "Temat zaproponowany!";
                    msg.className = "text-success";
                    form.reset();
                    selectedCategories = [];
                    updateSelectedCategories();
                    setTimeout(() => {
                        bootstrap.Modal.getInstance(document.getElementById('proposeTopicModal')).hide();
                        window.loadTopicsUniversal({
                            listId: "proposedTopicsList",
                            fetchUrl: "/api/topics/proposed-topics",
                            voteFn: "voteProposed",
                            followFn: "followProposed",
                            toggleCommentsFn: "toggleProposedComments",
                            commentsPrefix: "proposed-"
                        });
                    }, 1000);
                } else {
                    msg.textContent = "Błąd: " + await res.text();
                    msg.className = "text-danger";
                }
            } catch {
                msg.textContent = "Błąd sieci.";
                msg.className = "text-danger";
            }
        };
    }

// --- obsługa tagów ---
    const tagSelect = document.getElementById('topicTags');
    const selectedTagsList = document.getElementById('selectedTags');
    const tagsInput = document.getElementById('tagsInput');
    let allTags = [];
    let selectedTags = [];

// Wypełnianie selecta tagami
    fillSelect('/api/tags', 'topicTags');

// Dwuklik na tagu - dodaj do wybranych (jeśli nie ma)
    tagSelect?.addEventListener('dblclick', function() {
        Array.from(tagSelect.selectedOptions).forEach(option => {
            const id = String(option.value);
            if (!selectedTags.includes(id)) {
                selectedTags.push(id);
            }
        });
        updateSelectedTags();
    });

// Usuwanie z wybranych po kliknięciu na liście
    selectedTagsList?.addEventListener('click', function(e) {
        const li = e.target.closest('li');
        if (!li) return;
        const id = li.getAttribute('data-id');
        selectedTags = selectedTags.filter(tagId => String(tagId) !== String(id));
        updateSelectedTags();
    });

    function updateSelectedTags() {
        selectedTagsList.innerHTML = selectedTags.map(id => {
            const tag = allTags.find(t => String(t.id) === String(id));
            return `<li class="list-group-item py-1" data-id="${id}" style="cursor:pointer;">
            ${tag ? tag.name : id} <span class="text-danger ms-2">&times;</span>
        </li>`;
        }).join('');
        tagsInput.value = JSON.stringify(selectedTags);
    }
});