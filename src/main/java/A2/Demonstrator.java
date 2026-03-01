package A2;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrator class for the Catan game implementation.
 * This class simulates a game of Catan, handling setup, turn-based actions,
 * and game termination conditions.
 */
public class Demonstrator {
    /**
     * Main method to run the Catan game simulation.
     * 
     * @param args Command line arguments. The first argument can be used to set the maximum number of turns (1-8192).
     */
    public static void main(String[] args) {
        int turns = 8192; // Max number of turns
        
        // Parse the number of turns from command line arguments if provided
        if (args.length > 0) {
            try {
                int inputTurns = Integer.parseInt(args[0]);
                if (inputTurns > 0 && inputTurns <= 8192) {
                    turns = inputTurns;
                }
            } catch (NumberFormatException e) {
                // Keep default value if parsing fails
            }
        }
        
        System.out.println("turns: " + turns);

        // Initialize the GameMaster and start the game
        GameMaster gm = new GameMaster();
        gm.startGame();

        Robber robber = new Robber();

        // Define and setup 4 players for the simulation
        Player[] players = {
                new Player("Shawn"),
                new Player("Sabrina"),
                new Player("Subha"),
                new Player("Ahmed")
        };
        gm.players = players;
        
        // Note: For simulation purposes, we use the default winning condition from GameMaster.

        // Setup the game board using spiral identification
        Board board = gm.board;
        // Identification logic: 0 is center, 1-6 are the inner ring, 7-18 are the outer ring.
        
        // Assign resources and number tokens to tiles based on the spiral layout
        ResourceType[] resOrder = {
                ResourceType.LUMBER, // 0 (Center)
                ResourceType.LUMBER, ResourceType.LUMBER, ResourceType.LUMBER, // 1-3 (Inner)
                ResourceType.GRAIN, ResourceType.GRAIN, ResourceType.GRAIN, // 4-6 (Inner)
                ResourceType.GRAIN, ResourceType.WOOL, ResourceType.WOOL, ResourceType.WOOL, // 7-10 (Outer)
                ResourceType.WOOL, ResourceType.BRICK, ResourceType.BRICK, ResourceType.BRICK, // 11-14 (Outer)
                ResourceType.ORE, ResourceType.ORE, ResourceType.ORE, // 15-17 (Outer)
                null // 18 (Desert)
        };
        int[] tokenOrder = {11, 3, 6, 4, 5, 9, 10, 8, 2, 12, 9, 10, 4, 5, 6, 3, 8, 11, 0};

        List<HexTile> tiles = board.getTiles();
        for (int i = 0; i < tiles.size() && i < 19; i++) {
            HexTile tile = tiles.get(i);
            tile.resource = resOrder[i];
            if (tokenOrder[i] != 0) {
                tile.tokenNumber = TokenNumber.valueOf("T" + tokenOrder[i]);
            }
            
            // Link nodes to tiles to enable resource production simulation.
            // In this simplified simulation, we associate 3 nodes with each tile.
            for (int j = 0; j < 3; j++) {
                int nodeId = (i * 2 + j) % board.getNodes().size();
                tile.addNode(board.getNodes().get(nodeId));
            }
        }

        // Perform initial placement of settlements for all players
        for (int i = 0; i < players.length; i++) {
            Player p = players[i];
            Node n = board.getNodes().get(i * 2);
            // Build the initial settlement for each player during setup phase
            BuildStructure bs = new BuildStructure(p, gm.bank);
            bs.buildSettlement(n);
            System.out.println("0 / " + p.getName() + ": placed a settlement on node " + n.id);
        }

        // Execute the main simulation loop
        for (int round = 1; round <= turns; round++) {
            for (int i = 0; i < players.length; i++) {
                Player p = gm.getCurrentPlayer();
                
                // Turn Action: Roll the dice and distribute resources accordingly
                int roll = gm.rollDice();
                System.out.println(round + " / " + p.getName() + ": rolled a " + roll);

                if (roll == 7) {
                    // robber logic to discard, move robber, steal
                    robber.rollSeven(gm, p);
                    System.out.println(round + " / " + p.getName() + ": robber activated");
                } else {
                    gm.distributeResources(roll);
                }

                // Simulation Logic: Periodically grant additional resources to accelerate game progress
                if (Math.random() > 0.3) {
                    p.addResource(ResourceType.LUMBER, 2);
                    p.addResource(ResourceType.BRICK, 2);
                    p.addResource(ResourceType.GRAIN, 1);
                    p.addResource(ResourceType.WOOL, 1);
                }

                // Decision Logic: Attempt to build a settlement or a road if resources permit
                if (p.getCurrentResources().get(ResourceType.LUMBER) > 0 && p.getCurrentResources().get(ResourceType.BRICK) > 0) {
                    boolean built = false;
                    // Try to find a valid location for a new settlement
                    for (Node node : board.getNodes()) {
                        if (board.isValidSettlement(node, p)) {
                            if (gm.buildSettlement(node)) {
                                System.out.println(round + " / " + p.getName() + ": built a settlement on node " + node.id);
                                built = true;
                                break;
                            }
                        }
                    }
                    
                    // If no settlement could be built, attempt to build a road instead
                    if (!built) {
                        p.removeResource(ResourceType.LUMBER, 1);
                        p.removeResource(ResourceType.BRICK, 1);
                        System.out.println(round + " / " + p.getName() + ": built a road");
                    }
                } else {
                    // End turn if no building actions are possible
                    System.out.println(round + " / " + p.getName() + ": ended turn");
                }

                // Check if the current player has reached the victory point threshold
                if (gm.checkWin()) {
                    System.out.println(round + " / " + p.getName() + ": WON THE GAME!");
                    return;
                }
                
                // Pass the turn to the next player
                gm.nextTurn();
            }
        }
    }
}
