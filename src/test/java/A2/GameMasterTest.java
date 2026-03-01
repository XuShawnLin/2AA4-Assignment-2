package A2;

import org.junit.jupiter.api.Test;

import A2.GameMaster;
import A2.Player;
import A2.ResourceType;

import static org.junit.jupiter.api.Assertions.*;

public class GameMasterTest {

    static class SpyPlayer extends Player {
        boolean buildCalled = false;

        public SpyPlayer(String name) {
            super(name);
        }

        @Override
        public void build() {
            buildCalled = true;
        }
    }

    @Test
    void nextTurn_cyclesPlayers() {
        GameMaster gameMaster = new GameMaster();
        gameMaster.players = new Player[]{
                new Player("P1"),
                new Player("P2"),
                new Player("P3")
        };
        assertEquals("P1", gameMaster.getCurrentPlayer().getName());
        gameMaster.nextTurn();
        assertEquals("P2", gameMaster.getCurrentPlayer().getName());
        gameMaster.nextTurn();
        assertEquals("P3", gameMaster.getCurrentPlayer().getName());
        gameMaster.nextTurn();
        assertEquals("P1", gameMaster.getCurrentPlayer().getName());
    }

    @Test
    void checkWin_detects10VP() {
        GameMaster gameMaster = new GameMaster();
        Player player = new Player("P1");
        player.addVictoryPoints(8);
        gameMaster.players = new Player[]{ player };
        assertTrue(gameMaster.checkWin());
    }

    @Test
    void sevenResources_doesNotTriggerBuild() {
        GameMaster gameMaster = new GameMaster();
        SpyPlayer player = new SpyPlayer("P1");
        player.addResource(ResourceType.BRICK, 7);
        gameMaster.players = new Player[]{ player };
        gameMaster.handlePlayersWithMoreThanSevenCards();
        assertFalse(player.buildCalled);
    }

    @Test
    void eightResources_triggersBuild() {
        GameMaster gameMaster = new GameMaster();
        SpyPlayer player = new SpyPlayer("P1");
        player.addResource(ResourceType.BRICK, 8);
        gameMaster.players = new Player[]{ player };
        gameMaster.handlePlayersWithMoreThanSevenCards();
        assertTrue(player.buildCalled);
    }
}
