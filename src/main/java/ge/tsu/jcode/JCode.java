package ge.tsu.jcode;

import ge.tsu.jcode.fxml.AbstractController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class JCode extends Application {

    public void start(Stage stage) throws IOException {
        stage.setTitle("JCode");
        Parent mainNode = loadFxml(stage, "main.fxml");
        double[] widthHeight = getScreenDimensions();
        // Make width and height 80% of the screen by default
        Scene scene = new Scene(mainNode, widthHeight[0] * .8, widthHeight[1] * .8);
        setStylesheet(scene);
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadFxml(Stage stage, String resourcePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try (InputStream inputStream = JCode.class.getResourceAsStream(resourcePath)) {
            Parent parent = fxmlLoader.load(inputStream);
            AbstractController controller = fxmlLoader.getController();
            controller.initStage(stage);
            return parent;
        }
    }

    private double[] getScreenDimensions() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new double[]{screenSize.getWidth(), screenSize.getHeight()};
    }

    private void setStylesheet(Scene scene) {
        scene.getStylesheets().add(
                this.getClass().getResource("/ge/tsu/jcode/style.css").toExternalForm()
        );
    }

    public static void main(String[] args) {
        launch();
    }
}
