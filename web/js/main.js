/**
 * main.js – Core JS for Sunrise Dental Clinic
 * Handles: login, registration, auth guard, sidebar setup, toast notifications.
 * CIS6003 Advanced Programming
 */

document.addEventListener('DOMContentLoaded', () => {
    const loginForm    = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm)    loginForm.addEventListener('submit', handleLogin);
    if (registerForm) registerForm.addEventListener('submit', handleRegister);
});

/* =====================================================================
   AUTH – Login
   ===================================================================== */
async function handleLogin(e) {
    e.preventDefault();
    const form       = e.target;
    const btn        = document.getElementById('loginBtn');
    const btnText    = btn.querySelector('span');
    const loader     = btn.querySelector('.loader');
    const errorEl    = document.getElementById('loginError');
    const successEl  = document.getElementById('loginSuccess');

    setLoading(btn, btnText, loader, true);
    hideMsg(errorEl);
    hideMsg(successEl);

    const data = new URLSearchParams(new FormData(form));

    try {
        const res    = await fetch('api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data
        });
        const result = await res.json();

        if (result.status === 'success') {
            localStorage.setItem('userRole', result.role);
            localStorage.setItem('userName', result.name);
            showMsg(successEl, 'Login successful! Redirecting...');
            setTimeout(() => { window.location.href = 'dashboard.html'; }, 600);
        } else {
            showMsg(errorEl, result.message || 'Invalid credentials.');
            setLoading(btn, btnText, loader, false);
        }
    } catch (err) {
        showMsg(errorEl, 'Server connection error. Please try again.');
        setLoading(btn, btnText, loader, false);
    }
}

/* =====================================================================
   AUTH – Registration
   ===================================================================== */
async function handleRegister(e) {
    e.preventDefault();
    const form      = e.target;
    const btn       = document.getElementById('registerBtn');
    const btnText   = btn.querySelector('span');
    const loader    = btn.querySelector('.loader');
    const errorEl   = document.getElementById('registerError');
    const successEl = document.getElementById('registerSuccess');

    setLoading(btn, btnText, loader, true);
    hideMsg(errorEl);
    hideMsg(successEl);

    // Client-side validation
    const pwd    = document.getElementById('regPassword').value;
    const cpwd   = document.getElementById('regConfirmPassword').value;
    const role   = document.getElementById('regRole').value;

    if (!role) {
        showMsg(errorEl, 'Please select a role.');
        setLoading(btn, btnText, loader, false);
        return;
    }
    if (pwd !== cpwd) {
        showMsg(errorEl, 'Passwords do not match.');
        setLoading(btn, btnText, loader, false);
        return;
    }
    if (pwd.length < 6) {
        showMsg(errorEl, 'Password must be at least 6 characters.');
        setLoading(btn, btnText, loader, false);
        return;
    }

    const data = new URLSearchParams(new FormData(form));

    try {
        const res    = await fetch('api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data
        });
        const result = await res.json();

        if (result.status === 'success') {
            showMsg(successEl, result.message);
            form.reset();
        } else {
            showMsg(errorEl, result.message || 'Registration failed.');
        }
    } catch (err) {
        showMsg(errorEl, 'Server error. Please try again.');
    } finally {
        setLoading(btn, btnText, loader, false);
    }
}

/* =====================================================================
   AUTH – Guard (protects dashboard pages)
   ===================================================================== */
function checkAuth() {
    const role = localStorage.getItem('userRole');
    const path = window.location.pathname;
    // Redirect to login if not authenticated and not already on login page
    if (!role && !path.endsWith('index.html') && path !== '/SunriseDental/' && path !== '/SunriseDental') {
        window.location.href = 'index.html';
    }
}

/* =====================================================================
   SIDEBAR – User info + Logout
   ===================================================================== */
function setupDashboardLayout() {
    const nameEl = document.getElementById('sidebarUserName');
    const roleEl = document.getElementById('sidebarUserRole');
    const rawRole = localStorage.getItem('userRole') || 'Staff';

    if (nameEl) nameEl.textContent = localStorage.getItem('userName') || 'User';
    if (roleEl) roleEl.textContent = toTitleCase(rawRole);
    setupNotifications();

    // Role-based nav visibility
    const role = localStorage.getItem('userRole');
    if (role === 'DENTIST') {
        const revenueCard = document.getElementById('revenueCard');
        const navReports  = document.getElementById('navReports');
        const navBilling  = document.getElementById('navBilling');
        if (revenueCard) revenueCard.classList.add('hidden');
        if (navReports)  navReports.classList.add('hidden');
        if (navBilling)  navBilling.classList.add('hidden');
    }
    // Admin panel link only for admins
    const navAdmin = document.getElementById('navAdmin');
    if (navAdmin) {
        if (role !== 'ADMIN') navAdmin.classList.add('hidden');
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm('Are you sure you want to logout?')) {
                localStorage.clear();
                window.location.href = 'logout';
            }
        });
    }
}

/* =====================================================================
   NOTIFICATIONS
   ===================================================================== */
function setupNotifications() {
    const role = localStorage.getItem('userRole');
    const header = document.querySelector('.top-header');
    if (!header || (role !== 'ADMIN' && role !== 'DENTIST')) return;
    if (document.getElementById('notificationButton')) return;

    const wrapper = document.createElement('div');
    wrapper.className = 'notification-wrap';
    wrapper.innerHTML = `
        <button type="button" class="notification-button" id="notificationButton"
                aria-label="Notifications" title="Notifications">
            <span aria-hidden="true">&#128276;</span>
            <span class="notification-count hidden" id="notificationCount">0</span>
        </button>
        <div class="notification-panel hidden" id="notificationPanel" aria-live="polite">
            <div class="notification-panel-header">
                <strong>Notifications</strong>
                <span class="text-muted" id="notificationSummary">Unread</span>
            </div>
            <div id="notificationList"></div>
        </div>`;
    header.appendChild(wrapper);

    const button = document.getElementById('notificationButton');
    const panel = document.getElementById('notificationPanel');
    button.addEventListener('click', (event) => {
        event.stopPropagation();
        panel.classList.toggle('hidden');
    });
    document.addEventListener('click', (event) => {
        if (!wrapper.contains(event.target)) panel.classList.add('hidden');
    });
    loadNotifications();
}

async function loadNotifications() {
    try {
        const response = await fetch('api/notifications');
        if (!response.ok) return;
        const notifications = await response.json();
        renderNotifications(Array.isArray(notifications) ? notifications : []);
    } catch (error) {
        renderNotifications([]);
    }
}

function renderNotifications(notifications) {
    const list = document.getElementById('notificationList');
    const count = document.getElementById('notificationCount');
    const summary = document.getElementById('notificationSummary');
    if (!list || !count || !summary) return;

    count.textContent = notifications.length;
    count.classList.toggle('hidden', notifications.length === 0);
    summary.textContent = notifications.length + ' unread';
    list.innerHTML = '';

    if (notifications.length === 0) {
        list.innerHTML = '<p class="notification-empty">No new notifications.</p>';
        return;
    }

    notifications.forEach((notification) => {
        const item = document.createElement('div');
        item.className = 'notification-item';
        item.dataset.notificationId = notification.id;
        item.innerHTML = `
            <div class="notification-copy">
                <strong></strong>
                <p></p>
                <small></small>
            </div>
            <button type="button" class="notification-read" aria-label="Mark notification as read"
                    title="Mark as read">&#10003;</button>`;
        item.querySelector('strong').textContent = notification.title || 'Notification';
        item.querySelector('p').textContent = notification.message || '';
        item.querySelector('small').textContent = formatNotificationDate(notification.createdAt);
        item.querySelector('.notification-read').addEventListener('click', () => {
            markNotificationAsRead(notification.id, item);
        });
        list.appendChild(item);
    });
}

async function markNotificationAsRead(notificationId, item) {
    try {
        const data = new URLSearchParams({ notificationId: notificationId });
        const response = await fetch('api/notifications', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data
        });
        const result = await response.json();
        if (response.ok && result.status === 'success') {
            item.remove();
            const remaining = document.querySelectorAll('.notification-item').length;
            const count = document.getElementById('notificationCount');
            const summary = document.getElementById('notificationSummary');
            if (count) {
                count.textContent = remaining;
                count.classList.toggle('hidden', remaining === 0);
            }
            if (summary) summary.textContent = remaining + ' unread';
            if (remaining === 0) {
                document.getElementById('notificationList').innerHTML = '<p class="notification-empty">No new notifications.</p>';
            }
        }
    } catch (error) {
        showToast('Could not update notification.', 'error');
    }
}

function formatNotificationDate(value) {
    if (!value) return '';
    const date = new Date(value.replace(' ', 'T'));
    return Number.isNaN(date.getTime()) ? value : date.toLocaleString();
}

/* =====================================================================
   TOAST NOTIFICATIONS
   ===================================================================== */
function showToast(message, type = 'success') {
    // Remove existing toast
    const existing = document.getElementById('toastNotif');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'toastNotif';
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    document.body.appendChild(toast);

    // Animate in
    setTimeout(() => toast.classList.add('toast-show'), 10);
    // Remove after 3.5s
    setTimeout(() => {
        toast.classList.remove('toast-show');
        setTimeout(() => toast.remove(), 400);
    }, 3500);
}

/* =====================================================================
   UTILITIES
   ===================================================================== */
function showMsg(el, msg) {
    if (!el) return;
    el.textContent = msg;
    el.classList.remove('hidden');
}

function hideMsg(el) {
    if (!el) return;
    el.classList.add('hidden');
    el.textContent = '';
}

function setLoading(btn, btnText, loader, isLoading) {
    btn.disabled = isLoading;
    if (isLoading) {
        btnText.classList.add('hidden');
        loader.classList.remove('hidden');
    } else {
        btnText.classList.remove('hidden');
        loader.classList.add('hidden');
    }
}

function toTitleCase(str) {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function formatCurrency(amount) {
    return 'Rs. ' + parseFloat(amount).toLocaleString('en-LK', { minimumFractionDigits: 2 });
}
