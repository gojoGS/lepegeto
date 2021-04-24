package lepegeto.state;

public class GameState {

    public static final int BOARD_SIZE = 7;

    private Position[] redPositions;
    private Position[] bluePositions;
    private Position[] forbiddenPositions;

    private Player currentPlayer;

    public void nextPlayer() {
        currentPlayer = Player.other(currentPlayer);
    }






}
