package nz.ac.auckland.se206.words;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CanvasController;
import nz.ac.auckland.se206.SpreadSheetReaderWriter;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;

public class WordPageController {

  @FXML private Text wordToDraw;
  @FXML private Button readyButton;
  @FXML private Button newButton;
  @FXML private Label textToSpeechLabel;
  @FXML private Label userLabel;
  @FXML private ImageView volumeImage;
  @FXML private ImageView newImage;

  private String currentWord;
  private Boolean textToSpeech;
  private TextToSpeechBackground textToSpeechBackground;

  private String currentUsername = null;

  /** Picks a random word from the easy category using category selector */
  private void setWordToDraw() throws IOException, URISyntaxException, CsvException {
    if (currentUsername == null) { // if guest
      CategorySelector categorySelector = new CategorySelector(); // picks random easy word
      currentWord = categorySelector.getRandomCategory(CategorySelector.Difficulty.E);
    } else { // if user chosen from their pool of words left
      SpreadSheetReaderWriter spreadSheetReaderWriter = new SpreadSheetReaderWriter();
      currentWord = spreadSheetReaderWriter.findWordsLeft(currentUsername);
    }
    wordToDraw.setText(currentWord);
  }

  public void give(TextToSpeechBackground textToSpeechBackground, Boolean textToSpeech) {
    this.textToSpeechBackground = textToSpeechBackground;
    this.textToSpeech = textToSpeech;
    if (textToSpeech) { // checks if text to speech was previously enabled then will make this page
      // mirror
      textToSpeechLabel.setText("ON");
    }
  }

  public void getUsername(String username) throws IOException, URISyntaxException, CsvException {
    // Check if username is not null
    if (username != null) {
      // If not null, update label as current username
      currentUsername = username;
      userLabel.setText(currentUsername);
    } else {
      userLabel.setText("Guest");
    }

    setWordToDraw();
  }

  @FXML
  private void onNewWord() throws IOException, URISyntaxException, CsvException {
    // Get new word
    setWordToDraw();
  }

  @FXML
  private void onHoverNew() {
    newImage.setFitHeight(57);
    newImage.setFitWidth(57);
  }

  @FXML
  private void onHoverTitle() {
    textToSpeechBackground.backgroundSpeak("You have 60 seconds to draw a ...", textToSpeech);
  }

  @FXML
  private void onHoverWord() {
    textToSpeechBackground.backgroundSpeak(currentWord, textToSpeech);
  }

  @FXML
  private void onHoverReady() {
    textToSpeechBackground.backgroundSpeak("Ready Button", textToSpeech);
    readyButton.setStyle(
        "-fx-background-radius: 15px; -fx-border-radius: 15px; -fx-background-color: #99F4B3;");
  }

  @FXML
  private void onExitReady() {
    readyButton.setStyle(
        "-fx-background-radius: 25px; -fx-border-radius: 25px; -fx-background-color: white;");
  }

  @FXML
  private void onHoverTextToSpeechLabel() {
    textToSpeechBackground.backgroundSpeak("ON", textToSpeech);
  }

  @FXML
  private void onTextToSpeech() {
    textToSpeech = !textToSpeech; // toggles text to speech
    if (textToSpeech) { // sets label to correct value
      textToSpeechLabel.setText("ON");
    } else {
      textToSpeechLabel.setText("OFF");
    }
  }

  @FXML
  private void onHoverTextToSpeech() {
    textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
    volumeImage.setFitHeight(48);
    volumeImage.setFitWidth(48);
  }

  @FXML
  private void onReady() throws IOException {
    Stage stage =
        (Stage) readyButton.getScene().getWindow(); // uses the ready button to fine the stage
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/canvas.fxml"));
    Scene scene = new Scene(loader.load(), 1000, 680);
    stage.setScene(scene);
    stage.show();
    CanvasController canvasController =
        loader.getController(); // gets the newly created controller for next page
    canvasController.setWordLabel(
        currentWord); // passes the current word so that the next screen can display it
    canvasController.give(
        textToSpeechBackground, textToSpeech); // passes the background threaded text to speech
    // and whether it is on or not
    canvasController.getUsername(currentUsername);
  }

  // Below is list of methods for when mouse exits a button

  @FXML
  private void onVolumeExit() {
    volumeImage.setFitHeight(45);
    volumeImage.setFitWidth(45);
  }

  @FXML
  private void onNewExit() {
    newImage.setFitHeight(55);
    newImage.setFitWidth(55);
  }
}
