const homeButton = document.getElementById("homeButton");
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

function onChangeUsername() {

}

function onChangePassword() {

}

function onDeleteAcocunt() {

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


changeUsernameButton.addEventListener('click', displayChangeUsernameForm);
changePasswordButton.addEventListener('click', displayChangePasswordForm);
deleteAccountButton.addEventListener('click', displayDeleteAccountForm);
submitChangeUsernameButton.addEventListener('click', onChangeUsername);
submitChangePasswordButton.addEventListener('click', onChangePassword);
submitDeleteAccountButton.addEventListener('click', onDeleteAccount);
homeButton.addEventListener('click', fetchHomePage);
window.onload = fetchSessionAttributes;