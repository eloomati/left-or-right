document.addEventListener("DOMContentLoaded", () => {

    const btn = document.getElementById('showProposeBtn');
    btn?.addEventListener('click', function() {
        const modalEl = document.getElementById('proposeTopicModal');
        if (!modalEl) {
            console.error("Nie znaleziono modala!");
            return;
        }
        const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
        modal.show();
    });

    const form = document.getElementById("proposeTopicForm");
    const categorySelect = document.getElementById('topicCategory');
    const selectedCategoriesList = document.getElementById('selectedCategories');
    const categoriesInput = document.getElementById('categoriesInput');
    let allCategories = [];
    let selectedCategories = [];

    // --- Wypełnianie selectów ---
    async function fillSelect(url, selectId, labelKey = "name", valueKey = "id") {
        const select = document.getElementById(selectId);
        if (!select) return;
        try {
            const res = await fetch(url);
            if (!res.ok) return;
            const data = await res.json();
            if (selectId === "topicCategory") allCategories = data;
            if (selectId === "topicTags") allTags = data;
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

    // --- Kategorie ---
    categorySelect?.addEventListener('dblclick', function() {
        Array.from(categorySelect.selectedOptions).forEach(option => {
            const id = option.value;
            if (!selectedCategories.includes(id)) selectedCategories.push(id);
        });
        updateSelectedCategories();
    });
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

    // --- Tagi ---
    const tagSelect = document.getElementById('topicTags');
    const selectedTagsList = document.getElementById('selectedTags');
    const tagsInput = document.getElementById('tagsInput');
    let allTags = [];
    let selectedTags = [];
    tagSelect?.addEventListener('dblclick', function() {
        Array.from(tagSelect.selectedOptions).forEach(option => {
            const id = String(option.value);
            if (!selectedTags.includes(id)) selectedTags.push(id);
        });
        updateSelectedTags();
    });
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

    // --- Obsługa formularza ---
    if (form) {
        form.onsubmit = async function (e) {
            e.preventDefault();
            const msg = document.getElementById('proposeTopicMsg');
            msg.textContent = "";
            msg.className = "";

            // Zbierz dane z formularza
            categoriesInput.value = JSON.stringify(selectedCategories);
            tagsInput.value = JSON.stringify(selectedTags);
            const userId = localStorage.getItem("userId");

            const data = {
                title: form.title.value,
                description: form.description.value,
                categories: selectedCategories.map(id => ({ id: Number(id) })),
                tags: selectedTags.map(id => ({ id: Number(id) })),
                country: form.country.value,
                continent: form.continent.value,
                proposedById: userId
            };

            try {
                const token = localStorage.getItem("jwtToken");
                const res = await fetch('/api/proposed-topics', {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        ...(token ? { 'Authorization': 'Bearer ' + token } : {})
                    },
                    body: JSON.stringify(data)
                });
                if (res.ok) {
                    msg.textContent = "Temat zaproponowany!";
                    msg.className = "text-success";
                    form.reset();
                    selectedCategories = [];
                    selectedTags = [];
                    updateSelectedCategories();
                    updateSelectedTags();
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
                    const errorText = await res.text();
                    msg.textContent = "Błąd: " + errorText;
                    msg.className = "text-danger";
                }
            } catch (e) {
                msg.textContent = "Błąd sieci.";
                msg.className = "text-danger";
            }
        };
    }
});