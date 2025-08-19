const filterBtn = document.getElementById('filterButton');
const sidebarEl = document.getElementById('sidebar');

// Synchronizacja przycisku z sidebar
sidebarEl.addEventListener('show.bs.offcanvas', () => {
    filterBtn.style.left = sidebarEl.offsetWidth + 'px';
});
sidebarEl.addEventListener('hide.bs.offcanvas', () => {
    filterBtn.style.left = '0';
});