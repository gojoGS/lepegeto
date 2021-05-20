package lepegeto.javafx.controller;

import jakarta.xml.bind.JAXBException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lepegeto.model.*;
import lepegeto.results.GameResult;
import lepegeto.results.ResultManager;
import lombok.SneakyThrows;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Controller class of the game UI.
 */
public class GameController {
    @FXML
    private GridPane gameBoard;

    @FXML
    private TextField currentPlayer;

    @FXML
    private TextField messageTextField;

    private GameState gameState;

    /**
     * Initializes a game with a default state.
     */
    @FXML
    public void initialize() {
        initialize(new GameState());
    }

    /**
     * Initializes a game with a given state.
     *
     * @param state the state of the game after initialization.
     */
    public void initialize(GameState state) {
        gameState = state;
        gameBoard.getChildren().clear();

        for (int i = 0; i < gameBoard.getRowCount(); i++) {
            for (int j = 0; j < gameBoard.getColumnCount(); j++) {
                var square = createSquare(gameState.owner(new Position(i, j)));
                gameBoard.add(square, j, i);
            }
        }
        clearMessage();

        Logger.info("Controller state initialized");
    }

    public void setPlayerNames(HashMap<Player, String> players) {
        gameState.setPlayers(players);
        setCurrentPlayerText();
    }

    private void setMessage(String message) {
        messageTextField.setText(message);
        Logger.info("New message set");
    }

    private void clearMessage() {
        messageTextField.clear();
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

        square.setOnMouseClicked(this::onMouseClick);

        square.getChildren().add(piece);
        return square;
    }

    private Position getPositionOfEvent(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var figure = (Circle) square.getChildren().get(0);

        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);

        return new Position(row, col);
    }

    private Color getGhostColor() {
        Color color;
        switch (gameState.getCurrentPlayer()) {
            case RED -> color = Color.web("fb4934");
            case BLUE -> color = Color.web("83a598");
            default -> throw new IllegalArgumentException();
        }

        return color;
    }

    private Color getColor() {
        Color color;
        switch (gameState.getCurrentPlayer()) {
            case RED -> color = Color.web("cc241d");
            case BLUE -> color = Color.web("458588");
            default -> throw new IllegalArgumentException();
        }

        return color;
    }

    private Color getDarkColor() {
        Color color;
        switch (gameState.getCurrentPlayer()) {
            case RED -> color = Color.web("9d0006");
            case BLUE -> color = Color.web("076678");
            default -> throw new IllegalArgumentException();
        }

        return color;
    }

    private Circle getFigureOfEvent(MouseEvent event) {
        var square = (StackPane) event.getSource();
        return (Circle) square.getChildren().get(0);
    }

    private void onLeftClick(MouseEvent event) {
        Position position = getPositionOfEvent(event);
        Logger.info(String.format("Player clicked on position %s", position.toString()));

        try {
            gameState.addSelection(position);
            getFigureOfEvent(event).setFill(getDarkColor());
        } catch(IllegalArgumentException e) {
            setMessage("You've already selected that one");
            Logger.warn("Invalid selection: figure already selected");
        } catch(IllegalStateException e) {
            setMessage("You shall not select more!");
            Logger.warn("Invalid selection: cannot select more figure");
        } catch(IllegalCallerException e) {
            setMessage("Not yours to command, that one!");
            Logger.warn("Invalid selection: not current player figure");
        }

    }

    private void onRightClick(MouseEvent event) {
        Position position = getPositionOfEvent(event);
        Logger.info(String.format("Player clicked on position %s", position.toString()));

        try{
            gameState.addGhost(position);
            getFigureOfEvent(event).setFill(getGhostColor());
            Logger.info("Valid move target");
        } catch(IllegalArgumentException e) {
            setMessage("You've already moved that one!");
            Logger.warn("Invalid move target: already moved");
        } catch(IllegalStateException e) {
            setMessage("You shall not move more!");
            Logger.warn("Invalid move target: maximum move targets");
        } catch(IllegalCallerException e) {
            setMessage("You shall not go there!");
            Logger.warn("Invalid move target: invalid target position");
        }
    }

    private void onMouseClick(MouseEvent event) {
        clearMessage();
        switch (event.getButton()) {
            case PRIMARY -> onLeftClick(event);
            case SECONDARY -> onRightClick(event);
        }
    }

    private StackPane getSquareByPosition(int row, int col) {
        for (var square : gameBoard.getChildren()) {
            if (square instanceof StackPane) {
                if (GridPane.getColumnIndex(square) == col && GridPane.getRowIndex(square) == row) {
                    return (StackPane) square;
                }
            }
        }

        Logger.error(String.format("Invalid coordinates: row=%d col=%d", row, col));
        throw new IllegalArgumentException();
    }

    private StackPane getSquareByPosition(Position position) {
        return getSquareByPosition(position.getRow(), position.getCol());
    }

    private void reincarnate() {
        for (var pos : gameState.getGhosts()) {
            var square = getSquareByPosition(pos.getRow(), pos.getCol());
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(Color.TRANSPARENT);
        }
    }

    private void deselect() {
        for (var pos : gameState.getSelected()) {
            var square = getSquareByPosition(pos.getRow(), pos.getCol());
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(getColor());
        }
    }

    private String getCurrentPlayerName() {
        return gameState.getPlayers().get(gameState.getCurrentPlayer());
    }

    private void setCurrentPlayerText() {
        currentPlayer.setText(String.format("%s's turn", getCurrentPlayerName()));
    }

    private void repaintOnMove(ArrayList<Position> selected, ArrayList<Position> ghosts) {
        gameState.nextPlayer();
        for (var p : selected) {
            var square = getSquareByPosition(p);
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(Color.TRANSPARENT);
        }

        for (var p : ghosts) {
            var square = getSquareByPosition(p);
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(getColor());
        }
        gameState.nextPlayer();
    }

    /**
     * resetSelectionButton handler.
     *
     * @param event the event object of the action
     */
    public void onResetSelection(ActionEvent event) {
        Logger.info("resetSelectionButton clicked");

        reincarnate();
        deselect();

        gameState.clearSelection();
    }

    /**
     * endTurnButton handler.
     *
     * @param event the event object of the action
     */
    public void onEndTurn(ActionEvent event) {
        Logger.info("endTurnButton clicked");
        var selected = (ArrayList<Position>) gameState.getSelected().clone();
        var ghosts = (ArrayList<Position>) gameState.getGhosts().clone();
        try {
            var won = gameState.endTurn();

            repaintOnMove(selected, ghosts);
            if (won) {
                Logger.info("A player has won the game");
                onYield(event);
            } else {
                Logger.info("Next turn");
                setCurrentPlayerText();
                clearMessage();
            }
        } catch(IllegalArgumentException e) {
            setMessage("Please, make your moves!");
            Logger.warn("Not enough moves");
        } catch(IllegalStateException e) {
            setMessage("Invalid move! Try something else!");
            Logger.warn("Invalid move");
        }
    }

    /**
     * saveGameButton handler.
     *
     * @param event the event object of the action
     */
    public void onSaveGame(ActionEvent event) {
        Logger.info("saveGameButton clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.xml"));
        File selected = fileChooser.showSaveDialog(gameBoard.getScene().getWindow());

        if (selected != null) {
            try {
                util.jaxb.JAXBHelper.toXML(gameState, new FileOutputStream(selected));
                Logger.info(String.format("File saved at %s", selected.getAbsolutePath()));
            } catch (JAXBException | FileNotFoundException e) {
                setMessage("Error while saving file.");
                Logger.error("Error while saving file.");
            }
        } else {
            Logger.warn("File not selected");
        }
    }

    /**
     * loadGameButton handler.
     *
     * @param event the event object of the action
     */
    public void onLoadGame(ActionEvent event) {
        Logger.info("loadGameButton clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.xml"));
        File selected = fileChooser.showOpenDialog(gameBoard.getScene().getWindow());

        if (selected != null) {
            try {
                var newState = util.jaxb.JAXBHelper.fromXML(GameState.class, new FileInputStream(selected));
                gameState.clearSelection();
                initialize(newState);
                Logger.info(String.format("Gamestate loaded from %s", selected.getAbsolutePath()));
            } catch (JAXBException | FileNotFoundException e) {
                setMessage("Error while loading file.");
                Logger.error("Error while loading file.");
            }
        } else {
            Logger.warn("File not selected");
        }
    }

    /**
     * saveAndExitButton handler.
     *
     * @param event the event object of the action
     */
    public void onSaveAndExit(ActionEvent event) {
        Logger.info("saveAndExitButton clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.xml"));
        File selected = fileChooser.showSaveDialog(gameBoard.getScene().getWindow());

        if (selected != null) {
            try {
                util.jaxb.JAXBHelper.toXML(gameState, new FileOutputStream(selected));
                Logger.info("Exiting from Game UI");
                Platform.exit();
            } catch (JAXBException | FileNotFoundException e) {
                setMessage("Error while saving file.");
                Logger.error("Error while saving file.");
            }
        } else {
            Logger.warn("File not selected");
        }
    }

    /**
     * exitButton handler.
     *
     * @param event the event object of the action
     */
    public void onExit(ActionEvent event) {
        Logger.info("exitButton clicked");
        Logger.info("Exiting from Game UI");
        Platform.exit();
    }

    /**
     * yieldButton handler.
     *
     * @param event the event object of the action
     */
    @SneakyThrows
    public void onYield(ActionEvent event) {
        Logger.info("yieldButton clicked");
        gameState.nextPlayer();

        ResultManager resultManager = new ResultManager();
        resultManager.insert(createGameResult());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ending.fxml"));
        Parent root = fxmlLoader.load();

        var controller = (EndingController) fxmlLoader.getController();
        controller.setWinner(getCurrentPlayerName(), gameState.getNumberOfTurns());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        Logger.info("Screen switched to endscreen");
    }

    private GameResult createGameResult() {
        return GameResult.builder()
                .player1(gameState.getPlayers().get(Player.BLUE))
                .player2(gameState.getPlayers().get(Player.RED))
                .winner(getCurrentPlayerName())
                .steps(gameState.getNumberOfTurns())
                .created(ZonedDateTime.now())
                .build();
    }

    /**
     * resetButton handler.
     *
     * @param event the event object of the action
     */
    public void onReset(ActionEvent event) {
        Logger.info("resetButton clicked");
        gameState.clearSelection();
        initialize();
        Logger.info("Gamestate reset done");
    }

}
