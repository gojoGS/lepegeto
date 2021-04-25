package lepegeto.state;

import jakarta.xml.bind.annotation.*;

import java.util.StringJoiner;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GameState {

    private static final int BOARD_SIZE = 5;
    private Player currentPlayer;

    @XmlElementWrapper(name = "redPositions")
    @XmlElement(name = "position")
    private Position[] redPositions;

    @XmlElementWrapper(name = "bluePositions")
    @XmlElement(name = "position")
    private Position[] bluePositions;

    @XmlElementWrapper(name = "forbiddenPositions")
    @XmlElement(name = "position")
    private Position[] forbiddenPositions;

    GameState() {
        redPositions = new Position[BOARD_SIZE];
        bluePositions = new Position[BOARD_SIZE];
        forbiddenPositions = new Position[4];

        for (int i = 0; i < BOARD_SIZE; ++i) {
            bluePositions[i] = new Position(0, i);
            redPositions[i] = new Position(BOARD_SIZE - 1, i);
        }

        forbiddenPositions[0] = new Position(1, 1);
        forbiddenPositions[1] = new Position(1, 3);
        forbiddenPositions[2] = new Position(3, 1);
        forbiddenPositions[3] = new Position(3, 3);
    }

    public void nextPlayer() {
        currentPlayer = Player.other(currentPlayer);
    }

    public Position[] getCurrentPlayerPositions() {
        if (currentPlayer == Player.RED) {
            return redPositions;
        } else {
            return bluePositions;
        }
    }

    public boolean isCurrentPlayerWinner() {
        int destinationRowNumber = -1;
        switch (currentPlayer) {
            case BLUE -> destinationRowNumber = 6;
            case RED -> destinationRowNumber = 0;
        }

        for (var figure : getCurrentPlayerPositions()) {
            if (figure.getRow() != destinationRowNumber) {
                return false;
            }
        }
        return true;
    }

    private boolean isOnBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < BOARD_SIZE &&
                position.getCol() >= 0 && position.getCol() < BOARD_SIZE;
    }

    private boolean isForbidden(Position position) {
        for (var pos: forbiddenPositions) {
            if(position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOccupied(Position position) {
        for(var pos: redPositions) {
            if(position.equals(pos)) {
                return true;
            }
        }

        for(var pos: bluePositions) {
            if(position.equals(pos)) {
                return true;
            }
        }

        return false;
    }

    public boolean isValid(Position position) {
        return !isForbidden(position) && !isOccupied(position) && isOnBoard(position);
    }


    @Override
    public String toString() {
        var mainSj = new StringJoiner(",", "{", "}");
        var blueSj = new StringJoiner(",", "blue: [", "]");
        var redSj = new StringJoiner(",", "red: [", "]");
        var forbiddenSj = new StringJoiner(",", "forbidden: [", "]");

        for (var position : bluePositions) {
            blueSj.add(position.toString());
        }

        for (var position : redPositions) {
            redSj.add(position.toString());
        }

        for (var position : forbiddenPositions) {
            forbiddenSj.add(position.toString());
        }

        mainSj.add(blueSj.toString());
        mainSj.add(redSj.toString());
        mainSj.add(forbiddenSj.toString());

        return mainSj.toString();
    }

}
