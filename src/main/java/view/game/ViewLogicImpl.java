package view.game;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import controller.GameController;
import controller.PowerUpGameController;
import controller.PowerUpGameControllerImpl;
import controller.StandardGameController;
import controller.StandardGameControllerImpl;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import model.roundenvironment.barriers.Barrier;
import model.roundenvironment.barriers.Barrier.Orientation;
import model.roundenvironment.coordinate.Coordinate;
import model.roundenvironment.powerups.PowerUp;
import view.menu.MenuController;
import view.menu.MenuController.GameStatus;
import view.scenechanger.ScenesItem;

/**
 * The Logic implementation for controlling the view.
 *
 * @author Stefano D'Ambrosio
 *
 */
public class ViewLogicImpl implements ViewLogic{
	
	private GameController controller;
	
	private final ViewController view;

	private final Map<Coordinate, BorderPane> gridMap;

	private Optional<Integer> selectedBarrier;

	private String player1;
	private String player2;

	public ViewLogicImpl(final ViewController viewController) {
		this.view = viewController;
		if (MenuController.gameStatus.equals(GameStatus.NORMAL)
				|| MenuController.gameStatus.equals(GameStatus.LOADNORMAL)) {
			this.controller = new StandardGameControllerImpl(this);						
		} else if (MenuController.gameStatus.equals(GameStatus.POWERUP)
				|| MenuController.gameStatus.equals(GameStatus.LOADPOWERUP)) {
			this.controller = new PowerUpGameControllerImpl(this);				
		}
    	this.gridMap = new HashMap<Coordinate, BorderPane>();
    	this.selectedBarrier = Optional.empty();
	}
	
	/**
	 * Starts the game in the controller.
	 */
	public void startGame() {
	    switch(MenuController.gameStatus) {
		case LOADNORMAL:
			this.controller.loadGame();
			break;
		case LOADPOWERUP:
			this.controller.loadGame();
			break;
		case NORMAL:
			((StandardGameController) this.controller).newStandardGame(player1, player2);	    
			break;
		case POWERUP:
			((PowerUpGameController) this.controller).newPowerUpGame(player1, player2);	    	
			break;
		default:
			break;
	    }
	}
	
	/**
	 * Set the players nicknames.
	 * 
	 * @param Optional<Pair<String, String>> An optional containing a pair with nicknames
	 * 
	 */
	@Override
	public void setPlayer(final Optional<Pair<String, String>> result) {
		// If you leave it empty it automatically set default nicknames
    	if (result.get().getKey().equals("")) {
    		this.player1 = "Player 1";
    	} else {
    		this.player1 = result.get().getKey();    		
    	}
    	
    	if (result.get().getValue().equals("")) {
    		this.player2 = "Player 2";
    	} else {
    		this.player2 = result.get().getValue();    		
    	}
    	
    	// Nicknames can't be the same
    	if (player1.equals(player2)) {
    		final Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		alert.setHeaderText("ERROR");
    		alert.setContentText("You can't use the same name!");
    		alert.showAndWait();
    		this.view.returnToMainMenu();
    	}
    	this.view.setNicknames(player1, player2);
	}

	/**
	 * Adds the pane logic.
	 *
	 * @param position the position
	 * @return the pane to add to the grid
	 */
	@Override
	public BorderPane addPaneLogic(final Coordinate position) {
		final BorderPane pane = new BorderPane();
        pane.setOnMouseClicked(e -> {
        	this.setUpClickHandler(position);
        });
        this.gridMap.put(position, pane);        
		return pane;
	}
	

	/**
	 * Sets the up click handler.
	 *
	 * @param position the position in which the click handler will be setted
	 */
	@Override
	public void setUpClickHandler(final Coordinate position) {
        //System.out.printf("Mouse clicked cell " + position.toString() + "\n");
        if (this.selectedBarrier.isEmpty()) {
        	this.controller.movePlayer(position);
        } else {
        	if(this.selectedBarrier.get().equals(0)) {
        		this.controller.placeBarrier(position, Orientation.VERTICAL);
        		//System.out.printf("Barrier placement request: " + position.toString() + " Orientation: " + Orientation.VERTICAL + "\n");
        		
        	} else {
        		controller.placeBarrier(position, Orientation.HORIZONTAL);            		
        		//System.out.printf("Barrier placement request: " + position.toString() + " Orientation: " + Orientation.HORIZONTAL + "\n");
        	}
        	this.selectedBarrier = Optional.empty();
        }
	}
	
	/**
	 * Clears the grid and set player in the given coordinate, it also update barriers number
	 *
	 * @param player1pos the player 1 position
	 * @param player2pos the player 2 position
	 * @param barriersP1 the barrier number of player 1
	 * @param barriersP2 the barrier number of player 2
	 */
	public void setupGrid(final Coordinate player1pos, final Coordinate player2pos, int barriersP1, int barriersP2) {
		this.clearGrid();
		this.view.setPlayerInPane(this.gridMap.get(player1pos), this.player1);
		this.view.setPlayerInPane(this.gridMap.get(player2pos), this.player2);
		this.view.updateBarriersNumber(player1, barriersP1);
		this.view.updateBarriersNumber(player2, barriersP2);
	}
	
	/**
	 * Same as setup grid but with a list of barriers to draw in the grid.
	 *
	 * @param player1pos the player 1 position
	 * @param player2pos the player 2 position
	 * @param barriersP1 the barrier number of player 1
	 * @param barriersP2 the barrier number of player 2
	 * @param barrierList the barrier list
	 */
	public void setupGrid(final Coordinate player1pos, final Coordinate player2pos, final int barriersP1, final int barriersP2, final List<Barrier> barrierList) {
		this.setupGrid(player1pos, player2pos, barriersP1, barriersP2);
		this.drawBarriersOnLoad(barrierList);
	}
	
	/**
	 * Clears the grid.
	 */
	public void clearGrid() {
		this.gridMap.entrySet().forEach(e -> e.getValue().getChildren().remove(0, e.getValue().getChildren().size()));
	}
	
    /**
     * Move the player in the given position.
     *
     * @param position the position
     * @param player the player
     */
    public void move(final Coordinate position, final String player) {
    	this.view.setPlayerInPane(this.gridMap.get(position), player);
    	this.drawTextLogic("move");
    }
    
    /**
     * Change selected label.
     *
     * @param player the current player 
     */
    @Override
    public void changeSelectedLabel(final String player) {
    	this.view.changeSelectedLabel(player);
    }
    
    /**
     * Sets the selected barrier, it is used to get which type of barrier has been clicked
     *
     * @param type the selected barrier type, 
     */
    public void setSelectedBarrier(final String type) {
    	if (type.equals("vertical")) {
    		this.selectedBarrier = Optional.of(0);
    		this.drawTextLogic("verticalBarrier");
    	} else if (type.equals("horizontal")) {
    		this.selectedBarrier = Optional.of(1);
    		this.drawTextLogic("horizontalBarrier");
    	}
    }
    
    /**
     * Draw a barrier/
     *
     * @param barrier the barrier to draw
     */
    public void drawBarrier(final Barrier barrier) {
    	final BorderPane selected = this.gridMap.get(barrier.getCoordinate());
    	// barrier styling
    	final Rectangle verticalBarrier = new Rectangle();
    	verticalBarrier.getStyleClass().add("Barrier");
    	verticalBarrier.setFill(Color.ORANGE);
    	final Rectangle horizontalBarrier = new Rectangle();
    	horizontalBarrier.getStyleClass().add("Barrier");
    	horizontalBarrier.setFill(Color.ORANGE);
    	
    	if (barrier.getOrientation().equals(Orientation.HORIZONTAL)) {
    		selected.setBottom(horizontalBarrier);
    		BorderPane.setAlignment(horizontalBarrier, Pos.CENTER);
    		this.view.getHorizontalBarrierList().add(horizontalBarrier);
    	} else if (barrier.getOrientation().equals(Orientation.VERTICAL)) {
    		selected.setRight(verticalBarrier);
    		BorderPane.setAlignment(verticalBarrier, Pos.CENTER);
    		this.view.getVerticalBarrierList().add(verticalBarrier);
    	}
    	this.view.setCorrectSize();
    }
    
    /**
     * Draw barriers on load.
     *
     * @param barrierList the barrier list to draw
     */
    public void drawBarriersOnLoad(final List<Barrier> barrierList) {
    	for (Barrier barrier : barrierList) {
    		this.drawBarrier(barrier);
    	}
    }
    
    /**
     * Update barriers number.
     *
     * @param player the player
     * @param barriersNumber the barriers number
     */
    @Override
    public void updateBarriersNumber(final String player, final int barriersNumber) {
    	this.view.updateBarriersNumber(player, barriersNumber);
    }
    
    /**
     * Draw power ups.
     *
     * @param powerUpsAsList the power ups as list
     */
    @Override
    public void drawPowerUps(final List<PowerUp> powerUpsAsList) {
		for (final PowerUp p : powerUpsAsList) {
			switch (p.getType()) {
			case PLUS_ONE_MOVE:
				final ImageView doubleMoveIcon = new ImageView(new Image(this.getClass()
						.getResourceAsStream(ScenesItem.DOUBLEPUP.get())));
				this.view.drawPowerUp(this.gridMap.get(p.getCoordinate()), doubleMoveIcon);
				break;
			case PLUS_ONE_BARRIER:
				final ImageView plusOneBarrierIcon = new ImageView(new Image(this.getClass()
						.getResourceAsStream(ScenesItem.BARRIERPUP.get())));
				this.view.drawPowerUp(this.gridMap.get(p.getCoordinate()), plusOneBarrierIcon);
				break;
			default:
				break;
			}
		}
    }
    
    /**
     * Delete power up.
     *
     * @param p the powerUp to delete
     */
    public void deletePowerUp(final PowerUp p) {
    	final List<Node> toRemove = this.gridMap.get(p.getCoordinate()).getChildren().stream()
				.filter(e -> e.getClass().equals(ImageView.class))
				.collect(Collectors.toList()); 
		this.gridMap.get(p.getCoordinate()).getChildren().removeAll(toRemove);
    }
    
    
    /**
     * Calls the endRound method in view and also calls the controller method for changing round.
     *
     * @param winner the winner
     */
    public void endRound(final String winner) {
    	this.view.endRound(winner);
    	this.controller.nextRound();
    }
    
    /**
     * Calls the endGame method in view.
     *
     * @param winner the winner
     */
    public void endGame(final String winner) {
    	this.view.endGame(winner);
    }
    
    /**
     * Calls the saveGame method in controller.
     */
    @Override
    public void saveGame() {
    	this.controller.saveGame();
    }

	/**
	 * Draw text in the textAreas by calling the respective method in view. 
	 *
	 * @param textToDisplay the text to display
	 */
	@Override
	public void drawTextLogic(final String textToDisplay) {
    	final String start = "- Welcome to Quoridor! \n";
    	final String moveTutorial = "- Click on a cell to move the player\n"
    			+ "- Click on a barrier to place it\n"
    			+ "- You can jump over the other player when he is in front of you\n";
    	final String barrierTutorial = "To place a barrier, click on a cell: \n"
    			+ "- The vertical barrier will be placed right and in the cell below\n"
    			+ "- The horizontal barrier will be placed below and in the cell to the right\n";
    	final String verticalBarrierSelected = "Selected barrier: Vertical\n\n";
    	final String horizontalBarrierSelected = "Selected barrier: Horizontal\n\n";
    	
    	switch(textToDisplay) {
    	case "start" :
    		this.view.drawText(start);
    		this.view.appendText(moveTutorial);
    		break;
    	case "move" :
    		this.view.drawText(moveTutorial);
    		break;
    		case "verticalBarrier" :
    			this.view.drawText(verticalBarrierSelected);
    			this.view.appendText(barrierTutorial);		
    			break;
    		case "horizontalBarrier" :
    			this.view.drawText(horizontalBarrierSelected);
    			this.view.appendText(barrierTutorial);		
    			break;
    		default :
    			break;
    	}		
	}
}
