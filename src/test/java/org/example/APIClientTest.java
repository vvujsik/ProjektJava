package org.example;

import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class APIClientTest {
    @Test
    public void testGetUserData() {
        APIClient api = new APIClient("RGAPI-6b5aa502-9779-460a-ba58-f3d32a2b7bc2");
        RiotAccount account = null;
        String gameName = "Grzypport";
        String tagLine = "EUNE";
        try {
            account = api.getUserData(gameName, tagLine);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(account);
        assertEquals("Grzypport", account.getGameName());
    }

    @Test
    public void testGetMatchData() {
        APIClient api = new APIClient("RGAPI-6b5aa502-9779-460a-ba58-f3d32a2b7bc2");
        String matchId = "EUN1_3612120722";
        LoLMatch match = null;
        try {
            match = api.getMatchData(matchId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(match);
        assertEquals(matchId, match.getMetadata().getMatchId());
    }
}