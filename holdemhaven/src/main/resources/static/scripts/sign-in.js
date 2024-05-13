//Get references to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
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
    //Get form input values
    const name = document.getElementById('name').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    //TODO sign-in logic

    //Close modal after sign-in
    const signInModal = new bootstrap.Modal(document.getElementById('signInModal'));
    signInModal.hide();

    //Reset form fields
    signInForm.reset();
}


//Handles register form submission
function handleRegister() {
    const player = {
        firstName: document.getElementById('newFirstName').value,
        lastName: document.getElementById('newLastName').value,
        email: document.getElementById('newEmail').value,
        username: document.getElementById('newUsername').value,
        password: document.getElementById('newPassword').value
    };

    //Sends POST request to the Player Controller
    fetch('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(player)
    })
        .then(response => {
            if (response.ok) {
                alert('Registration successful!');
                location.reload();
            }
            else {
                return response.json().then(data => { throw new Error(data.message || 'Registration failed'); });
            }
        })
        .catch(error => alert(error.message));
}

//Event listeners
signInButton.addEventListener('click', openSignInModal);
registerButton.addEventListener('click', openRegisterModal);
signInModalBtn.addEventListener('click', handleSignIn);
registerModalBtn.addEventListener('click', handleRegister)

