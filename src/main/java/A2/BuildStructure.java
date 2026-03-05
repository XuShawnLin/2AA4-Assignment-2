package A2;

/**
 * Performs build actions (SRP) using a validator.
 * Kept intentionally close to the original BuildService logic.
 */
public class BuildStructure {
    private final BuildValidator validator;

    public BuildStructure() {
        this.validator = new BuildValidator();
    }

    public BuildStructure(BuildValidator validator) {
        this.validator = (validator == null) ? new BuildValidator() : validator;
    }

    public boolean buildRoad(Player player, Edge edge, Board board, Bank bank) {
        if (!validator.canBuildRoad(player, edge, board)) {
            return false;
        }
        edge.setOwner(player);
        edge.setBuilding(BuildingType.ROAD);
        return true;
    }

    public boolean buildSettlement(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildSettlement(player, node, board)){
            return false;
        }
        node.setOwner(player);
        node.setBuilding(BuildingType.SETTLEMENT);
        player.addVictoryPoints(1);
        return true;
    }

    public boolean buildCity(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildCity(player, node, board)) {
            return false;
        }
        node.setBuilding(BuildingType.CITY);
        player.addVictoryPoints(1);
        return true;
    }
}
