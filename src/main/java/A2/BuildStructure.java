package A2;

import java.util.HashMap;
import java.util.Map;

public class BuildStructure {

    private final BuildValidator validator;

    public static final Map<ResourceType, Integer> SETTLEMENT_COST = new HashMap<>();
    public static final Map<ResourceType, Integer> ROAD_COST = new HashMap<>();
    public static final Map<ResourceType, Integer> CITY_COST = new HashMap<>();

    static {
        SETTLEMENT_COST.put(ResourceType.BRICK, 1);
        SETTLEMENT_COST.put(ResourceType.LUMBER, 1);
        SETTLEMENT_COST.put(ResourceType.WOOL, 1);
        SETTLEMENT_COST.put(ResourceType.GRAIN, 1);

        ROAD_COST.put(ResourceType.BRICK, 1);
        ROAD_COST.put(ResourceType.LUMBER, 1);

        CITY_COST.put(ResourceType.ORE, 3);
        CITY_COST.put(ResourceType.GRAIN, 2);
    }

    public BuildStructure() {
        this.validator = new BuildValidator();
    }

    public BuildStructure(BuildValidator validator) {
        this.validator = (validator == null) ? new BuildValidator() : validator;
    }

    // ---------------- New getter ----------------
    public BuildValidator getValidator() {
        return this.validator;
    }

    private boolean hasResources(Player player, Map<ResourceType, Integer> cost) {
        for (ResourceType r : cost.keySet()) {
            if (player.getCurrentResources().getOrDefault(r, 0) < cost.get(r)) return false;
        }
        return true;
    }

    private void payResources(Player player, Map<ResourceType, Integer> cost) {
        for (ResourceType r : cost.keySet()) {
            player.removeResource(r, cost.get(r));
        }
    }

    public boolean buildRoad(Player player, Edge edge, Board board, Bank bank) {
        if (!validator.canBuildRoad(player, edge, board)) return false;
        if (!hasResources(player, ROAD_COST)) return false;

        payResources(player, ROAD_COST);
        edge.setOwner(player);
        edge.setBuilding(BuildingType.ROAD);
        return true;
    }

    public boolean buildSettlement(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildSettlement(player, node, board, false)) return false;
        if (!hasResources(player, SETTLEMENT_COST)) return false;

        payResources(player, SETTLEMENT_COST);
        node.setOwner(player);
        node.setBuilding(BuildingType.SETTLEMENT);
        player.addVictoryPoints(1);
        return true;
    }

    public boolean buildCity(Player player, Node node, Board board, Bank bank) {
        if (!validator.canBuildCity(player, node, board)) return false;
        if (!hasResources(player, CITY_COST)) return false;

        payResources(player, CITY_COST);
        node.setBuilding(BuildingType.CITY);
        player.addVictoryPoints(1); // +1 VP
        return true;
    }
}