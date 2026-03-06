package A2;

/**
 * Class representing a player's action to build structures in the game.
 * Performs build actions using a validator to check if builds are allowed.
 */
public class BuildStructure {

    /**
     * Validator responsible for checking whether a structure
     * can legally be built according to game rules.
     */
    private final BuildValidator validator;

    /**
     * Constructor for BuildStructure class.
     * Creates a default validator used to verify build actions.
     */
    public BuildStructure() {
        this.validator = new BuildValidator();
    }

    /**
     * Constructor for BuildStructure class with a provided validator.
     *
     * @param validator The validator used to verify build conditions.
     */
    public BuildStructure(BuildValidator validator) {
        this.validator = (validator == null) ? new BuildValidator() : validator;
    }

    /**
     * Attempts to build a road.
     *
     * @param player The player performing the build action.
     * @param edge   The edge where to build.
     * @param board  The game board.
     * @param bank   The bank instance.
     * @return True if road is successfully built, false otherwise.
     */
    public boolean buildRoad(Player player, Edge edge, Board board, Bank bank) {
        if (!validator.canBuildRoad(player, edge, board)) {
            return false;
        }
        edge.setOwner(player);
        edge.setBuilding(BuildingType.ROAD);
        return true;
    }

    /**
     * Attempts to build a settlement.
     *
     * @param player The player performing the build action.
     * @param node   The node where to build.
     * @param board  The game board.
     * @param bank   The bank instance.
     * @return True if settlement is successfully built, false otherwise.
     */
    public boolean buildSettlement(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildSettlement(player, node, board)) {
            return false;
        }
        node.setOwner(player);
        node.setBuilding(BuildingType.SETTLEMENT);
        player.addVictoryPoints(1);
        return true;
    }

    /**
     * Attempts to build a city.
     *
     * @param player The player performing the build action.
     * @param node   The node where to build.
     * @param board  The game board.
     * @param bank   The bank instance.
     * @return True if city is successfully built, false otherwise.
     */
    public boolean buildCity(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildCity(player, node, board)) {
            return false;
        }
        node.setBuilding(BuildingType.CITY);
        player.addVictoryPoints(1); // Settlement (1) -> City (2) is +1 VP
        return true;
    }
}