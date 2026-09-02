/**
 * main.js - Core functionality for Sunrise Dental Clinic
 * Handles login and global utilities.
 */

document.addEventListener('DOMContentLoaded', () => {
    
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

});

/**
 * Handles the login form submission via AJAX
 */
async function handleLogin(e) {
    e.preventDefault();
    
    const form = e.target;
    const btn = form.querySelector('button');
    const btnText = btn.querySelector('span');
    const loader = btn.querySelector('.loader');
    const errorMsg = document.getElementById('loginError');
    
    // UI Loading state
    btn.disabled = true;
    btnText.classList.add('hidden');
    loader.classList.remove('hidden');
    errorMsg.classList.add('hidden');
    
    const formData = new FormData(form);
    const data = new URLSearchParams(formData);
    
    try {
        const response = await fetch('api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: data
        });
        
        const result = await response.json();
        
        if (result.status === 'success') {
            // Save user info in localStorage for frontend checks
            localStorage.setItem('userRole', result.role);
            localStorage.setItem('userName', result.name);
            
            // Redirect to dashboard
            window.location.href = 'dashboard.html';
        } else {
            showError(errorMsg, result.message || 'Authentication failed');
            resetLoginBtn(btn, btnText, loader);
        }
    } catch (error) {
        showError(errorMsg, 'Server connection error. Please try again.');
        resetLoginBtn(btn, btnText, loader);
    }
}

function showError(element, msg) {
    element.textContent = msg;
    element.classList.remove('hidden');
}

function resetLoginBtn(btn, btnText, loader) {
    btn.disabled = false;
    btnText.classList.remove('hidden');
    loader.classList.add('hidden');
}

/**
 * Utility: Check if user is logged in for protected pages
 */
function checkAuth() {
    const role = localStorage.getItem('userRole');
    if (!role && !window.location.href.includes('index.html')) {
        window.location.href = 'index.html';
    }
}

/**
 * Utility: Setup sidebar user info and logout
 */
function setupDashboardLayout() {
    const nameEl = document.getElementById('sidebarUserName');
    const roleEl = document.getElementById('sidebarUserRole');
    
    if (nameEl && roleEl) {
        nameEl.textContent = localStorage.getItem('userName') || 'User';
        roleEl.textContent = localStorage.getItem('userRole') || 'Staff';
    }
    
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            localStorage.clear();
            window.location.href = 'logout'; // Relative path → /SunriseDental/logout
        });
    }
}
