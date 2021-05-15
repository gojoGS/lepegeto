package lepegeto.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class LepegetoApplication extends Application {

    @Inject
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws Exception {
        fxmlLoader.setLocation(getClass().getResource("/fxml/gui.xml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Jatek");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
