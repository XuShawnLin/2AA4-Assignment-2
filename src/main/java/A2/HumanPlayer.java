package A2;

import java.util.List;
import java.util.Scanner;

/**
 * Human-controlled player.
 * Allows interactive commands for rolling, building, and viewing resources.
 */
public class HumanPlayer extends Player {

    private final Scanner scanner;

    public HumanPlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Let the human choose the initial settlement during game setup.
     */
    public Node chooseInitialNode(Board board) {
        List<Node> nodes = board.getNodes();
        Node chosenNode = null;

        while (chosenNode == null) {
            System.out.println("Choose a node id to place your settlement:");
            for (Node node : nodes) {
                System.out.print(node.getId() + " ");
            }
            System.out.println();

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) return null;

            try {
                int nodeId = Integer.parseInt(input);
                Node node = board.getNodes().stream()
                        .filter(n -> n.getId() == nodeId)
                        .findFirst()
                        .orElse(null);

                // ✅ FIX: Pass the boolean for initial placement (ignoreRoads = true)
                if (node != null && board.isValidSettlement(node, this, true)) {
                    chosenNode = node;
                } else {
                    System.out.println("Invalid node. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        return chosenNode;
    }

    /**
     * Perform a human player's turn.
     */
    @Override
    public void takeTurn(GameMaster gameMaster, int round) {
        boolean rolled = false;
        boolean turnFinished = false;


        while (!turnFinished) {
            System.out.print("Command (Roll, Build, List, Go): ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "roll":
                    if (!rolled) {
                        int roll = gameMaster.rollDice();
                        System.out.println(round + " / " + getName() + ": rolled a " + roll);
                        if (roll == 7) {
                            System.out.println("Robber activated");
                            // robber logic can go here
                        } else {
                            gameMaster.distributeResources(roll);
                        }
                        rolled = true;
                    } else {
                        System.out.println("You already rolled this turn.");
                    }
                    break;

                case "list":
                    System.out.println("Your resources:");
                    System.out.println(getCurrentResources());
                    break;

                case "build":
                    if (!rolled) {
                        System.out.println("You must roll first.");
                        break;
                    }
                    buildAction(gameMaster);
                    break;

                case "go":
                    if (!rolled) {
                        System.out.println("You must roll first.");
                    } else {
                        System.out.println(round + " / " + getName() + ": ended turn");
                        turnFinished = true;
                    }
                    break;

                default:
                    System.out.println("Unknown command.");
            }
        }
    }

    /**
     * Build logic for human player with resource check and back option.
     */
    private void buildAction(GameMaster gameMaster) {
        BuildStructure buildService = gameMaster.getBuildService();
        Board board = gameMaster.getBoard();

        System.out.print("What do you want to build? (Settlement, Road, City) or 'back': ");
        String type = scanner.nextLine().trim().toLowerCase();

        switch (type) {
            case "settlement":
                // Check resources: 1 Brick + 1 Lumber + 1 Wool + 1 Grain
                if (getCurrentResources().getOrDefault(ResourceType.BRICK, 0) < 1 ||
                        getCurrentResources().getOrDefault(ResourceType.LUMBER, 0) < 1 ||
                        getCurrentResources().getOrDefault(ResourceType.WOOL, 0) < 1 ||
                        getCurrentResources().getOrDefault(ResourceType.GRAIN, 0) < 1) {
                    System.out.println("Not enough resources to build a settlement.");
                    return;
                }

                Node settlementNode = chooseBuildNode(board, false); // normal gameplay
                if (settlementNode == null) return; // back option

                if (buildService.buildSettlement(this, settlementNode, board, gameMaster.getBank())) {
                    removeResource(ResourceType.BRICK, 1);
                    removeResource(ResourceType.LUMBER, 1);
                    removeResource(ResourceType.WOOL, 1);
                    removeResource(ResourceType.GRAIN, 1);
                    System.out.println(getName() + ": built a settlement on node " + settlementNode.getId());
                } else {
                    System.out.println("Cannot build settlement there.");
                }
                break;

            case "city":
                // Check resources: 2 Grain + 3 Ore
                if (getCurrentResources().getOrDefault(ResourceType.GRAIN, 0) < 2 ||
                        getCurrentResources().getOrDefault(ResourceType.ORE, 0) < 3) {
                    System.out.println("Not enough resources to build a city.");
                    return;
                }

                Node cityNode = chooseBuildNode(board, false); // normal gameplay
                if (cityNode == null) return; // back option

                if (buildService.buildCity(this, cityNode, board, gameMaster.getBank())) {
                    removeResource(ResourceType.GRAIN, 2);
                    removeResource(ResourceType.ORE, 3);
                    System.out.println(getName() + ": built a city on node " + cityNode.getId());
                } else {
                    System.out.println("Cannot build city there.");
                }
                break;

            case "road":
                // Check resources: 1 Brick + 1 Lumber
                if (getCurrentResources().getOrDefault(ResourceType.BRICK, 0) < 1 ||
                        getCurrentResources().getOrDefault(ResourceType.LUMBER, 0) < 1) {
                    System.out.println("Not enough resources to build a road.");
                    return;
                }

                Edge edge = chooseBuildEdge(board);
                if (edge == null) return; // back option

                if (buildService.buildRoad(this, edge, board, gameMaster.getBank())) {
                    removeResource(ResourceType.BRICK, 1);
                    removeResource(ResourceType.LUMBER, 1);
                    System.out.println(getName() + ": built a road");
                } else {
                    System.out.println("Cannot build road there.");
                }
                break;

            case "back":
                System.out.println("Build canceled.");
                break;

            default:
                System.out.println("Unknown build type.");
                break;
        }
    }

    // Choose node to build (normal gameplay or initial placement)
    private Node chooseBuildNode(Board board, boolean initialPlacement) {
        Node chosenNode = null;
        while (chosenNode == null) {
            System.out.print("Enter node id or 'back': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) return null;

            try {
                int nodeId = Integer.parseInt(input);
                Node node = board.getNodes().stream()
                        .filter(n -> n.getId() == nodeId)
                        .findFirst()
                        .orElse(null);

                if (node != null && board.isValidSettlement(node, this, initialPlacement)) {
                    chosenNode = node;
                } else {
                    System.out.println("Cannot build there. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return chosenNode;
    }

    private Edge chooseBuildEdge(Board board) {
        Edge chosenEdge = null;
        while (chosenEdge == null) {
            System.out.print("Enter edge id or 'back': ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("back")) return null;

            try {
                int edgeId = Integer.parseInt(input);
                Edge edge = board.getEdges().stream()
                        .filter(e -> e.getId() == edgeId)
                        .findFirst()
                        .orElse(null);

                if (edge != null) {
                    chosenEdge = edge;
                } else {
                    System.out.println("Invalid edge. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        return chosenEdge;
    }
}