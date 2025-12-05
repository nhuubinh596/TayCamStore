(function () {
    'use strict';
    console.log('[main.js] loaded');
    function installPasswordToggles() {
        document.querySelectorAll('button[data-target]').forEach(function (btn) {
            if (!btn.getAttribute('type')) btn.setAttribute('type', 'button');
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

    function autoHideAlerts(ms) {
        setTimeout(function () {
            document.querySelectorAll('.alert-box').forEach(function (el) { el.remove(); });
        }, ms || 3000);
    }

    // Run after DOM loaded
    document.addEventListener('DOMContentLoaded', function () {
        installPasswordToggles();
        autoHideAlerts(3000);
        console.log('[main.js] password inputs:', document.querySelectorAll('.pw-field, input[type="password"]').length);
    });

})();