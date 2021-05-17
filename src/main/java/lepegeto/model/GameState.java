package lepegeto.model;

import jakarta.xml.bind.annotation.*;
import org.tinylog.Logger;

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
     * The size of the board.
     */
    @XmlElement
    private static final int BOARD_SIZE = 5;
    /**
     * The current player.
     */
    @XmlElement
    private Player currentPlayer = Player.BLUE;
    /**
     * The positions of the Red player.
     */
    @XmlElementWrapper(name = "redPositions")
    @XmlElement(name = "position")
    private Position[] redPositions;
    /**
     * The positions of the Blue player.
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

    /**
     * Returns the current players positions.
     * @return An array of positions.
     */
    public Position[] getCurrentPlayerPositions() {
        if (currentPlayer.equals(Player.RED)) {
            return redPositions;
        } else {
            return bluePositions;
        }
    }

    /**
     * Returns whether the current player is the winner, based on the positions of his figures.
     * @return whether the current player is the winner, based on the positions of his figures.
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

    /**
     * Returns whether the owner of the {@link Position} is the current player.
     * @param position the position in question
     * @return whether {@code position} is owned by the current player.
     */
    public boolean isOccupiedByCurrentPlayer(Position position) {
        for(var figure: getCurrentPlayerPositions()) {
            if(position.equals(figure)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the position of the current player thats equals to the parameter {@code position}.
     * @param position that we are looking for
     * @throws IllegalArgumentException if position isnt owned by the current player
     * @return a position of the current player
     */
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

    /**
     * Returns whether the owner of the {@link Position} is forbidden.
     * @param position the position in question
     * @return whether {@code position} is forbidden.
     */
    public boolean isForbidden(Position position) {
        for (var pos : forbiddenPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the owner of the {@link Position} is red.
     * @param position the position in question
     * @return whether {@code position} is owned by Red
     */
    public boolean isRed(Position position) {
        for (var pos : redPositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the owner of the {@link Position} is no one.
     * @param position the position in question
     * @return whether {@code position} is owned by no one
     */
    public boolean isFree(Position position) {
        return !isBlue(position) && !isRed(position) && !isForbidden(position);
    }

    /**
     * Returns the current {@link Player}.
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns whether the owner of the {@link Position} is blue.
     * @param position the position in question
     * @return whether {@code position} is owned by Blue
     */
    public boolean isBlue(Position position) {
        for (var pos : bluePositions) {
            if (position.equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the {@link Owner} of a {@link Position}.
     * @param position The position
     * @return The owner of the parameter {@code position}
     */
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

    /**
     * Moves a {@link Position} in a {@link Direction}.
     * @param direction the direction of the move
     * @param position the initial position of the move.
     */
    public void move(Direction direction, Position position) {
        Logger.info(String.format("%s is moved in direction %s", position.toString(), direction.toString()));
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

    @Override
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
