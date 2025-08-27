document.addEventListener("DOMContentLoaded", async function() {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('jwtToken');
    if (!userId || !token) return;

    let countries = [];
    let continents = [];

    // Pobierz listę krajów i kontynentów
    async function loadCountriesAndContinents() {
        try {
            const resCountries = await fetch('/api/countries');
            countries = await resCountries.json();
        } catch {}
        try {
            const resContinents = await fetch('/api/continents');
            continents = await resContinents.json();
        } catch {}
    }

    // Pobierz dane użytkownika
    let user = {};
    async function loadUser() {
        try {
            const res = await fetch(`/api/users/${userId}`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                user = await res.json();
                document.getElementById('profileUsername').textContent = user.username || "";
                document.getElementById('profileEmail').textContent = user.email || "";
                document.getElementById('profileCountry').textContent = user.country?.name || user.country || "";
                document.getElementById('profileContinent').textContent = user.country?.continent?.name || user.continent?.name || user.continent || "";
                if (user.avatarUrl) {
                    document.getElementById('profileAvatar').src = user.avatarUrl;
                }
            }
        } catch {}
    }

    // Inicjalizacja
    await loadCountriesAndContinents();
    await loadUser();

    // --- Obsługa edycji kraju ---
    const countryDisplay = document.getElementById('countryDisplay');
    const editCountryBtn = document.getElementById('editCountryBtn');
    const editCountryForm = document.getElementById('editCountryForm');
    const editCountrySelect = document.getElementById('editCountrySelect');
    const cancelEditCountryBtn = document.getElementById('cancelEditCountryBtn');
    const editCountryMsg = document.getElementById('editCountryMsg');
    const profileCountry = document.getElementById('profileCountry');
    const profileContinent = document.getElementById('profileContinent');

    function fillCountrySelect(selectedName) {
        editCountrySelect.innerHTML = countries.map(c =>
            `<option value="${c.id}" ${c.name === selectedName ? 'selected' : ''}>${c.name}</option>`
        ).join('');
    }

    editCountrySelect.addEventListener('change', function() {
        const selectedCountry = countries.find(c => c.id == editCountrySelect.value);
        if (selectedCountry && selectedCountry.continent) {
            profileContinent.textContent = selectedCountry.continent.name;
        }
    });

    editCountryBtn.addEventListener('click', function() {
        countryDisplay.style.display = 'none';
        editCountryForm.style.display = 'block';
        fillCountrySelect(profileCountry.textContent);
        editCountryMsg.innerHTML = "";
    });
    cancelEditCountryBtn.addEventListener('click', function() {
        editCountryForm.style.display = 'none';
        countryDisplay.style.display = 'block';
    });
    editCountryForm.onsubmit = async function(e) {
        e.preventDefault();
        const selectedCountryId = editCountrySelect.value;
        const selectedCountry = countries.find(c => c.id == selectedCountryId);
        if (!selectedCountry) return;
        try {
            const res = await fetch(`/api/users/${userId}/profile`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    avatarUrl: null,
                    country: selectedCountry.name,
                    continent: selectedCountry.continent.name
                })
            });
            if (res.ok) {
                profileCountry.textContent = selectedCountry.name;
                profileContinent.textContent = selectedCountry.continent.name;
                editCountryForm.style.display = 'none';
                countryDisplay.style.display = 'block';
            } else {
                editCountryMsg.innerHTML = '<span class="text-danger">Błąd zapisu.</span>';
            }
        } catch {
            editCountryMsg.innerHTML = '<span class="text-danger">Błąd sieci.</span>';
        }
    };

    // --- Obsługa zmiany avatara ---
    const avatar = document.getElementById('profileAvatar');
    const avatarForm = document.getElementById('avatarForm');
    const avatarFile = document.getElementById('avatarFile');
    const avatarMsg = document.getElementById('avatarMsg');

    avatarForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        if (!avatarFile.files.length) return;
        const formData = new FormData();
        formData.append('file', avatarFile.files[0]);
        try {
            const res = await fetch(`/api/users/${userId}/avatar`, {
                method: "POST",
                headers: { "Authorization": "Bearer " + token },
                body: formData
            });
            if (res.ok) {
                const user = await res.json();
                avatar.src = user.avatarUrl;
                avatarMsg.innerHTML = '<span class="text-success">Zaktualizowano zdjęcie.</span>';
                setTimeout(() => {
                    avatarMsg.innerHTML = "";
                    bootstrap.Modal.getInstance(document.getElementById('avatarModal')).hide();
                }, 1000);
            } else {
                avatarMsg.innerHTML = '<span class="text-danger">Błąd zapisu.</span>';
            }
        } catch {
            avatarMsg.innerHTML = '<span class="text-danger">Błąd sieci.</span>';
        }
    });

    // Wyświetlenie daty utworzenia profilu
    document.getElementById('profileCreatedAt').textContent =
        new Date(user.createdAt).toLocaleString('pl-PL', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

    // --- Obsługa zmiany hasła ---
    const changePasswordBtn = document.getElementById('changePasswordBtn');
    const changePasswordModal = new bootstrap.Modal(document.getElementById('changePasswordModal'));
    const changePasswordForm = document.getElementById('changePasswordForm');
    const changePasswordMsg = document.getElementById('changePasswordMsg');

    changePasswordBtn.addEventListener('click', function() {
        changePasswordForm.reset();
        changePasswordMsg.innerHTML = "";
        changePasswordModal.show();
    });

    changePasswordForm.onsubmit = async function(e) {
        e.preventDefault();
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmNewPassword = document.getElementById('confirmNewPassword').value;
        if (newPassword !== confirmNewPassword) {
            changePasswordMsg.innerHTML = '<span class="text-danger">Hasła nie są zgodne.</span>';
            return;
        }
        try {
            const res = await fetch(`/api/users/${userId}/change-password`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    currentPassword,
                    newPassword
                })
            });
            if (res.ok) {
                changePasswordMsg.innerHTML = '<span class="text-success">Hasło zmienione.</span>';
                setTimeout(() => {
                    changePasswordMsg.innerHTML = "";
                    changePasswordModal.hide();
                }, 1000);
            } else {
                const msg = await res.text();
                changePasswordMsg.innerHTML = '<span class="text-danger">' + msg + '</span>';
            }
        } catch {
            changePasswordMsg.innerHTML = '<span class="text-danger">Błąd sieci.</span>';
        }
    };

    async function loadNotifications() {
        try {
            const res = await fetch('/api/notifications', {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            if (res.ok) {
                const notifications = await res.json();
                const list = document.getElementById('notificationsList');
                list.innerHTML = notifications.length
                    ? notifications.map(n =>
                        `<li class="list-group-item notification-item" data-id="${n.id}" style="cursor:pointer;">
                        ${n.message} <small class="text-muted">(${n.count})</small>
                     </li>`
                    ).join('')
                    : '<li class="list-group-item text-muted">Brak powiadomień</li>';

                // PODPINANIE OBSŁUGI KLIKNIĘCIA PO KAŻDYM ODŚWIEŻENIU LISTY
                list.querySelectorAll('.notification-item').forEach(item => {
                    item.addEventListener('click', async function() {
                        const id = this.getAttribute('data-id');
                        await fetch(`/api/notifications/${id}/read`, {
                            method: "PUT",
                            headers: { 'Authorization': 'Bearer ' + token }
                        });
                        await loadNotifications();
                    });
                });
            }
        } catch {}
    }

// Po załadowaniu profilu:
    await loadNotifications();

});
