package app;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML private ImageView originalImageView;
    @FXML private ImageView processedImageView;
    @FXML private Button btnLoad, btnSave, btnRotateLeft, btnRotateRight, btnScale, btnExecute;
    @FXML private ComboBox<String> operationsComboBox;

    private Image originalImage = null;
    private Image processedImage = null;
    private boolean hasChanges = false;

    @FXML
    public void initialize() {
        operationsComboBox.setItems(FXCollections.observableArrayList("Negatyw", "Progowanie", "Konturowanie"));
        operationsComboBox.setValue(null);
    }

    private Stage getStage() {
        return (Stage) btnLoad.getScene().getWindow();
    }

    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik obrazu JPG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Obrazy JPG (*.jpg)", "*.jpg"));
        
        File selectedFile = fileChooser.showOpenDialog(getStage());
        
        if (selectedFile != null) {
            if (!selectedFile.getName().toLowerCase().endsWith(".jpg")) {
                Toast.show(getStage(), "Niedozwolony format pliku", true);
                return;
            }

            try {
                if (originalImage != null) {
                    originalImageView.setImage(null);
                    processedImageView.setImage(null);
                    originalImage = null;
                    processedImage = null;
                    System.gc();
                }

                originalImage = new Image(selectedFile.toURI().toString());
                
                if (originalImage.isError()) {
                    throw new Exception("Błąd ładowania obrazu");
                }

                processedImage = originalImage; 
                originalImageView.setImage(originalImage);
                processedImageView.setImage(processedImage);
                hasChanges = false;

                setControlsDisabled(false);
                Toast.show(getStage(), "Pomyślnie załadowano plik", false);

            } catch (Exception e) {
                Toast.show(getStage(), "Nie udało się załadować pliku", true);
            }
        }
    }

    @FXML
    private void handleSaveImage() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(getStage());
        modalStage.setTitle("Zapisz obraz");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        if (!hasChanges) {
            Label alertLabel = new Label("Na pliku nie zostały wykonane żadne operacje!");
            alertLabel.setStyle("-fx-text-fill: #E65100; -fx-background-color: #FFE0B2; -fx-padding: 5; -fx-border-color: #B26A00;");
            layout.getChildren().add(alertLabel);
        }

        Label promptLabel = new Label("Podaj nazwę pliku (bez rozszerzenia):");
        TextField txtFileName = new TextField();
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        Button btnConfirmSave = new Button("Zapisz");
        Button btnCancelSave = new Button("Anuluj");
        btnBox.getChildren().addAll(btnConfirmSave, btnCancelSave);

        layout.getChildren().addAll(promptLabel, txtFileName, errorLabel, btnBox);
        modalStage.setScene(new Scene(layout, 350, 200));

        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                txtFileName.setText(oldValue);
            }
        });

        btnCancelSave.setOnAction(e -> {
            txtFileName.clear();
            modalStage.close();
        });

        btnConfirmSave.setOnAction(e -> {
            String name = txtFileName.getText().trim();
            if (name.length() < 3) {
                errorLabel.setText("Wpisz co najmniej 3 znaki");
                return;
            }

            modalStage.close();
            executeFileSystemSave(name);
        });

        modalStage.showAndWait();
    }

   private void executeFileSystemSave(String fileNameWithoutExt) {
        String fullFileName = fileNameWithoutExt.replaceAll("\\..*$", "") + ".jpg";
        
        String userHome = System.getProperty("user.home");
        File targetFile = new File(userHome + File.separator + "Desktop", fullFileName);

        if (targetFile.exists()) {
            Toast.show(getStage(), "Plik " + fullFileName + " już istnieje na Pulpicie. Podaj inną nazwę!", true);
            return;
        }

        try {
            
            BufferedImage fXImage = SwingFXUtils.fromFXImage(processedImage, null);
            if (fXImage == null) throw new IOException();
            
         
            BufferedImage bImage = new BufferedImage(fXImage.getWidth(), fXImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = bImage.createGraphics();
            g.drawImage(fXImage, 0, 0, null);
            g.dispose();
            
            boolean success = ImageIO.write(bImage, "jpg", targetFile);
            if (success) {
                Toast.show(getStage(), "Zapisano na Pulpicie jako " + fullFileName, false);
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            Toast.show(getStage(), "Nie udało się zapisać pliku " + fullFileName, true);
        }
    }

    @FXML
    private void handleScaleImage() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(getStage());
        modalStage.setTitle("Skaluj obraz");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        TextField txtWidth = new TextField(String.valueOf((int)processedImage.getWidth()));
        TextField txtHeight = new TextField(String.valueOf((int)processedImage.getHeight()));
        
        Label lblWidthError = new Label(); lblWidthError.setStyle("-fx-text-fill: red;");
        Label lblHeightError = new Label(); lblHeightError.setStyle("-fx-text-fill: red;");

        addNumericValidation(txtWidth);
        addNumericValidation(txtHeight);

        Button btnReset = new Button("Przywróć oryginalne wymiary");
        btnReset.setOnAction(e -> {
            txtWidth.setText(String.valueOf((int)originalImage.getWidth()));
            txtHeight.setText(String.valueOf((int)originalImage.getHeight()));
        });

        HBox btnBox = new HBox(10);
        Button btnConfirm = new Button("Zmień rozmiar");
        Button btnCancel = new Button("Anuluj");
        btnBox.getChildren().addAll(btnConfirm, btnCancel);

        layout.getChildren().addAll(
                new Label("Szerokość (0-3000 px):"), txtWidth, lblWidthError,
                new Label("Wysokość (0-3000 px):"), txtHeight, lblHeightError,
                btnReset, btnBox
        );

        modalStage.setScene(new Scene(layout, 300, 320));

        btnCancel.setOnAction(e -> modalStage.close());

        btnConfirm.setOnAction(e -> {
            lblWidthError.setText("");
            lblHeightError.setText("");
            boolean isValid = true;

            if (txtWidth.getText().isEmpty()) { lblWidthError.setText("Pole jest wymagane"); isValid = false; }
            if (txtHeight.getText().isEmpty()) { lblHeightError.setText("Pole jest wymagane"); isValid = false; }

            if (!isValid) return;

            int w = Integer.parseInt(txtWidth.getText());
            int h = Integer.parseInt(txtHeight.getText());

            if (w <= 0 || w > 3000) { lblWidthError.setText("Wymiar poza zakresem 1-3000"); isValid = false; }
            if (h <= 0 || h > 3000) { lblHeightError.setText("Wymiar poza zakresem 1-3000"); isValid = false; }

            if (!isValid) return;

            WritableImage scaledImg = new WritableImage(w, h);
            PixelReader reader = processedImage.getPixelReader();
            PixelWriter writer = scaledImg.getPixelWriter();

            double srcWidth = processedImage.getWidth();
            double srcHeight = processedImage.getHeight();

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int srcX = (int) Math.min(Math.floor(x * srcWidth / w), srcWidth - 1);
                    int srcY = (int) Math.min(Math.floor(y * srcHeight / h), srcHeight - 1);
                    writer.setArgb(x, y, reader.getArgb(srcX, srcY));
                }
            }

            processedImage = scaledImg;
            processedImageView.setImage(processedImage);
            hasChanges = true;
            modalStage.close();
        });

        modalStage.showAndWait();
    }

    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void handleRotateLeft() {
        rotateImage(false);
    }

    @FXML
    private void handleRotateRight() {
        rotateImage(true);
    }

    private void rotateImage(boolean clockwise) {
        int w = (int) processedImage.getWidth();
        int h = (int) processedImage.getHeight();
        WritableImage rotatedImg = new WritableImage(h, w);
        PixelReader reader = processedImage.getPixelReader();
        PixelWriter writer = rotatedImg.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = reader.getArgb(x, y);
                if (clockwise) {
                    writer.setArgb(h - 1 - y, x, argb);
                } else {
                    writer.setArgb(y, w - 1 - x, argb);
                }
            }
        }
        processedImage = rotatedImg;
        processedImageView.setImage(processedImage);
        hasChanges = true;
    }

    @FXML
    private void handleExecuteOperation() {
        String selectedOp = operationsComboBox.getValue();
        
        if (selectedOp == null) {
            Toast.show(getStage(), "Nie wybrano operacji do wykonania", true);
            return;
        }

        switch (selectedOp) {
            case "Negatyw" -> executeNegatyw();
            case "Progowanie" -> openProgowanieModal();
            case "Konturowanie" -> executeKonturowanie();
        }
    }

    private void executeNegatyw() {
        try {
            int w = (int) processedImage.getWidth();
            int h = (int) processedImage.getHeight();
            WritableImage target = new WritableImage(w, h);
            PixelReader r = processedImage.getPixelReader();
            PixelWriter wtr = target.getPixelWriter();

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = r.getColor(x, y);
                    Color inverted = new Color(1.0 - c.getRed(), 1.0 - c.getGreen(), 1.0 - c.getBlue(), c.getOpacity());
                    wtr.setColor(x, y, inverted);
                }
            }
            processedImage = target;
            processedImageView.setImage(processedImage);
            hasChanges = true;
            Toast.show(getStage(), "Negatyw został wygenerowany pomyślnie!", false);
        } catch (Exception e) {
            Toast.show(getStage(), "Nie udało się wykonać negatywu.", true);
        }
    }

    private void openProgowanieModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(getStage());
        modalStage.setTitle("Parametry progowania");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        
        Label lblInfo = new Label("Wpisz wartość progu (0-255):");
        TextField txtThreshold = new TextField("128");
        addNumericValidation(txtThreshold);
        
        Label lblError = new Label(); lblError.setStyle("-fx-text-fill: red;");

        HBox btns = new HBox(10);
        Button btnRun = new Button("Wykonaj progowanie");
        Button btnCancel = new Button("Anuluj");
        btns.getChildren().addAll(btnRun, btnCancel);

        layout.getChildren().addAll(lblInfo, txtThreshold, lblError, btns);
        modalStage.setScene(new Scene(layout, 280, 180));

        btnCancel.setOnAction(e -> modalStage.close());

        btnRun.setOnAction(e -> {
            if (txtThreshold.getText().isEmpty()) {
                lblError.setText("Pole wymagane");
                return;
            }
            int thresh = Integer.parseInt(txtThreshold.getText());
            if (thresh < 0 || thresh > 255) {
                lblError.setText("Próg musi być w zakresie 0-255");
                return;
            }

            modalStage.close();
            executeProgowanie(thresh);
        });

        modalStage.showAndWait();
    }

    private void executeProgowanie(int threshold) {
        try {
            int w = (int) processedImage.getWidth();
            int h = (int) processedImage.getHeight();
            WritableImage target = new WritableImage(w, h);
            PixelReader r = processedImage.getPixelReader();
            PixelWriter wtr = target.getPixelWriter();

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = r.getColor(x, y);
                    double gray = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
                    double scaledThresh = threshold / 255.0;

                    Color resColor = (gray >= scaledThresh) ? Color.WHITE : Color.BLACK;
                    wtr.setColor(x, y, resColor);
                }
            }
            processedImage = target;
            processedImageView.setImage(processedImage);
            hasChanges = true;
            Toast.show(getStage(), "Progowanie zostało przeprowadzone pomyślnie!", false);
        } catch (Exception e) {
            Toast.show(getStage(), "Nie udało się wykonać progowania.", true);
        }
    }

    private void executeKonturowanie() {
        try {
            int w = (int) processedImage.getWidth();
            int h = (int) processedImage.getHeight();
            WritableImage target = new WritableImage(w, h);
            PixelReader r = processedImage.getPixelReader();
            PixelWriter wtr = target.getPixelWriter();

            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    double c = getGrayscale(r.getColor(x, y));
                    double left = getGrayscale(r.getColor(x - 1, y));
                    double right = getGrayscale(r.getColor(x + 1, y));
                    double up = getGrayscale(r.getColor(x, y - 1));
                    double down = getGrayscale(r.getColor(x, y + 1));

                    double edge = Math.abs(4 * c - left - right - up - down);
                    edge = Math.min(1.0, edge * 5.0);
                    Color edgeColor = new Color(1.0 - edge, 1.0 - edge, 1.0 - edge, 1.0);

                    wtr.setColor(x, y, edgeColor);
                }
            }
            processedImage = target;
            processedImageView.setImage(processedImage);
            hasChanges = true;
            Toast.show(getStage(), "Konturowanie zostało przeprowadzone pomyślnie!", false);
        } catch (Exception e) {
            Toast.show(getStage(), "Nie udało się wykonać konturowania.", true);
        }
    }

    private double getGrayscale(Color c) {
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }

    private void setControlsDisabled(boolean disabled) {
        btnSave.setDisable(disabled);
        btnRotateLeft.setDisable(disabled);
        btnRotateRight.setDisable(disabled);
        btnScale.setDisable(disabled);
        btnExecute.setDisable(disabled);
    }
}