//constants for page elements that are frequently accessed
const handHistoryTable = document.getElementById('handHistoryTable');
const changeUsernameForm = document.getElementById('changeUsernameForm');
const changePasswordForm = document.getElementById('changePasswordForm');
const deleteAccountForm = document.getElementById('deleteAccountForm');

//hide other divs and display change username form
function displayChangeUsernameForm() {
    changePasswordForm.style.display = 'none';
    deleteAccountForm.style.display = 'none';
    handHistoryTable.style.display = 'none';

    changeUsernameForm.style.display = 'block';
}

//hide other divs and display change password form
function displayChangePasswordForm() {
    changeUsernameForm.style.display = 'none';
    deleteAccountForm.style.display = 'none';
    handHistoryTable.style.display = 'none';

    changePasswordForm.style.display = 'block';
}


//hide other divs and display delete account form
function displayDeleteAccountForm() {
    changeUsernameForm.style.display = 'none';
    changePasswordForm.style.display = 'none';
    handHistoryTable.style.display = 'none';

    deleteAccountForm.style.display = 'block';
}

//fetch hand history from the repository
function getHandHistory() {
    fetch('/table/get-hand-history', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            displayHandHistory(data);
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

//hide other divs and display hand history data
function displayHandHistory(data) {
    changePasswordForm.style.display = 'none';
    deleteAccountForm.style.display = 'none';
    changeUsernameForm.style.display = 'none';

    const tableBody = document.getElementById('handHistoryTableBody');
    tableBody.innerHTML = '';

    data.forEach(hand => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${hand.handId}</td>
            <td>${hand.anteBet}</td>
            <td>${hand.dealerBet}</td>
            <td>${hand.playBet}</td>
            <td>${hand.tripsBet}</td>
            <td>${hand.boardCards}</td>
            <td>${hand.playerHoleCards}</td>
            <td>${hand.dealerHoleCards}</td>
            <td>${hand.result}</td>
            <td>${hand.playerPayout}</td>
        `;

        tableBody.appendChild(row);
    });

    handHistoryTable.style.display = 'block';
}

//after user submits change username form, send request to apply change in the backend
async function onChangeUsername() {
    const newUsername = document.getElementById('newUsername').value;

    fetch('/player/change-username', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUsername),
    })
        .then(response => response.json())
        .then(data => {
            //if change was accepted:
            if (data.success) {
                //display an alert and change the username in the navbar
                alert('Successfully changed username.');
                document.getElementById('username').textContent = data.username;
            }
            //else display error alert
            else {
                alert('Username change failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

//after user submits change password form, send request to apply change in the backend
function onChangePassword() {
    //create change password request DTO
    const changePasswordRequest = {
        currentPassword: document.getElementById('currentPassword').value,
        newPassword: document.getElementById('newPassword').value,
        confirmNewPassword: document.getElementById('confirmNewPassword').value
    }

    fetch('/player/change-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(changePasswordRequest),
    })
        .then(response => response.json())
        .then(async data => {
            if (data.success) {
                alert('Successfully changed password.');
                //TODO redirect to home page, log out
                try {
                    const response = await fetch("/logout", {
                        method: 'POST',
                    });
                    if (response.ok) {
                        await response.json().catch(() => ({}));

                        await fetchHomePage();
                    } else {
                        console.error("Failed to log out.");
                    }
                } catch (error) {
                    console.log("Error: ", error);
                }
            } else {
                alert('Password change failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

//after user submits delete account form, send request to apply change in the backend
function onDeleteAccount() {
    fetch('/player/delete-account', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(async data => {
            //if success, alert the user, log them out, and redirect them to the home page
            if (data.success) {
                alert('Account deleted.');

                try {
                    const response = await fetch("/logout", {
                        method: 'POST',
                    });
                    if (response.ok) {
                        await response.json().catch(() => ({}));

                        await fetchHomePage();
                    } else {
                        console.error("Failed to log out.");
                    }
                } catch (error) {
                    console.log("Error: ", error);
                }
            } else {
                alert('Delete account failed: ' + data.message);
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
            console.error("Failed to fetch home page");
        }
    } catch (error){
        console.error("Error ", error);
    }
}

//event listeners for all the buttons on the page
document.getElementById("homeButton").addEventListener('click', fetchHomePage);
document.getElementById("handHistory").addEventListener('click', getHandHistory);
document.getElementById("changeUsernameButton").addEventListener('click', displayChangeUsernameForm);
document.getElementById("changePasswordButton").addEventListener('click', displayChangePasswordForm);
document.getElementById("deleteAccountButton").addEventListener('click', displayDeleteAccountForm);
document.getElementById("submitChangeUsernameButton").addEventListener('click', onChangeUsername);
document.getElementById("submitChangePasswordButton").addEventListener('click', onChangePassword);
document.getElementById("submitDeleteAccountButton").addEventListener('click', onDeleteAccount);
window.onload = fetchSessionAttributes;