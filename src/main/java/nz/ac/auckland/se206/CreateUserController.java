package nz.ac.auckland.se206;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nz.ac.auckland.se206.speech.TextToSpeechBackground;
import nz.ac.auckland.se206.words.CategorySelector;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

public class CreateUserController {
	// Set name of the file to set user data
	private static final String fileName = "userdata.csv";

	@FXML
	private Button backButton;
	@FXML
	private Button mainmenuButton;
	@FXML
	private Button createButton;
	@FXML
	private TextField usernameText;
	@FXML
	private ImageView volumeImage;
	@FXML
	private ImageView selectedProfile;
	@FXML
	private Label outputLabel;
	@FXML
	private Label textToSpeechLabel;
	@FXML
	private Label profileLabel;

	private String currentUsername = null; // The username currently logged in
	private String currentProfile = null;
	private Boolean textToSpeech;
	private TextToSpeechBackground textToSpeechBackground;

	/**
	 * This method ensures all parameters are valid before a profile is created
	 * 
	 * @param username
	 */
	private void createProfile(String username) {
		String[] profile = new String[17];

		try {
			FileWriter csvwriter = new FileWriter(fileName, true);
			try (CSVWriter writer = new CSVWriter(csvwriter)) {
				// Check if username exists
				if (!searchUsername(username)) {
					// Check if a profile picture is selected
					if (currentProfile != null) {
						this.currentUsername = username;
						usernameText.clear();
						usernameText.setPromptText("Hi, " + username);

						outputLabel.setText("Logged in as " + username);
						outputLabel.setStyle("-fx-text-fill: green;");
						outputLabel.setOpacity(0.5);

						// create profile
						profile[0] = username;

						// adds all the easy words to the csv
						CategorySelector category = new CategorySelector();
						profile[1] = category.getCategory(Difficulty.E).toString();
						profile[2] = "0"; // number of wins
						profile[3] = "0"; // number of losses
						profile[4] = "100"; // fastest time
						profile[5] = null; // history words
						profile[6] = "0"; // Largest streak
						profile[7] = "0"; // Current streak
						profile[8] = "0"; // wins on easy
						profile[9] = "0"; // wins on medium
						profile[10] = "0"; // wins on hard
						profile[11] = "0"; // wins on master
						profile[12] = "60"; // users last time selection
						profile[13] = "1"; // users last word selection
						profile[14] = "1"; // users last confidence selection
						profile[15] = "3"; // users last accuracy selection
						profile[16] = currentProfile; // User's profile picture

						writer.writeNext(profile);

						// Set current username
						currentUsername = username;
					} else {
						// Set label to show profile picture is not selected
						outputLabel.setText("You havn't selected a profile picture");
						outputLabel.setStyle("-fx-text-fill: red;");
						outputLabel.setOpacity(0.5);
					}
				} else {
					// Set label to show username is invalid
					usernameText.clear();
					outputLabel.setText("Invalid Username");
					outputLabel.setStyle("-fx-text-fill: red;");
					outputLabel.setOpacity(0.5);
				}
			} catch (URISyntaxException | CsvException e) {
				throw new RuntimeException(e);
			}

			csvwriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method searches in the csv file for the username passed and returns a
	 * flag
	 *
	 * @param username the name of the person using the app
	 * @return if name was found or not
	 * @throws IOException if file name was empty
	 */
	private static Boolean searchUsername(String username) throws IOException {
		boolean flag = false;
		FileReader fr;

		fr = new FileReader(fileName); // starts a file reader to scan the spread sheet for the username
		BufferedReader br = new BufferedReader(fr);

		String line;
		while ((line = br.readLine()) != null) {
			// Check if current line contains the username to be found
			String[] record = line.split(",");
			String tempUsername = record[0]; // username is stored in first pos of array
			tempUsername = tempUsername.substring(1, (tempUsername.length() - 1));

			if (username.equals(tempUsername)) {
				flag = true;
			}
		}

		br.close();
		return flag;
	}

	public void setUsername(String username) {
		// Set current username
		currentUsername = username;
	}

	public void give(TextToSpeechBackground textToSpeechBackground, Boolean textToSpeech) {
		this.textToSpeech = textToSpeech;
		this.textToSpeechBackground = (textToSpeechBackground);
		if (textToSpeech) { // updates text to speech label to ensure it is up-to-date
			textToSpeechLabel.setText("ON");
		}
	}

	/**
	 * This method creates a new user
	 * 
	 */
	@FXML
	private void onCreate() {
		String username = usernameText.getText(); // retrieves name

		if (usernameText.getText().trim().isEmpty()) { // checks if there was an input
			outputLabel.setText("The textbox is empty");
			outputLabel.setStyle("-fx-text-fill: red;");
			outputLabel.setOpacity(0.5);
		} else {
			createProfile(username); // creates user
		}
	}

	/**
	 * This method goes to the previous page
	 *
	 * @throws IOException if name of file is not found
	 */
	@FXML
	private void onBack() throws IOException, CsvException {
		Stage stage = (Stage) backButton.getScene().getWindow();
		LoadPage loadPage = new LoadPage();
		loadPage.extractedLogin(textToSpeechBackground, textToSpeech, currentUsername, stage);
	}

	/**
	 * This method goes to the main menu
	 *
	 * @throws IOException if name of file is not found
	 */
	@FXML
	private void onMainMenu() throws IOException, CsvException {
		Stage stage = (Stage) backButton.getScene().getWindow();
		LoadPage loadPage = new LoadPage();
		loadPage.extractedMainMenu(textToSpeechBackground, textToSpeech, currentUsername, stage);
	}

	/**
	 * This method toggles the text to speech
	 * 
	 */
	@FXML
	private void onTextToSpeech() {
		textToSpeech = !textToSpeech; // inverts boolean of text to speech
		if (textToSpeech) { // sets label accordingly
			textToSpeechLabel.setText("ON");
		} else {
			textToSpeechLabel.setText("OFF");
		}
	}

	// Profile picture button methods
	@FXML
	private void onProfileOne() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic1.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic1.png";
		profileLabel.setText("HUMAN 1");
	}

	@FXML
	private void onProfileTwo() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic2.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic2.png";
		profileLabel.setText("HUMAN 2");
	}

	@FXML
	private void onProfileThree() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic3.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic3.png";
		profileLabel.setText("HUMAN 3");
	}

	@FXML
	private void onProfileFour() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic4.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic4.png";
		profileLabel.setText("WIZARD");
	}

	@FXML
	private void onProfileFive() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic5.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic5.png";
		profileLabel.setText("SUPERHUMAN");
	}

	@FXML
	private void onProfileSix() {
		File file = new File("src/main/resources/images/ProfilePics/ProfilePic6.png");
		Image image = new Image(file.toURI().toString());
		selectedProfile.setImage(image);
		currentProfile = "src/main/resources/images/ProfilePics/ProfilePic6.png";
		profileLabel.setText("DUCK");
	}

	// Below is a list of methods when mouse hovers a button
	@FXML
	private void onHoverTextToSpeech() {
		textToSpeechBackground.backgroundSpeak("On", textToSpeech);
		volumeImage.setFitHeight(48);
		volumeImage.setFitWidth(48);
	}

	@FXML
	private void onHoverTextToSpeechLabel() {
		textToSpeechBackground.backgroundSpeak("toggle text to speech", textToSpeech);
	}

	@FXML
	private void onHoverBack() {
		textToSpeechBackground.backgroundSpeak("Back", textToSpeech);
		backButton.setStyle(
				"-fx-background-radius: 100px; -fx-text-fill: white; -fx-border-radius: 100px; -fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
	}

	@FXML
	private void onHoverMainMenu() {
		textToSpeechBackground.backgroundSpeak("Main Menu", textToSpeech);
		mainmenuButton.setStyle(
				"-fx-background-radius: 100px;-fx-text-fill: white; -fx-border-radius: 100px; -fx-background-color: #99DAF4; -fx-border-color: #99DAF4;");
	}

	@FXML
	private void onHoverCreate() {
		textToSpeechBackground.backgroundSpeak("Create", textToSpeech);
		createButton.setStyle(
				"-fx-background-radius: 10; -fx-background-color: #EB4A5A; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 0.5;");
	}

	// Below is a list of methods when mouse exits a button
	@FXML
	private void onVolumeExit() {
		volumeImage.setFitHeight(45);
		volumeImage.setFitWidth(45);
	}

	@FXML
	private void onBackExit() {
		backButton.setStyle(
				"-fx-background-radius: 100px; -fx-background-color: #EB4A5A; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 100px;");
	}

	@FXML
	private void onMainMenuExit() {
		mainmenuButton.setStyle(
				"-fx-background-radius: 100px;-fx-background-color: #EB4A5A; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 100px;");
	}

	@FXML
	private void onCreateExit() {
		createButton.setStyle(
				"-fx-background-radius: 10; -fx-background-color: #EB4A5A; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 10; -fx-border-width: 3; -fx-opacity: 1;");
	}
}
