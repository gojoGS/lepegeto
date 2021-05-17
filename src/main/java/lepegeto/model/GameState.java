package lepegeto.model;

import jakarta.xml.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents the model of the game.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"BOARD_SIZE", "currentPlayer", "redPositions", "bluePositions", "forbiddenPositions"})
public class GameState implements Cloneable {

    /**
     * The size of the board
     */
    @XmlElement
    private static final int BOARD_SIZE = 5;
    /**
     * The current player
     */
    @XmlElement
    private Player currentPlayer = Player.BLUE;
    /**
     * The positions of the Red player
     */
    @XmlElementWrapper(name = "redPositions")
    @XmlElement(name = "position")
    private Position[] redPositions;
    /**
     * The positions of the Blue player
     */
    @XmlElementWrapper(name = "bluePositions")
    @XmlElement(name = "position")
    private Position[] bluePositions;
    /**
     * The position of block, inaccessible to both sides.
     */
    @XmlElementWrapper(name = "forbiddenPositions")
    @XmlElement(name = "position")
    private Position[] forbiddenPositions;

    /**
     * Creates a {@code GameState} object that corresponds to the initial model of the game.
     */
    public GameState() {
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

    /**
     * Sets the current player to the not current one.
     */
    public void nextPlayer() {
        currentPlayer = currentPlayer.other();
    }

    public Position[] getCurrentPlayerPositions() {
        if (currentPlayer.equals(Player.RED)) {
            return redPositions;
        } else {
            return bluePositions;
        }
    }

    /**
     * {@return whether the current player is the winner, based on the positions of his figures}
     */
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

    public boolean isForbidden(Position position) {
        for (var pos : forbiddenPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRed(Position position) {
        for (var pos : redPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFree(Position position) {
        return !isBlue(position) && !isRed(position) && !isForbidden(position);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isBlue(Position position) {
        for (var pos : bluePositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public Owner owner(Position position) {
        if(isBlue(position)) {
            return Owner.BLUE;
        }else if(isRed(position)) {
            return Owner.RED;
        }else if(isForbidden(position)) {
            return Owner.FORBIDDEN;
        } else {
            return Owner.NONE;
        }
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

    private boolean isValid(Position position) {
        return !isForbidden(position) && !isOccupied(position) && isOnBoard(position);
    }

    public void move(Direction direction, Position position) {
        switch (direction) {
            case WEST -> moveWest(position);
            case EAST -> moveEast(position);
            case NORTH -> moveNorth(position);
            case SOUTH -> moveSouth(position);
            case NORTHEAST -> moveNorthEast(position);
            case NORTHWEST -> moveNorthWest(position);
            case SOUTHEAST -> moveSouthEast(position);
            case SOUTHWEST -> moveSouthWest(position);
        }
    }

    private void moveWest(Position position) {
        position.setWest();
    }
    private void moveEast(Position position) {
        position.setEast();
    }
    private void moveSouth(Position position) {
        position.setSouth();
    }
    private void moveNorth(Position position){
        position.setNorth();
    }
    private void moveSouthEast(Position position) {
        position.setSoutheast();
    }
    private void moveSouthWest(Position position) {
        position.setSouthwest();
    }
    private void moveNorthEast(Position position) {
        position.setNortheast();
    }
    private void moveNorthWest(Position position) {
        position.setNorthwest();
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

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(redPositions), Arrays.hashCode(bluePositions), Arrays.hashCode(forbiddenPositions));
    }

    @Override
    public String toString() {
        var mainSj = new StringJoiner(", ", "{", "}");
        var blueSj = new StringJoiner(", ", "blue: [", "]");
        var redSj = new StringJoiner(", ", "red: [", "]");
        var forbiddenSj = new StringJoiner(", ", "forbidden: [", "]");

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
        mainSj.add(String.format("BOARD_SIZE: %d",BOARD_SIZE));
        mainSj.add(String.format("currentPlayer: %s", currentPlayer.toString()));

        return mainSj.toString();
    }
}
