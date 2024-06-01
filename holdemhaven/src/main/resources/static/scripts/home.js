//References to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
const registerForm = document.getElementById('registerForm');
const registerModalBtn = document.getElementById('registerModalBtn');
const registerButton = document.getElementById('registerButton');
const cashierButton = document.getElementById('cashierButton');
const logOutButton = document.getElementById('logOutButton');
const playButton = document.getElementById('playButton');

document.addEventListener("DOMContentLoaded", function() {
    const chips = document.querySelectorAll(".chip");
    const dealButton = document.getElementById("deal-button");
    const clearButton = document.getElementById("clear-button");
    const anteArea = document.getElementById("ante-area");
    const dealerArea = document.getElementById("dealer-area");

    let selectedChipSrc = '';

    //initialize bootstrap tooltips
    chips.forEach(chip => {
        new bootstrap.Tooltip(chip);
    });

    //handle chip selection
    chips.forEach(chip => {
        chip.addEventListener("click", function() {
            document.querySelectorAll('.selected-chip').forEach(selectedChip => {
                selectedChip.classList.remove('selected-chip');
            });
            this.classList.add('selected-chip');
            selectedChipSrc = this.src;

            anteArea.style.display = 'flex';
            dealerArea.style.display = 'flex';
            anteArea.classList.add('flashing');
            dealerArea.classList.add('flashing');
        });
    });

    //handle placing chip on ante area
    anteArea.addEventListener("click", function() {
        if (selectedChipSrc) {
            placeChip(this, selectedChipSrc);
            placeChip(dealerArea, selectedChipSrc);
            hideBetAreaBorders();
            enableButtons();
        }
    });

    //handle placing chip on dealer area
    dealerArea.addEventListener("click", function() {
        if (selectedChipSrc) {
            placeChip(this, selectedChipSrc);
            placeChip(anteArea, selectedChipSrc);
            hideBetAreaBorders();
            enableButtons();
        }
    });

    //places the chip on the table
    function placeChip(area, src) {
        // Remove any existing chip in the area
        while (area.firstChild) {
            area.removeChild(area.firstChild);
        }
        const chip = document.createElement("img");
        chip.src = src;
        chip.classList.add("placed-chip");
        area.appendChild(chip);
    }

    //hides and stops flashing the ante and dealer areas
    function hideBetAreaBorders() {
        anteArea.classList.remove('flashing');
        dealerArea.classList.remove('flashing');
        anteArea.classList.add('no-border');
        dealerArea.classList.add('no-border');
    }

    //enables the "deal" and "clear" buttons
    function enableButtons() {
        dealButton.disabled = false;
        clearButton.disabled = false;
    }
});

//fetches the session ID
async function fetchSessionInformation() {
    try {
        const response = await fetch("/session-username-accBal");
        if(response.ok) {
            const attributes = await response.json();
            if(attributes.username != null && attributes.accountBalance != null) {
                document.getElementById('usernameDisplay').textContent = attributes.username;
                document.getElementById('balanceDisplay').textContent = attributes.accountBalance.toFixed(2);

                document.getElementById('loginContainer').style.display = 'none';
                document.getElementById('userInfoContainer').style.display = 'block';

                document.getElementById('playButton').removeAttribute('disabled');
            }
            else {
                document.getElementById('loginContainer').style.display = 'block';
                document.getElementById('userInfoContainer').style.setProperty('display', 'none', 'important');
            }
        }
        else {
            console.error("Failed to fetch session attributes.");
        }
    }
    catch (error) {
        console.error("Error ", error);
    }
}


//opens sign-in modal
function openSignInModal() {
    const signInModal = new bootstrap.Modal(document.getElementById('signInModal'));
    signInModal.show();
}
//opens register modal
function openRegisterModal() {
    const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
    registerModal.show();
}

//handles sign-in form submission
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
            if(data.success) {
                updateHeaderUponSignIn(data.playerUsername, data.accountBalance);

                //resets form fields
                signInForm.reset();

                //hides the modal
                const signInModalElement = document.getElementById('signInModal');
                const signInModal = bootstrap.Modal.getInstance(signInModalElement);
                if (signInModal) {
                    signInModal.hide();
                } else {
                    console.error('Modal instance not found');
                }

                document.getElementById('playButton').removeAttribute('disabled');
            }
            else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert(error.message);
        });
}

//handles register form submission
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
                //closes modal after sign-in
                const registerModal = new bootstrap.Modal(document.getElementById('registerModal'));
                registerModal.hide();

                //resets form fields
                registerForm.reset();
                location.reload();
                alert(data.message);
            }
            else {
                alert(data.message);
            }
        })
        .catch(error => {
            alert(error.message);
        });
}

async function fetchCashierPage() {
    try {
        const response = await fetch("/cashier");
        if(response.ok) {
            console.log("Successfully fetched cashier page")
        }
        else {
            console.error("Failed to fetch cashier page");
        }
    } catch (error){
        console.error("Error ", error);
    }
}

function updateHeaderUponSignIn(username, accountBalance) {
    const loginContainer = document.getElementById('loginContainer');
    const usernameDisplay = document.getElementById('usernameDisplay');
    const balanceDisplay = document.getElementById('balanceDisplay');
    const userInfoContainer = document.getElementById('userInfoContainer')

    //Update the username and balance
    usernameDisplay.textContent = username;
    balanceDisplay.textContent = accountBalance;

    //Hide login/register buttons
    loginContainer.style.display = 'none';

    //Show user info
    userInfoContainer.style.display = 'block';
}

async function handleLogOut() {
    try {
        const response = await fetch("/logout", {
            method: 'POST',
        });
        if (response.ok) {
            const result = await response.json().catch(() => ({}));
            if(result.message) {
                console.log(result.message());
            }
            document.getElementById('loginContainer').style.display = 'block';
            document.getElementById('userInfoContainer').style.setProperty('display', 'none', 'important');
            document.getElementById('playButton').setAttribute('disabled', true);
        }
        else {
            console.error("Failed to log out.");
        }
    } catch (error) {
        console.log("Error: ", error);
    }
}

function openGame() {
    document.getElementById('startGameContainer').style.display = 'none';

    document.getElementById('gameContainer').style.display = 'block'
}

//Event listeners
signInButton.addEventListener('click', openSignInModal);
registerButton.addEventListener('click', openRegisterModal);
signInModalBtn.addEventListener('click', handleSignIn);
registerModalBtn.addEventListener('click', handleRegister);
cashierButton.addEventListener('click', fetchCashierPage);
logOutButton.addEventListener('click', handleLogOut);
playButton.addEventListener('click', openGame);
//Get session id when user opens the page
window.onload = fetchSessionInformation;

