const depositButton = document.getElementById('depositButton');
const withdrawButton = document.getElementById('withdrawButton');
const depositForm = document.getElementById('depositForm');
const withdrawForm = document.getElementById('withdrawForm');
const depositInfo = document.getElementById('depositInfo');
const withdrawInfo = document.getElementById('withdrawInfo');
const submitDepositButton = document.getElementById('submitDepositButton ');
const submitWithdrawalButton = document.getElementById('submitWithdrawalButton');

function displayDepositForm() {
    if(withdrawForm.style.display === 'block') {
        withdrawForm.style.display = 'none';
        withdrawInfo.style.display = 'none';
    }
    depositForm.style.display = 'block';
    depositInfo.style.display = 'block';
}
function displayWithdrawForm() {
    if(depositForm.style.display === 'block') {
        depositForm.style.display = 'none';
        depositInfo.style.display = 'none';
    }
    withdrawForm.style.display = 'block';
    withdrawInfo.style.display = 'block';
}
function onSubmitDeposit() {
    const depositAmount = document.getElementById('depositAmount').value;

    const moneyTransferRequest = {
        requestType: "DEPOSIT",
        amount: depositAmount
    };

    console.log("Sending request:", moneyTransferRequest);

    fetch('/api/deposit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(moneyTransferRequest),
    })
        .then(response => {
            console.log("Received response:", response);
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log("Data received:", data);
            if (data.success) {
                alert('Deposit successful');
            } else {
                alert('Deposit failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

function onSubmitWithdrawal() {
    const withdrawAmount = document.getElementById('withdrawAmount');

}

depositButton.addEventListener('click', displayDepositForm);
withdrawButton.addEventListener('click', displayWithdrawForm);
submitDepositButton.addEventListener('click', onSubmitDeposit);
submitWithdrawalButton.addEventListener('click', onSubmitWithdrawal);
