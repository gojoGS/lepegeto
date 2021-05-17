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
import lepegeto.model.Direction;
import lepegeto.model.GameState;
import lepegeto.model.Owner;
import lepegeto.model.Position;
import lombok.SneakyThrows;

import org.tinylog.Logger;

import java.io.*;
import java.util.ArrayList;


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

    private ArrayList<Position> selected;
    private ArrayList<Position> ghosts;

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
     * @param state the state of the game after initialization.
     */
    public void initialize(GameState state) {
        gameState = state;
        gameBoard.getChildren().clear();
        setCurrentPlayerText();

        for (int i = 0; i < gameBoard.getRowCount(); i++) {
            for (int j = 0; j < gameBoard.getColumnCount(); j++) {
                var square = createSquare(gameState.owner(new Position(i, j)));
                gameBoard.add(square, j, i);
            }
        }

        selected = new ArrayList<Position>();
        ghosts = new ArrayList<Position>();

        clearMessage();

        Logger.info("Controller state initialized");
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
        switch (gameState.getCurrentPlayer()){
            case RED -> color = Color.web("fb4934");
            case BLUE -> color = Color.web("83a598");
            default -> throw new IllegalArgumentException();
        }

        return color;
    }

    private Color getColor() {
        Color color;
        switch (gameState.getCurrentPlayer()){
            case RED -> color = Color.web("cc241d");
            case BLUE -> color = Color.web("458588");
            default -> throw new IllegalArgumentException();
        }

        return color;
    }

    private Color getDarkColor() {
        Color color;
        switch (gameState.getCurrentPlayer()){
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

        if(gameState.isOccupiedByCurrentPlayer(position)) {
            if(selected.size() < 2) {
                if(!selected.contains(position)) {
                    getFigureOfEvent(event).setFill(getDarkColor());
                    selected.add(position);
                    Logger.info("Valid selection");
                } else {
                    setMessage("You've already selected that one");
                    Logger.warn("Invalid selection: figure already selected");
                }
            } else {
                setMessage("You shall not select more!");
                Logger.warn("Invalid selection: cannot select more figure");
            }
        } else {
            setMessage("Not yours to command, that one!");
            Logger.warn("Invalid selection: not current player figure");
        }
    }

    private void onRightClick(MouseEvent event) {
        Position position = getPositionOfEvent(event);
        Logger.info(String.format("Player clicked on position %s", position.toString()));

        if(gameState.isFree(position)) {
            if(ghosts.size() < 2) {
                if(!ghosts.contains(position)) {
                    getFigureOfEvent(event).setFill(getGhostColor());
                    ghosts.add(position);
                    Logger.info("Valid move target");
                } else {
                    setMessage("You've already moved that one!");
                    Logger.warn("Invalid move target: already moved");
                }
            } else {
                setMessage("You shall not move more!");
                Logger.warn("Invalid move target: maximum move targets");
            }
        } else {
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
        for(var pos: ghosts) {
            var square = getSquareByPosition(pos.getRow(), pos.getCol());
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(Color.TRANSPARENT);
        }
    }

    private void deselect() {
        for(var pos: selected) {
            var square = getSquareByPosition(pos.getRow(), pos.getCol());
            var figure = (Circle) square.getChildren().get(0);
            figure.setFill(getColor());
        }
    }

    private void setCurrentPlayerText() {
        currentPlayer.setText(String.format("%s's turn", gameState.getCurrentPlayer().toString()));
    }

    private boolean isValidSelection() {
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

    private Direction getSelectionDirection() {
        Position selection1 = selected.get(0);
        Position ghost1 = ghosts.get(0);

        return Direction.of(ghost1.getRow() - selection1.getRow(), ghost1.getCol() - selection1.getCol());
    }

    private void repaintOnMove() {
        for(var p: selected) {
            var square = getSquareByPosition(p);
            var figure = (Circle)square.getChildren().get(0);
            figure.setFill(Color.TRANSPARENT);
        }

        for(var p: ghosts) {
            var square = getSquareByPosition(p);
            var figure = (Circle)square.getChildren().get(0);
            figure.setFill(getColor());
        }
    }

    private void moveSelected(Direction direction) {
        for(var pos: selected) {
            Position newPosition = gameState.getPositionAt(pos);
            gameState.move(direction, newPosition);
        }
    }

    private void clearSelection() {
        ghosts.clear();
        selected.clear();
    }

    /**
     * resetSelectionButton handler.
     * @param event the event object of the action
     */
    public void onResetSelection(ActionEvent event) {
        Logger.info("resetSelectionButton clicked");

        reincarnate();
        deselect();

        clearSelection();
    }

    /**
     * endTurnButton handler.
     * @param event the event object of the action
     */
    public void onEndTurn(ActionEvent event) {
        Logger.info("endTurnButton clicked");

        if(ghosts.size() != 2 && selected.size() != 2) {
            setMessage("Please, make your moves!");
            Logger.warn("Not enough moves");
            return;
        }

        if(isValidSelection()) {
            moveSelected(getSelectionDirection());
            repaintOnMove();
            clearSelection();

            gameState.nextPlayer();
            if(gameState.isCurrentPlayerWinner()) {
                Logger.info("A player has won the game");
                onYield(event);
            } else {
                Logger.info("Next turn");
                setCurrentPlayerText();
                clearMessage();
            }
        } else {
            setMessage("Invalid move! Try something else!");
            Logger.warn("Invalid move");
        }

    }

    /**
     * saveGameButton handler.
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
               clearSelection();
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
     * @param event the event object of the action
     */
    public void onExit(ActionEvent event) {
        Logger.info("exitButton clicked");
        Logger.info("Exiting from Game UI");
        Platform.exit();
    }

    /**
     * yieldButton handler.
     * @param event the event object of the action
     */
    @SneakyThrows
    public void onYield(ActionEvent event)  {
        Logger.info("yieldButton clicked");
        gameState.nextPlayer();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ending.fxml"));
        Parent root = fxmlLoader.load();

        var controller = (EndingController)fxmlLoader.getController();
        controller.setWinner(gameState.getCurrentPlayer());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        Logger.info("Screen switched to endscreen");
    }

    /**
     * resetButton handler.
     * @param event the event object of the action
     */
    public void onReset(ActionEvent event) {
        Logger.info("resetButton clicked");
        clearSelection();
        initialize();
        Logger.info("Gamestate reset done");
    }

}
