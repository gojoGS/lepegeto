package lepegeto.javafx.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lepegeto.results.GameResult;
import lepegeto.results.ResultManager;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Controller class for the end screen of the game.
 */
public class EndingController {

    @FXML
    private TextField winnerTextField;

    @FXML
    private TableView<GameResult> highScoreTable;

    @FXML
    private TableColumn<GameResult, String> winner;

    @FXML
    private TableColumn<GameResult, String> player1;

    @FXML
    private TableColumn<GameResult, String> player2;

    @FXML
    private TableColumn<GameResult, Integer> steps;

    @FXML
    private TableColumn<GameResult, ZonedDateTime> created;

    @FXML
    private void initialize() {
        Logger.debug("Loading high scores...");
        var manager = new ResultManager();
        List<GameResult> highScoreList = manager.fetch();

        winner.setCellValueFactory(new PropertyValueFactory<>("winner"));
        player1.setCellValueFactory(new PropertyValueFactory<>("player1"));
        player2.setCellValueFactory(new PropertyValueFactory<>("player2"));
        steps.setCellValueFactory(new PropertyValueFactory<>("steps"));
        created.setCellValueFactory(new PropertyValueFactory<>("created"));

        created.setCellFactory(column -> {
            TableCell<GameResult, ZonedDateTime> cell = new TableCell<GameResult, ZonedDateTime>() {
                private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
                @Override
                protected void updateItem(ZonedDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    } else {
                        setText(item.format(formatter));
                    }
                }
            };
            return cell;
        });

        ObservableList<GameResult> observableResult = FXCollections.observableArrayList();
        observableResult.addAll(highScoreList);

        highScoreTable.setItems(observableResult);
    }

    /**
     * Sets {@code winnerTextField}'s value to the given parameter.
     *
     * @param name the namw of the winner.
     * @param turns number of turns
     */
    public void setWinner(String name, int turns) {
        winnerTextField.setText(String.format("%s is triumphant after %d turns!", name, turns));
    }

    /**
     * exitButton handler.
     *
     * @param event the event object of the action
     */
    public void onExit(ActionEvent event) {
        Logger.info("Player has exited from endscreen");
        Platform.exit();
    }

    /**
     * mainMenuButton handler.
     *
     * @param event the event object of the action
     * @throws IOException if fmxl file not found or IO related error happens.
     */
    public void onMainMenu(ActionEvent event) throws IOException {
        var stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/opening.fxml"));
        stage.setTitle("Jatek");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
        Logger.info("Player has switched back to Main Menu from end screen");
    }
}
