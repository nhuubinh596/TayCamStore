// main.js - basic JS helpers for site
(function () {
    'use strict';

    // Debug: confirm loaded
    console.log('[main.js] loaded');

    // Toggle password fields helper:
    // Buttons that should toggle need attribute: data-target (CSS selector)
    // e.g. <button data-target=".pw-field" id="togglePasswordBtn">👁</button>
    function installPasswordToggles() {
        document.querySelectorAll('button[data-target]').forEach(function (btn) {
            // ensure not a submit button
            if (!btn.getAttribute('type')) btn.setAttribute('type', 'button');

            // prevent duplicate listeners: clone & replace
            const clone = btn.cloneNode(true);
            btn.parentNode.replaceChild(clone, btn);

            clone.addEventListener('click', function (e) {
                e.preventDefault();
                const sel = clone.getAttribute('data-target') || '.pw-field';
                const inputs = Array.from(document.querySelectorAll(sel)).filter(Boolean);
                if (!inputs.length) {
                    console.warn('[main.js] toggle password: no inputs found for selector', sel);
                    return;
                }
                const shouldShow = inputs.some(i => i.type === 'password');
                inputs.forEach(i => i.type = shouldShow ? 'text' : 'password');
                clone.textContent = shouldShow ? '🙈 Ẩn mật khẩu' : '👁 Hiện mật khẩu';
            });
        });
    }

    // Small helper to auto-hide any .alert-box elements
    function autoHideAlerts(ms) {
        setTimeout(function () {
            document.querySelectorAll('.alert-box').forEach(function (el) { el.remove(); });
        }, ms || 3000);
    }

    // Run after DOM loaded
    document.addEventListener('DOMContentLoaded', function () {
        installPasswordToggles();
        autoHideAlerts(3000);

        // debug: show number of password fields found
        console.log('[main.js] password inputs:', document.querySelectorAll('.pw-field, input[type="password"]').length);
    });

})();
