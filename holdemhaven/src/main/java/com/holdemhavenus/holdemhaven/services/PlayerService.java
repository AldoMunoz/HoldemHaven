package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.requestDTOs.LoginPlayerRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.MoneyTransferRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.RegisterPlayerRequest;
import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.repositories.PlayerRepository;
import com.holdemhavenus.holdemhaven.responseDTOs.LoginPlayerResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.MoneyTransferResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.RegisterPlayerResponse;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.logging.Logger;
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
