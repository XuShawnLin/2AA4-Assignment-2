package A2;

/**
 * Centralizes building rule checks (SRP).
 * Uses Board validation when available; otherwise falls back to basic safety checks.
 */
public class BuildValidator {

    public boolean canBuildRoad(Player player, Edge edge, Board board) {
        if (player == null || edge == null) return false;
        if (edge.isOccupied()) return false;
        // If board has richer rules, use them.
        if (board != null) {
            return board.isValidRoad(edge, player);
        }
        return true;
    }

    public boolean canBuildSettlement(Player player, Node node, Board board) {
        if (player == null || node == null) return false;
        if (node.isOccupied()) return false;
        if (board != null) {
            return board.isValidSettlement(node, player);
        }
        return true;
    }

    public boolean canBuildCity(Player player, Node node, Board board) {
        if (player == null || node == null) return false;
        // City upgrade requires ownership and existing settlement
        return node.getOwner() == player && node.getBuilding() == BuildingType.SETTLEMENT;
    }
}
