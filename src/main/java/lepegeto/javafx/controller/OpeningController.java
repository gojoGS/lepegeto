package lepegeto.javafx.controller;

import jakarta.xml.bind.JAXBException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lepegeto.model.GameState;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Controller class of the opening scene.
 */
public class OpeningController {
    @FXML
    private Button exitButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button loadGameButton;

    /**
     * newGameButton handler.
     *
     * @param event the event object of the action
     * @throws IOException
     */
    public void onNewGame(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

        Logger.info("Switched to game UI");
    }

    /**
     * loadGameButton handler.
     *
     * @param event the event object of the action
     * @throws IOException if savefile not found
     */
    public void onLoadGame(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.xml"));
        File selected = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selected != null) {
            try {
                var state = util.jaxb.JAXBHelper.fromXML(GameState.class, new FileInputStream(selected));

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
                Parent root = fxmlLoader.load();

                var controller = (GameController) fxmlLoader.getController();
                controller.initialize(state);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                Logger.info("Player loaded the game from Main Menu");

            } catch (JAXBException | FileNotFoundException e) {
                e.printStackTrace();
                Logger.error("Error has occured in Main Menu while reading and initializing new stage and gamestate");
            }
        } else {
            Logger.info("File not selected");
        }
    }

    /**
     * exitButton handler.
     */
    public void onExit() {
        Logger.info("Player exited the game from Main Menu");
        Platform.exit();
    }
}
