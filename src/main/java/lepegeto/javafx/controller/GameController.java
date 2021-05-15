package lepegeto.javafx.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lepegeto.model.GameState;
import lepegeto.model.Owner;
import lepegeto.model.Position;

public class GameController {
    @FXML
    private GridPane gameBoard;

    @FXML
    private Button resetButton;

    @FXML
    private Button yield;

    @FXML
    private Button endTurnButton;

    @FXML
    private Label currentPlayer;

    private GameState gameState;

    @FXML
    public void initialize() {
        gameState = new GameState();

        for (int i = 0; i < gameBoard.getRowCount(); i++) {
            for (int j = 0; j < gameBoard.getColumnCount(); j++) {
                var square = createSquare(gameState.owner(new Position(i, j)));
                gameBoard.add(square, j, i);
            }
        }
    }

    private StackPane createSquare(Owner owner) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        var piece = new Circle(40);
        piece.setFill(Color.TRANSPARENT);

        switch (owner) {
            case RED -> piece.setFill(Color.web("#cc241d"));
            case BLUE -> piece.setFill(Color.web("#458588"));
            case FORBIDDEN -> square.setBackground(new Background(new BackgroundFill(Color.web("#282828"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        square.getChildren().add(piece);
        return square;
    }

}
