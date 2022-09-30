function prevent() {
    return false;
}

function ready() {
    document.oncontextmenu = prevent;
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', ready);
} else {
    ready();
}
