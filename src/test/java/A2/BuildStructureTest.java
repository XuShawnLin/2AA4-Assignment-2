package test.java.A2;

import org.junit.jupiter.api.Test;

import main.java.A2.Bank;
import main.java.A2.BuildStructure;
import main.java.A2.Player;
import main.java.A2.Node;
import main.java.A2.Edge;
import main.java.A2.BuildingType;

import static org.junit.jupiter.api.Assertions.*;

public class BuildStructureTest {

    @Test
    void buildSettlement_setsOwnerAndGivesVP() {
        Player player = new Player("P1");
        Bank bank = new Bank();
        BuildStructure buildStructure = new BuildStructure(player, bank);
        Node node = new Node(1);
        int beforeVP = player.getVictoryPoints();
        assertTrue(buildStructure.buildSettlement(node));
        assertEquals(player, node.owner);
        assertEquals(BuildingType.SETTLEMENT, node.building);
        assertEquals(beforeVP + 1, player.getVictoryPoints());
    }

    @Test
    void buildCity_requiresOwnSettlement() {
        Player player = new Player("P1");
        Bank bank = new Bank();
        BuildStructure buildStructure = new BuildStructure(player, bank);
        Node node = new Node(2);
        assertFalse(buildStructure.buildCity(node));
        assertTrue(buildStructure.buildSettlement(node));
        int beforeVP = player.getVictoryPoints();
        assertTrue(buildStructure.buildCity(node));
        assertEquals(BuildingType.CITY, node.building);
        assertEquals(beforeVP + 1, player.getVictoryPoints());
    }

    @Test
    void buildRoad_setsOwnerAndType() {
        Player player = new Player("P1");
        Bank bank = new Bank();
        BuildStructure buildStructure = new BuildStructure(player, bank);
        Edge edge = new Edge(10);
        assertTrue(buildStructure.buildRoad(edge));
        assertEquals(player, edge.owner);
        assertEquals(BuildingType.ROAD, edge.building);
    }
}
