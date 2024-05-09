// Get references to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
const registerButton = document.getElementById('registerButton');

// Function to open sign-in modal
function openSignInModal() {
    const signInModal = new bootstrap.Modal(document.getElementById('signInModal'));
    signInModal.show();
}
// Function to open sign-in modal
function openRegisterModal() {
    const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
    registerModal.show();
}

// Function to handle sign-in form submission
function handleSignIn() {
    // Get form input values
    const name = document.getElementById('name').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    //TODO sign-in logic

    // Close modal after sign-in
    const signInModal = new bootstrap.Modal(document.getElementById('signInModal'));
    signInModal.hide();

    // Optional: Reset form fields
    signInForm.reset();
}

// Event listeners
signInButton.addEventListener('click', openSignInModal);
registerButton.addEventListener('click', openRegisterModal);
signInModalBtn.addEventListener('click', handleSignIn);
