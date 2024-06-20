package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.requestDTOs.*;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.repositories.PlayerRepository;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
//service class used for handling the functionality of player actions, including registering, signing in, and any transactional action
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final String USERNAME_REGEX = "^[A-Za-z0-9_]{3,20}$";
    private final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);


    //checks input fields before saving a new Player to the repository
    public RegisterPlayerResponse verifyRegisterPlayerInformation(RegisterPlayerRequest request) {
        //Check if first name is valid
        if(!isValidName(request.getFirstName())) {
            return new RegisterPlayerResponse(false, "Invalid first name. Please use english letters only.");
        }
        //check if last name is valid
        else if(!isValidName(request.getLastName())) {
            return new RegisterPlayerResponse(false, "Invalid last name. Please use english letters only.");
        }
        //Check if email is valid
        else if(!isValidEmail(request.getEmail())) {
            return new RegisterPlayerResponse(false, "Invalid email.");
        }
        //check if email is already associated with an account in the database
        else if(!doesEmailExist(request.getEmail())) {
            return new RegisterPlayerResponse(false, "Email is already associated with an account.");
        }
        //Check if username is valid
        else if(!isValidUsername(request.getUsername())) {
            return new RegisterPlayerResponse(false, "Invalid username. Use only english letters, numbers and \"_\"");
        }
        //check if username exists
        else if(!doesUsernameExist(request.getUsername())) {
            return new RegisterPlayerResponse(false, "Username is already associated with an account.");
        }
        //Check if passwords match
        else if (!(request.getPassword().equals(request.getConfirmPassword()))) {
            return new RegisterPlayerResponse(false, "Passwords do not match.");
        }
        //Check if password contains certain characters
        else if(!isValidPassword(request.getPassword(), request.getConfirmPassword())) {
            return new RegisterPlayerResponse(false, "Invalid password. Must be between 8-30 characters, " +
                    "and only contain letters, numbers, and the following characters: \"!@#$%^&*()-_,.?\"");
        }
        else {
            //Create and save player to the repository
            Player player = new Player();
            player.setFirstName(request.getFirstName());
            player.setLastName(request.getLastName());
            player.setUsername(request.getUsername());
            player.setEmail(request.getEmail());
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            player.setPassword(encodedPassword);
            player.setAccountBalance(BigDecimal.valueOf(0));

            playerRepository.save(player);

            //Send a success message to the controller
            return new RegisterPlayerResponse(true, "Success, you can now sign in.");
        }
    }

    //checks if player's sign in credentials match those found in the player repository
    public LoginPlayerResponse verifySignInCredentials(LoginPlayerRequest request) {
        Player player = playerRepository.findByUsername(request.getUsername());

        if(player != null && passwordEncoder.matches(request.getPassword(), player.getPassword()))
            return new LoginPlayerResponse(true, "Success. You are now logged in.", player.getUsername(), player.getAccountBalance(), player.getId());

        return new LoginPlayerResponse(false, "Unsuccessful login. Try again.");
    }

    @Transactional
    //check if deposit meets deposit requirements
    public MoneyTransferResponse verifyDeposit(MoneyTransferRequest request, String username) {
        Player player = playerRepository.findByUsername(username);
        MoneyTransferResponse response;

        //check if the player exists, and if the deposit amount is between $10 and $25000
        if (player != null &&
                request.getAmount().compareTo(BigDecimal.valueOf(10)) > 0 &&
                request.getAmount().compareTo(BigDecimal.valueOf(25000)) < 0) {

            player.setAccountBalance(player.getAccountBalance().add(request.getAmount()));
            playerRepository.save(player);

            response = new MoneyTransferResponse(true, "Successful deposit", player.getAccountBalance());
        }
        else {
            response = new MoneyTransferResponse(false, "Unsuccessful deposit. Double check the deposit requirements.");
        }
        return response;
    }

    @Transactional
    //check if withdrawal meets withdrawal requirements
    public MoneyTransferResponse verifyWithdrawal(MoneyTransferRequest request, String username) {
        Player player = playerRepository.findByUsername(username);
        MoneyTransferResponse response;

        //check if the player exists, and if the withdrawal amount is greater than 0, less than 10000, and less than the player's account balance.
        if (player != null &&
                request.getAmount().compareTo(BigDecimal.valueOf(0)) > 0 &&
                request.getAmount().compareTo(BigDecimal.valueOf(10000)) < 0 &&
                request.getAmount().compareTo(player.getAccountBalance()) < 0) {

            player.setAccountBalance(player.getAccountBalance().subtract(request.getAmount()));
            playerRepository.save(player);

            response = new MoneyTransferResponse(true, "Successful withdrawal", player.getAccountBalance());
        }
        else {
            response = new MoneyTransferResponse(false, "Unsuccessful withdrawal. Double check the withdrawal requirements.");
        }
        return response;
    }

    //validates username change and updates the change in the player repository
    public ChangeUsernameResponse changeUsername(String username, String newUsername) {
        Player player = playerRepository.findByUsername(username);

        //Check if username is valid
        if(!isValidUsername(newUsername)) {
            return new ChangeUsernameResponse(false, "Invalid username. Use only english letters, numbers and \"_\"");
        }
        //check if username exists
        else if(!doesUsernameExist(newUsername)) {
            return new ChangeUsernameResponse(false, "Username is already associated with an account.");
        }
        else {
            player.setUsername(newUsername);
            playerRepository.save(player);

            return new ChangeUsernameResponse(true, "Successfully changed usernames.", newUsername);
        }
    }

    //validates password change and updates the change the player repository
    public ChangePasswordResponse changePassword(ChangePasswordRequest request, String username) {
        Player player = playerRepository.findByUsername(username);

        if(!passwordEncoder.matches(request.getCurrentPassword(), player.getPassword())) {
            return new ChangePasswordResponse(false, "Incorrect password input");
        }
        else if(!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return new ChangePasswordResponse(false, "New passwords do not match");
        }
        else if(!isValidPassword(request.getNewPassword(), request.getConfirmNewPassword())) {
            return new ChangePasswordResponse(false, "\"Invalid password. Must be between 8-30 characters, \" +\n" +
                    "\"and only contain letters, numbers, and the following characters: \\\"!@#$%^&*()-_,.?\\\"\"");
        }
        else {
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            player.setPassword(encodedPassword);
            playerRepository.save(player);

            return new ChangePasswordResponse(true, "Password successfully updated");
        }
    }


    //deletes the user from the player repository
    public DeleteAccountResponse deleteAccount(String username) {
        playerRepository.delete(playerRepository.findByUsername(username));


        return new DeleteAccountResponse(true, "Successfully deleted account");
    }

    //verify that the ante, dealer, and trips bet placed during a UTH hand is valid
    public VerifyBetResponse verifyBet(VerifyBetRequest request, String username) {
        Player player = playerRepository.findByUsername(username);
        VerifyBetResponse response;

        //min required account balance is 3 times the amount of the ante bet + the trips bet amount
        //this ensures that the user can place the ante bet, dealer bet, and a 1x play bet (all the same value, hence why 3x)
        BigDecimal minRequiredAccountBalance = (request.getAnteBetAmount().multiply(BigDecimal.valueOf(3)).add(request.getTripsBetAmount()));

        //if the user has sufficient funds withdraw the amount from their account and place the bet
        if(player != null &&
                minRequiredAccountBalance.compareTo(player.getAccountBalance()) <= 0) {
            player.setAccountBalance(player.getAccountBalance().subtract(request.getAnteBetAmount().multiply(BigDecimal.valueOf(2)).add(request.getTripsBetAmount())));
            playerRepository.save(player);

            response = new VerifyBetResponse(true, "Bet accepted. Deal hand.");
            response.setAccountBalance(player.getAccountBalance());
        }
        else {
            response = new VerifyBetResponse(false, "Bet not accepted. You need at least one bet equal to your dealer bet left in your account in order to play.");
        }
        return response;
    }

    //verify that the play bet placed during a UTH hand is valid
    public VerifyBetResponse verifyPlay(BigDecimal playAmount, String username) {
        Player player = playerRepository.findByUsername(username);
        VerifyBetResponse response;

        //make sure the user has an account balance equal to or greater than the play bet amount
        if(player != null &&
                playAmount.compareTo(player.getAccountBalance()) <= 0) {
            player.setAccountBalance(player.getAccountBalance().subtract(playAmount));
            playerRepository.save(player);

            response = new VerifyBetResponse(true, "Bet accepted. Runout hand.");
            response.setAccountBalance(player.getAccountBalance());
        }
        else {
            response = new VerifyBetResponse(false, "Bet not accepted. Insufficient funds.");
        }
        return response;
    }

    //determines how much the user should be paid out after the end of a UTH hand
    public PayoutResponse determinePayout(PayoutRequest request, String username) {
        Player player = playerRepository.findByUsername(username);

        if(player != null) {
            //methods to check ante, dealer, trips, and play payout
            BigDecimal antePayout = antePayout(request);
            BigDecimal dealerPayout = dealerPayout(request);
            BigDecimal tripsPayout = tripsPayout(request.getPlayerHandRanking(), request.getTripsBetAmount());
            BigDecimal playPayout = playPayout(request);

            //sum of the payouts
            BigDecimal payoutSum = antePayout.add(dealerPayout).add(tripsPayout).add(playPayout);

            //update the account balance by adding the payout sum and update the repository
            player.setAccountBalance(player.getAccountBalance().add(payoutSum));
            playerRepository.save(player);

            return new PayoutResponse(true, "Successful payout.", player.getId(), player.getAccountBalance(), antePayout, dealerPayout, tripsPayout, playPayout, payoutSum);
        }
        else {
            return new PayoutResponse(false, "Error, player not logged in");
        }
    }

    //determines how much the user should be paid out after the end of a UTH hand where they folded
    //the only case in which they can get paid out is if they placed a trips bet and folded three-of-a-kind or better
    public PayoutResponse determineFoldPayout(FoldPayoutRequest request, String username) {
        Player player = playerRepository.findByUsername(username);

        //if no trips bet was placed, no payout
        if(request.getTripsBetAmount().compareTo(BigDecimal.valueOf(0)) == 0) return new PayoutResponse(true, "No trips bet.", player.getId(), BigDecimal.valueOf(0));
        else if(player != null) {
            //else, check to see if player had trips or better
            BigDecimal tripsPayout = tripsPayout(request.getPlayerHandRanking(), request.getTripsBetAmount());

            //if they did, update the account balance and save the player in the repository
            if(!tripsPayout.equals(BigDecimal.valueOf(0))) {
                player.setAccountBalance(player.getAccountBalance().add(tripsPayout));
                playerRepository.save(player);

                return new PayoutResponse(true, "Successful payout.", player.getId(), player.getAccountBalance(), tripsPayout);
            }
            else {
                return new PayoutResponse(true, "No payout.", player.getId(), BigDecimal.valueOf(0));
            }
        }
        else return new PayoutResponse(false, "Error, player not logged in");
    }

    //checks to see how much the user should be paid out for the ante bet
    private BigDecimal antePayout(PayoutRequest request) {
        //if the dealer won, payout is 0
        if(request.getWinner() == 'd' && request.getDealerHandRanking() >= 1) return BigDecimal.valueOf(0);
        //if dealer won but dealer had less than a pair, the bet is pushed
        else if(request.getWinner() == 'd' && request.getDealerHandRanking() == 0) return request.getAnteBetAmount();
        //if tied, the bet is pushed
        else if(request.getWinner() == 't') return request.getAnteBetAmount();
        else {
            //if player won but dealer had less than a pair, the bet is pushed
            if(request.getDealerHandRanking() == 0) return request.getAnteBetAmount();
            //else payout 1:1
            else return request.getAnteBetAmount().multiply(BigDecimal.valueOf(2));
        }
    }

    //checks to see how much the user should be paid out for the dealer bet
    private BigDecimal dealerPayout(PayoutRequest request) {
        //if the dealer won, payout is 0
        if(request.getWinner() == 'd') return BigDecimal.valueOf(0);
        //if tied, the bet is pushed
        else if(request.getWinner() == 't') return request.getAnteBetAmount();
        //else, either push or payout depending on the strength of the player's hand
        else {
            if(request.getPlayerHandRanking() <= 3) return request.getAnteBetAmount();
            else if (request.getPlayerHandRanking() == 4) return request.getAnteBetAmount().multiply(BigDecimal.valueOf(2));
            else if (request.getPlayerHandRanking() == 5) return ((request.getAnteBetAmount().divide(BigDecimal.valueOf(2))).multiply(BigDecimal.valueOf(3))).add(request.getAnteBetAmount());
            else if (request.getPlayerHandRanking() == 6) return request.getAnteBetAmount().multiply(BigDecimal.valueOf(4));
            else if (request.getPlayerHandRanking() == 7) return request.getAnteBetAmount().multiply(BigDecimal.valueOf(11));
            else if (request.getPlayerHandRanking() == 8) return request.getAnteBetAmount().multiply(BigDecimal.valueOf(51));
            else if (request.getPlayerHandRanking() == 9) return request.getAnteBetAmount().multiply(BigDecimal.valueOf(501));
        }
        return BigDecimal.valueOf(0);
    }

    //checks to see how much the user should be paid out for the trips bet
    private BigDecimal tripsPayout(int playerHandRanking, BigDecimal tripsBetAmount) {
        //payout ratio depends on the hand ranking of player
        if(playerHandRanking <= 2) return BigDecimal.valueOf(0);
        else if (playerHandRanking == 3) return tripsBetAmount.multiply(BigDecimal.valueOf(4));
        else if (playerHandRanking == 4) return tripsBetAmount.multiply(BigDecimal.valueOf(5));
        else if (playerHandRanking == 5) return tripsBetAmount.multiply(BigDecimal.valueOf(8));
        else if (playerHandRanking == 6) return tripsBetAmount.multiply(BigDecimal.valueOf(9));
        else if (playerHandRanking == 7) return tripsBetAmount.multiply(BigDecimal.valueOf(31));
        else if (playerHandRanking == 8) return tripsBetAmount.multiply(BigDecimal.valueOf(41));
        else if (playerHandRanking == 9) return tripsBetAmount.multiply(BigDecimal.valueOf(51));

        return BigDecimal.valueOf(0);
    }


    //checks to see how much the user should be paid out for the play bet
    private BigDecimal playPayout(PayoutRequest request) {
        //if player wins, payout 1:1
        if(request.getWinner() == 'p') return request.getPlayBetAmount().multiply(BigDecimal.valueOf(2));
        //if tied, bet is pushed
        else if (request.getWinner() == 't') return request.getPlayBetAmount();
        //if dealer wins, payout is 0
        else if (request.getWinner() == 'd') return BigDecimal.valueOf(0);
        else return BigDecimal.valueOf(0);
    }

    //checks if email follows proper regex pattern
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;

        //compare the email against the pattern
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    //checks if an email is associated with an account in the database
    private boolean doesEmailExist(String email) {
        return playerRepository.findByEmail(email) == null;
    }

    //checks if first or last name follows proper regex pattern
    private boolean isValidName(String name) {
        if(name.length() == 0) return false;

        for(int i = 0; i < name.length(); i++) {
            if(!Character.isLetter(name.charAt(i)))
                return false;
        }
        return true;
    }

    //checks if username follows proper regex pattern
    private boolean isValidUsername(String username) {
        if(username == null) return false;

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    //checks if username is already associated with account in the database
    private boolean doesUsernameExist(String username) {
        return playerRepository.findByUsername(username) == null;
    }

    //checks if both user entered passwords are equal to each other
    private boolean isValidPassword(String password, String confirmPassword) {
        if(password == null || confirmPassword == null) return false;

        if(password.equals(confirmPassword)) return true;
        return false;
    }
}
