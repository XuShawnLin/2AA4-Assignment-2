package A2;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Demonstrator {

    private static final Logger LOGGER = Logger.getLogger(Demonstrator.class.getName());

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
                        LOGGER.log(Level.WARNING, "[Config] Ignoring unrecognized argument '{0}': {1}", new Object[]{arg, nfe.getMessage()});
                    }
                }
            }
        }

        LOGGER.log(Level.INFO, "Turns: {0}", maxTurns);

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

        // Removed unused Scanner to satisfy static analysis

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

            LOGGER.log(Level.INFO, "Turn: {0}", round);

            for (int i = 0; i < players.length; i++) {

                Player p = gameMaster.getCurrentPlayer();

                LOGGER.log(Level.INFO, "----- {0}'s Turn -----", p.getName());

                handleDiceRoll(gameMaster, board, players, p, round);

                if (p instanceof HumanPlayer humanPlayer) {
                    humanPlayer.takeTurn(gameMaster, round);
                } else {
                    aiBuildTurn(p, board, validator, buildService, gameMaster, round);
                }

                // Update visualizer JSON and render/watch after each player's turn
                VisualExporter.export(board, players, !useWatch);

                if (gameMaster.checkWin()) {
                    LOGGER.log(Level.INFO, "{0} / {1}: WON THE GAME!", new Object[]{round, p.getName()});
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

        if (p instanceof HumanPlayer human) {

            node = human.chooseInitialNode(board);

            if (!board.isValidSettlement(node, p, true)) {
                LOGGER.info("Invalid node chosen, pick again.");
                node = human.chooseInitialNode(board);
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

        LOGGER.log(Level.INFO, "0 / {0}: placed {1} settlement on node {2}", new Object[]{p.getName(), order, node.getId()});
    }

    private static void handleDiceRoll(GameMaster gameMaster, Board board,
                                       Player[] players, Player p, int round) {

        if (!(p instanceof HumanPlayer)) {

            int roll = gameMaster.rollDice();

            LOGGER.log(Level.INFO, "{0} / {1}: rolled a {2}", new Object[]{round, p.getName(), roll});

            if (roll == 7) {

                new Robber().rollSeven(board, players, p);

                LOGGER.log(Level.INFO, "{0} / {1}: robber activated", new Object[]{round, p.getName()});

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

                if (buildService.buildSettlement(p, n, board)) {

                    LOGGER.log(Level.INFO, "{0} / {1}: built a settlement on node {2}", new Object[]{round, p.getName(), n.getId()});

                    built = true;
                    break;
                }
            }
        }

        if (!built) {

            for (Edge e : board.getEdges()) {

                if (board.isValidRoad(e, p)) {

                    if (buildService.buildRoad(p, e, board)) {

                        LOGGER.log(Level.INFO, "{0} / {1}: built a road on edge {2}", new Object[]{round, p.getName(), e.getId()});

                        built = true;
                        break;
                    }
                }
            }
        }

        if (!built) {
            LOGGER.log(Level.INFO, "{0} / {1}: ended turn (no valid build or not enough resources)", new Object[]{round, p.getName()});
        }
    }
}
