document.addEventListener("DOMContentLoaded", async function() {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('jwtToken');
    if (!userId || !token) return;

    const headersAuth = { 'Authorization': 'Bearer ' + token };

    // Pobierz dane użytkownika
    async function loadUser() {
        try {
            const res = await fetch(`/api/users/${userId}`, { headers: headersAuth });
            if (res.ok) {
                const user = await res.json();
                document.getElementById('profileUsername').textContent = user.username || "";
                document.getElementById('profileEmail').textContent = user.email || "";
                document.getElementById('profileCountry').textContent = user.country || "";
                document.getElementById('profileContinent').textContent = user.continent || "";
                if (user.avatarUrl) document.getElementById('profileAvatar').src = user.avatarUrl;
            }
        } catch (e) {
            console.error("Błąd ładowania profilu:", e);
        }
    }
    await loadUser();

    // ===== Edycja kraju =====
    setupEditForm("Country", "country");
    // ===== Edycja kontynentu =====
    setupEditForm("Continent", "continent");

    function setupEditForm(name, field) {
        const display = document.getElementById(`${field}Display`);
        const editBtn = document.getElementById(`edit${name}Btn`);
        const form = document.getElementById(`edit${name}Form`);
        const input = document.getElementById(`edit${name}Input`);
        const cancelBtn = document.getElementById(`cancelEdit${name}Btn`);
        const msg = document.getElementById(`edit${name}Msg`);
        const span = document.getElementById(`profile${name}`);

        if (!editBtn || !form || !input) return;

        editBtn.addEventListener("click", () => {
            display.style.display = "none";
            form.style.display = "block";
            input.value = span.textContent;
            msg.innerHTML = "";
        });

        cancelBtn.addEventListener("click", () => {
            form.style.display = "none";
            display.style.display = "block";
        });

        form.onsubmit = async (e) => {
            e.preventDefault();
            const newValue = input.value.trim();
            if (!newValue) return;
            try {
                const res = await fetch(`/api/users/${userId}/profile`, {
                    method: "PUT",
                    headers: { ...headersAuth, "Content-Type": "application/json" },
                    body: JSON.stringify({
                        avatarUrl: null,
                        country: document.getElementById("profileCountry").textContent,
                        continent: document.getElementById("profileContinent").textContent,
                        [field.toLowerCase()]: newValue
                    })
                });
                if (res.ok) {
                    span.textContent = newValue;
                    form.style.display = "none";
                    display.style.display = "block";
                } else {
                    msg.innerHTML = '<span class="text-danger">Błąd zapisu.</span>';
                }
            } catch {
                msg.innerHTML = '<span class="text-danger">Błąd sieci.</span>';
            }
        };
    }

    // ===== Zmiana avatara =====
    const avatarForm = document.getElementById("avatarForm");
    const avatarFile = document.getElementById("avatarFile");
    const avatarMsg = document.getElementById("avatarMsg");
    const avatar = document.getElementById("profileAvatar");

    if (avatarForm) {
        avatarForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            if (!avatarFile.files.length) return;
            const formData = new FormData();
            formData.append("file", avatarFile.files[0]);
            try {
                const res = await fetch(`/api/users/${userId}/avatar`, {
                    method: "POST",
                    headers: headersAuth,
                    body: formData
                });
                if (res.ok) {
                    const user = await res.json();
                    avatar.src = user.avatarUrl;
                    avatarMsg.innerHTML = '<span class="text-success">Zaktualizowano zdjęcie.</span>';
                    setTimeout(() => {
                        avatarMsg.innerHTML = "";
                        if (bootstrap?.Modal) {
                            bootstrap.Modal.getInstance(document.getElementById("avatarModal"))?.hide();
                        }
                    }, 1000);
                } else {
                    avatarMsg.innerHTML = '<span class="text-danger">Błąd zapisu.</span>';
                }
            } catch {
                avatarMsg.innerHTML = '<span class="text-danger">Błąd sieci.</span>';
            }
        });
    }

    // ===== Zmiana hasła =====
    const changePasswordBtn = document.getElementById("changePasswordBtn");
    const changePasswordModalEl = document.getElementById("changePasswordModal");
    const changePasswordForm = document.getElementById("changePasswordForm");
    const changePasswordMsg = document.getElementById("changePasswordMsg");
    const changePasswordModal = changePasswordModalEl && bootstrap?.Modal ? new bootstrap.Modal(changePasswordModalEl) : null;

    if (changePasswordBtn && changePasswordForm && changePasswordModal) {
        changePasswordBtn.addEventListener("click", () => {
            changePasswordForm.reset();
            changePasswordMsg.innerHTML = "";
            changePasswordModal.show();
        });

        changePasswordForm.onsubmit = async (e) => {
            e.preventDefault();
            const currentPassword = document.getElementById("currentPassword").value;
            const newPassword = document.getElementById("newPassword").value;
            const confirmNewPassword = document.getElementById("confirmNewPassword").value;
            if (newPassword !== confirmNewPassword) {
                changePasswordMsg.innerHTML = '<span class="text-danger">Hasła nie są zgodne.</span>';
                return;
            }
            try {
                const res = await fetch(`/api/users/${userId}/change-password`, {
                    method: "POST",
                    headers: { ...headersAuth, "Content-Type": "application/json" },
                    body: JSON.stringify({ currentPassword, newPassword })
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
    }
});
