package A2;

/**
 * Class representing the game master for managing game flow and rules.
 */
public class GameMaster {
	/**
	 * Index of the current player.
	 */
	private int currentPlayerIndex;
	/**
	 * Value of the first dice roll.
	 */
	private int dice1;
	/**
	 * Value of the second dice roll.
	 */
	private int dice2;
	/**
	 * Flag indicating if the game is over.
	 */
	private boolean gameOver;
	/**
	 * Winning victory points threshold.
	 */
	private int winningVP;
	/**
	 * Array of players in the game.
	 */
	public Player[] players;
	/**
	 * Bank instance for resource management.
	 */
	public Bank bank;
	/**
	 * Board instance for game map and tiles.
	 */
	public Board board;
	/**
	 * Constructor for GameMaster class.
	 */
	public GameMaster() {
		currentPlayerIndex = 0;
		gameOver = false;
		winningVP = 10;
		dice1 = 0;
		dice2 = 0;
	}
	/**
	 * Starts the game initialization and setup.
	 */
	public void startGame() {
		bank = new Bank();
		board = new Board();
		currentPlayerIndex = 0;
		gameOver = false;
	}
	/**
	 * Advances the game to the next player's turn.
	 */
	public void nextTurn() {
		if (players != null && players.length > 0) {
			currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
		}
	}
	/**
	 * Retrieves the current player.
	 * @return The current player, or null if no players are present.
	 */
	public Player getCurrentPlayer() {
		if (players != null && currentPlayerIndex >= 0 && currentPlayerIndex < players.length) {
			return players[currentPlayerIndex];
		}
		return null;
	}
	/**
	 * Rolls two dice and returns the sum of the results.
	 * @return The sum of the two dice rolls.
	 */
	public int rollDice() {
		dice1 = (int)(Math.random() * 6) + 1;
		dice2 = (int)(Math.random() * 6) + 1;
		return dice1 + dice2;
	}
	/**
	 * Distributes resources to players based on dice roll.
	 * @param roll The result of the dice roll.
	 */
	public void distributeResources(int roll) {
		for (HexTile tile : board.getTiles()) {
			if (tile.getTokenNumber() != roll) continue;

			ResourceType resource = tile.getResource();

			for (Node node : tile.getNodes()) {
				if (node.isOccupied() && bank.giveResource(resource, 1)) {
					node.getOwner().addResource(resource, 1);
				}
			}
		}
	}

	public boolean buildRoad(Edge edge) {
		BuildStructure bs = new BuildStructure(getCurrentPlayer(), bank);
		return bs.buildRoad(edge);
	}

	public boolean buildSettlement(Node node) {
		BuildStructure bs = new BuildStructure(getCurrentPlayer(), bank);
		return bs.buildSettlement(node);
	}

	public boolean buildCity(Node node) {
		BuildStructure bs = new BuildStructure(getCurrentPlayer(), bank);
		return bs.buildCity(node);
	}

	public boolean checkWin() {
		if (getCurrentPlayer().getVictoryPoints() >= winningVP) {
			gameOver = true;
			return true;
		}
		return false;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void handlePlayersWithMoreThanSevenCards() {
		for (Player player : players) {
			if (player.getTotalResources() > 7) {
				player.build();
			}
		}
	}
}
