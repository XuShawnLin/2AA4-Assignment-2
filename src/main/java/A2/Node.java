package A2;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a node in the game board.
 */
public class Node {
	/**
	 * Unique identifier for the node.
	 */
	public int id;
	/**
	 * Player who owns a building on this node.
	 */
	public Player owner;
	/**
	 * Type of building on this node (Settlement or City).
	 */
	public BuildingType building;
	/**
	 * Neighbors of this node.
	 */
	public List<Node> neighbors;
	/**
	 * Edges connected to this node.
	 */
	public List<Edge> connectedEdges;

	/**
	 * Constructor for Node.
	 */
	public Node() {
		this.owner = null;
		this.building = null;
		this.neighbors = new ArrayList<>();
		this.connectedEdges = new ArrayList<>();
	}

	/**
	 * Constructor with ID.
	 * @param id The node's ID.
	 */
	public Node(int id) {
		this();
		this.id = id;
	}

	/**
	 * Checks if the node is already occupied by a building.
	 * @return True if occupied, false otherwise.
	 */
	public boolean isOccupied() {
		return owner != null;
	}

	public Player getOwner() {
		return owner;
	}
}
