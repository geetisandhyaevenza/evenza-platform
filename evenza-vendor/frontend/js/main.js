/* ============================================================
   EVENZA VENDOR — Main JavaScript
   Shared utilities, animations, API helpers
   ============================================================ */

const API_BASE = '/api';
const STORAGE_TOKEN = 'evenza_token';
const STORAGE_USER  = 'evenza_user';

// ============================================================
// API HELPER
// ============================================================
const api = {
  async get(url) {
    const res = await fetch(API_BASE + url, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}` }
    });
    return res.json();
  },
  async post(url, body) {
    const res = await fetch(API_BASE + url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}`
      },
      body: JSON.stringify(body)
    });
    return res.json();
  },
  async put(url, body) {
    const res = await fetch(API_BASE + url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}`
      },
      body: JSON.stringify(body)
    });
    return res.json();
  },
  async patch(url, body) {
    const res = await fetch(API_BASE + url, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}`
      },
      body: JSON.stringify(body)
    });
    return res.json();
  },
  async delete(url) {
    const res = await fetch(API_BASE + url, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}` }
    });
    return res.json();
  },
  async upload(url, formData) {
    const res = await fetch(API_BASE + url, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${localStorage.getItem(STORAGE_TOKEN)}` },
      body: formData
    });
    return res.json();
  }
};

// ============================================================
// AUTH HELPERS
// ============================================================
const auth = {
  getToken:   () => localStorage.getItem(STORAGE_TOKEN),
  getUser:    () => JSON.parse(localStorage.getItem(STORAGE_USER) || 'null'),
  isLoggedIn: () => !!localStorage.getItem(STORAGE_TOKEN),
  setSession(token, user) {
    localStorage.setItem(STORAGE_TOKEN, token);
    localStorage.setItem(STORAGE_USER, JSON.stringify(user));
  },
  clearSession() {
    localStorage.removeItem(STORAGE_TOKEN);
    localStorage.removeItem(STORAGE_USER);
  },
  requireAuth(redirectTo = 'login.html') {
    if (!this.isLoggedIn()) {
      window.location.href = redirectTo;
      return false;
    }
    return true;
  }
};

// ============================================================
// TOAST NOTIFICATIONS
// ============================================================
function showToast(message, type = 'info', duration = 3500) {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const icons = {
    success: '✓', error: '✕', warning: '⚠', info: 'ℹ'
  };

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <div class="toast-icon">${icons[type] || icons.info}</div>
    <div>
      <div class="toast-title">${message}</div>
    </div>
    <button onclick="this.parentElement.remove()" style="margin-left:auto;background:none;border:none;cursor:pointer;color:var(--gray-400);font-size:16px;line-height:1;padding:0;">×</button>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.animation = 'slideInRight 0.3s reverse';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// ============================================================
// SCROLL REVEAL
// ============================================================
function initScrollReveal() {
  const els = document.querySelectorAll('[data-reveal]');
  if (!els.length) return;

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry, i) => {
      if (entry.isIntersecting) {
        const delay = entry.target.dataset.delay
          ? parseFloat(entry.target.dataset.delay) * 100
          : 0;
        setTimeout(() => entry.target.classList.add('revealed'), delay);
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.15, rootMargin: '0px 0px -40px 0px' });

  els.forEach(el => observer.observe(el));
}

// ============================================================
// COUNTER ANIMATION
// ============================================================
function initCounters() {
  const counters = document.querySelectorAll('[data-target]');
  if (!counters.length) return;

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (!entry.isIntersecting) return;
      const el = entry.target;
      const target = parseInt(el.dataset.target);
      const suffix = el.dataset.suffix || '';
      const duration = 1800;
      const start = performance.now();

      function update(now) {
        const elapsed = now - start;
        const progress = Math.min(elapsed / duration, 1);
        const eased = 1 - Math.pow(1 - progress, 3);
        const value = Math.round(eased * target);
        el.textContent = value.toLocaleString('en-IN') + suffix;
        if (progress < 1) requestAnimationFrame(update);
      }
      requestAnimationFrame(update);
      observer.unobserve(el);
    });
  }, { threshold: 0.5 });

  counters.forEach(el => observer.observe(el));
}

// ============================================================
// NAVBAR SCROLL BEHAVIOR
// ============================================================
function initNavbarScroll() {
  const nav = document.getElementById('navbar');
  if (!nav) return;

  let lastY = 0;
  window.addEventListener('scroll', () => {
    const y = window.scrollY;
    if (y > 60) {
      nav.classList.add('scrolled');
    } else {
      nav.classList.remove('scrolled');
    }
    lastY = y;
  }, { passive: true });
}

// ============================================================
// FORM VALIDATION
// ============================================================
function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validatePhone(phone) {
  return /^[+]?[0-9]{10,15}$/.test(phone.replace(/\s/g, ''));
}

function validatePassword(password) {
  return {
    length: password.length >= 8,
    upper: /[A-Z]/.test(password),
    lower: /[a-z]/.test(password),
    number: /\d/.test(password),
    special: /[@$!%*?&]/.test(password)
  };
}

function getPasswordStrength(password) {
  const v = validatePassword(password);
  const score = Object.values(v).filter(Boolean).length;
  if (score <= 2) return { level: 'weak', label: 'Weak', color: 'var(--danger)' };
  if (score === 3) return { level: 'fair', label: 'Fair', color: 'var(--warning)' };
  if (score === 4) return { level: 'good', label: 'Good', color: 'var(--gold)' };
  return { level: 'strong', label: 'Strong', color: 'var(--success)' };
}

function initPasswordStrength(inputId, barsId, labelId) {
  const input = document.getElementById(inputId);
  const bars  = document.getElementById(barsId);
  const label = document.getElementById(labelId);
  if (!input || !bars) return;

  input.addEventListener('input', () => {
    const { level, label: lbl, color } = getPasswordStrength(input.value);
    const levels = { weak: 1, fair: 2, good: 3, strong: 4 };
    const filled  = levels[level];
    bars.querySelectorAll('.pw-strength-bar').forEach((bar, i) => {
      bar.style.background = i < filled ? color : 'var(--gray-200)';
    });
    if (label) { label.textContent = lbl; label.style.color = color; }
  });
}

function showFieldError(fieldId, message) {
  const el = document.getElementById(fieldId);
  if (!el) return;
  el.classList.add('is-invalid');
  const existing = el.parentElement.querySelector('.form-error');
  if (existing) existing.remove();
  const err = document.createElement('div');
  err.className = 'form-error';
  err.textContent = message;
  el.parentElement.appendChild(err);
}

function clearFieldError(fieldId) {
  const el = document.getElementById(fieldId);
  if (!el) return;
  el.classList.remove('is-invalid');
  el.classList.remove('is-valid');
  const err = el.parentElement.querySelector('.form-error');
  if (err) err.remove();
}

function markFieldValid(fieldId) {
  const el = document.getElementById(fieldId);
  if (!el) return;
  el.classList.remove('is-invalid');
  el.classList.add('is-valid');
}

// ============================================================
// IMAGE PREVIEW HELPER
// ============================================================
function previewImage(inputEl, previewEl, fallback = null) {
  inputEl.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      showToast('Please select an image file', 'error');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      showToast('Image must be under 5MB', 'error');
      return;
    }
    const reader = new FileReader();
    reader.onload = (ev) => {
      if (previewEl.tagName === 'IMG') {
        previewEl.src = ev.target.result;
      } else {
        previewEl.style.backgroundImage = `url(${ev.target.result})`;
      }
    };
    reader.readAsDataURL(file);
  });
}

// ============================================================
// DRAG & DROP UPLOAD
// ============================================================
function initDropZone(zoneId, inputId, callback) {
  const zone  = document.getElementById(zoneId);
  const input = document.getElementById(inputId);
  if (!zone || !input) return;

  zone.addEventListener('click', () => input.click());
  zone.addEventListener('dragover', e => {
    e.preventDefault();
    zone.classList.add('drag-over');
  });
  zone.addEventListener('dragleave', () => zone.classList.remove('drag-over'));
  zone.addEventListener('drop', e => {
    e.preventDefault();
    zone.classList.remove('drag-over');
    const files = e.dataTransfer.files;
    if (files.length && callback) callback(files);
  });
  input.addEventListener('change', e => {
    if (callback) callback(e.target.files);
  });
}

// ============================================================
// DEBOUNCE / THROTTLE
// ============================================================
function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

function throttle(fn, delay) {
  let last = 0;
  return (...args) => {
    const now = Date.now();
    if (now - last >= delay) { last = now; fn(...args); }
  };
}

// ============================================================
// FORMAT HELPERS
// ============================================================
function formatCurrency(amount, currency = '₹') {
  return currency + Number(amount).toLocaleString('en-IN');
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric'
  });
}

function timeAgo(dateStr) {
  const diff = Date.now() - new Date(dateStr);
  const mins = Math.floor(diff / 60000);
  if (mins < 1)  return 'just now';
  if (mins < 60) return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24)  return `${hrs}h ago`;
  return `${Math.floor(hrs / 24)}d ago`;
}

// ============================================================
// LOADING STATES
// ============================================================
function setLoading(btnId, loading, originalText = null) {
  const btn = document.getElementById(btnId);
  if (!btn) return;
  if (loading) {
    btn._original = btn.innerHTML;
    btn.innerHTML = `<span class="btn-loading">${originalText || 'Please wait...'}</span>`;
    btn.disabled = true;
  } else {
    btn.innerHTML = btn._original || originalText || btn.innerHTML;
    btn.disabled = false;
  }
}

// ============================================================
// LOCAL STORAGE HELPERS
// ============================================================
const store = {
  set:    (k, v) => localStorage.setItem('evenza_' + k, JSON.stringify(v)),
  get:    (k)    => { try { return JSON.parse(localStorage.getItem('evenza_' + k)); } catch(e) { return null; } },
  remove: (k)    => localStorage.removeItem('evenza_' + k),
};

// ============================================================
// CLIPBOARD COPY
// ============================================================
async function copyToClipboard(text, successMsg = 'Copied!') {
  try {
    await navigator.clipboard.writeText(text);
    showToast(successMsg, 'success');
  } catch(e) {
    const el = document.createElement('textarea');
    el.value = text;
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);
    showToast(successMsg, 'success');
  }
}

// ============================================================
// OTP INPUT AUTO-ADVANCE
// ============================================================
function initOtpInputs(containerSelector) {
  const container = document.querySelector(containerSelector);
  if (!container) return;
  const inputs = container.querySelectorAll('input[data-otp]');

  inputs.forEach((inp, i) => {
    inp.addEventListener('input', e => {
      const val = e.target.value.replace(/\D/g, '');
      e.target.value = val.slice(-1);
      if (val && i < inputs.length - 1) inputs[i + 1].focus();
    });
    inp.addEventListener('keydown', e => {
      if (e.key === 'Backspace' && !e.target.value && i > 0) inputs[i - 1].focus();
    });
    inp.addEventListener('paste', e => {
      e.preventDefault();
      const text = e.clipboardData.getData('text').replace(/\D/g, '');
      [...text].forEach((ch, j) => {
        if (inputs[j]) inputs[j].value = ch;
      });
      const next = Math.min(text.length, inputs.length - 1);
      inputs[next].focus();
    });
  });

  return () => [...inputs].map(i => i.value).join('');
}

// ============================================================
// STAR RATING UI
// ============================================================
function renderStars(rating, size = 14) {
  const full  = Math.floor(rating);
  const half  = rating % 1 >= 0.5;
  const empty = 5 - full - (half ? 1 : 0);
  const star  = (type) => `<svg width="${size}" height="${size}" viewBox="0 0 14 14" fill="none">
    <path d="M7 1l1.54 3.26L12 4.86l-2.5 2.56.59 3.58L7 9.26l-3.09 1.74.59-3.58L2 4.86l3.46-.6z"
      fill="${type === 'full' ? 'var(--gold)' : type === 'half' ? 'url(#hg)' : 'var(--gray-200)'}"/>
  </svg>`;
  return `<span style="display:inline-flex;gap:1px">
    ${Array(full).fill(star('full')).join('')}
    ${half ? star('half') : ''}
    ${Array(empty).fill(star('empty')).join('')}
  </span>`;
}

// ============================================================
// PROFILE AVATAR INITIALS
// ============================================================
function getInitials(name = '') {
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
}

// ============================================================
// INIT ON DOMContentLoaded
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
  initNavbarScroll();
  initScrollReveal();
  initCounters();
});
