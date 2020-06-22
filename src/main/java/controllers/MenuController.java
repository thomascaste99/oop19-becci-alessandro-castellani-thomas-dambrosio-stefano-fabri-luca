package controllers;

import java.util.Optional;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import savings.LoadGame;
import viewmenu.SceneChanger;
import viewmenu.SceneChangerImpl;
import viewmenu.ScenesItem;
/**
 * This is the @FXML controller of the Menu.
 * @author Alessandro Becci
 *
 */
public class MenuController {
	
	@FXML private BorderPane rootPane;
	@FXML private Button newGameButton;
	@FXML private Button loadGameButton;
	@FXML private Button leaderBoardButton;
	@FXML private Button exitGameButton;
	@FXML private Label title;
	
	public static boolean to_load;
	public static boolean powerup_game;
	private SceneChanger sceneChange;
	private Stage stage = Main.STAGE;
	private LoadGame loading;
	
	//I need this changeListener for setting labels and buttons size while resizing.
	private ChangeListener<Number> changeListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			String styleButtons = "-fx-font-size:" + newValue.doubleValue()/40 + ";"; 
			String styleLabel = "-fx-font-size:" + newValue.doubleValue()/13 + ";";
			newGameButton.setStyle(styleButtons);
			loadGameButton.setStyle(styleButtons);
			leaderBoardButton.setStyle(styleButtons);
			exitGameButton.setStyle(styleButtons);
			title.setStyle(styleLabel);
		}
		
	};
	
	
	public void initialize() {
		//setting on the stage the changeListener for resizing
		stage.widthProperty().addListener(changeListener);
	}
	
	@FXML
    public void exitButtonPressHandler() {
        System.exit(0);
    }
	
	
	 @FXML
	 public void newGameButtonPressHandler(ActionEvent event) {
		 //removing the listener before starting a game(exiting from menu)
		 stage.widthProperty().removeListener(changeListener);
		 //static variable to tell UIController to NOT load the game
		 to_load = false;
		 
		//setting up an alert for gameType
		 Alert alert = new Alert(AlertType.CONFIRMATION);
	     alert.setTitle("Choose your game type !");
	     alert.setHeaderText("Game Type choice");
	     alert.setContentText("Choose your option");
	     alert.getDialogPane().setStyle("-fx-background-color: #2B2D42; -fx-fill: #FFFFFF;");
	     // Set the icon 
	     ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add
	   		(new Image(this.getClass().getResourceAsStream("/logo/logo.png")));
	     
	     ButtonType buttonTypeOne = new ButtonType("Normal");
	     ButtonType buttonTypeTwo = new ButtonType("PowerUp");
	     ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	     alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
		 Optional<ButtonType> result = alert.showAndWait();
		 if (result.get() == buttonTypeOne){
		     powerup_game = false;
		 } else if (result.get() == buttonTypeTwo) {
		     powerup_game = true;
		 } else {
		     powerup_game = false;
		 }
		 //changing scene with Game
		 sceneChange = new SceneChangerImpl();
		 sceneChange.change(ScenesItem.GAME.get(), ScenesItem.GAMETITLE.get());
	 }
	 
	 @FXML
	 public void loadGameButtonPressHandler() {
		 loading = new LoadGame();
		 //setting static variable for loading(UIController)
		 if(loading.saveExist()) { to_load = true; } else { to_load = false; }
		 //changing scene with Game
		 sceneChange = new SceneChangerImpl();
		 sceneChange.change(ScenesItem.GAME.get(), ScenesItem.GAMETITLE.get());
	 }
	 
	 @FXML
	 public void leaderboardButtonPressHandler() {
		//removing the listener before going in a game(exiting from menu)
		 stage.widthProperty().removeListener(changeListener);
		 //changing scene with leaderBoard
		 sceneChange = new SceneChangerImpl();
		 sceneChange.change(ScenesItem.LEADERBOARD.get(), ScenesItem.LEADERBOARDTITLE.get());
	 }
	 
}
