package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.Player;
import com.holdemhavenus.holdemhaven.repositories.PlayerRepository;
import com.holdemhavenus.holdemhaven.requestDTOs.LoginPlayerRequest;
import com.holdemhavenus.holdemhaven.requestDTOs.MoneyTransferRequest;
import com.holdemhavenus.holdemhaven.responseDTOs.LoginPlayerResponse;
import com.holdemhavenus.holdemhaven.responseDTOs.MoneyTransferResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlayerServiceTest {
    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //creates a valid deposit request and tests method functionality
    public void testVerifyDeposit_Successful() {
        String username = "testUser";
        Player player = new Player();
        player.setUsername(username);
        player.setAccountBalance(BigDecimal.valueOf(100));

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setAmount(BigDecimal.valueOf(1000));

        when(playerRepository.findByUsername(username)).thenReturn(player);

        MoneyTransferResponse response = playerService.verifyDeposit(request, username);

        assertTrue(response.isSuccess());
        assertEquals("Successful deposit", response.getMessage());
        assertEquals(BigDecimal.valueOf(1100), response.getAmount());

        verify(playerRepository, times(1)).save(player);
    }

    @Test
    //creates an invalid deposit request and tests method functionality
    public void testVerifyDeposit_Unsuccessful() {
        String username = "testUser";
        Player player = new Player();
        player.setUsername(username);
        player.setAccountBalance(BigDecimal.valueOf(100));

        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setAmount(BigDecimal.valueOf(5));

        when(playerRepository.findByUsername(username)).thenReturn(player);

        MoneyTransferResponse response = playerService.verifyDeposit(request, username);

        assertFalse(response.isSuccess());
        assertEquals("Unsuccessful deposit. Double check the deposit requirements.", response.getMessage());

        verify(playerRepository, times(0)).save(player);
    }

    @Test
    //creates a valid sign in request and tests method functionality
    public void testVerifySignInCredentials_Successful() {
        String username = "testUser";
        String password = "testPassword";

        Player player = new Player();
        player.setUsername(username);
        player.setPassword("$2a$10$7s6ds7...");
        player.setAccountBalance(BigDecimal.valueOf(1000));

        LoginPlayerRequest request = new LoginPlayerRequest();
        request.setUsername(username);
        request.setPassword(password);

        when(playerRepository.findByUsername(username)).thenReturn(player);
        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(true);

        LoginPlayerResponse response = playerService.verifySignInCredentials(request);

        assertTrue(response.isSuccess());
        assertEquals("Success. You are now logged in.", response.getMessage());
        assertEquals(username, response.getPlayerUsername());
        assertEquals(BigDecimal.valueOf(1000), response.getAccountBalance());

        verify(passwordEncoder, times(1)).matches(password, player.getPassword());
    }

    @Test
    //creates am invalid sign in request and tests method functionality
    public void testVerifySignInCredentials_Unsuccessful() {
        String username = "testUser";
        String password = "wrongPassword";

        Player player = new Player();
        player.setUsername(username);
        player.setPassword("$2a$10$7s6ds7...");
        LoginPlayerRequest request = new LoginPlayerRequest();
        request.setUsername(username);
        request.setPassword(password);

        when(playerRepository.findByUsername(username)).thenReturn(player);
        when(passwordEncoder.matches(password, player.getPassword())).thenReturn(false);

        LoginPlayerResponse response = playerService.verifySignInCredentials(request);

        assertFalse(response.isSuccess());
        assertEquals("Unsuccessful login. Try again.", response.getMessage());

        verify(passwordEncoder, times(1)).matches(password, player.getPassword());
    }
}
