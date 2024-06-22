package com.holdemhavenus.holdemhaven.services;

import com.holdemhavenus.holdemhaven.entities.DBHand;
import com.holdemhavenus.holdemhaven.repositories.DBHandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UTHTableTest {
    @InjectMocks
    private UTHTableService uthTableService;

    @Mock
    private DBHandRepository dbHandRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetHandHistory() {
        Long playerId = 1L;
        ArrayList<DBHand> handList = new ArrayList<>();
        DBHand hand = new DBHand();
        hand.setHandId(1L);
        hand.setPlayerId(1L);
        handList.add(hand);

        when(dbHandRepository.find100LatestHands(playerId)).thenReturn(handList);

        ArrayList<DBHand> result = uthTableService.getHandHistory(playerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(playerId, result.get(0).getPlayerId());
        verify(dbHandRepository, times(1)).find100LatestHands(playerId);
    }
}
