package lepegeto.state;

/**
 * Represents a player.
 */
public enum Player {
    BLUE,
    RED;

    public static Player other(Player player) {
        if(player.equals(RED)) {
            return BLUE;
        } else {
            return RED;
        }
    }

    @Override
    public String toString() {
        if(this.equals(BLUE)) {
            return "Blue Player";
        } else {
            return "Red Player";
        }
    }

}
