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
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @PostMapping("/register")
    public RegisterPlayerResponse registerPlayer(@RequestBody RegisterPlayerRequest request) {
        return playerService.verifyRegisterPlayerInformation(request);
    }

    @PostMapping("/signIn")
    public LoginPlayerResponse signInPlayer(@RequestBody LoginPlayerRequest request, HttpSession session) {
        LoginPlayerResponse response = playerService.verifySignInCredentials(request);
        if(response.isSuccess()) {
            session.setAttribute("username", response.getPlayerUsername());
            session.setAttribute("accountBalance", response.getAccountBalance());
            System.out.println("success session attributes");
        }
        return response;
    }

    @PostMapping("/deposit")
    public MoneyTransferResponse deposit(@RequestBody MoneyTransferRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        //Handle case where user is not logged in
        if (playerUsername == null)
            return new MoneyTransferResponse(false, "User not logged in or session expired");

        MoneyTransferResponse response = playerService.verifyDeposit(request, playerUsername);
        if(response.isSuccess()) {
            session.setAttribute("accountBalance", response.getAmount());
        }
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

    @PostMapping("/changeUsername")
    public ChangeUsernameResponse changeUsername(@RequestBody String newUsername, HttpSession session) {
        String playerUsername = getUsername(session);
        newUsername = newUsername.replaceAll("\"", "");

        if(playerUsername == null) {
            return new ChangeUsernameResponse(false, "User not logged in or session expired.");
        }

        ChangeUsernameResponse response = playerService.changeUsername(playerUsername, newUsername);
        if(response.isSuccess())
            session.setAttribute("username", response.getUsername());

        return response;
    }

    @PostMapping("/changePassword")
    public ChangePasswordResponse changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new ChangePasswordResponse(false, "User not logged in or session expired.");
        }

        return playerService.changePassword(request, playerUsername);
    }

    @PostMapping("/deleteAccount")
    public DeleteAccountResponse deleteAccount(HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new DeleteAccountResponse(false, "User not logged in or session expired.");
        }

        return playerService.deleteAccount(playerUsername);
    }


    @PostMapping("/verifyBet")
    public VerifyBetResponse verifyBet(@RequestBody VerifyBetRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new VerifyBetResponse(false, "User not logged in or session expired.");
        }


        VerifyBetResponse response = playerService.verifyBet(request, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/verifyPlay")
    public VerifyBetResponse verifyPlay(@RequestBody BigDecimal betAmount, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new VerifyBetResponse(false, "User not logged in or session expired.");
        }

        VerifyBetResponse response = playerService.verifyPlay(betAmount, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/payout")
    public PayoutResponse payout(@RequestBody PayoutRequest payoutRequest, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new PayoutResponse(false, "User not logged in or session expired.");
        }

        PayoutResponse response = playerService.determinePayout(payoutRequest, playerUsername);
        if(response.isSuccess())
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    @PostMapping("/foldPayout")
    public PayoutResponse foldPayout(@RequestBody FoldPayoutRequest request, HttpSession session) {
        String playerUsername = getUsername(session);

        if(playerUsername == null) {
            return new PayoutResponse(false, "User not logged in or session expired.");
        }

        PayoutResponse response = playerService.determineFoldPayout(request, playerUsername);

        if(response.isSuccess() && response.getAccountBalance() != null)
            session.setAttribute("accountBalance", response.getAccountBalance());

        return response;
    }

    private String getUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }
}
