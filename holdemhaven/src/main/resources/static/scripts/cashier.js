const cashierButton = document.getElementById('cashierButton');

function openCashierPage() {
    window.location.href = 'http://localhost:8080/cashier';
}

cashierButton.addEventListener('click', openCashierPage);