package app;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {
    public static void show(Stage ownerStage, String toastMsg, boolean isError) {
        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setStyle("-fx-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 13px;");
        
        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: " + (isError ? "#D32F2F;" : "#388E3C;") + " -fx-padding: 10px 20px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.fillProperty().set(Color.TRANSPARENT);
        toastStage.setScene(scene);
        
        if (ownerStage != null && ownerStage.isShowing()) {
            toastStage.setX(ownerStage.getX() + ownerStage.getWidth() / 2 - 150);
            toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - 120);
        }
        
        toastStage.show();

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(300), new javafx.animation.KeyValue(root.opacityProperty(), 0.9));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        
        Timeline fadeOutTimeline = new Timeline();
        KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(300), new javafx.animation.KeyValue(root.opacityProperty(), 0));
        fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
        fadeOutTimeline.setOnFinished(e -> toastStage.close());

        fadeInTimeline.setOnFinished(e -> {
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ex) { ex.printStackTrace(); }
                fadeOutTimeline.play();
            }).start();
        });
        
        fadeInTimeline.play();
    }
}