package com.holdemhavenus.holdemhaven.controllers;

import com.holdemhavenus.holdemhaven.requestDTOs.*;
import com.holdemhavenus.holdemhaven.responseDTOs.VerifyBetResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.LoginPlayerResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.MoneyTransferResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.RegisterPlayerResponse;
import com.holdemhavenus.holdemhaven.services.PlayerService;
import com.holdemhavenus.holdemhaven.services.TableService;
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

    private String getUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }
}
