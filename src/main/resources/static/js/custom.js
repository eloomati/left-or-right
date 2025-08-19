document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ custom.js loaded");

    const filterBtn = document.getElementById('filterButton');
    const sidebarEl = document.getElementById('sidebar');

    // Synchronizacja przycisku z sidebar
    if (filterBtn && sidebarEl) {
        sidebarEl.addEventListener('show.bs.offcanvas', () => {
            filterBtn.style.left = sidebarEl.offsetWidth + 'px';
        });
        sidebarEl.addEventListener('hide.bs.offcanvas', () => {
            filterBtn.style.left = '0';
        });
    }

    // Obsługa formularza rejestracji
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        console.log("✅ Found #registerForm, attaching handler");

        registerForm.addEventListener('submit', async function (e) {
            e.preventDefault(); // blokujemy standardowe wysłanie formularza
            console.log("🚀 Submit handler triggered");

            const form = e.target;
            const data = {
                username: form.username.value,
                email: form.email.value,
                confirmEmail: form.confirmEmail.value,
                password: form.password.value,
                confirmPassword: form.confirmPassword.value,
                termsAccepted: form.termsAccepted.checked
            };

            try {
                const response = await fetch('/api/users/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(data)
                });

                if (response.status === 201) {
                    console.log("✅ Registration success");
                    // zamykamy modal ręcznie
                    const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'))
                        || new bootstrap.Modal(document.getElementById('registerModal'));
                    modal.hide();

                    // przekierowanie
                    window.location.href = '/register-success';
                } else {
                    const errMsg = await response.text();
                    console.warn("⚠️ Registration failed", response.status, errMsg);
                    showRegisterError("Rejestracja nie powiodła się: " + errMsg);
                }
            } catch (err) {
                console.error("❌ Request error", err);
                showRegisterError("Błąd sieci podczas rejestracji");
            }
        });
    } else {
        console.warn("⚠️ Nie znaleziono formularza #registerForm");
    }
});

/**
 * Funkcja pokazująca komunikat błędu w formularzu rejestracji
 */
function showRegisterError(message) {
    let errorBox = document.getElementById("registerErrorBox");
    if (!errorBox) {
        const form = document.getElementById("registerForm");
        errorBox = document.createElement("div");
        errorBox.id = "registerErrorBox";
        errorBox.className = "alert alert-danger mt-2";
        form.prepend(errorBox);
    }
    errorBox.innerText = message;
}
