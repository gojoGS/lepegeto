package lepegeto.model;

import jakarta.xml.bind.annotation.*;
import javafx.geometry.Pos;
import org.tinylog.Logger;

import java.util.*;

/**
 * Represents the model of the game.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"BOARD_SIZE", "currentPlayer", "redPositions", "bluePositions", "forbiddenPositions", "players", "numberOfTurns"})
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

    @XmlElement
    HashMap<Player, String> players;

    @XmlElement
    int numberOfTurns;

    @XmlTransient
    private ArrayList<Position> selected;
    @XmlTransient
    private ArrayList<Position> ghosts;

    public HashMap<Player, String> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Player, String> players) {
        this.players = players;
    }

    /**
     * Creates a {@code GameState} object that corresponds to the initial model of the game.
     */
    public GameState() {
        selected = new ArrayList<Position>();
        ghosts = new ArrayList<Position>();

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

        players = new HashMap<Player, String>();
    }

    private static Position[] deepClone(Position[] a) {
        Position[] copy = a.clone();
        for (var i = 0; i < a.length; i++) {
            copy[i] = a[i].clone();
        }
        return copy;
    }

    public ArrayList<Position> getGhosts() {
        return ghosts;
    }

    public ArrayList<Position> getSelected() {
        return selected;
    }

    public boolean isValidSelection() {
        Position selection1 = selected.get(0);
        Position selection2 = selected.get(1);
        Position ghost1 = ghosts.get(0);
        Position ghost2 = ghosts.get(1);

        try {
            Direction direction1 = Direction.of(ghost1.getRow() - selection1.getRow(), ghost1.getCol() - selection1.getCol());
            Direction direction2 = Direction.of(ghost2.getRow() - selection2.getRow(), ghost2.getCol() - selection2.getCol());

            return direction2.equals(direction1);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public Direction getSelectionDirection() {
        Position selection1 = selected.get(0);
        Position ghost1 = ghosts.get(0);

        return Direction.of(ghost1.getRow() - selection1.getRow(), ghost1.getCol() - selection1.getCol());
    }

    public void moveSelected(Direction direction) {
        for (var pos : selected) {
            Position newPosition = getPositionAt(pos);
            move(direction, newPosition);
        }
    }

    public void clearSelection() {
        ghosts.clear();
        selected.clear();
    }

    public boolean endTurn() throws IllegalArgumentException, IllegalStateException {

        if (getGhosts().size() != 2 && getSelected().size() != 2) {
            Logger.warn("Not enough moves");
            throw new IllegalArgumentException();
        }

        if (isValidSelection()) {
            moveSelected(getSelectionDirection());
            clearSelection();

            nextPlayer();
            ++numberOfTurns;
            if (isCurrentPlayerWinner()) {
                Logger.info("A player has won the game");
                return true;
            } else {
                Logger.info("Next turn");
                return false;
            }
        } else {
            Logger.warn("Invalid move");
            throw new IllegalStateException();
        }
    }

    public void addGhost(Position position) {
        if (isFree(position)) {
            if (getGhosts().size() < 2) {
                if (!ghosts.contains(position)) {
                    ghosts.add(position);
                    Logger.info("Valid move target");
                } else {
                    Logger.warn("Invalid move target: already moved");
                    throw new IllegalArgumentException();
                }
            } else {
                Logger.warn("Invalid move target: maximum move targets");
                throw new IllegalStateException();
            }
        } else {
            Logger.warn("Invalid move target: invalid target position");
            throw new IllegalCallerException();
        }
    }

    public void addSelection(Position position) {
        if (isOccupiedByCurrentPlayer(position)) {
            if (getSelected().size() < 2) {
                if (!getSelected().contains(position)) {
                    getSelected().add(position);
                    Logger.info("Valid selection");
                } else {
                    Logger.warn("Invalid selection: figure already selected");
                    throw new IllegalArgumentException();
                }
            } else {
                Logger.warn("Invalid selection: cannot select more figure");
                throw new IllegalStateException();
            }
        } else {
            Logger.warn("Invalid selection: not current player figure");
            throw new IllegalCallerException();
        }
    }

    /**
     * Sets the current player to the not current one.
     */
    public void nextPlayer() {
        currentPlayer = currentPlayer.other();
    }

    /**
     * Returns the current players positions.
     *
     * @return An array of positions.
     */
    public Position[] getCurrentPlayerPositions() {
        if (currentPlayer.equals(Player.RED)) {
            return redPositions;
        } else {
            return bluePositions;
        }
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    /**
     * Returns whether the current player is the winner, based on the positions of his figures.
     *
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
     *
     * @param position the position in question
     * @return whether {@code position} is owned by the current player.
     */
    public boolean isOccupiedByCurrentPlayer(Position position) {
        for (var figure : getCurrentPlayerPositions()) {
            if (position.equals(figure)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the position of the current player thats equals to the parameter {@code position}.
     *
     * @param position that we are looking for
     * @return a position of the current player
     * @throws IllegalArgumentException if position isnt owned by the current player
     */
    public Position getPositionAt(Position position) {

        for (var figure : getCurrentPlayerPositions()) {
            if (position.equals(figure)) {
                return figure;
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * Returns whether the owner of the {@link Position} is forbidden.
     *
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
     *
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
     *
     * @param position the position in question
     * @return whether {@code position} is owned by no one
     */
    public boolean isFree(Position position) {
        return !isBlue(position) && !isRed(position) && !isForbidden(position);
    }

    /**
     * Returns the current {@link Player}.
     *
     * @return the current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns whether the owner of the {@link Position} is blue.
     *
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
     *
     * @param position The position
     * @return The owner of the parameter {@code position}
     */
    public Owner owner(Position position) {
        if (isBlue(position)) {
            return Owner.BLUE;
        } else if (isRed(position)) {
            return Owner.RED;
        } else if (isForbidden(position)) {
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

    /**
     * Moves a {@link Position} in a {@link Direction}.
     *
     * @param direction the direction of the move
     * @param position  the initial position of the move.
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

    private void moveNorth(Position position) {
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
        mainSj.add(String.format("BOARD_SIZE: %d", BOARD_SIZE));
        mainSj.add(String.format("currentPlayer: %s", currentPlayer.toString()));

        return mainSj.toString();
    }
}
