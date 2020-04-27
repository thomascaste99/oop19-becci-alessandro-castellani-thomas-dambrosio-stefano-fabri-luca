package controllers;

import java.io.IOException;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import jdk.jshell.spi.ExecutionControl.*;
import viewmenu.SceneBuilder;
import viewmenu.SceneBuilderImpl;
import viewmenu.SceneChanger;
import viewmenu.SceneChangerImpl;

public class MenuController {
	
	@FXML
    public void exitButtonPressHandler() {
        System.exit(0);
    }

	 @FXML
	 public void newGameButtonPressHandler(ActionEvent event) throws IOException {
		 SceneChanger sceneChange = new SceneChangerImpl(event);
		 sceneChange.change("layouts/main/scene.fxml", "Quoridor2D - Game");
	 }
	 
	 @FXML
	 public void loadGameButtonPressHandler() throws NotImplementedException {
		 throw new NotImplementedException("Not Implemented Yet");
	 }
	 
	 @FXML
	 public void leaderboardButtonPressHandler() throws NotImplementedException {
		 throw new NotImplementedException("Not Implemented Yet");
	 }
	 
}
