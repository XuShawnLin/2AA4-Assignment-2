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
    private Player[] players;

    /**
     * Bank instance for resource management.
     */
    private Bank bank;

    /**
     * Board instance for game map and tiles.
     */
    private Board board;

    /**
     * Dice service used to generate dice rolls.
     */
    private final Dice dice;

    /**
     * Service responsible for handling building actions
     * such as roads, settlements, and cities.
     */
    private final BuildStructure buildStructure;

    /**
     * Service responsible for distributing resources
     * to players after a dice roll.
     */
    private final ResourceDistributor distributor;

    /**
     * Service responsible for checking win conditions.
     */
    private final WinChecker winChecker;

    /**
     * Constructor for GameMaster class.
     */
    public GameMaster() {
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.winningVP = 10;

        this.dice = new Dice();
        this.buildStructure = new BuildStructure();
        this.distributor = new ResourceDistributor();
        this.winChecker = new WinChecker();
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
     * Rolls the dice and returns the resulting value.
     * @return The sum of the dice roll in the range {2..12}.
     */
    public int rollDice() {
        return dice.roll();
    }

    /**
     * Distributes resources to players based on dice roll.
     * @param roll The result of the dice roll.
     */
    public void distributeResources(int roll) {
        distributor.distribute(roll, board, players, bank);
    }

    /**
     * Attempts to build a road for the current player.
     * @param edge The edge where the road should be built.
     * @return true if the road was successfully built.
     */
    public boolean buildRoad(Edge edge) {
        return buildStructure.buildRoad(getCurrentPlayer(), edge, board, bank);
    }

    /**
     * Attempts to build a settlement for the current player.
     * @param node The node where the settlement should be built.
     * @return true if the settlement was successfully built.
     */
    public boolean buildSettlement(Node node) {
        return buildStructure.buildSettlement(getCurrentPlayer(), node, board, bank);
    }

    /**
     * Attempts to upgrade a settlement to a city.
     * @param node The node where the city should be built.
     * @return true if the city was successfully built.
     */
    public boolean buildCity(Node node) {
        return buildStructure.buildCity(getCurrentPlayer(), node, board, bank);
    }

    /**
     * Checks whether the current player has reached the winning victory points.
     * @return true if the current player has won the game.
     */
    public boolean checkWin() {
        Player current = getCurrentPlayer();
        if (current != null && current.getVictoryPoints() >= winningVP) {
            gameOver = true;
            return true;
        }
        return false;
    }

    /**
     * Checks whether the game has ended.
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Handles the case where players have more than seven resource cards.
     * Players exceeding this limit must perform the required action
     * (such as discarding or building depending on the implementation).
     */
    public void handlePlayersWithMoreThanSevenCards() {
        if (players == null) return;
        for (Player player : players) {
            if (player != null && player.getTotalResources() > 7) {
                player.build();
            }
        }
    }

    /**
     * Retrieves the players participating in the game.
     * @return array of players.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets the players participating in the game.
     * @param players array of players.
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * Retrieves the bank used for resource management.
     * @return bank instance.
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank used for resource management.
     * @param bank bank instance.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Retrieves the game board.
     * @return board instance.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the game board.
     * @param board board instance.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Retrieves the dice service.
     * @return dice instance.
     */
    public Dice getDice() {
        return dice;
    }

    /**
     * Retrieves the build structure service.
     * @return buildStructure instance.
     */
    public BuildStructure getBuildService() {
        return buildStructure;
    }

    /**
     * Retrieves the resource distributor service.
     * @return distributor instance.
     */
    public ResourceDistributor getDistributor() {
        return distributor;
    }

    /**
     * Retrieves the win checker service.
     * @return winChecker instance.
     */
    public WinChecker getWinChecker() {
        return winChecker;
    }

    /**
     * Retrieves the victory point threshold required to win.
     * @return winning victory points.
     */
    public int getWinningVP() {
        return winningVP;
    }

    /**
     * Sets the victory point threshold required to win.
     * @param winningVP victory points required to win.
     */
    public void setWinningVP(int winningVP) {
        this.winningVP = winningVP;
    }

}