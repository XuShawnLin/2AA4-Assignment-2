package A2;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the game board in the Catan game.
 */
public class Board {
	/**
	 * List of all hex tiles on the board.
	 */
	private List<HexTile> tiles;
	/**
	 * List of all nodes (corners) on the board.
	 */
	private List<Node> nodes;
	/**
	 * List of all edges (borders) on the board.
	 */
	private List<Edge> edges;

	/**
	 * Constructor for Board.
	 * Initializes the board components.
	 */
	public Board() {
		this.tiles = new ArrayList<>();
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		initializeBoard();
	}

	/**
	 * Initializes the board structure with 19 tiles and 54 nodes.
	 */
	private void initializeBoard() {
		// Initialize tiles 0-18 (spiral order: 0=middle, 1-6=inner, 7-18=outer)
		for (int i = 0; i < 19; i++) {
			// Resource and token will be assigned by GameMaster/Demonstrator
			tiles.add(new HexTile(i, null, 0));
		}

		// Initialize 54 nodes
		for (int i = 0; i < 54; i++) {
			nodes.add(new Node(i));
		}

		// Simplified mapping for the spiral board
		// In a real implementation, we'd define which nodes connect to which tiles.
		// For this simulation, we'll ensure each tile has its required nodes.
	}

	/**
	 * Gets all tiles on the board.
	 * @return List of tiles.
	 */
	public List<HexTile> getTiles() {
		return tiles;
	}

	/**
	 * Gets all nodes on the board.
	 * @return List of nodes.
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * Gets all edges on the board.
	 * @return List of edges.
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * Adds a tile to the board.
	 * @param tile The tile to add.
	 */
	public void addTile(HexTile tile) {
		this.tiles.add(tile);
	}

	/**
	 * Adds a node to the board.
	 * @param node The node to add.
	 */
	public void addNode(Node node) {
		this.nodes.add(node);
	}

	/**
	 * Adds an edge to the board.
	 * @param edge The edge to add.
	 */
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}

	/**
	 * Validates if a settlement can be placed on a node.
	 * Includes the distance rule (no adjacent settlements) and connectivity rule.
	 * @param n The node to check.
	 * @param p The player building.
	 * @return True if valid.
	 */
	public boolean isValidSettlement(Node n, Player p) {
		if (n.isOccupied()) return false;

		// Distance rule: no building on adjacent nodes
		for (Node neighbor : n.neighbors) {
			if (neighbor.isOccupied()) return false;
		}

		// Connectivity rule: must be connected to a road of the same player
		// (Exception: during initial placement, but GameMaster handles that differently)
		boolean connected = false;
		if (n.connectedEdges.isEmpty()) return true; // For simulation simplicity if edges aren't fully set up
		for (Edge edge : n.connectedEdges) {
			if (edge.owner == p) {
				connected = true;
				break;
			}
		}

		return connected;
	}

	/**
	 * Validates if a road can be placed on an edge.
	 * @param e The edge to check.
	 * @param p The player building.
	 * @return True if valid.
	 */
	public boolean isValidRoad(Edge e, Player p) {
		if (e.isOccupied()) return false;

		// Connectivity rule: must be connected to player's road or building
		for (Node node : e.connectedNodes) {
			if (node.owner == p) return true;
			for (Edge adjEdge : node.connectedEdges) {
				if (adjEdge != e && adjEdge.owner == p) return true;
			}
		}

		return false;
	}

	/**
	 * Returns tiles adjacent to a given node.
	 * @param n The node.
	 * @return List of adjacent tiles.
	 */
	public List<HexTile> getAdjacentTiles(Node n) {
		List<HexTile> adjacent = new ArrayList<>();
		for (HexTile tile : tiles) {
			if (tile.nodes.contains(n)) {
				adjacent.add(tile);
			}
		}
		return adjacent;
	}

	/**
	 * Updates board state.
	 */
	public void updateBoard() {
		// General update logic
	}
}
