function prevent() {
    return false;
}

function ready() {
    document.oncontextmenu = prevent;
}

document.addEventListener("DOMContentLoaded", ready);
