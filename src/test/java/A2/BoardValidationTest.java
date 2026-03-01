package test.java.A2;

import org.junit.jupiter.api.Test;

import main.java.A2.Board;
import main.java.A2.Player;
import main.java.A2.Node;
import main.java.A2.Edge;

import static org.junit.jupiter.api.Assertions.*;


public class BoardValidationTest {

    @Test
    void settlement_invalidIfNodeOccupied() {
        Board board = new Board();
        Player player = new Player("P1");
        Node node = new Node(1);
        node.owner = player;
        assertFalse(board.isValidSettlement(node, player));
    }

    @Test
    void settlement_invalidIfNeighborOccupied() {
        Board board = new Board();
        Player player1 = new Player("P1");
        Player player2 = new Player("P2");
        Node node1 = new Node(1);
        Node neighbor = new Node(2);
        node1.neighbors.add(neighbor);
        neighbor.owner = player2;
        assertFalse(board.isValidSettlement(node1, player1));
    }

    @Test
    void settlement_validIfUnoccupiedAndNeighborsFree() {
        Board board = new Board();
        Player player = new Player("P1");
        Node node1 = new Node(3);
        Node neighbor = new Node(4);
        node1.neighbors.add(neighbor);
        assertTrue(board.isValidSettlement(node1, player));
    }

    @Test
    void road_invalidIfEdgeOccupied() {
        Board board = new Board();
        Player player = new Player("P1");
        Edge edge = new Edge(7);
        edge.owner = player;
        assertFalse(board.isValidRoad(edge, player));
    }

    @Test
    void road_validIfConnectedToPlayersNode() {
        Board board = new Board();
        Player player = new Player("P1");
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        node1.owner = player;
        Edge edge = new Edge(99);
        edge.connectedNodes.add(node1);
        edge.connectedNodes.add(node2);
        assertTrue(board.isValidRoad(edge, player));
    }
}
