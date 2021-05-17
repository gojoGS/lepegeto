package lepegeto.javafx.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lepegeto.model.Player;
import org.tinylog.Logger;

import java.io.IOException;

/**
 * Controller class for the end screen of the game.
 */
public class EndingController {

    @FXML
    private TextField winnerTextField;

    /**
     * Sets {@code winnerTextField}'s value to the given parameter.
     * @param winner the color of the winner.
     */
    public void setWinner(Player winner) {
        winnerTextField.setText(String.format("%s is triumphant!", winner.toString()));
    }

    /**
     * exitButton handler.
     * @param event the event object of the action
     */
    public void onExit(ActionEvent event) {
        Logger.info("Player has exited from endscreen");
        Platform.exit();
    }

    /**
     * mainMenuButton handler.
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
