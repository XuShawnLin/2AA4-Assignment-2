package A2;

import java.util.List;
import java.util.Scanner;

public class Demonstrator {

    public static void main(String[] args) {
        int maxTurns = 8192;

        // Optional command-line override
        if (args.length > 0) {
            try {
                int inputTurns = Integer.parseInt(args[0]);
                if (inputTurns > 0 && inputTurns <= 8192) maxTurns = inputTurns;
            } catch (NumberFormatException ignored) {}
        }

        System.out.println("Turns: " + maxTurns);

        // Initialize game master
        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();

        // Players: Shawn is human
        Player[] players = {
                new HumanPlayer("Shawn"),
                new Player("Sabrina"),
                new Player("Subha"),
                new Player("Ahmed")
        };
        gameMaster.setPlayers(players);

        Board board = gameMaster.getBoard();
        BuildStructure buildService = gameMaster.getBuildService();
        BuildValidator validator = buildService.getValidator();

        // Setup tiles and tokens (your existing spiral setup)
        ResourceType[] resOrder = {
                ResourceType.LUMBER, ResourceType.LUMBER, ResourceType.LUMBER, ResourceType.LUMBER,
                ResourceType.GRAIN, ResourceType.GRAIN, ResourceType.GRAIN,
                ResourceType.GRAIN, ResourceType.WOOL, ResourceType.WOOL, ResourceType.WOOL,
                ResourceType.WOOL, ResourceType.BRICK, ResourceType.BRICK, ResourceType.BRICK,
                ResourceType.ORE, ResourceType.ORE, ResourceType.ORE,
                null
        };
        int[] tokenOrder = {11,3,6,4,5,9,10,8,2,12,9,10,4,5,6,3,8,11,0};
        List<HexTile> tiles = board.getTiles();
        for (int i = 0; i < tiles.size() && i < 19; i++) {
            HexTile tile = tiles.get(i);
            tile.setResource(resOrder[i]);
            if (tokenOrder[i] != 0) tile.setTokenNumber(tokenOrder[i]);
            for (int j = 0; j < 3; j++) {
                int nodeId = (i * 2 + j) % board.getNodes().size();
                tile.addNode(board.getNodes().get(nodeId));
            }
        }

        Scanner scanner = new Scanner(System.in);

        // ====== INITIAL PLACEMENT (2 settlements each) ======

        // First round: normal order
        for (Player p : players) {
            Node node = null;
            if (p instanceof HumanPlayer) {
                node = ((HumanPlayer) p).chooseInitialNode(board);
                // Ensure node is valid
                if (!board.isValidSettlement(node, p, true)) {
                    System.out.println("Invalid node chosen, pick again.");
                    node = ((HumanPlayer) p).chooseInitialNode(board);
                }
            } else {
                // AI chooses first available valid node
                for (Node n : board.getNodes()) {
                    if (validator.canBuildSettlement(p, n, board, true)) { // initialPlacement = true
                        node = n;
                        break;
                    }
                }
            }

            // Place settlement directly (ignore resources)
            node.setOwner(p);
            node.setBuilding(BuildingType.SETTLEMENT);
            p.addVictoryPoints(1);

            System.out.println("0 / " + p.getName() + ": placed first settlement on node " + node.getId());
        }

        // Second round: reverse order
        for (int i = players.length - 1; i >= 0; i--) {
            Player p = players[i];
            Node node = null;
            if (p instanceof HumanPlayer) {
                node = ((HumanPlayer) p).chooseInitialNode(board);
                if (!board.isValidSettlement(node, p, true)) {
                    System.out.println("Invalid node chosen, pick again.");
                    node = ((HumanPlayer) p).chooseInitialNode(board);
                }
            } else {
                for (Node n : board.getNodes()) {
                    if (validator.canBuildSettlement(p, n, board, true)) {
                        node = n;
                        break;
                    }
                }
            }

            node.setOwner(p);
            node.setBuilding(BuildingType.SETTLEMENT);
            p.addVictoryPoints(1);

            System.out.println("0 / " + p.getName() + ": placed second settlement on node " + node.getId());
        }

        // ====== MAIN GAME LOOP ======
        for (int round = 1; round <= maxTurns; round++) {
            System.out.println("----- Round " + round + " -----");

            for (int i = 0; i < players.length; i++) {
                Player p = gameMaster.getCurrentPlayer();

                System.out.println("----- " + p.getName() + "'s Turn -----");

                // Only roll automatically for AI players
                if (!(p instanceof HumanPlayer)) {
                    int roll = gameMaster.rollDice();
                    System.out.println(round + " / " + p.getName() + ": rolled a " + roll);

                    if (roll == 7) {
                        new Robber().rollSeven(board, players, p);
                        System.out.println(round + " / " + p.getName() + ": robber activated");
                    } else {
                        gameMaster.distributeResources(roll);
                    }
                }

                // ====== BUILDING PHASE ======
                if (p instanceof HumanPlayer) {
                    ((HumanPlayer) p).takeTurn(gameMaster, round);
                } else {
                    boolean built = false;

                    // Try to build settlement (normal gameplay)
                    for (Node n : board.getNodes()) {
                        if (validator.canBuildSettlement(p, n, board, false)) { // initialPlacement = false
                            if (buildService.buildSettlement(p, n, board, gameMaster.getBank())) {
                                System.out.println(round + " / " + p.getName() + ": built a settlement on node " + n.getId());
                                built = true;
                                break;
                            }
                        }
                    }

                    // Try to build road if no settlement built
                    if (!built) {
                        for (Edge e : board.getEdges()) {
                            if (board.isValidRoad(e, p)) {
                                if (buildService.buildRoad(p, e, board, gameMaster.getBank())) {
                                    System.out.println(round + " / " + p.getName() + ": built a road on edge " + e.getId());
                                    built = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!built) {
                        System.out.println(round + " / " + p.getName() + ": ended turn (no valid build or not enough resources)");
                    }
                }

                // Check for win
                if (gameMaster.checkWin()) {
                    System.out.println(round + " / " + p.getName() + ": WON THE GAME!");
                    return;
                }

                gameMaster.nextTurn();
            }
        }
    }
}