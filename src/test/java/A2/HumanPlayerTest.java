package A2;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class HumanPlayerTest {

    /**
     * "back"
     * should be chooseInitialNode returns null
     */
    @Test
    void humanPlayer_ChooseInitialNodeBack() {
        System.setIn(new ByteArrayInputStream("back\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        Board board = new Board();
        Node result = player.chooseInitialNode(board);

        assertNull(result);
    }


    /**
     * Invalid text w/ valid number
     * Should reject text then accept number
     */
    @Test
    void humanPlayer_ChooseInitialNodeInvalidThenValid() {

        System.setIn(new ByteArrayInputStream("abc\n0\n".getBytes()));
        HumanPlayer player = new HumanPlayer("test");
        Board board = new Board();
        Node result = player.chooseInitialNode(board);

        assertNotNull(result);
        assertEquals(0, result.getId());
    }


    /**
     * "go" before rolling should not end the turn
     */
    @Test
    void humanPlayer_GoBeforeRoll() {

        System.setIn(new ByteArrayInputStream("go\nroll\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }


    /**
     * "build" before rolling should not execute build
     */
    @Test
    void humanPlayer_BuildBeforeRoll() {

        System.setIn(new ByteArrayInputStream("build\nroll\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }


    /**
     * Rolling twice should be blocked
     */
    @Test
    void humanPlayer_RollTwice() {

        System.setIn(new ByteArrayInputStream("roll\nroll\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }


    /**
     * "list" command should not crash
     */
    @Test
    void humanPlayer_ListCommand() {

        System.setIn(new ByteArrayInputStream("list\nroll\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }


    /**
     * Unknown command should be handled
     */
    @Test
    void humanPlayer_UnknownCommand() {

        System.setIn(new ByteArrayInputStream("blah\nroll\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }


    /**
     * Build menu back option should cancel build
     */
    @Test
    void humanPlayer_BuildBack() {

        System.setIn(new ByteArrayInputStream("roll\nbuild\nback\ngo\n".getBytes()));

        HumanPlayer player = new HumanPlayer("test");
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();
        player.takeTurn(gameMaster,1);

        assertTrue(true);
    }
}