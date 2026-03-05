package A2;

import org.junit.jupiter.api.Test;

import A2.*;

import static org.junit.jupiter.api.Assertions.*;

public class BuildStructureTest {

    @Test
    void buildSettlement_setsOwnerAndGivesVP() {
        Player player = new Player("P1");
        Bank bank = new Bank();
        Board board = new Board(); 
        BuildStructure buildStructure = new BuildStructure();

        Node node = new Node(1);
        int beforeVP = player.getVictoryPoints();

        assertTrue(buildStructure.buildSettlement(player, node, board, bank));
        assertEquals(player, node.getOwner());
        assertEquals(BuildingType.SETTLEMENT, node.getBuilding());
        assertEquals(beforeVP + 1, player.getVictoryPoints());
    }

    @Test
    void buildCity_requiresOwnSettlement() {
        Player player = new Player("P1");
        Bank bank = new Bank();
        Board board = new Board();
        BuildStructure buildStructure = new BuildStructure();

        Node node = new Node(2);

        assertFalse(buildStructure.buildCity(player, node, board, bank));

        assertTrue(buildStructure.buildSettlement(player, node, board, bank));

        int beforeVP = player.getVictoryPoints();
        assertTrue(buildStructure.buildCity(player, node, board, bank));

        assertEquals(BuildingType.CITY, node.getBuilding());
        assertEquals(beforeVP + 1, player.getVictoryPoints());
    }

}
