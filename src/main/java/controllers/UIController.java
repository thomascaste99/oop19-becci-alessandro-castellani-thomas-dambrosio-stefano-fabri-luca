package controllers;

import application.Main;

import controller.StandardGameControllerImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.roundenvironment.barriers.Barrier;
import model.roundenvironment.barriers.Barrier.Orientation;
import model.roundenvironment.coordinate.Coordinate;
import viewmenu.SceneBuilder;
import viewmenu.SceneBuilderImpl;
import viewmenu.SceneChanger;
import viewmenu.SceneChangerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The Controller related to the scene.fxml GUI.
 *
 */
public final class UIController{
    
    @FXML private GridPane grid;
    
    @FXML private Label label1;
    @FXML private Label label2;
    @FXML private TextArea textArea1;
    @FXML private TextArea textArea2;
    
    @FXML private MenuItem retToMainMenu;
    
    @FXML private MenuItem exit;
    
    @FXML private Rectangle player1vertical;
    @FXML private Rectangle player1horizontal;
    @FXML private Rectangle player2vertical;
    @FXML private Rectangle player2horizontal;
    
    @FXML private Label barriersNumber1;
    @FXML private Label barriersNumber2;
    
    private Circle bluePlayer;
	private Circle redPlayer;
	
	private StandardGameControllerImpl controller;
	
	private Optional<String> player1;
	private Optional<String> player2;
	
	//0 for vertical, 1 for horizontal
	private Optional<Integer> selectedBarrier;
	
	private Map<Coordinate, BorderPane> gridMap;
	
	public UIController() {
		this.controller = new StandardGameControllerImpl(this);
	}
	
	public void initialize() {
    	System.out.println("Initializing...");
    	
    	// Dialog setup
    	Dialog<Pair<String, String>> dialog = new Dialog<>();
    	dialog.setHeaderText("Choose your nicknames");

    	// Set the icon 
    	//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

    	ButtonType startButton = new ButtonType("Start", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(startButton, ButtonType.CANCEL);
    	
    	GridPane dialogGrid = new GridPane();
    	dialogGrid.setHgap(10);
    	dialogGrid.setVgap(10);
    	dialogGrid.setPadding(new Insets(20, 150, 10, 10));

    	TextField player1name = new TextField();
    	player1name.setPromptText("Player 1");
    	TextField player2name = new TextField();
    	player2name.setPromptText("Player 2");
    	
    	dialogGrid.add(new Label("Nickname for player 1:"), 0, 0);
    	dialogGrid.add(player1name, 1, 0);
    	dialogGrid.add(new Label("Nickname for player 2:"), 0, 1);
    	dialogGrid.add(player2name, 1, 1);

    	dialog.getDialogPane().setContent(dialogGrid);

    	dialog.setResultConverter(dialogButton -> {
    	    if (dialogButton == startButton) {
    	    	return new Pair<String, String>(player1name.getText(),player2name.getText());
    	    }
    	    return null;
    	});

    	Optional<Pair<String, String>> result = dialog.showAndWait();
    	
    	this.player1 = Optional.of(result.get().getKey());
    	this.player2 = Optional.of(result.get().getValue());
	
    	
    	// Grid setup
    	int numCols = 9;
    	int numRows = 9;
    	
    	gridMap = new HashMap<Coordinate, BorderPane>();
    	
    	bluePlayer = new Circle(25);
    	bluePlayer.getStyleClass().add("BluePlayer");
    	redPlayer = new Circle(25);
    	redPlayer.getStyleClass().add("RedPlayer");
    	label1.setText(player1.get());
    	label2.setText(player2.get());
    	label1.getStyleClass().add("SelectedLabel");
    	label2.getStyleClass().add("Label");
  
	    for (int i = 0 ; i < numCols ; i++) {
	        for (int j = 0; j < numRows; j++) {
	            addPane(i, j);
	            System.out.println("pane added" + i + j);
	        }
	    }
	    
	    //Starts the game
	    this.controller.newStandardGame(this.player1.get(), this.player2.get());
	}
    
    private void addPane(int colIndex, int rowIndex) {
        BorderPane pane = new BorderPane();
        Coordinate position = new Coordinate(colIndex, rowIndex);
        pane.setOnMouseClicked(e -> {
            System.out.printf("Mouse clicked cell " + position.toString() + "\n");
            if(this.selectedBarrier.isEmpty()) {
            	controller.movePlayer(position);
            } else {
            	if(this.selectedBarrier.get().equals(0)) {
            		controller.placeBarrier(position, Orientation.VERTICAL);
            		System.out.printf("Barrier placement request: " + position.toString() + " Orientation: " + Orientation.VERTICAL + "\n");
            		
            	} else {
            		controller.placeBarrier(position, Orientation.HORIZONTAL);            		
            		System.out.printf("Barrier placement request: " + position.toString() + " Orientation: " + Orientation.HORIZONTAL + "\n");
            	}
            	this.selectedBarrier = Optional.empty();
            }
        });
        pane.getStyleClass().add("GridBorderPane");
        grid.add(pane, position.getX(), position.getY());
        gridMap.put(position, pane);     
        
        this.selectedBarrier = Optional.empty();
    }
    
    public void setupGrid(Coordinate player1pos, Coordinate player2pos) {
    	System.out.println(player1pos);
    	System.out.println(player2pos);
    	gridMap.get(player1pos).setCenter(bluePlayer);
    	gridMap.get(player2pos).setCenter(redPlayer);
    }
    
    public void startGame(String player1, String player2) {
    	System.out.println("Game started");
    	this.player1 = Optional.of(player1);
    	this.player2 = Optional.of(player2);
    }
    
    public void move(Coordinate position, String player) {
    	if(player.equals(this.player1.get())) {
    		gridMap.get(position).getChildren().add(bluePlayer);
    		BorderPane.setAlignment(redPlayer, Pos.CENTER);
    	} else {
    		gridMap.get(position).getChildren().add(redPlayer);
    		BorderPane.setAlignment(redPlayer, Pos.CENTER);
    	}
    }
    
    public void barrierPlacement(MouseEvent event) {
    	if (event.getSource().equals(player1vertical) || event.getSource().equals(player2vertical)) {
    		this.selectedBarrier = Optional.of(0);
    		this.drawText(0);
    	} else {
    		this.selectedBarrier = Optional.of(1);
    		this.drawText(1);
    	}
    }
    
    public void drawBarrier(Barrier barrier, String player) {
    	BorderPane selected = this.gridMap.get(barrier.getCoordinate());
    	Pair<Double, Double> barrierSize = new Pair<>(selected.getWidth()/10, selected.getHeight()/10);
    	Rectangle verticalBarrier = new Rectangle(barrierSize.getKey(), barrierSize.getValue()*8);
    	verticalBarrier.getStyleClass().add("Barrier");
    	Rectangle horizontalBarrier = new Rectangle(barrierSize.getKey()*8, barrierSize.getValue());
    	horizontalBarrier.getStyleClass().add("Barrier");
    	if (barrier.getOrientation().equals(Orientation.HORIZONTAL)) {
    		if (player.equals(this.player1.get())) {
    			horizontalBarrier.setFill(Color.BLUE);
    			selected.setBottom(horizontalBarrier);
    			BorderPane.setAlignment(horizontalBarrier, Pos.CENTER);
    		} else {
    			horizontalBarrier.setFill(Color.RED);
    			selected.setBottom(horizontalBarrier);	
    			BorderPane.setAlignment(horizontalBarrier, Pos.CENTER);
    		}
    	} else {
    		if (player.equals(this.player1.get())) {
    			verticalBarrier.setFill(Color.BLUE);
    			selected.setRight(verticalBarrier);
    			BorderPane.setAlignment(verticalBarrier, Pos.CENTER);
    		} else {
    			verticalBarrier.setFill(Color.RED);
    			selected.setRight(verticalBarrier);
    			BorderPane.setAlignment(verticalBarrier, Pos.CENTER);
    		}
    	}	
    }
    
    public void updateBarriersNumber(String player, int barriersNumber) {
    	if (player.equals(player1.get())) {
    		this.barriersNumber1.setText(String.valueOf(barriersNumber));
    	} else {
    		this.barriersNumber2.setText(String.valueOf(barriersNumber));
    	}
    }
    
    public void changeSelectedLabel(String player) { 	
    	if (player.equals(player1.get())) {
    		label2.getStyleClass().clear();
    		label2.getStyleClass().add("SelectedLabel");
    		label1.getStyleClass().clear();
    		label1.getStyleClass().add("Label");
    	} else {
    		label1.getStyleClass().clear();
    		label1.getStyleClass().add("SelectedLabel");   		
    		label2.getStyleClass().clear();
    		label2.getStyleClass().add("Label");
    	}
    	
    }
    
    public void endRound(String winner) {
    	for(Entry<Coordinate, BorderPane> p : this.gridMap.entrySet()) {
    		if (p.getValue().getChildren().contains(bluePlayer)) {
    			p.getValue().getChildren().remove(bluePlayer);
    		}
    		if (p.getValue().getChildren().contains(redPlayer)) {
    			p.getValue().getChildren().remove(redPlayer);
    		}
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("We have a winner!");
    	alert.setHeaderText(winner + " won the round!");
    	alert.setContentText("");
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	
    	this.controller.nextRound();
    }
    
    public void endGame(String winner) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("We have a winner!");
    	alert.setHeaderText(winner + " won the game!");
    	alert.setContentText("Do you want to return to the main menu?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    try {
				this.returnToMainMenu();
			} catch (IOException e) {
				System.exit(0);
			}
    	} else {
    	    System.exit(0);
    	}
    }
    
    private void drawText(int player) {
    	String barrierTutorial = "Per posizionare la barriera, clicca la casella dove vuoi posizionarla: \n"
    			+ "- La barriera verticale sara` posizionata a destra e nella cassela in basso\n"
    			+ "- La barriera orizzontale sara` piazzata in basso e nella casella a destra";
    	if (player == 0) {
    		this.textArea1.setText(barrierTutorial);
    	} else {
    		this.textArea2.setText(barrierTutorial);  		
    	}
    			
    }

    /**
     * A method that handles the return to the main menu.
     */ 
    @FXML
    public void returnToMainMenu() throws IOException{
    	SceneChanger sceneChange = new SceneChangerImpl();
    	sceneChange.change("layouts/menu/MainMenu.fxml", "Game");
    }
    
    /**
     * A method that handles the exit of the application.
     */
     @FXML
     public void exitToDesktop(ActionEvent event) {
    	 System.exit(0);
     }
}


