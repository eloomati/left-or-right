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

    // Uniwersalne ładowanie listy tematów
    if (document.getElementById("proposedTopicsList")) {
        window.loadTopicsUniversal({
            listId: "proposedTopicsList",
            fetchUrl: "/api/proposed-topics",
            voteFn: "voteProposed",
            followFn: "followProposed",
            toggleCommentsFn: "toggleProposedComments",
            commentsPrefix: "proposed-"
        });
    }
    if (document.getElementById("topicsList") && document.getElementById("pagination")) {
        window.loadTopicsUniversal({
            listId: "topicsList",
            fetchUrl: "/api/topics/popular?page=0&size=10",
            voteFn: "vote",
            followFn: "followTopic",
            toggleCommentsFn: "toggleComments",
            commentsPrefix: ""
        });
    }

// --- Mechanizm menu użytkownika ---
    const loginBtn = document.querySelector('button[data-bs-target="#loginModal"]');
    const registerBtn = document.querySelector('button[data-bs-target="#registerModal"]');
    const headerBtnContainer = loginBtn?.parentElement;

    async function showUserMenu() {
        if (!headerBtnContainer) return;
        if (loginBtn) loginBtn.style.display = "none";
        if (registerBtn) registerBtn.style.display = "none";

        let userMenu = document.getElementById("userMenuDropdown");
        if (!userMenu) {
            userMenu = document.createElement("div");
            userMenu.className = "dropdown d-inline-block";
            userMenu.id = "userMenuDropdown";

            // Pobierz avatar użytkownika
            let avatarUrl = null;
            try {
                const token = localStorage.getItem("jwtToken");
                if (token) {
                    const res = await fetch('/api/users/me', {
                        headers: { "Authorization": "Bearer " + token }
                    });
                    if (res.ok) {
                        const user = await res.json();
                        avatarUrl = user.avatarUrl;
                    }
                }
            } catch {}

            const btn = document.createElement("button");
            btn.className = "btn rounded-circle dropdown-toggle";
            btn.type = "button";
            btn.id = "userMenuBtn";
            btn.setAttribute("data-bs-toggle", "dropdown");
            btn.setAttribute("aria-expanded", "false");
            btn.style.width = "44px";
            btn.style.height = "44px";
            btn.style.padding = "0";
            btn.style.overflow = "hidden";
            btn.style.position = "relative";
            btn.style.background = "none";
            btn.style.border = "none";

            if (avatarUrl) {
                btn.innerHTML = `<img src="${avatarUrl}" alt="avatar" style="position:absolute;top:0;left:0;width:100%;height:100%;object-fit:cover;border-radius:50%;">`;
            } else {
                btn.innerHTML = '<i class="bi bi-person-circle fs-4"></i>';
            }

            const menu = document.createElement("ul");
            menu.className = "dropdown-menu dropdown-menu-end";
            menu.setAttribute("aria-labelledby", "userMenuBtn");
            menu.innerHTML = `
            <li><a class="dropdown-item" href="/notifications">Powiadomienia</a></li>
            <li><a class="dropdown-item" href="#" id="profileTabBtn">Profil</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="#" id="logoutMenuBtn">Wyloguj się</a></li>
        `;

            userMenu.appendChild(btn);
            userMenu.appendChild(menu);
            headerBtnContainer.appendChild(userMenu);

            menu.querySelector("#logoutMenuBtn").onclick = logout;
            menu.querySelector("#profileTabBtn").onclick = function(e) {
                e.preventDefault();
                window.location.href = "/profile";
            };
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
});

// --- Uniwersalne ładowanie tematów (popularnych i proponowanych) ---
window.loadTopicsUniversal = function({
                                          listId,
                                          fetchUrl,
                                          voteFn,
                                          followFn,
                                          toggleCommentsFn,
                                          commentsPrefix
                                      }) {
    fetch(fetchUrl)
        .then(res => res.json())
        .then(data => {
            // Obsługa paginacji dla popularnych tematów
            if (listId === "topicsList" && data.content) data = data.content;
            const list = document.getElementById(listId);
            if (!data || !data.length) {
                list.innerHTML = '<li class="list-group-item text-muted">Brak tematów.</li>';
                return;
            }
            list.innerHTML = data.map(t =>
                `<li class="list-group-item d-flex flex-column" id="${commentsPrefix}topic-${t.id}">
                    <div class="d-flex justify-content-between align-items-center">
                        <span>${t.title}</span>
                        <div>
                            <button class="btn btn-success btn-sm me-1" onclick="${voteFn}(${t.id}, 'RIGHT')">PRAWO</button>
                            <button class="btn btn-danger btn-sm me-1" onclick="${voteFn}(${t.id}, 'LEFT')">LEWO</button>
                            <button class="btn btn-secondary btn-sm me-1" onclick="${followFn}(${t.id})">Follow</button>
                            <button class="btn btn-link btn-sm" onclick="${toggleCommentsFn}(${t.id}, 'RIGHT')">Komentarze PRAWO</button>
                            <button class="btn btn-link btn-sm" onclick="${toggleCommentsFn}(${t.id}, 'LEFT')">Komentarze LEWO</button>
                        </div>
                    </div>
                    <div class="text-muted small mb-2">${t.description || t.desctription || ""}</div>
                    <div class="comments-container mt-2" id="${commentsPrefix}comments-${t.id}-RIGHT" style="display:none"></div>
                    <div class="comments-container mt-2" id="${commentsPrefix}comments-${t.id}-LEFT" style="display:none"></div>
                </li>`
            ).join("");
        });
};

// --- Uniwersalne funkcje globalne dla tematów i propozycji ---

window.vote = async function(topicId, side) {
    await voteUniversal(`/api/votes/vote`, topicId, side);
};
window.voteProposed = async function(topicId, side) {
    await voteUniversal(`/api/votes/proposed-topic/vote`, topicId, side);
};
async function voteUniversal(url, topicId, side) {
    // ZAMIANA: topicId => proposedTopicId dla propozycji
    const isProposed = url.includes("proposed-topic");
    const idParam = isProposed ? "proposedTopicId" : "topicId";
    const token = localStorage.getItem("jwtToken");
    if (!token) return alert("Musisz być zalogowany, aby głosować.");
    const userId = localStorage.getItem("userId");
    if (!userId) return alert("Brak userId. Zaloguj się ponownie.");
    try {
        const res = await fetch(`${url}?userId=${userId}&${idParam}=${topicId}&side=${side}`, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            alert("Głos oddany!");
        } else {
            // obsługa błędu
        }
    } catch (e) {
        alert("Błąd sieci: " + e);
    }
}

window.followTopic = async function(topicId) {
    await followUniversal(`/api/topics/${topicId}/watch`);
};
window.followProposed = async function(topicId) {
    await followUniversal(`/api/topics/proposed/${topicId}/watch`);
};
async function followUniversal(url) {
    const token = localStorage.getItem("jwtToken");
    if (!token) return alert("Musisz być zalogowany, aby obserwować temat.");
    try {
        const res = await fetch(url, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            alert("Dodano do obserwowanych!");
        } else {
            alert("Błąd obserwowania: " + await res.text());
        }
    } catch (e) {
        alert("Błąd sieci: " + e);
    }
}

// --- Uniwersalne komentarze ---
window.toggleComments = function(topicId, side) {
    toggleCommentsUniversal({
        topicId,
        side,
        commentsUrl: `/api/comments/by-topic-and-side`,
        postUrl: `/api/comments`,
        putUrl: `/api/comments/`,
        deleteUrl: `/api/comments/`,
        containerPrefix: ""
    });
};
window.toggleProposedComments = function(proposedTopicId, side) {
    toggleCommentsUniversal({
        topicId: proposedTopicId,
        side,
        commentsUrl: `/api/comments/by-proposed-topic-and-side`,
        postUrl: `/api/comments`,
        putUrl: `/api/comments/`,
        deleteUrl: `/api/comments/`,
        containerPrefix: "proposed-"
    });
};

function toggleCommentsUniversal({
                                     topicId, side, commentsUrl, postUrl, putUrl, deleteUrl, containerPrefix
                                 }) {
    const loggedUserId = localStorage.getItem("userId");
    const token = localStorage.getItem("jwtToken");
    const containerId = `${containerPrefix}comments-${topicId}-${side}`;
    const container = document.getElementById(containerId);
    if (!container) return;

    // Rozróżnienie parametrów w URL
    let url;
    if (containerPrefix === "proposed-") {
        url = `${commentsUrl}?proposedTopicId=${topicId}&side=${side}`;
    } else {
        url = `${commentsUrl}?topicId=${topicId}&side=${side}`;
    }

    if (container.style.display === "none" || container.innerHTML === "") {
        fetch(url, {
            headers: token ? { "Authorization": "Bearer " + token } : {}
        })
            .then(res => {
                if (!res.ok) throw new Error();
                return res.json();
            })
            .then(comments => {
                let commentsHtml = "";
                if (!Array.isArray(comments) || comments.length === 0) {
                    commentsHtml = `<div class="text-muted">Brak komentarzy po stronie ${side}.</div>`;
                } else {
                    commentsHtml = `
    <ul class="list-group">
        ${comments.map(c => {
                        const date = new Date(c.createdAt);
                        const formattedDate = date.toLocaleString();
                        const isOwn = (loggedUserId && c.user && String(c.user.id) === String(loggedUserId));
                        return `<li class="list-group-item py-1" data-comment-id="${c.id}">
<strong>${(c.user && c.user.username) ? c.user.username : "Anon"}:</strong>
<span class="comment-content"${isOwn ? ` ondblclick="startEditCommentUniversal(${c.id}, ${topicId}, '${side}', '${putUrl}', '${containerPrefix}')" ` : ""}>${c.content}</span>
<div class="text-muted small d-flex align-items-center">
    <span>${formattedDate}</span>
    ${
                            isOwn
                                ? `<button class="btn btn-link btn-sm p-0 ms-2 text-danger" title="Usuń" onclick="deleteCommentUniversal(${c.id}, ${topicId}, '${side}', '${deleteUrl}', '${containerPrefix}')">
                <i class="bi bi-trash"></i>
               </button>`
                                : ""
                        }
</div>
</li>`;
                    }).join("")}
    </ul>
`;
                }
                container.innerHTML = `
                    <div class="comments-scroll">
                        ${commentsHtml}
                    </div>
                    <form class="mt-2 comments-form-sticky" onsubmit="return submitCommentUniversal(event, ${topicId}, '${side}', '${postUrl}', '${containerPrefix}')">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="Dodaj komentarz..." name="commentContent" required maxlength="500">
                            <button class="btn btn-primary" type="submit">Wyślij</button>
                        </div>
                        <div class="invalid-feedback text-danger" style="display:none"></div>
                    </form>
                `;
                container.style.position = "relative";
                container.style.display = "block";
            })
            .catch(() => {
                container.innerHTML = `<div class="text-danger">Błąd pobierania komentarzy.</div>`;
                container.style.position = "relative";
                container.style.display = "block";
            });
    } else {
        container.style.display = "none";
    }
}

window.startEditCommentUniversal = function(commentId, topicId, side, putUrl, containerPrefix) {
    const li = document.querySelector(`[data-comment-id="${commentId}"]`);
    const contentSpan = li.querySelector('.comment-content');
    const oldContent = contentSpan.textContent;

    contentSpan.innerHTML = `
        <input type="text" class="form-control form-control-sm d-inline w-75" value="${oldContent.replace(/"/g, '&quot;')}" id="editInput${commentId}">
        <button class="btn btn-sm btn-success ms-1" onclick="saveEditCommentUniversal(${commentId}, ${topicId}, '${side}', '${putUrl}', '${containerPrefix}')">Zapisz</button>
        <button class="btn btn-sm btn-secondary ms-1" onclick="cancelEditCommentUniversal(${commentId}, '${oldContent.replace(/'/g, "\\'")}')">Anuluj</button>
    `;
    document.getElementById(`editInput${commentId}`).focus();
};

window.saveEditCommentUniversal = function(commentId, topicId, side, putUrl, containerPrefix) {
    const input = document.getElementById(`editInput${commentId}`);
    const newContent = input.value.trim();
    if (!newContent) return;
    const token = localStorage.getItem("jwtToken");

    // Ustal odpowiedni parametr ID
    let bodyObj = {
        content: newContent,
        side: side
    };
    if (containerPrefix === "proposed-") {
        bodyObj.proposedTopicId = topicId;
    } else {
        bodyObj.topicId = topicId;
    }

    fetch(`${putUrl}${commentId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify(bodyObj)
    })
        .then(res => {
            if (res.ok) {
                if (containerPrefix === "proposed-") {
                    window.toggleProposedComments(topicId, side);
                } else {
                    window.toggleComments(topicId, side);
                }
            } else {
                res.text().then(msg => alert("Błąd edycji: " + msg));
            }
        })
        .catch(() => alert("Błąd sieci."));
};

window.cancelEditCommentUniversal = function(commentId, oldContent) {
    const li = document.querySelector(`[data-comment-id="${commentId}"]`);
    const contentSpan = li.querySelector('.comment-content');
    contentSpan.textContent = oldContent;
};

window.deleteCommentUniversal = async function(commentId, topicId, side, deleteUrl, containerPrefix) {
    const token = localStorage.getItem("jwtToken");
    if (!token) return alert("Musisz być zalogowany.");
    if (!confirm("Na pewno usunąć komentarz?")) return;
    try {
        const res = await fetch(`${deleteUrl}${commentId}`, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            if (containerPrefix === "proposed-") {
                window.toggleProposedComments(topicId, side);
            } else {
                window.toggleComments(topicId, side);
            }
        } else {
            alert("Błąd usuwania: " + await res.text());
        }
    } catch (e) {
        alert("Błąd sieci.");
    }
};

window.submitCommentUniversal = async function(event, topicId, side, postUrl, containerPrefix) {
    event.preventDefault();
    const form = event.target;
    const input = form.commentContent;
    const errorBox = form.querySelector('.invalid-feedback');
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        errorBox.textContent = "Musisz być zalogowany, aby dodać komentarz.";
        errorBox.style.display = "block";
        return false;
    }
    errorBox.style.display = "none";

    // Ustal odpowiedni parametr ID
    let bodyObj = {
        side: side,
        content: input.value
    };
    if (containerPrefix === "proposed-") {
        bodyObj.proposedTopicId = topicId;
    } else {
        bodyObj.topicId = topicId;
    }

    try {
        const res = await fetch(postUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(bodyObj)
        });
        if (res.ok) {
            input.value = "";
            if (containerPrefix === "proposed-") {
                window.toggleProposedComments(topicId, side);
            } else {
                window.toggleComments(topicId, side);
            }
        } else {
            const msg = await res.text();
            errorBox.textContent = "Błąd: " + msg;
            errorBox.style.display = "block";
        }
    } catch (e) {
        errorBox.textContent = "Błąd sieci.";
        errorBox.style.display = "block";
    }
    return false;
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