package A2;


/**
 * Class representing the game master for managing game flow and rules.
 */
public class GameMaster {
    private int currentPlayerIndex;
    private boolean gameOver;
    private int winningVP;

    private Player[] players;
    private Bank bank;
    private Board board;

    private final Dice dice;
    private final BuildStructure buildStructure;
    private final ResourceDistributor distributor;
    private final WinChecker winChecker;


    public GameMaster() {
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.winningVP = 10;

        this.dice = new Dice();
        this.buildStructure = new BuildStructure();
        this.distributor = new ResourceDistributor();
        this.winChecker = new WinChecker();
    }

    public void startGame() {
        bank = new Bank();
        board = new Board();
        currentPlayerIndex = 0;
        gameOver = false;
    }

    public void nextTurn() {
        if (players != null && players.length > 0) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }
    }

    public Player getCurrentPlayer() {
        if (players != null && currentPlayerIndex >= 0 && currentPlayerIndex < players.length) {
            return players[currentPlayerIndex];
        }
        return null;
    }

    /** @return sum in {2..12} */
    public int rollDice() {
        return dice.roll();
    }

    public void distributeResources(int roll) {
        distributor.distribute(roll, board, players, bank);
    }

    public boolean buildRoad(Edge edge) {
        return buildStructure.buildRoad(getCurrentPlayer(), edge, board, bank);
    }

    public boolean buildSettlement(Node node) {
        return buildStructure.buildSettlement(getCurrentPlayer(), node, board, bank);
    }

    public boolean buildCity(Node node) {
        return buildStructure.buildCity(getCurrentPlayer(), node, board, bank);
    }

    public boolean checkWin() {
        Player current = getCurrentPlayer();
        if (current != null && current.getVictoryPoints() >= winningVP) {
            gameOver = true;
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void handlePlayersWithMoreThanSevenCards() {
        if (players == null) return;
        for (Player player : players) {
            if (player != null && player.getTotalResources() > 7) {
                player.build();
            }
        }
    }

    public Player[] getPlayers() {
        return players;
    }
    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Bank getBank() {
        return bank;
    }
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }

    public Dice getDice() {
        return dice;
    }
    public BuildStructure getBuildService() {
        return buildStructure;
    }
    public ResourceDistributor getDistributor() {
        return distributor;
    }
    public WinChecker getWinChecker() {
        return winChecker;
    }

    public int getWinningVP() {
        return winningVP;
    }
    public void setWinningVP(int winningVP) {
        this.winningVP = winningVP;
    }

}