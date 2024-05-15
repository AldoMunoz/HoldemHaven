//Get references to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
const registerForm = document.getElementById('registerForm');
const registerModalBtn = document.getElementById('registerModalBtn');
const registerButton = document.getElementById('registerButton');

//Opens sign-in modal
function openSignInModal() {
    const signInModal = new bootstrap.Modal(document.getElementById('signInModal'));
    signInModal.show();
}
//Opens register modal
function openRegisterModal() {
    const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
    registerModal.show();
}

//Handles sign-in form submission
function handleSignIn() {
    const loginPlayerRequest = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    fetch('/api/signIn', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginPlayerRequest)
    })
        .then(response => response.json())
        .then(data => {
            console.log("Data: ", data);
            //TODO reload the page with the player logged in
            if(data.success) {
                updateHeaderUponSignIn(data.playerUsername, data.accountBalance);

                // Reset form fields
                signInForm.reset();

                // Try to hide the modal
                const signInModalElement = document.getElementById('signInModal');
                const signInModal = bootstrap.Modal.getInstance(signInModalElement);
                if (signInModal) {
                    signInModal.hide();
                } else {
                    console.error('Modal instance not found');
                }
            }
            else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert(error.message);  // Display any caught errors
        });
}


//Handles register form submission
function handleRegister() {
    const registerPlayerRequest = {
        firstName: document.getElementById('newFirstName').value,
        lastName: document.getElementById('newLastName').value,
        email: document.getElementById('newEmail').value,
        username: document.getElementById('newUsername').value,
        password: document.getElementById('newPassword').value,
        confirmPassword: document.getElementById('newConfirmPassword').value
    };

    fetch('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerPlayerRequest)
    })
        .then(response => response.json())
        .then(data => {
            console.log("Data:", data);
            if(data.success) {
                //Close modal after sign-in
                const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
                registerModal.hide();

                //Reset form fields
                registerForm.reset();
                location.reload();
                alert(data.message);
            }
            else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert(error.message);  // Display any caught errors
        });
}

function updateHeaderUponSignIn(username, accountBalance) {
    const loginContainer = document.getElementById('loginContainer');
    const usernameContainer = document.getElementById('usernameContainer');
    const balanceContainer = document.getElementById('balanceContainer');
    const usernameDisplay = document.getElementById('usernameDisplay');
    const balanceDisplay = document.getElementById('balanceDisplay');
    const cashierButton = document.getElementById('cashierButton');

    //Update the username and balance
    usernameDisplay.textContent = username;
    balanceDisplay.textContent = accountBalance;

    //Hide login/register buttons
    loginContainer.style.display = 'none';

    //Show user info
    usernameContainer.style.display = 'block';
    balanceContainer.style.display = 'block';
    cashierButton.style.display = 'block';
}


//Event listeners
signInButton.addEventListener('click', openSignInModal);
registerButton.addEventListener('click', openRegisterModal);
signInModalBtn.addEventListener('click', handleSignIn);
registerModalBtn.addEventListener('click', handleRegister)

