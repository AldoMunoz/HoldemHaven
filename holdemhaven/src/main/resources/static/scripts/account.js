const homeButton = document.getElementById("homeButton");
const handHistory = document.getElementById('handHistory');
const changeUsernameButton = document.getElementById('changeUsernameButton');
const changePasswordButton = document.getElementById('changePasswordButton');
const deleteAccountButton = document.getElementById('deleteAccountButton');
const changeUsernameForm = document.getElementById('changeUsernameForm');
const changePasswordForm = document.getElementById('changePasswordForm');
const deleteAccountForm = document.getElementById('deleteAccountForm');
const submitChangeUsernameButton = document.getElementById('submitChangeUsernameButton');
const submitChangePasswordButton = document.getElementById('submitChangePasswordButton');
const submitDeleteAccountButton = document.getElementById('submitDeleteAccountButton');

function displayChangeUsernameForm() {
    changePasswordForm.style.display = 'none';
    deleteAccountForm.style.display = 'none';
    changeUsernameForm.style.display = 'block';
}

function displayChangePasswordForm() {
    changeUsernameForm.style.display = 'none';
    deleteAccountForm.style.display = 'none';
    changePasswordForm.style.display = 'block';
}

function displayDeleteAccountForm() {
    changeUsernameForm.style.display = 'none';
    changePasswordForm.style.display = 'none';
    deleteAccountForm.style.display = 'block';
}

function getHandHistory() {
    fetch('/table/get-hand-history', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
    //get player id
    //go to table,filter and retrieve hands by player id
    //call function that displays last 100 hand history in a table
}

async function onChangeUsername() {
    const newUsername = document.getElementById('newUsername').value;

    fetch('/api/changeUsername', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUsername),
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Successfully changed username.');
                document.getElementById('username').textContent = data.username;
            } else {
                alert('Username change failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}

function onChangePassword() {
    const changePasswordRequest = {
        currentPassword: document.getElementById('currentPassword').value,
        newPassword: document.getElementById('newPassword').value,
        confirmNewPassword: document.getElementById('confirmNewPassword').value
    }

    fetch('/api/changePassword', {
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

function onDeleteAccount() {
    fetch('/api/deleteAccount', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(async data => {
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
                //TODO redirect to home page and log out
            } else {
                alert('Delete account failed: ' + data.message);
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert(error.message);
        });
}
async function fetchSessionAttributes() {
    try {
        const response = await fetch("/session-username-accBal");
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

handHistory.addEventListener('click', getHandHistory);
changeUsernameButton.addEventListener('click', displayChangeUsernameForm);
changePasswordButton.addEventListener('click', displayChangePasswordForm);
deleteAccountButton.addEventListener('click', displayDeleteAccountForm);
submitChangeUsernameButton.addEventListener('click', onChangeUsername);
submitChangePasswordButton.addEventListener('click', onChangePassword);
submitDeleteAccountButton.addEventListener('click', onDeleteAccount);
homeButton.addEventListener('click', fetchHomePage);
window.onload = fetchSessionAttributes;