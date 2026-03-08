package A2;

import java.util.List;
import java.util.Scanner;

public class Demonstrator {

    public static void main(String[] args) {
        int maxTurns = 8192;
        boolean useWatch = false;

        if (args.length > 0) {
            // Accept either a numeric turns arg or flags like --watch
            for (String arg : args) {
                if ("--watch".equalsIgnoreCase(arg)) {
                    useWatch = true;
                } else {
                    try {
                        int inputTurns = Integer.parseInt(arg);
                        if (inputTurns > 0 && inputTurns <= 8192) maxTurns = inputTurns;
                    } catch (NumberFormatException nfe) {
                        System.err.println("[Config] Ignoring unrecognized argument '" + arg + "': " + nfe.getMessage());
                    }
                }
            }
        }

        System.out.println("Turns: " + maxTurns);

        GameMaster gameMaster = new GameMaster();
        gameMaster.startGame();

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

        setupTiles(board);

        Scanner scanner = new Scanner(System.in);

        // Initialize visualizer integration
        if (useWatch) {
            // Start watch mode once; subsequent exports will just write JSON
            VisualExporter.export(board, players, false);
            VisualExporter.ensureWatchRunning();
        } else {
            // One-off render after each export
            VisualExporter.export(board, players, true);
        }

        //Call method so players can place intial nodes
        for (Player p : players) {
            Node node = chooseInitialNode(p, board, validator);
            placeSettlement(p, node, "first");
            // Update JSON and render/watch after each initial placement
            VisualExporter.export(board, players, !useWatch);
        }

        for (int i = players.length - 1; i >= 0; i--) {
            Player p = players[i];
            Node node = chooseInitialNode(p, board, validator);
            placeSettlement(p, node, "second");
            VisualExporter.export(board, players, !useWatch);
        }

        //Main game loop
        for (int round = 1; round <= maxTurns; round++) {

            System.out.println("Turn: " + round);

            for (int i = 0; i < players.length; i++) {

                Player p = gameMaster.getCurrentPlayer();

                System.out.println("----- " + p.getName() + "'s Turn -----");

                handleDiceRoll(gameMaster, board, players, p, round);

                if (p instanceof HumanPlayer) {
                    ((HumanPlayer) p).takeTurn(gameMaster, round);
                } else {
                    aiBuildTurn(p, board, validator, buildService, gameMaster, round);
                }

                // Update visualizer JSON and render/watch after each player's turn
                VisualExporter.export(board, players, !useWatch);

                if (gameMaster.checkWin()) {
                    System.out.println(round + " / " + p.getName() + ": WON THE GAME!");
                    return;
                }

                gameMaster.nextTurn();
            }
        }
    }

    //Set up board method
    private static void setupTiles(Board board) {

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

            if (tokenOrder[i] != 0)
                tile.setTokenNumber(tokenOrder[i]);

            for (int j = 0; j < 3; j++) {
                int nodeId = (i * 2 + j) % board.getNodes().size();
                tile.addNode(board.getNodes().get(nodeId));
            }
        }
    }

    //Method for placing settlements on Nodes
    private static Node chooseInitialNode(Player p, Board board, BuildValidator validator) {

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

        return node;
    }

    private static void placeSettlement(Player p, Node node, String order) {

        node.setOwner(p);
        node.setBuilding(BuildingType.SETTLEMENT);
        p.addVictoryPoints(1);

        System.out.println("0 / " + p.getName() + ": placed " + order +
                " settlement on node " + node.getId());
    }

    private static void handleDiceRoll(GameMaster gameMaster, Board board,
                                       Player[] players, Player p, int round) {

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
    }

    private static void aiBuildTurn(Player p, Board board, BuildValidator validator,
                                    BuildStructure buildService, GameMaster gameMaster, int round) {

        boolean built = false;

        for (Node n : board.getNodes()) {

            if (validator.canBuildSettlement(p, n, board, false)) {

                if (buildService.buildSettlement(p, n, board, gameMaster.getBank())) {

                    System.out.println(round + " / " + p.getName()
                            + ": built a settlement on node " + n.getId());

                    built = true;
                    break;
                }
            }
        }

        if (!built) {

            for (Edge e : board.getEdges()) {

                if (board.isValidRoad(e, p)) {

                    if (buildService.buildRoad(p, e, board, gameMaster.getBank())) {

                        System.out.println(round + " / " + p.getName()
                                + ": built a road on edge " + e.getId());

                        built = true;
                        break;
                    }
                }
            }
        }

        if (!built) {
            System.out.println(round + " / " + p.getName()
                    + ": ended turn (no valid build or not enough resources)");
        }
    }
}
