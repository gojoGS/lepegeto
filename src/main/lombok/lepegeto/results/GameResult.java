package lepegeto.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Class representing the result of a game played by a specific player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GameResult {

    private String winner;
    /**
     * The name of the player.
     */
    private String player1;
    private String player2;

    /**
     * The number of steps made by the player.
     */
    private int steps;

    /**
     * The timestamp when the result was saved.
     */
    private ZonedDateTime created;

}