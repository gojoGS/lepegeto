package lepegeto.state;

import jakarta.xml.bind.annotation.*;

import java.util.Arrays;
import java.util.StringJoiner;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GameState implements Cloneable {

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

    private static Position[] deepClone(Position[] a) {
        Position[] copy = a.clone();
        for (var i = 0; i < a.length; i++) {
            copy[i] = a[i].clone();
        }
        return copy;
    }

    public void nextPlayer() {
        currentPlayer = Player.other(currentPlayer);
    }

    public Position[] getCurrentPlayerPositions() {
        if (currentPlayer.equals(Player.RED)) {
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

    public boolean isOccupiedByCurrentPlayer(Position position) {
        for(var figure: getCurrentPlayerPositions()) {
            if(position.equals(figure)) {
                return true;
            }
        }

        return false;
    }

    public Position getPositionAt(Position position) {

        for(var figure: getCurrentPlayerPositions()) {
            if(position.equals(figure)) {
                return figure;
            }
        }

        throw new IllegalArgumentException();
    }

    private boolean isOnBoard(Position position) {
        return position.getRow() >= 0 && position.getRow() < BOARD_SIZE &&
                position.getCol() >= 0 && position.getCol() < BOARD_SIZE;
    }

    private boolean isForbidden(Position position) {
        for (var pos : forbiddenPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOccupied(Position position) {
        for (var pos : redPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }

        for (var pos : bluePositions) {
            if (position.equals(pos)) {
                return true;
            }
        }

        return false;
    }

    public boolean isValid(Position position) {
        return !isForbidden(position) && !isOccupied(position) && isOnBoard(position);
    }

    @Override
    public GameState clone() {
        GameState copy;
        try {
            copy = (GameState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

        copy.bluePositions = deepClone(bluePositions);
        copy.forbiddenPositions = deepClone(forbiddenPositions);
        copy.redPositions = deepClone(redPositions);

        return copy;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof GameState)) {
            return false;
        }

        return Arrays.equals(redPositions, ((GameState) o).redPositions)
                && Arrays.equals(bluePositions, ((GameState) o).bluePositions)
                && Arrays.equals(forbiddenPositions, ((GameState) o).forbiddenPositions);
    }


    // TODO make adquite solution
    @Override
    public int hashCode() {
        return Arrays.hashCode(redPositions);
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
