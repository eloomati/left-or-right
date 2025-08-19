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

    // --- Mechanizm menu użytkownika ---
    const loginBtn = document.querySelector('button[data-bs-target="#loginModal"]');
    const registerBtn = document.querySelector('button[data-bs-target="#registerModal"]');
    const headerBtnContainer = loginBtn?.parentElement;

    function showUserMenu() {
        if (!headerBtnContainer) return;
        if (loginBtn) loginBtn.style.display = "none";
        if (registerBtn) registerBtn.style.display = "none";

        let userMenu = document.getElementById("userMenuDropdown");
        if (!userMenu) {
            userMenu = document.createElement("div");
            userMenu.className = "dropdown d-inline-block";
            userMenu.id = "userMenuDropdown";

            const btn = document.createElement("button");
            btn.className = "btn btn-outline-light rounded-circle dropdown-toggle";
            btn.type = "button";
            btn.id = "userMenuBtn";
            btn.setAttribute("data-bs-toggle", "dropdown");
            btn.setAttribute("aria-expanded", "false");
            btn.style.width = "44px";
            btn.style.height = "44px";
            btn.innerHTML = '<i class="bi bi-person-circle fs-4"></i>';

            const menu = document.createElement("ul");
            menu.className = "dropdown-menu dropdown-menu-end";
            menu.setAttribute("aria-labelledby", "userMenuBtn");
            menu.innerHTML = `
                <li><a class="dropdown-item" href="/notifications">Powiadomienia</a></li>
                <li><a class="dropdown-item" href="/profile">Profil</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item text-danger" href="#" id="logoutMenuBtn">Wyloguj się</a></li>
            `;

            userMenu.appendChild(btn);
            userMenu.appendChild(menu);
            headerBtnContainer.appendChild(userMenu);

            menu.querySelector("#logoutMenuBtn").onclick = logout;
        }
        userMenu.style.display = "inline-block";
    }

    function showLoginRegisterButtons() {
        if (!headerBtnContainer) return;
        if (loginBtn) loginBtn.style.display = "";
        if (registerBtn) registerBtn.style.display = "";
        const userMenu = document.getElementById("userMenuDropdown");
        if (userMenu) userMenu.style.display = "none";
    }

    function logout() {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("userId");
        showLoginRegisterButtons();
        window.location.reload();
    }

    // --- ZMIANA: automatyczne pobieranie userId jeśli jest token, ale nie ma userId ---
    if (localStorage.getItem("jwtToken")) {
        if (!localStorage.getItem("userId")) {
            fetch('/api/users/me', {
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem("jwtToken") }
            })
                .then(res => res.ok ? res.json() : Promise.reject())
                .then(userInfo => {
                    localStorage.setItem('userId', userInfo.id);
                    showUserMenu();
                })
                .catch(() => {
                    localStorage.removeItem("jwtToken");
                    showLoginRegisterButtons();
                });
        } else {
            showUserMenu();
        }
    } else {
        showLoginRegisterButtons();
    }

    // Obsługa formularza rejestracji
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function (e) {
            e.preventDefault();

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
                    const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'))
                        || new bootstrap.Modal(document.getElementById('registerModal'));
                    modal.hide();
                    window.location.href = '/register-success';
                } else {
                    const errMsg = await response.text();
                    showRegisterError("Rejestracja nie powiodła się: " + errMsg);
                }
            } catch (err) {
                showRegisterError("Błąd sieci podczas rejestracji");
            }
        });
    }

    // Obsługa formularza logowania
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const form = e.target;
            const data = {
                username: form.username.value,
                password: form.password.value
            };

            try {
                const response = await fetch('/api/users/login', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(data)
                });

                if (response.status === 200) {
                    const token = await response.text();
                    localStorage.setItem('jwtToken', token);

                    // Pobierz userId z endpointu /api/users/me
                    const userInfoRes = await fetch('/api/users/me', {
                        headers: {'Authorization': 'Bearer ' + token}
                    });
                    if (userInfoRes.ok) {
                        const userInfo = await userInfoRes.json();
                        localStorage.setItem('userId', userInfo.id);
                        window.location.href = '/';
                    } else {
                        showLoginError("Nie udało się pobrać danych użytkownika.");
                    }
                } else {
                    const errMsg = await response.text();
                    showLoginError("Błąd logowania: " + errMsg);
                }
            } catch (err) {
                showLoginError("Błąd sieci podczas logowania");
            }
        });
    }

    // --- Ładowanie popularnych tematów na stronie głównej ---
    const topicsList = document.getElementById("topicsList");
    const pagination = document.getElementById("pagination");

    if (topicsList && pagination) {
        loadTopics(0);

        function loadTopics(page) {
            fetch(`/api/topics/popular?page=${page}&size=10`)
                .then(res => res.json())
                .then(data => {
                    console.log("📦 Topics response:", data);
                    console.log("📋 Content array:", data.content);

                    topicsList.innerHTML = data.content
                        .map(topic => `
        <li class="list-group-item d-flex justify-content-between align-items-center">
            <span>${topic.title}</span>
            <div>
                <button class="btn btn-success btn-sm me-1" onclick="vote(${topic.id}, 'RIGHT')">PRAWO</button>
                <button class="btn btn-danger btn-sm me-1" onclick="vote(${topic.id}, 'LEFT')">LEWO</button>
                <button class="btn btn-link btn-sm" onclick="showComments(${topic.id}, 'RIGHT')">Komentarze PRAWO</button>
                <button class="btn btn-link btn-sm" onclick="showComments(${topic.id}, 'LEFT')">Komentarze LEWO</button>
            </div>
        </li>
    `)
                        .join("");

                    // Paginacja
                    pagination.innerHTML = "";
                    for (let i = 0; i < data.totalPages; i++) {
                        const li = document.createElement("li");
                        li.className = "page-item" + (i === data.number ? " active" : "");
                        const a = document.createElement("a");
                        a.className = "page-link";
                        a.href = "#";
                        a.textContent = i + 1;
                        a.onclick = (e) => {
                            e.preventDefault();
                            loadTopics(i);
                        };
                        li.appendChild(a);
                        pagination.appendChild(li);
                    }
                })
                .catch(err => {
                    console.error("❌ Błąd ładowania tematów:", err);
                    topicsList.innerHTML = `<li class="list-group-item text-danger">Błąd wczytywania tematów</li>`;
                });
        }
    }
});

// --- Funkcje globalne ---

// Funkcja głosowania dostępna globalnie
window.vote = async function(topicId, side) {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        alert("Musisz być zalogowany, aby głosować.");
        return;
    }
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("Brak userId. Zaloguj się ponownie.");
        return;
    }
    try {
        const res = await fetch(`/api/votes/vote?userId=${userId}&topicId=${topicId}&side=${side}`, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            alert("Głos oddany!");
        } else {
            alert("Błąd głosowania: " + await res.text());
        }
    } catch (e) {
        alert("Błąd sieci: " + e);
    }
};

// Placeholder, by nie było błędu JS jeśli nie masz jeszcze tej funkcji
window.showComments = function(topicId, side) {
    alert("Komentarze dla tematu " + topicId + " po stronie " + side);
};

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

// Funkcja do wyświetlania błędów logowania
function showLoginError(message) {
    let errorBox = document.getElementById("loginErrorBox");
    if (!errorBox) {
        const form = document.getElementById("loginForm");
        errorBox = document.createElement("div");
        errorBox.id = "loginErrorBox";
        errorBox.className = "alert alert-danger mt-2";
        form.prepend(errorBox);
    }
    errorBox.innerText = message;
}