//References to DOM elements
const signInButton = document.getElementById('signInButton');
const signInModalBtn = document.getElementById('signInModalBtn');
const signInForm = document.getElementById('signInForm');
const registerForm = document.getElementById('registerForm');
const registerModalBtn = document.getElementById('registerModalBtn');
const registerButton = document.getElementById('registerButton');
const cashierButton = document.getElementById('cashierButton');
const accountButton = document.getElementById('accountButton');
const logOutButton = document.getElementById('logOutButton');
const playNowButton = document.getElementById('playNowButton');

document.addEventListener("DOMContentLoaded", function() {
    const chips = document.querySelectorAll(".chip");
    const anteArea = document.getElementById("ante-area");
    const dealerArea = document.getElementById("dealer-area");
    const tripsArea = document.getElementById("trips-area");
    const playArea = document.getElementById('play-area');

    let anteBetAmount = 0;
    let tripsBetAmount = 0;
    let playBetAmount = 0;
    let selectedChipSrc = '';
    let selectedChipValue = 0;

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

    //handle placing chips on trips area
    tripsArea.addEventListener('click', function() {
       if(selectedChipSrc) {
           placeTripsBet(selectedChipSrc);
           hideTripsAreaBorders();
       }
    });

    //logic for placing chips on the dealer and ante areas
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

    //logic for placing chip on trips area
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

    //stops flashing the ante and dealer areas
    function hideBetAreaBorders() {
        anteArea.classList.remove('flashing');
        dealerArea.classList.remove('flashing');
        anteArea.classList.add('no-border');
        dealerArea.classList.add('no-border');
    }

    //stops flashing of the trips area
    function hideTripsAreaBorders() {
        tripsArea.classList.remove('flashing');
        tripsArea.classList.add('no-border');
    }

    //enables the "deal" and "clear" buttons
    function enableButtons() {
        document.getElementById('deal-button').disabled = false;
        document.getElementById('clear-button').disabled = false;
    }

    //disables the "deal" and "clear" buttons
    function disableButtons() {
        document.getElementById('deal-button').disabled = true;
        document.getElementById('clear-button').disabled = true;
    }

    //display trips area
    function showTripsArea() {
        tripsArea.style.display = 'flex';
        tripsArea.classList.add('flashing');
    }

    //logic after "clear" button click
    function clearBets() {
        //reset and hide bet areas
        tripsArea.innerHTML = '';
        anteArea.innerHTML = '';
        dealerArea.innerHTML = '';

        tripsArea.classList.remove('no-border');
        tripsArea.style.display = 'none';

        anteArea.classList.remove('no-border');
        anteArea.style.display = 'none';

        dealerArea.classList.remove('no-border');
        dealerArea.style.display = 'none';

        //deselect chip
        deselectChip();

        //disable buttons
        disableButtons();

        //reset fields
        anteBetAmount = 0;
        tripsBetAmount = 0;
    }

    //logic after "deal" button click
    function dealHand() {
        const verifyBetRequest = {
            anteBetAmount: anteBetAmount,
            tripsBetAmount: tripsBetAmount
        }

        //verify the bet is valid
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
                    document.getElementById('balanceDisplay').textContent = data.accountBalance;

                    //deal cards
                    dealCards();
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    //logic to deal player and dealer hole cards
    function dealCards() {
        //call Table Service to deal hands
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
                //disable navbar buttons
                document.querySelector("#cashierButton a").classList.add('disabled');
                document.querySelector("#accountButton a").classList.add('disabled');
                document.querySelector("#logOutButton a").classList.add('disabled');

                //change the front-end after successful deal
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

    //displays dealer's hole cards in the front-end
    function displayDealerHoleCards() {
        const dealerHoleCardsDiv = document.querySelector('#dealer-cards-container');
        dealerHoleCardsDiv.innerHTML = `
            <img src="/images/cards/card-back.png" alt="Card Back" class="game-cards">
            <img src="/images/cards/card-back.png" alt="Card Back" class="game-cards">
        `;
    }

    //displays player's hole cards in the front-end
    function displayPlayerHoleCards(holeCardsArray) {
        const playerHoleCardsDiv = document.querySelector('#player-cards-container');
        const holeCard1 = `/images/cards/${holeCardsArray[0]}.png`;
        const holeCard2 = `/images/cards/${holeCardsArray[1]}.png`;

        playerHoleCardsDiv.innerHTML = `
            <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
            <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `;
    }

    //deselect and hide the chips div
    function hideChips() {
        deselectChip();
        document.querySelector('.chip-container').style.displalay = 'none';
    }

    //deselect the selected chip
    function deselectChip() {
        document.querySelectorAll('.selected-chip').forEach(selectedChip => {
            selectedChip.classList.remove('selected-chip');
        });
    }

    //change the button container to display the pre-flop betting options
    function displayPreFlopBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="4x-button" class="btn btn-primary btn-circle">4x</button>
            <button id="3x-button" class="btn btn-primary btn-circle">3x</button>
            <button id="check-button" class="btn btn-secondary btn-circle">Check</button>
        `;

        //add event listeners to the 4x, 3x, and check buttons
        document.getElementById("4x-button").addEventListener('click', function() {
            verifyPlayBet(4)
        });document.getElementById("3x-button").addEventListener('click', function() {
            verifyPlayBet(3)
        });
        document.getElementById("check-button").addEventListener('click', checkOption);
    }


    //change the button container to display the flop betting options
    function displayFlopBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="2x-button" class="btn btn-primary btn-circle">2x</button>
            <button id="check-button" class="btn btn-secondary btn-circle">Check</button>
        `;

        //add event listeners to the 2x and check buttons
        document.getElementById("2x-button").addEventListener('click', function() {
            verifyPlayBet(2)
        });
        document.getElementById("check-button").addEventListener('click', checkOption);
    }

    //change the button container to display the river betting options
    function displayRiverBettingOptions() {
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.innerHTML = `
            <button id="1x-button" class="btn btn-primary btn-circle">1x</button>
            <button id="check-button" class="btn bg-danger btn-circle">Fold</button>
        `;

        //add event listeners to the 1x and fold buttons
        document.getElementById("1x-button").addEventListener('click', function() {
            verifyPlayBet(1)
        });
        document.getElementById("check-button").addEventListener('click', foldHand);
    }


    //logic after 4x, 3x, 2x, and 1x bet button click
    function verifyPlayBet(betMultiplier) {
        //sends request to Player Service to verify if play bet is valid
        fetch('/api/verifyPlay', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(anteBetAmount*betMultiplier)
        })
            .then(response => response.json())
            .then(data => {
                //if the bet is valid, update the front-end accordingly
                if(data.success) {
                    //update player account balance
                    document.getElementById('balanceDisplay').textContent = data.accountBalance;
                    //set play bet Amount
                    playBetAmount = anteBetAmount*betMultiplier;
                    //display chips in front-end
                    displayPlayBet(betMultiplier);
                    //hide button container
                    document.querySelector('.button-container').style.display = 'none';

                    placePlayBet(playBetAmount);
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    //logic for after placing the play bet
    function placePlayBet(betAmount) {
        const playerActionRequest = {
            action: 'B',
            betAmount: betAmount
        }

        //send request to Table Service to simulate game logic after a valid bet
        fetch('/table/player-action', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(playerActionRequest)
        })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    //reveal the rest of the board, hole cards, and hand result, with timeout for dramatic effect
                    const timeouts = runOutBoard(data.boardCards, data.street);
                    setTimeout(() => {
                        revealDealerHoleCards(data.dealerHoleCards);
                    }, timeouts.timeout1);
                    setTimeout(() => {
                        determineWinner();
                    }, timeouts.timeout2);
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    //runout and display the rest of the board, depending on the current street
    function runOutBoard(cards, street) {
        //if pre-flop, runout the flop and then the turn and river
        if(street === "f") {
            dealFlop(cards.slice(0, 3));
            setTimeout(() => {
                dealTurnAndRiver(cards.slice(3, 5));
            }, 2000);
            return {timeout1: 4000, timeout2: 6000}
        }
        //if post-flop, only runout turn and river
        else if(street === "r") {
            dealTurnAndRiver(cards);
            return {timeout1: 2000, timeout2: 4000}
        }
        else {
            console.error("Street does not exist.");
        }
    }

    //change the images in the dealer cards div to display the dealer's hole cards face up
    function revealDealerHoleCards(cards) {
        const dealerCardsDiv = document.querySelector('#dealer-cards-container');
        const holeCard1 = `/images/cards/${cards[0]}.png`;
        const holeCard2 = `/images/cards/${cards[1]}.png`;

        dealerCardsDiv.innerHTML = `
            <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
            <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `;
    }

    //displays the play-bet in the front-end after a user selects a bet-multiplier (1x-4x)
    function displayPlayBet(chipCount) {
        let chipImageSource;

        //finds the correct image source based on the bet multiplier (chip count) and chip value
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

        //add the image the play area div
        playArea.innerHTML = `
            <img src="${chipImageSource}" alt="Play Chip" class="placed-chip" id="play-area">
        `

        //display the image
        playArea.classList.add('no-border');
        playArea.style.display = 'flex';
    }


    //calls Table Service method showdown to see if the player or dealer has the best hand
    function determineWinner() {
        fetch('/table/showdown', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                //after finding the best hand, call method to determine the payout
                determinePayout(data.winner, data.playerHandRanking, data.dealerHandRanking, data.playerHandToString, data.dealerHandToString);
            })
            .catch(error => {
                alert(error.message);
            });
    }

    //calls Player Service method payout which calculates how much the player won at the end of the hand
    function determinePayout(winner, playerHandRanking, dealerHandRanking, playerHandToString, dealerHandToString) {
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
                //if payout determination is successful:
                if(data.success) {
                    //display the dealer's hand
                    displayDealerHand(dealerHandToString);
                    //display the player's hand and the payout details
                    displayPlayerHandAndPayoutDetails(data.totalPayout, data.antePayout, data.dealerPayout, data.playPayout,
                        data.tripsPayout, playerHandToString);
                    //display the end hand message after timeout
                    setTimeout(() => {
                        displayEndHandMessage(data.totalPayout);
                    }, 2000);
                    //update the account balance in the header
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

    //display the dealer's hand ranking under their cards in the UI
    function displayDealerHand(dealerHandToString) {
        const dealerHandContainer = document.querySelector("#dealer-hand-container");
        dealerHandContainer.innerHTML = `
        <h6>${dealerHandToString}</h6>
        `
        dealerHandContainer.style.display = 'flex';
    }

    //displays the player's hand ranking, as well as how much each bet paid out, in the UI
    function displayPlayerHandAndPayoutDetails(totalPayout, antePayout, dealerPayout, playPayout, tripsPayout, playerHandToString) {
        const playerHandContainer = document.querySelector("#player-hand-container");

        //adds (push) to the payout amount to indicate that the bet was a tie
        if(antePayout === anteBetAmount) antePayout = antePayout + " (push)";
        if(dealerPayout === anteBetAmount) dealerPayout = dealerPayout + " (push)";
        if(playPayout === playBetAmount) playPayout = playPayout + " (push)";

        //if the player lost all bets, display a "NO WINS" message
        if(totalPayout === 0) {
            playerHandContainer.innerHTML = `
            <h6><u>${playerHandToString}</u></h6>
            <h5>NO WINS</h5>
            `
        }
        //if the player did not place a trips bet, omit trips bet from payout details
        else if(tripsBetAmount === 0) {
            playerHandContainer.innerHTML = `
            <h6><u>${playerHandToString}</u></h6>
            <p class="small">ANTE WIN: ${antePayout}</p>
            <p class="small">DEALER WIN: ${dealerPayout}</p>
            <p class="small">PLAY WIN: ${playPayout}</p>
            `
        }
        //if the player placed a trips bet, show all payouts
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

    //displays message with how much the user won
    //displays button that clears the table and starts the next hand
    function displayEndHandMessage(totalPayout) {
        const handSummaryContainer = document.querySelector("#hand-summary-container");

        //if the player didn't win, display "NO WINS" message
        if(totalPayout === 0) {
            handSummaryContainer.innerHTML = `
            <h2>NO WINS</h2>
            <button id="end-hand-button" class="btn btn-primary">click to continue</button>
            `
        }
        //else display how much they won
        else {
            handSummaryContainer.innerHTML = `
            <h2>YOU WON: $${totalPayout}</h2>
            <button id="end-hand-button" class="btn btn-primary">click to continue</button>
            `
        }

        document.getElementById("end-hand-button").addEventListener('click', endHand);
        handSummaryContainer.style.display = 'flex';
    }

    //send message to Table Service to reset all fields in the back-end
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
                //calls clear table if successful
                clearTable();
            })
            .catch(error => {
                alert(error.message);
            });

    }

    //clears and resets the front-end UI to its original state after the end of a hand
    function clearTable() {
        //display chips
        document.querySelector('.chip-container').style.displalay = 'flex';

        //reset all the bet containers
        document.getElementById('player-cards-container').innerHTML = '';
        document.getElementById('dealer-cards-container').innerHTML = '';
        document.getElementById('board-cards-container').innerHTML = '';
        anteArea.innerHTML = '';
        anteArea.classList.remove('no-border');
        anteArea.style.display = 'none';
        dealerArea.innerHTML = '';
        dealerArea.classList.remove('no-border');
        dealerArea.style.display = 'none';
        tripsArea.innerHTML = '';
        tripsArea.classList.remove('no-border');
        tripsArea.style.display = 'none';
        playArea.innerHTML = '';

        //reset and display the action button container
        document.querySelector('.button-container').innerHTML = `
            <button id="deal-button" class="btn btn-success btn-circle" disabled>Deal</button>
            <button id="clear-button" class="btn btn-danger btn-circle" disabled>Clear</button>
        `
        document.querySelector('.button-container').style.display = 'flex';

        //clear and reset the hand and board containers
        document.getElementById('player-hand-container').innerHTML = '';
        document.getElementById('player-hand-container').style.display = 'none';
        document.getElementById('dealer-hand-container').innerHTML = '';
        document.getElementById('dealer-hand-container').style.display = 'none';
        document.getElementById('hand-summary-container').innerHTML = '';
        document.getElementById('hand-summary-container').style.display = 'none';

        //reset the variable fields
        anteBetAmount = 0;
        tripsBetAmount = 0;
        playBetAmount = 0;
        selectedChipSrc = '';
        selectedChipValue = 0;

        //enable navbar buttons
        document.querySelector("#cashierButton a").classList.remove('disabled');
        document.querySelector("#accountButton a").classList.remove('disabled');
        document.querySelector("#logOutButton a").classList.remove('disabled');

        //re-add event listeners to the "deal" and "clear" buttons
        document.getElementById('deal-button').addEventListener('click', dealHand);
        document.getElementById('clear-button').addEventListener('click', clearBets);
    }

    //displays the flop in the UI
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

    //displays the turn and river in the UI
    function dealTurnAndRiver(cards) {
        const boardCardsDiv = document.querySelector('#board-cards-container');
        const holeCard1 = `/images/cards/${cards[0]}.png`;
        const holeCard2 = `/images/cards/${cards[1]}.png`;

        boardCardsDiv.insertAdjacentHTML('beforeend', `
        <img src="${holeCard1}" alt="Hole Card 1" class="game-cards">
        <img src="${holeCard2}" alt="Hole Card 2" class="game-cards">
        `);
    }


    //calls Table Service function playerAction, with data for when a user checks their hand option
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
                //if the request is successful, display the next street accordingly
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
            });
    }

    //calls Table Service function playerAction, with data for when a user folds their hand
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
               //if the request is successful, commence fold hand sequence
                if(data.success) {
                    document.querySelector('.button-container').style.display = 'none';
                    //calls function to retrieve the dealer's hand ranking
                    getDealerHand();
                }
                else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }

    //calls Table Service method getDealerHand for when the player folds
    function getDealerHand() {
        fetch('/table/get-dealer-hand', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                //reveal the dealer hole cards
                revealDealerHoleCards(data.dealerHoleCards);
                //display the dealer's hand ranking
                displayDealerHand(data.dealerHandToString);
                //display the end hand message after timeout
                setTimeout(() => {
                    displayEndHandMessage(0);
                }, 2000);
            })
            .catch(error => {
                alert(error.message);
            });
    }


    //add event listener to the deal and clear buttons
    document.getElementById('deal-button').addEventListener('click', dealHand);
    document.getElementById('clear-button').addEventListener('click', clearBets);
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

async function fetchAccountPage() {
    try {
        const response = await fetch("/account");
        if(response.ok) {
            console.log("Successfully fetched account page")
        }
        else {
            console.error("Failed to fetch account page");
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


            document.getElementById('gameContainer').style.display = 'none';
            document.getElementById('startGameContainer').style.display = 'flex';

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
accountButton.addEventListener('click', fetchAccountPage);
logOutButton.addEventListener('click', handleLogOut);
playNowButton.addEventListener('click', openGame);
//Get session id when user opens the page
window.onload = fetchSessionInformation;

