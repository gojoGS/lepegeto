package lepegeto.state;

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

}
