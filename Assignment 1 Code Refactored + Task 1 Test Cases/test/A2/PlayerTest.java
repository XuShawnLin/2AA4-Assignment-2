package A2;

import org.junit.jupiter.api.Test;

import A2.Player;
import A2.ResourceType;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void defaultPlayer_startsWith2VictoryPoints() {
        Player player = new Player();
        assertEquals(2, player.getVictoryPoints());
    }

    @Test
    void addVictoryPoints_increasesPoints() {
        Player player = new Player();
        player.addVictoryPoints(3);
        assertEquals(5, player.getVictoryPoints());
    }

    @Test
    void addAndRemoveResource_updatesCorrectly() {
        Player player = new Player();
        player.addResource(ResourceType.LUMBER, 4);
        assertTrue(player.removeResource(ResourceType.LUMBER, 3));
        assertEquals(1, player.getCurrentResources().get(ResourceType.LUMBER));
    }

    @Test
    void removeResource_failsIfNotEnough() {
        Player player = new Player();
        player.addResource(ResourceType.ORE, 1);
        assertFalse(player.removeResource(ResourceType.ORE, 2));
        assertEquals(1, player.getCurrentResources().get(ResourceType.ORE));
    }
}
