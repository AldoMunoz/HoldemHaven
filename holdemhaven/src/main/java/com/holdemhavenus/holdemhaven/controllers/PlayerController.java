package com.holdemhavenus.holdemhaven.controllers;

import com.holdemhavenus.holdemhaven.requestDTOs.*;
import com.holdemhavenus.holdemhaven.responseDTOs.*;
import com.holdemhavenus.holdemhaven.services.PlayerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/player")
//Rest Controller used to call and return Player Service methods from the front-end to the back-end
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/register")
    public RegisterPlayerResponse registerPlayer(@RequestBody RegisterPlayerRequest request) {
        return playerService.verifyRegisterPlayerInformation(request);
    }

    @PostMapping("/sign-in")
    public LoginPlayerResponse signInPlayer(@RequestBody LoginPlayerRequest request, HttpSession session) {
        LoginPlayerResponse response = playerService.verifySignInCredentials(request);

        if(response.isSuccess()) {
            //is user successfully logged in, save player's username, accountBalance, and id in the session information
            session.setAttribute("username", response.getPlayerUsername());
            session.setAttribute("accountBalance", response.getAccountBalance());
            session.setAttribute("playerId", response.getPlayerId());
        }

        return response;
    }

    @PostMapping("/deposit")
    public MoneyTransferResponse deposit(@RequestBody MoneyTransferRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if (playerUsername == null)
            return new MoneyTransferResponse(false, "User not logged in or session expired");

        MoneyTransferResponse response = playerService.verifyDeposit(request, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAmount());

        return response;
    }

    @PostMapping("/withdraw")
    public MoneyTransferResponse withdraw(@RequestBody MoneyTransferRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new MoneyTransferResponse(false, "User not logged in or session expired.");

        MoneyTransferResponse response = playerService.verifyWithdrawal(request, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAmount());

        return response;
    }

    @PostMapping("/change-username")
    public ChangeUsernameResponse changeUsername(@RequestBody String newUsername, HttpSession session) {
        String playerUsername = getUsername(session);
        newUsername = newUsername.replaceAll("\"", "");

        if(playerUsername == null)
            return new ChangeUsernameResponse(false, "User not logged in or session expired.");

        ChangeUsernameResponse response = playerService.changeUsername(playerUsername, newUsername);
        if(response.isSuccess())
            session.setAttribute("username", response.getUsername());

        return response;
    }

    @PostMapping("/change-password")
    public ChangePasswordResponse changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new ChangePasswordResponse(false, "User not logged in or session expired.");

        return playerService.changePassword(request, playerUsername);
    }

    @PostMapping("/delete-account")
    public DeleteAccountResponse deleteAccount(HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new DeleteAccountResponse(false, "User not logged in or session expired.");

        return playerService.deleteAccount(playerUsername);
    }


    @PostMapping("/verify-bet")
    public VerifyBetResponse verifyBet(@RequestBody VerifyBetRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new VerifyBetResponse(false, "User not logged in or session expired.");

        VerifyBetResponse response = playerService.verifyBet(request, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/verify-play")
    public VerifyBetResponse verifyPlay(@RequestBody BigDecimal betAmount, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new VerifyBetResponse(false, "User not logged in or session expired.");

        VerifyBetResponse response = playerService.verifyPlay(betAmount, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/payout")
    public PayoutResponse payout(@RequestBody PayoutRequest payoutRequest, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new PayoutResponse(false, "User not logged in or session expired.");

        PayoutResponse response = playerService.determinePayout(payoutRequest, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/fold-payout")
    public PayoutResponse foldPayout(@RequestBody FoldPayoutRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null)
            return new PayoutResponse(false, "User not logged in or session expired.");

        PayoutResponse response = playerService.determineFoldPayout(request, playerUsername);

        if(response.isSuccess() && response.getAccountBalance() != null)
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    //method used to retrieve player username from session attributes
    private String getUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }
}
