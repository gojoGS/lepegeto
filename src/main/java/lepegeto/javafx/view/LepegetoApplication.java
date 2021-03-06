package lepegeto.javafx.view;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.AbstractModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import javax.inject.Inject;
import java.util.List;

/**
 * A class that represents the JavaFX application.
 */
public class LepegetoApplication extends Application {


    @Inject
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/opening.fxml"));
        stage.setTitle("Jatek");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
