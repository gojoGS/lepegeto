package lepegeto.state;

public enum Player {
    BLUE,
    RED;

    public static Player other(Player player) {
        if(player == RED) {
            return BLUE;
        } else {
            return RED;
        }
    }

}
