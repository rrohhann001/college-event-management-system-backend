/* ====================================================
   E-Event Portal — app.js
   Backend: Spring Boot @ http://localhost:8080/api
   ==================================================== */

const API_BASE_URL = 'http://localhost:8080/api';

// ─── Global State ───────────────────────────────────
let currentRegistrationEmail = '';
let timerInterval;
let currentUserId    = null;
let isUserAdmin      = false;

// ─── On Page Load ───────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        loadMyProfile();
    }
});

// ===================================================
//  UTILITIES
// ===================================================

/**
 * Decode JWT payload without a library.
 */
function parseJwt(token) {
    try { return JSON.parse(atob(token.split('.')[1])); }
    catch (e) { return null; }
}

/**
 * Show the auth-page alert (login/register/otp).
 */
function showAlert(message, isError = false) {
    const box = document.getElementById('alertBox');
    box.textContent = message;
    box.className = `alert ${isError ? 'error' : 'success'}`;
    box.classList.remove('hidden');
}

/**
 * Show the dashboard alert (inside the main content area).
 */
function showDashAlert(message, isError = false) {
    const box = document.getElementById('dashboardAlertBox');
    box.textContent = message;
    box.className = `alert ${isError ? 'error' : 'success'}`;
    box.classList.remove('hidden');
    // Auto-hide after 4 s
    setTimeout(() => box.classList.add('hidden'), 4000);
}

function hideAlert() {
    document.getElementById('alertBox')?.classList.add('hidden');
}

function togglePassword(inputId, btn) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        btn.textContent = '🙈';
    } else {
        input.type = 'password';
        btn.textContent = '👁️';
    }
}

// ===================================================
//  AUTH SECTIONS (login / register / otp)
// ===================================================

function showAuthSection(sectionId) {
    document.querySelectorAll('.auth-section').forEach(s => s.classList.add('hidden'));
    document.getElementById(sectionId).classList.remove('hidden');
    hideAlert();
}

// ===================================================
//  DASHBOARD TABS
// ===================================================

function switchTab(tabId, clickedBtn) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(t => t.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');

    // Update active nav item
    document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
    if (clickedBtn) clickedBtn.classList.add('active');

    // Auto-load data when switching tabs
    if (tabId === 'eventsTab')    loadEvents();
    if (tabId === 'myEventsTab')  loadMyTickets();
    if (tabId === 'studentsTab')  loadAllStudents();
}

// ===================================================
//  MOBILE SIDEBAR TOGGLE
// ===================================================

function toggleSidebar() {
    const sidebar  = document.getElementById('sidebar');
    const overlay  = document.getElementById('sidebarOverlay');
    const isOpen   = sidebar.classList.contains('open');
    sidebar.classList.toggle('open', !isOpen);
    overlay.classList.toggle('hidden', isOpen);
}

// ===================================================
//  OTP TIMER
// ===================================================

function startTimer(durationSeconds) {
    clearInterval(timerInterval);
    let remaining = durationSeconds;

    const display  = document.getElementById('timerDisplay');
    const verifyBtn = document.getElementById('verifyBtn');
    verifyBtn.disabled = false;

    timerInterval = setInterval(() => {
        const m = String(Math.floor(remaining / 60)).padStart(2, '0');
        const s = String(remaining % 60).padStart(2, '0');
        display.textContent = `⏱️  Time Left: ${m}:${s}`;

        if (--remaining < 0) {
            clearInterval(timerInterval);
            display.textContent = '❌  OTP Expired! Please register again.';
            verifyBtn.disabled = true;
        }
    }, 1000);
}

// ===================================================
//  API — REGISTER (POST /api/student/register)
// ===================================================

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const student = {
        name:       document.getElementById('regName').value.trim(),
        email:      document.getElementById('regEmail').value.trim(),
        rollNumber: document.getElementById('regRollNo').value.trim(),
        course:     document.getElementById('regDepartment').value.trim(),
        password:   document.getElementById('regPassword').value
    };

    try {
        showAlert('Sending OTP, please wait…', false);

        const res    = await fetch(`${API_BASE_URL}/student/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student)
        });
        const result = await res.json();

        if (result.success) {
            currentRegistrationEmail = student.email;
            showAuthSection('otpSection');
            showAlert('OTP sent! Check your email inbox.', false);
            startTimer(120); // 2 minutes (backend README says 1 min, keep 2 for safety)
        } else {
            let msg = result.message || 'Registration failed.';
            if (result.data && typeof result.data === 'object') {
                msg = Object.values(result.data).join(' | ');
            }
            showAlert('Error: ' + msg, true);
        }
    } catch {
        showAlert('Cannot reach the server. Is the backend running?', true);
    }
});

// ===================================================
//  API — VERIFY OTP (POST /api/student/verify)
// ===================================================

document.getElementById('otpForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const otp = document.getElementById('otpCode').value.trim();

    try {
        const res    = await fetch(
            `${API_BASE_URL}/student/verify?email=${encodeURIComponent(currentRegistrationEmail)}&otp=${otp}`,
            { method: 'POST' }
        );
        const result = await res.json();

        if (result.success) {
            clearInterval(timerInterval);
            localStorage.setItem('jwtToken', result.data.token);
            showAlert('Email verified! Logging you in…', false);
            loadMyProfile();
        } else {
            showAlert(result.message || 'Invalid OTP.', true);
        }
    } catch {
        showAlert('Verification failed. Try again.', true);
    }
});

// ===================================================
//  API — LOGIN (POST /api/auth/login)
// ===================================================

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email    = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    try {
        const res    = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const result = await res.json();

        if (res.ok && result.data && result.data.token) {
            localStorage.setItem('jwtToken', result.data.token);
            showAlert('Login successful!', false);
            loadMyProfile();
        } else {
            showAlert(result.message || 'Invalid credentials.', true);
        }
    } catch {
        showAlert('Login failed. Server may be offline.', true);
    }
});

// ===================================================
//  API — LOAD MY PROFILE (GET /api/student/my-profile)
//         + Role detection + Switch to Dashboard
// ===================================================

async function loadMyProfile() {
    const token = localStorage.getItem('jwtToken');
    if (!token) return;

    try {
        const res    = await fetch(`${API_BASE_URL}/student/my-profile`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        if (result.success && result.data) {
            const data    = result.data;
            const payload = parseJwt(token);

            // Detect admin role from JWT roles/authorities array
            isUserAdmin = !!(payload && (
                (payload.roles    && payload.roles.some(r => r.toString().toUpperCase().includes('ADMIN'))) ||
                (payload.role     && payload.role.toString().toUpperCase().includes('ADMIN')) ||
                JSON.stringify(payload).toUpperCase().includes('ADMIN')
            ));

            currentUserId = data.id || data.studentId;

            // ─── Populate sidebar profile ───
            const firstName  = (data.name || 'User').split(' ')[0];
            const initial    = (data.name || 'U')[0].toUpperCase();

            document.getElementById('profileAvatar').textContent    = initial;
            document.getElementById('profileAvatarLg').textContent  = initial;
            document.getElementById('profileName').textContent      = data.name;
            document.getElementById('profileEmail').textContent     = data.email;
            document.getElementById('profileFullName').textContent  = data.name;
            document.getElementById('profileFullEmail').textContent = data.email;
            document.getElementById('detailName').textContent       = data.name;
            document.getElementById('detailEmail').textContent      = data.email;

            // Badge
            const badgeText  = isUserAdmin ? 'Admin' : 'Student';
            const badgeClass = isUserAdmin ? 'profile-badge admin-badge' : 'profile-badge';
            document.getElementById('profileBadge').textContent    = badgeText;
            document.getElementById('profileBadge').className      = badgeClass;
            document.getElementById('profileBadgeLg').textContent  = badgeText;
            document.getElementById('profileBadgeLg').className    = badgeClass;

            // ─── Nav visibility ───
            if (isUserAdmin) {
                document.getElementById('navAdmin').classList.remove('hidden');
                document.getElementById('navStudents').classList.remove('hidden');
                document.getElementById('navMyTickets').classList.add('hidden');
                document.getElementById('studentDangerZone').classList.add('hidden');
            } else {
                document.getElementById('navAdmin').classList.add('hidden');
                document.getElementById('navStudents').classList.add('hidden');
                document.getElementById('navMyTickets').classList.remove('hidden');
                document.getElementById('studentDangerZone').classList.remove('hidden');
            }

            // ─── Show dashboard ───
            document.getElementById('authWrapper').classList.add('hidden');
            document.getElementById('dashboardWrapper').classList.remove('hidden');

            // Load events on initial entry
            loadEvents();

        } else {
            logout();
        }
    } catch {
        logout();
    }
}

// ===================================================
//  API — LOAD ALL EVENTS (GET /api/events)
// ===================================================

async function loadEvents() {
    const token = localStorage.getItem('jwtToken');
    const list  = document.getElementById('eventsList');
    list.innerHTML = '<div class="loading-skeleton"></div><div class="loading-skeleton"></div><div class="loading-skeleton"></div>';

    try {
        const res    = await fetch(`${API_BASE_URL}/events`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        if (result.success && result.data && result.data.length > 0) {
            list.innerHTML = '';
            result.data.forEach(event => renderEventCard(event, list));
        } else {
            list.innerHTML = `
                <div class="empty-state">
                    <span class="empty-icon">📭</span>
                    <p>No upcoming events at the moment.</p>
                </div>`;
        }
    } catch {
        list.innerHTML = `
            <div class="empty-state">
                <span class="empty-icon">⚠️</span>
                <p>Failed to load events. Please try again.</p>
            </div>`;
    }
}

function renderEventCard(event, container) {
    const eTitle = event.eventName  || 'Untitled Event';
    const eDate  = event.eventDate  || 'Date TBA';
    const eVenue = event.location   || 'Venue TBA';
    const eDesc  = event.description || 'No description provided.';
    const eCap   = event.capacity   ? `${event.capacity} Seats` : 'N/A';
    const eId    = event.id;

    const actionsHtml = isUserAdmin
        ? `<button class="btn btn-outline btn-sm" onclick="viewRegistrations(${eId}, '${escapeHtml(eTitle)}')">👥 View Attendees</button>
           <button class="btn btn-danger-outline btn-sm" onclick="deleteEvent(${eId})">🗑️ Delete</button>`
        : `<button class="btn btn-primary" onclick="registerForEvent(${eId})">🎟️ Get Ticket</button>`;

    const card = document.createElement('div');
    card.className = 'event-card';
    card.innerHTML = `
        <div>
            <h4>${escapeHtml(eTitle)}</h4>
            <div class="event-meta">
                <span>📅 ${eDate}</span>
                <span>📍 ${escapeHtml(eVenue)}</span>
                <span>👥 Capacity: ${eCap}</span>
            </div>
            <p class="event-desc">${escapeHtml(eDesc)}</p>
        </div>
        <div class="event-actions">${actionsHtml}</div>
    `;
    container.appendChild(card);
}

// ===================================================
//  API — STUDENT: REGISTER FOR EVENT
//         POST /api/registrations/event/{eventId}
// ===================================================

async function registerForEvent(eventId) {
    const token = localStorage.getItem('jwtToken');
    try {
        const res    = await fetch(`${API_BASE_URL}/registrations/event/${eventId}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        if (res.ok && result.success) {
            showDashAlert('Ticket Confirmed! 🎟️', false);
            loadMyTickets();
        } else {
            showDashAlert(result.message || 'Already registered or failed.', true);
        }
    } catch {
        showDashAlert('Registration failed. Please try again.', true);
    }
}

// ===================================================
//  API — STUDENT: MY TICKETS
//         GET /api/registrations/my-events
// ===================================================

async function loadMyTickets() {
    const token = localStorage.getItem('jwtToken');
    const list  = document.getElementById('myEventsList');
    list.innerHTML = '<div class="loading-skeleton"></div>';

    try {
        const res    = await fetch(`${API_BASE_URL}/registrations/my-events`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        if (result.success && result.data && result.data.length > 0) {
            list.innerHTML = '';
            result.data.forEach(reg => {
                const evObj  = reg.event || reg;
                const eTitle = evObj.eventName || 'Event';
                const eDate  = evObj.eventDate || 'TBA';
                const eId    = evObj.id || evObj.eventId;

                const card = document.createElement('div');
                card.className = 'event-card ticket-card';
                card.innerHTML = `
                    <div>
                        <h4>✅ ${escapeHtml(eTitle)}</h4>
                        <div class="event-meta">
                            <span>📅 ${eDate}</span>
                        </div>
                    </div>
                    <div class="event-actions">
                        <button class="btn btn-danger-outline" onclick="cancelRegistration(${eId})">❌ Cancel Ticket</button>
                    </div>
                `;
                list.appendChild(card);
            });
        } else {
            list.innerHTML = `
                <div class="empty-state">
                    <span class="empty-icon">🎟️</span>
                    <p>No tickets booked yet. Browse events and get one!</p>
                </div>`;
        }
    } catch {
        list.innerHTML = `<div class="empty-state"><p>Failed to load tickets.</p></div>`;
    }
}

// ===================================================
//  API — STUDENT: CANCEL TICKET
//         DELETE /api/registrations/cancel/event/{eventId}
// ===================================================

async function cancelRegistration(eventId) {
    if (!confirm('Are you sure you want to cancel this ticket?')) return;
    const token = localStorage.getItem('jwtToken');
    try {
        const res = await fetch(`${API_BASE_URL}/registrations/cancel/event/${eventId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        showDashAlert('Ticket cancelled successfully.', false);
        loadMyTickets();
    } catch {
        showDashAlert('Failed to cancel ticket.', true);
    }
}

// ===================================================
//  API — STUDENT: DELETE MY ACCOUNT
//         DELETE /api/student/{id}
// ===================================================

async function deleteMyAccount() {
    if (!currentUserId) return;
    if (!confirm('⚠️ WARNING: This will permanently delete your account and all your tickets. This cannot be undone!')) return;
    if (!confirm('Final confirmation — Delete account permanently?')) return;

    const token = localStorage.getItem('jwtToken');
    try {
        await fetch(`${API_BASE_URL}/student/${currentUserId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        showAlert('Account deleted permanently.', false);
        logout();
    } catch {
        showDashAlert('Failed to delete account. Try again.', true);
    }
}

// ===================================================
//  API — ADMIN: CREATE EVENT
//         POST /api/events
// ===================================================

document.getElementById('createEventForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('jwtToken');

    const newEvent = {
        eventName:   document.getElementById('evName').value.trim(),
        description: document.getElementById('evDesc').value.trim(),
        eventDate:   document.getElementById('evDate').value,
        location:    document.getElementById('evLocation').value.trim(),
        capacity:    parseInt(document.getElementById('evCapacity').value, 10)
    };

    try {
        const res = await fetch(`${API_BASE_URL}/events`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(newEvent)
        });
        const result = await res.json();

        if (res.ok) {
            showDashAlert('Event launched successfully! 🎉', false);
            document.getElementById('createEventForm').reset();
            // Switch to All Events tab to see the new event
            switchTab('eventsTab', document.getElementById('navEvents'));
        } else {
            showDashAlert(result.message || 'Failed to create event.', true);
        }
    } catch {
        showDashAlert('Server error while creating event.', true);
    }
});

// ===================================================
//  API — ADMIN: DELETE EVENT
//         DELETE /api/events/{id}
// ===================================================

async function deleteEvent(eventId) {
    if (!confirm('Permanently delete this event and all its registrations?')) return;
    const token = localStorage.getItem('jwtToken');
    try {
        await fetch(`${API_BASE_URL}/events/${eventId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        showDashAlert('Event deleted.', false);
        loadEvents();
    } catch {
        showDashAlert('Failed to delete event.', true);
    }
}

// ===================================================
//  API — ADMIN: VIEW REGISTRATIONS FOR AN EVENT
//         GET /api/registrations/event/{eventId}
// ===================================================

async function viewRegistrations(eventId, eventTitle) {
    const token   = localStorage.getItem('jwtToken');
    // Switch to Admin tab
    switchTab('adminTab', document.getElementById('navAdmin'));

    const listDiv = document.getElementById('adminRegistrationsList');
    listDiv.innerHTML = '<div class="empty-state"><p>⏳ Loading attendees…</p></div>';

    try {
        const res    = await fetch(`${API_BASE_URL}/registrations/event/${eventId}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        if (res.ok && result.data && result.data.length > 0) {
            let html = `
                <div class="reg-event-title">📋 ${escapeHtml(eventTitle)} — ${result.data.length} Attendee(s)</div>
                <table>
                    <thead><tr><th>#</th><th>Name</th><th>Email</th></tr></thead>
                    <tbody>`;

            result.data.forEach((reg, i) => {
                const sName  = reg.student ? reg.student.name  : (reg.name  || 'Unknown');
                const sEmail = reg.student ? reg.student.email : (reg.email || 'Unknown');
                html += `<tr>
                    <td style="color:var(--text-muted); font-size:12px;">${i + 1}</td>
                    <td>👤 ${escapeHtml(sName)}</td>
                    <td>✉️ ${escapeHtml(sEmail)}</td>
                </tr>`;
            });

            html += `</tbody></table>`;
            listDiv.innerHTML = html;
        } else {
            listDiv.innerHTML = `
                <div class="empty-state">
                    <span class="empty-icon">🎟️</span>
                    <p>No tickets sold yet for <strong>${escapeHtml(eventTitle)}</strong>.</p>
                </div>`;
        }
    } catch {
        listDiv.innerHTML = `<div class="empty-state"><p>⚠️ Failed to fetch attendees.</p></div>`;
    }
}

// ===================================================
//  API — ADMIN: ALL STUDENTS
//         GET /api/student
// ===================================================

async function loadAllStudents() {
    const token = localStorage.getItem('jwtToken');
    const wrap  = document.getElementById('studentsList');
    wrap.innerHTML = '<div class="loading-skeleton" style="min-height:120px;"></div>';

    try {
        const res    = await fetch(`${API_BASE_URL}/student`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const result = await res.json();

        // ✅ FIX: Backend alag format de sakta hai — sabko handle karo
        // Try: result.data[], result.content[], result[] (direct array)
        let students = [];
        if (Array.isArray(result))             students = result;
        else if (Array.isArray(result.data))   students = result.data;
        else if (result.data && Array.isArray(result.data.content)) students = result.data.content;
        else if (Array.isArray(result.content)) students = result.content;

        if (students.length > 0) {
            let html = `
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Roll No.</th>
                            <th>Course</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>`;

            students.forEach((s, i) => {
                const sId = s.id || s.studentId || '';
                html += `<tr>
                    <td style="color:var(--text-muted); font-size:12px;">${i + 1}</td>
                    <td><strong>${escapeHtml(s.name || '-')}</strong></td>
                    <td>${escapeHtml(s.email || '-')}</td>
                    <td><code style="font-family:var(--font-mono); font-size:12px;">${escapeHtml(s.rollNumber || s.roll_number || '-')}</code></td>
                    <td>${escapeHtml(s.course || '-')}</td>
                    <td>
                        <button class="btn btn-danger-outline btn-sm"
                                onclick="adminDeleteStudent(${sId}, '${escapeHtml(s.name || '')}')">
                            🗑️ Remove
                        </button>
                    </td>
                </tr>`;
            });

            html += `</tbody></table>`;
            wrap.innerHTML = html;
        } else {
            // Debug: console pe actual response dikhao
            console.log('Students API Response:', result);
            wrap.innerHTML = `
                <div class="empty-state">
                    <span class="empty-icon">👥</span>
                    <p>No registered students found.</p>
                    <small style="color:var(--text-light); margin-top:8px; display:block;">
                        Check browser Console (F12) for API response details.
                    </small>
                </div>`;
        }
    } catch (err) {
        console.error('loadAllStudents error:', err);
        wrap.innerHTML = `<div class="empty-state"><p>⚠️ Failed to load students.</p></div>`;
    }
}

// ===================================================
//  API — ADMIN: DELETE A STUDENT
//         DELETE /api/student/{id}
// ===================================================

async function adminDeleteStudent(studentId, studentName) {
    if (!confirm(`Remove student "${studentName}" and all their tickets permanently?`)) return;
    const token = localStorage.getItem('jwtToken');
    try {
        await fetch(`${API_BASE_URL}/student/${studentId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        showDashAlert(`Student "${studentName}" removed.`, false);
        loadAllStudents();
    } catch {
        showDashAlert('Failed to delete student.', true);
    }
}

// ===================================================
//  LOGOUT
// ===================================================

function logout() {
    clearInterval(timerInterval);
    localStorage.removeItem('jwtToken');

    currentUserId = null;
    isUserAdmin   = false;

    // Reset dashboard state
    document.getElementById('dashboardWrapper').classList.add('hidden');
    document.getElementById('authWrapper').classList.remove('hidden');

    // Show login section
    showAuthSection('loginSection');
    document.getElementById('loginForm').reset();

    // Close mobile sidebar if open
    document.getElementById('sidebar')?.classList.remove('open');
    document.getElementById('sidebarOverlay')?.classList.add('hidden');
}

// ===================================================
//  HELPER — escape HTML to prevent XSS
// ===================================================

function escapeHtml(str) {
    if (typeof str !== 'string') return String(str || '');
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}