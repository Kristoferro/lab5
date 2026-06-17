package app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("Laboratorium 6");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Launcher.main(args);
    }
}

class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}