//constants for page elements that are frequently accessed
const depositForm = document.getElementById('depositForm');
const withdrawForm = document.getElementById('withdrawForm');
const depositInfo = document.getElementById('depositInfo');
const withdrawInfo = document.getElementById('withdrawInfo');

//hide any visible divs and display the deposit form and accompanying information
function displayDepositForm() {
    withdrawForm.style.display = 'none';
    withdrawInfo.style.display = 'none';

    depositForm.style.display = 'block';
    depositInfo.style.display = 'block';
}

//hide any visible divs and display the withdrawal form and accompanying information
function displayWithdrawForm() {
    depositForm.style.display = 'none';
    depositInfo.style.display = 'none';

    withdrawForm.style.display = 'block';
    withdrawInfo.style.display = 'block';
}

//after user submits deposit form, send request to update balance
function onSubmitDeposit() {
    const depositAmount = document.getElementById('depositAmount').value;

    //if the user did not enter a number, alert the user
    if (!depositAmount) {
        alert('Please enter a deposit amount.');
        return;
    }

    //create response DTO
    const moneyTransferRequest = {
        requestType: "DEPOSIT",
        amount: depositAmount
    };

    fetch('/player/deposit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(moneyTransferRequest),
    })
        .then(response => response.json())
        .then(data => {
            //if deposit was successful, alert the user
            if (data.success) {
                alert('Deposit successful');
                document.getElementById('accountBalance').textContent = data.amount;
            }
            //else alert the user with what went wrong
            else {
                alert('Deposit failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

//after user submits withdrawal form, send request to update balance
function onSubmitWithdrawal() {
    const withdrawAmount = document.getElementById('withdrawAmount').value;

    //if the user did not enter a number, alert the user
    if (!withdrawAmount) {
        alert('Please enter a withdraw amount.');
        return;
    }

    //create response DTO
    const moneyTransferRequest = {
        requestType: "WITHDRAW",
        amount: withdrawAmount
    };

    fetch('/player/withdraw', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(moneyTransferRequest),
    })
        .then(response => response.json())
        .then(data => {
            //if withdrawal was successful, alert the user
            if (data.success) {
                alert('Withdraw successful');
                document.getElementById('accountBalance').textContent = data.amount;
            }
            //else alert the user with what went wrong
            else {
                alert('Withdraw failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

//fetch username and account balance session attributes and display them in the navbar
async function fetchSessionAttributes() {
    try {
        const response = await fetch("/get-player-info");
        if(response.ok) {
            const attributes = await response.json();
            document.getElementById('username').textContent = attributes.username;
            document.getElementById('accountBalance').textContent = attributes.accountBalance;
        }
        else {
            console.error("Failed to fetch session attributes.");
        }
    }
    catch (error) {
        console.error("Error ", error);
    }
}

//redirect user back to the home page
async function fetchHomePage() {
    try {
        const response = await fetch("/home");
        if(response.ok) {
            console.log("Successfully fetched home page")
            window.location.href = "/index.html";
        }
        else {
            console.error("Failed to fetch home page");
        }
    } catch (error){
        console.error("Error ", error);
    }
}

//event listeners for all the buttons on the page
document.getElementById('depositButton').addEventListener('click', displayDepositForm);
document.getElementById('withdrawButton').addEventListener('click', displayWithdrawForm);
document.getElementById('submitDepositButton').addEventListener('click', onSubmitDeposit);
document.getElementById('submitWithdrawalButton').addEventListener('click', onSubmitWithdrawal);
document.getElementById('homeButton').addEventListener('click', fetchHomePage);
window.onload = fetchSessionAttributes;