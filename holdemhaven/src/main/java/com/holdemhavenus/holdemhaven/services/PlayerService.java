package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.requestDTOs.*;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.repositories.PlayerRepository;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
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
        //Check if first name and last name are only letters
        if(!isValidName(request.getFirstName())) {
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(false);
            response.setMessage("Invalid first name. Please use english letters only.");

            return response;
        }
        else if(!isValidName(request.getLastName())) {
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(false);
            response.setMessage("Invalid last name. Please use english letters only.");

            return response;
        }
        //Check if email is valid or exists in the database
        else if(!isValidEmail(request.getEmail())) {
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(false);
            response.setMessage("Invalid email or an account already exists with the email address: " + request.getEmail());

            return response;
        }
        //Check if username exists, and only contains certain characters
        else if(!isValidUsername(request.getUsername())) {
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(false);
            response.setMessage("Invalid username. Use only english letters, numbers and \"_\"");

            return response;
        }
        //Check if password contains certain characters, and if passwords match
        else if(!isValidPassword(request.getPassword(), request.getConfirmPassword())) {
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(false);
            response.setMessage("Invalid password. Must be between 8-30 characters, and only contain letters, " +
                    "numbers, and the following characters: \"!@#$%^&*()-_,.?\"");

            return response;
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
            RegisterPlayerResponse response = new RegisterPlayerResponse();
            response.setSuccess(true);
            response.setMessage("Success, you can now sign in.");
            return response;
        }
    }

    public LoginPlayerResponse verifySignInCredentials(LoginPlayerRequest request) {
        Player player = playerRepository.findByUsername(request.getUsername());
        LoginPlayerResponse response = new LoginPlayerResponse();

        if(player != null && passwordEncoder.matches(request.getPassword(), player.getPassword())) {
            response.setSuccess(true);
            response.setMessage("Success. You are now logged in.");
            response.setPlayerUsername(player.getUsername());
            response.setAccountBalance(player.getAccountBalance());

            return response;
        }
        response.setSuccess(false);
        response.setMessage("Unsuccessful login. Try again.");

        return response;
    }

    @Transactional
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
            response = new MoneyTransferResponse(false, "Unsuccessful withdrawal. Double check the deposit requirements.");
        }
        return response;
    }

    public VerifyBetResponse verifyBet(VerifyBetRequest request, String username) {
        Player player = playerRepository.findByUsername(username);
        VerifyBetResponse response;

        BigDecimal minRequiredAccountBalance = (request.getAnteBetAmount().multiply(BigDecimal.valueOf(3)).add(request.getTripsBetAmount()));

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

    public VerifyBetResponse verifyPlay(BigDecimal playAmount, String username) {
        Player player = playerRepository.findByUsername(username);
        VerifyBetResponse response;

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

    public PayoutResponse determinePayout(PayoutRequest request, String username) {
        Player player = playerRepository.findByUsername(username);

        if(player != null) {
            BigDecimal antePayout = antePayout(request);
            BigDecimal dealerPayout = dealerPayout(request);
            BigDecimal tripsPayout = tripsPayout(request);
            BigDecimal playPayout = playPayout(request);

            BigDecimal payoutSum = antePayout.add(dealerPayout).add(tripsPayout).add(playPayout);

            player.setAccountBalance(player.getAccountBalance().add(payoutSum));
            playerRepository.save(player);

            return new PayoutResponse(true, "Successful payout.", player.getAccountBalance(), antePayout, dealerPayout, tripsPayout, playPayout, payoutSum);
        }
        else {
            return new PayoutResponse(false, "Error, player not logged in");
        }
    }

    private BigDecimal antePayout(PayoutRequest request) {
        if(request.getWinner() == 'd') return BigDecimal.valueOf(0);
        else if(request.getWinner() == 't') return request.getAnteBetAmount();
        else {
            if(request.getDealerHandRanking() == 0) return request.getAnteBetAmount();
            else return request.getAnteBetAmount().multiply(BigDecimal.valueOf(2));
        }
    }
    private BigDecimal dealerPayout(PayoutRequest request) {
        if(request.getWinner() == 'd') return BigDecimal.valueOf(0);
        else if(request.getWinner() == 't') return request.getAnteBetAmount();
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
    private BigDecimal tripsPayout(PayoutRequest request) {
        if(request.getPlayerHandRanking() <= 2) return BigDecimal.valueOf(0);
        else if (request.getPlayerHandRanking() == 3) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(4));
        else if (request.getPlayerHandRanking() == 4) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(5));
        else if (request.getPlayerHandRanking() == 5) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(8));
        else if (request.getPlayerHandRanking() == 6) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(9));
        else if (request.getPlayerHandRanking() == 7) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(31));
        else if (request.getPlayerHandRanking() == 8) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(41));
        else if (request.getPlayerHandRanking() == 9) return request.getTripsBetAmount().multiply(BigDecimal.valueOf(51));

        return BigDecimal.valueOf(0);
    }

    private BigDecimal playPayout(PayoutRequest request) {
        if(request.getWinner() == 'p') return request.getPlayBetAmount().multiply(BigDecimal.valueOf(2));
        else if (request.getWinner() == 't') return request.getPlayBetAmount();
        else if (request.getWinner() == 'd') return BigDecimal.valueOf(0);
        else return BigDecimal.valueOf(0);
    }

    //checks if email is valid, or if the email was already used to register an account
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;

        //compare the email against the pattern
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if(matcher.matches()) {
            return playerRepository.findByEmail(email) == null;
        }

        return false;
    }

    //checks if first of last name follows proper regex pattern
    private boolean isValidName(String name) {
        if(name.length() == 0) return false;

        for(int i = 0; i < name.length(); i++) {
            if(!Character.isLetter(name.charAt(i)))
                return false;
        }
        return true;
    }

    //checks if username follows proper regex pattern, and if username already exists in the database
    private boolean isValidUsername(String username) {
        if(username == null) return false;

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        if(matcher.matches()) {
            return playerRepository.findByUsername(username) == null;
        }
        return false;
    }

    //checks if both user entered passwords are equal to each other
    private boolean isValidPassword(String password, String confirmPassword) {
        if(password == null || confirmPassword == null) return false;

        if(password.equals(confirmPassword)) return true;
        return false;
    }
}
