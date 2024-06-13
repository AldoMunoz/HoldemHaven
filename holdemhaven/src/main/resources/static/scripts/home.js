//References to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
const registerForm = document.getElementById('registerForm');
const registerModalBtn = document.getElementById('registerModalBtn');
const registerButton = document.getElementById('registerButton');
const cashierButton = document.getElementById('cashierButton');
const logOutButton = document.getElementById('logOutButton');
const playNowButton = document.getElementById('playNowButton');

document.addEventListener("DOMContentLoaded", function() {
    const chips = document.querySelectorAll(".chip");
    const dealButton = document.getElementById("deal-button");
    const clearButton = document.getElementById("clear-button");
    const anteArea = document.getElementById("ante-area");
    const dealerArea = document.getElementById("dealer-area");
    const tripsArea = document.getElementById("trips-area");
    const playArea = document.getElementById('play-area');

    let anteBetAmount = 0;
    let tripsBetAmount = 0;
    let playBetAmount = 0;
    let selectedChipSrc = '';
    let selectedChipValue = 0;
    let playerHandToString = "";
    let dealerHandToString = "";

    //initialize bootstrap tooltips
    chips.forEach(chip => {
        new bootstrap.Tooltip(chip);
    });

    //handle chip selection
    chips.forEach(chip => {
        chip.addEventListener("click", function() {
            deselectChip();

            this.classList.add('selected-chip');
            selectedChipSrc = this.src;
            selectedChipValue = this.getAttribute('data-value');

            anteArea.style.display = 'flex';
            dealerArea.style.display = 'flex';
            anteArea.classList.add('flashing');
            dealerArea.classList.add('flashing');
        });
    });

    //handle placing chip on ante area
    anteArea.addEventListener("click", function() {
        if(selectedChipSrc) {
            placeMainBet(selectedChipSrc);
            hideBetAreaBorders();
            enableButtons();
            showTripsArea();
        }
    });

    //handle placing chip on dealer area
    dealerArea.addEventListener("click", function() {
        if(selectedChipSrc) {
            placeMainBet(selectedChipSrc);
            hideBetAreaBorders();
            enableButtons();
            showTripsArea();
        }
    });

    tripsArea.addEventListener('click', function() {
       if(selectedChipSrc) {
           placeTripsBet(selectedChipSrc);
           hideTripsAreaBorders();
       }
    });

    //places the chip on the trips area
    function placeTripsBet(src) {
        // Remove any existing chip in the area
        while (tripsArea.firstChild) {
            tripsArea.removeChild(tripsArea.firstChild);
        }
        const chip = document.createElement("img");
        chip.src = src;
        chip.classList.add("placed-chip");
        tripsArea.appendChild(chip);
        tripsBetAmount = selectedChipValue;
        console.log(tripsBetAmount);
    }

    //place chips on the dealer and ante areas
    function placeMainBet(src) {
        while(anteArea.firstChild ) {
            anteArea.removeChild(anteArea.firstChild);
            dealerArea.removeChild(dealerArea.firstChild);
        }
        const anteChip = document.createElement("img");
        anteChip.src = src;
        anteChip.classList.add("placed-chip");

        const dealerChip = document.createElement("img");
        dealerChip.src = src;
        dealerChip.classList.add("placed-chip");

        anteArea.appendChild(anteChip);
        dealerArea.appendChild(dealerChip);

        anteBetAmount = Number(selectedChipValue);
        console.log(anteBetAmount);
    }

    //hides and stops flashing the ante and dealer areas
    function hideBetAreaBorders() {
        anteArea.classList.remove('flashing');
        dealerArea.classList.remove('flashing');
        anteArea.classList.add('no-border');
        dealerArea.classList.add('no-border');
    }

    function hideTripsAreaBorders() {
        tripsArea.classList.remove('flashing');
        tripsArea.classList.add('no-border');
    }

    //enables the "deal" and "clear" buttons
    function enableButtons() {
        dealButton.disabled = false;
        clearButton.disabled = false;
    }

    //disables the "deal" and "clear" buttons
    function disableButtons() {
        dealButton.disabled = true;
        clearButton.disabled = true;
    }

    //display trips area
    function showTripsArea() {
        tripsArea.style.display = 'flex';
        tripsArea.classList.add('flashing');
    }

    function clearBets() {
        tripsArea.innerHTML = '';
        anteArea.innerHTML = '';
        dealerArea.innerHTML = '';

        tripsArea.classList.remove('no-border');
        tripsArea.style.display = 'none';

        anteArea.classList.remove('no-border');
        anteArea.style.display = 'none';

        dealerArea.classList.remove('no-border');
        dealerArea.style.display = 'none';

        document.querySelectorAll('.selected-chip').forEach(selectedChip => {
            selectedChip.classList.remove('selected-chip');
        });

        disableButtons();

        anteBetAmount = 0;
        tripsBetAmount = 0;
    }

    function dealHand() {
        const verifyBetRequest = {
            anteBetAmount: anteBetAmount,
            tripsBetAmount: tripsBetAmount
        }

        fetch('/api/verifyBet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(verifyBetRequest)
        })
            .then(response => response.json())
            .then(data => {
                console.log("Verify Bet Data: ", data);
                if(data.success) {
                    //update account balance
                    document.getElementById('balanceDisplay').textContent = data.accountBalance.toFixed(0);

                    //deal-cards
                    fetch('/table/deal-hand', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },

                        body: JSON.stringify({})
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.json();
                        })
                        .then(data => {
                            displayDealerHoleCards();
                            displayPlayerHoleCards(data.playerHoleCards);
                            hideChips();
                            hideTripsAreaBorders();
                            displayPreFlopBettingOptions();
                        })
                        .catch(error => {
                            console.error('There was a problem with the fetch operation:', error);
                        });
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    function displayDealerHoleCards() {
        const dealerHoleCardsDiv = document.querySelector('#dealer-cards-container');
        dealerHoleCardsDiv.innerHTML = `
            <img src="/images/cards/card-back.png" alt="Card Back" class="game-cards">
            <img src="/images/cards/card-back.png" alt="Card Back" class="game-cards">
        `;
    }

    function displayPlayerHoleCards(holeCardsArray) {
        const playerHoleCardsDiv = document.querySelector('#player-cards-container');
        const holeCard1 = `/images/cards/${holeCardsArray[0]}.png`;
        const holeCard2 = `/images/cards/${holeCardsArray[1]}.png`;

        playerHoleCardsDiv.innerHTML = `
            <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
            <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `;
    }

    function hideChips() {
        document.querySelector('.chip-container').style.displalay = 'none';
    }

    function showChips() {
        document.querySelector('.chip-container').style.displalay = 'flex';
    }


    function deselectChip() {
        document.querySelectorAll('.selected-chip').forEach(selectedChip => {
            selectedChip.classList.remove('selected-chip');
        });
    }

    function displayPreFlopBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="4x-button" class="btn btn-primary btn-circle">4x</button>
            <button id="3x-button" class="btn btn-primary btn-circle">3x</button>
            <button id="check-button" class="btn btn-secondary btn-circle">Check</button>
        `;

        document.getElementById("4x-button").addEventListener('click', function() {
            placePlayBet(4)
        });document.getElementById("3x-button").addEventListener('click', function() {
            placePlayBet(3)
        });
        document.getElementById("check-button").addEventListener('click', checkOption);
    }

    function displayFlopBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="2x-button" class="btn btn-primary btn-circle">2x</button>
            <button id="check-button" class="btn btn-secondary btn-circle">Check</button>
        `;

        document.getElementById("2x-button").addEventListener('click', function() {
            placePlayBet(2)
        });
        document.getElementById("check-button").addEventListener('click', checkOption);
    }

    function displayRiverBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="1x-button" class="btn btn-primary btn-circle">1x</button>
            <button id="check-button" class="btn bg-danger btn-circle">Fold</button>
        `;

        document.getElementById("1x-button").addEventListener('click', function() {
            placePlayBet(1)
        });
        document.getElementById("check-button").addEventListener('click', foldHand);
    }

    function displayPlayBet(chipCount) {
        let chipImageSource;

        if(anteBetAmount === 1) {
            chipImageSource = `/images/chip-stacks/${chipCount}x_one_dollar_chip.png`;
        }
        else if(anteBetAmount === 5) {
            chipImageSource = `/images/chip-stacks/${chipCount}x_five_dollar_chip.png`;
        }
        else if (anteBetAmount === 25) {
            chipImageSource = `/images/chip-stacks/${chipCount}x_twenty_five_dollar_chip.png`;
        }
        else {
            console.error("Ante bet amount not set.")
        }

        playArea.innerHTML = `
            <img src="${chipImageSource}" alt="Play Chip" class="placed-chip" id="play-area">
        `

        playArea.classList.add('no-border');
        playArea.style.display = 'flex';
    }

    function placePlayBet(betMultiplier) {
        fetch('/api/verifyPlay', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(anteBetAmount*betMultiplier)
        })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    //update player account balance
                    document.getElementById('balanceDisplay').textContent = data.accountBalance;
                    //set play bet Amount
                    playBetAmount = anteBetAmount*betMultiplier;
                    //display chips in front-end
                    displayPlayBet(betMultiplier);
                    //hide button container TODO (probably need to rename that)
                    document.querySelector('.button-container').style.display = 'none';

                    const playerActionRequest = {
                        action: 'B',
                        betAmount: anteBetAmount*betMultiplier
                    }

                    fetch('/table/player-action', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(playerActionRequest)
                    })
                        .then(response => response.json())
                        .then(data => {
                            console.log(data);
                            if(data.success) {
                                runOutBoard(data.boardCards, data.street);
                                setTimeout(() => {
                                    revealDealerHoleCards(data.dealerHoleCards);
                                }, 4000);
                                setTimeout(() => {
                                    determineWinner();
                                }, 6000);
                            }
                            else {
                                alert(data.message);
                            }
                        })
                        .catch(error => {
                            alert(error.message);
                        });
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    function determineWinner() {
        fetch('/table/showdown', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log("Winner data", data);
                //TODO save the dealer toString and player toString;
                playerHandToString = data.playerHandToString;
                dealerHandToString = data.dealerHandToString;
                determinePayout(data.winner, data.playerHandRanking, data.dealerHandRanking);
            })
            .catch(error => {
                alert(error.message);
            });
    }

    function determinePayout(winner, playerHandRanking, dealerHandRanking) {
        const payoutRequest = {
            anteBetAmount: anteBetAmount,
            tripsBetAmount: tripsBetAmount,
            playBetAmount: playBetAmount,
            winner: winner,
            playerHandRanking: playerHandRanking,
            dealerHandRanking: dealerHandRanking
        }

        fetch('/api/payout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payoutRequest)
        })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    console.log(data);
                    displayDealerHand();
                    displayPlayerHandAndPayoutDetails(data.totalPayout, data.antePayout, data.dealerPayout, data.playPayout, data.tripsPayout);
                    displayEndHandMessage(data.totalPayout);
                    document.getElementById('balanceDisplay').textContent = data.accountBalance;
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    function displayDealerHand() {
        const dealerHandContainer = document.querySelector("#dealer-hand-container");
        dealerHandContainer.innerHTML = `
        <h6>${dealerHandToString}</h6>
        `
        dealerHandContainer.style.display = 'flex';
    }

    function displayPlayerHandAndPayoutDetails(totalPayout, antePayout, dealerPayout, playPayout, tripsPayout) {
        const playerHandContainer = document.querySelector("#player-hand-container");

        if(antePayout === anteBetAmount) antePayout = antePayout + " (push)";
        if(dealerPayout === anteBetAmount) dealerPayout = dealerPayout + " (push)";
        if(playPayout === playBetAmount) playPayout = playPayout + " (push)";

        if(totalPayout === 0) {
            playerHandContainer.innerHTML = `
            <h6><u>${playerHandToString}</u></h6>
            <h5>NO WINS</h5>
            `
        }
        else if(tripsBetAmount === 0) {
            playerHandContainer.innerHTML = `
            <h6><u>${playerHandToString}</u></h6>
            <p class="small">ANTE WIN: ${antePayout}</p>
            <p class="small">DEALER WIN: ${dealerPayout}</p>
            <p class="small">PLAY WIN: ${playPayout}</p>
            `
        }
        else {
            playerHandContainer.innerHTML = `
            <h6><u>${playerHandToString}</u></h6>
            <p class="small">ANTE WIN: ${antePayout}</p>
            <p class="small">DEALER WIN: ${dealerPayout}</p>
            <p class="small">PLAY WIN: ${playPayout}</p>
            <p class="small">TRIPS WIN: ${tripsPayout}</p>
            `
        }

        playerHandContainer.style.display = 'flex';
    }

    function displayEndHandMessage(totalPayout) {
        const handSummaryContainer = document.querySelector("#hand-summary-container");

        if(totalPayout === 0) {
            handSummaryContainer.innerHTML = `
            <h2>NO WINS</h2>
            <button id="end-hand-button" class="btn btn-primary">click to continue</button>
            `
        }
        else {
            handSummaryContainer.innerHTML = `
            <h2>YOU WON: $${totalPayout}</h2>
            <button id="end-hand-button" class="btn btn-primary">click to continue</button>
            `
        }

        document.getElementById("end-hand-button").addEventListener('click', endHand);
        handSummaryContainer.style.display = 'flex';
    }

    function endHand() {
        fetch('/table/end-hand', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                console.log('Request was successful');
                clearTable();
            })
            .catch(error => {
                alert(error.message);
                // TODO: restart game?
            });

    }

    function clearTable() {
        showChips();
        document.getElementById('player-cards-container').innerHTML = '';
        document.getElementById('dealer-cards-container').innerHTML = '';
        document.getElementById('board-cards-container').innerHTML = '';
        anteArea.innerHTML = '';
        dealerArea.innerHTML = '';
        tripsArea.innerHTML = '';
        playArea.innerHTML = '';
        document.querySelector('.button-container').innerHTML = `
            <button id="deal-button" class="btn btn-success btn-circle" disabled>Deal</button>
            <button id="clear-button" class="btn btn-danger btn-circle" disabled>Clear</button>
        `
        document.querySelector('.button-container').style.display = 'flex';

        document.getElementById('player-hand-container').innerHTML = '';
        document.getElementById('player-hand-container').style.display = 'none';
        document.getElementById('dealer-hand-container').innerHTML = '';
        document.getElementById('dealer-hand-container').style.display = 'none';
        document.getElementById('hand-summary-container').innerHTML = '';
        document.getElementById('hand-summary-container').style.display = 'none';


        //TODO clear all cards divs
        //TODO clear placed chip divs
        //TODO reset, display, and disable bet and clear buttons
        //
        //TODO clear hand divs
    }

    function dealFlop(cards) {
        const boardCardsDiv = document.querySelector('#board-cards-container');
        const holeCard1 = `/images/cards/${cards[0]}.png`;
        const holeCard2 = `/images/cards/${cards[1]}.png`;
        const holeCard3 = `/images/cards/${cards[2]}.png`;

        boardCardsDiv.innerHTML = `
            <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
            <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
            <img src="${holeCard3}" alt="Hole Card 2" class="game-cards">
        `;
    }

    function dealTurnAndRiver(cards) {
        const boardCardsDiv = document.querySelector('#board-cards-container');
        const holeCard1 = `/images/cards/${cards[0]}.png`;
        const holeCard2 = `/images/cards/${cards[1]}.png`;

        boardCardsDiv.insertAdjacentHTML('beforeend', `
        <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
        <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `);
    }

    function runOutBoard(cards, street) {
        if(street === "f") {
            dealFlop(cards.slice(0, 3));
            setTimeout(() => {
                dealTurnAndRiver(cards.slice(3, 5));
            }, 2000);

        }
        else if(street === "r") {
            dealTurnAndRiver(cards);
        }
        else {
            console.error("Street does not exist.");
        }
    }

    function revealDealerHoleCards(cards) {
        const dealerCardsDiv = document.querySelector('#dealer-cards-container');
        const holeCard1 = `/images/cards/${cards[0]}.png`;
        const holeCard2 = `/images/cards/${cards[1]}.png`;

        dealerCardsDiv.innerHTML = `
            <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
            <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `;
    }

    function checkOption() {
        const playerActionRequest = {
            action: 'C',
            betAmount: 0
        }

        fetch('/table/player-action', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(playerActionRequest)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if(data.success) {
                    //if flop do flop action function call
                    if(data.street === 'f') {
                        dealFlop(data.boardCards);
                        displayFlopBettingOptions();
                    }
                    //if river do river action function call
                    else if(data.street === 'r') {
                        dealTurnAndRiver(data.boardCards);
                        displayRiverBettingOptions();
                    }
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
                //TODO restart game?
            });
    }

    //TODO
    function foldHand() {
        const playerActionRequest = {
            action: 'F',
            betAmount: 0
        }

        fetch('/table/player-action', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(playerActionRequest)
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                if(data.success) {
                    document.querySelector('.button-container').style.display = 'none';
                    getDealerHand();
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
                //TODO restart game?
            });
    }

    function getDealerHand() {
        fetch('/table/get-dealer-hand', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                console.log(data);
                revealDealerHoleCards(data.dealerHoleCards);
                dealerHandToString = data.dealerHandToString;
                displayDealerHand();
                setTimeout(() => {
                    displayEndHandMessage(0);
                }, 2000);
            })
            .catch(error => {
                alert(error.message);
                //TODO restart game?
            });
    }

    clearButton.addEventListener('click', clearBets);
    dealButton.addEventListener('click', dealHand);
});

//fetches the session ID
async function fetchSessionInformation() {
    try {
        const response = await fetch("/session-username-accBal");
        if(response.ok) {
            const attributes = await response.json();
            if(attributes.username != null && attributes.accountBalance != null) {
                document.getElementById('usernameDisplay').textContent = attributes.username;
                document.getElementById('balanceDisplay').textContent = attributes.accountBalance;

                document.getElementById('loginContainer').style.display = 'none';
                document.getElementById('userInfoContainer').style.display = 'block';

                document.getElementById('playNowButton').removeAttribute('disabled');
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

                document.getElementById('playNowButton').removeAttribute('disabled');
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
            document.getElementById('playNowButton').setAttribute('disabled', true);
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
    document.getElementById('gameContainer').style.display = 'block';


    //instantiate table in the back-end
    fetch('/table/new-game', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({})
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

//Event listeners
signInButton.addEventListener('click', openSignInModal);
registerButton.addEventListener('click', openRegisterModal);
signInModalBtn.addEventListener('click', handleSignIn);
registerModalBtn.addEventListener('click', handleRegister);
cashierButton.addEventListener('click', fetchCashierPage);
logOutButton.addEventListener('click', handleLogOut);
playNowButton.addEventListener('click', openGame);
//Get session id when user opens the page
window.onload = fetchSessionInformation;

