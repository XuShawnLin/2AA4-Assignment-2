package A2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an edge between nodes where roads can be built.
 */
public class Edge {
	/**
	 * Unique identifier for the edge.
	 */
	public int id;
	/**
	 * Player who owns a road on this edge.
	 */
	public Player owner;
	/**
	 * Type of building on this edge (Road).
	 */
	public BuildingType building;
	/**
	 * Nodes connected by this edge.
	 */
	public List<Node> connectedNodes;

	/**
	 * Constructor for Edge.
	 */
	public Edge() {
		this.owner = null;
		this.building = null;
		this.connectedNodes = new ArrayList<>();
	}

	/**
	 * Constructor with ID.
	 * @param id The edge's ID.
	 */
	public Edge(int id) {
		this();
		this.id = id;
	}

	/**
	 * Checks if the edge is already occupied by a road.
	 * @return True if occupied, false otherwise.
	 */
	public boolean isOccupied() {
		return owner != null;
	}
}
