package A2;

/**
 * Class representing a player's action to build structures in the game.
 */
public class BuildStructure {
	private Player player;

	private Bank bank;
	/**
	 * Constructor for BuildStructure class.
	 * @param p The player performing the build action.
	 * @param bank The bank instance.
	 */
	public BuildStructure(Player p, Bank bank) {
		this.player = p;
		this.bank = bank;
	}

	/**
	 * Attempts to build a settlement.
	 * @param node The node where to build.
	 * @return True if settlement is successfully built, false otherwise.
	 */
	public boolean buildSettlement(Node node) {
		if (node == null || node.isOccupied()) return false;
		node.owner = player;
		node.building = BuildingType.SETTLEMENT;
		player.addVictoryPoints(1);
		return true;
	}

	/**
	 * Attempts to build a city.
	 * @param node The node where to build.
	 * @return True if city is successfully built, false otherwise.
	 */
	public boolean buildCity(Node node) {
		if (node == null || node.owner != player || node.building != BuildingType.SETTLEMENT) return false;
		node.building = BuildingType.CITY;
		player.addVictoryPoints(1); // Settlement (1) -> City (2) is +1 VP
		return true;
	}

	/**
	 * Attempts to build a road.
	 * @param edge The edge where to build.
	 * @return True if road is successfully built, false otherwise.
	 */
	public boolean buildRoad(Edge edge) {
		if (edge == null || edge.isOccupied()) return false;
		edge.owner = player;
		edge.building = BuildingType.ROAD;
		return true;
	}

	/**
	 * Awards victory points to the player based on built structures.
	 */
	public void giveVPs() {
	}
}
