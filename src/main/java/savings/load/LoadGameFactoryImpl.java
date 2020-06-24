package savings.load;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import model.Model;
import model.ModelFactoryImpl;
import model.roundenvironment.RoundEnvironment;
import model.roundenvironment.RoundEnvironmentImpl;
import model.roundenvironment.RoundPUpEnvironment;
import model.roundenvironment.RoundPUpEnvironmentImpl;
import model.roundenvironment.barriers.RoundBarriers;
import model.roundenvironment.barriers.RoundBarriersImpl;
import model.roundenvironment.coordinate.Pair;
import model.roundenvironment.players.Player;
import model.roundenvironment.players.RoundPlayers;
import model.roundenvironment.players.RoundPlayersImpl;
import model.roundenvironment.powerups.RoundPowerUps;
import model.roundenvironment.powerups.RoundPowerUpsImpl;

public class LoadGameFactoryImpl implements LoadGameFactory{
	
	private LoadUtilities loadUtil = new LoadUtilities();
	
	@Override
	public LoadGame<RoundEnvironment> buildNormal() {
		return new LoadGame<RoundEnvironment>() {

			private Iterator<RoundEnvironment> iterator;
			private Model<RoundEnvironment> model;
			private boolean fileExist = loadUtil.fileExistNormal();

			@Override
			public void getData() {
				List<RoundEnvironment> roundEnvironments = new ArrayList<>();
				try {
					Pair<Player, Integer> currents = loadUtil.getCurrentRoundAndPlayer();
					//now i have to get for each roundEnvironment the things i need
					for(int i=0; i < 3; i++) {
						RoundPlayers players = new RoundPlayersImpl(loadUtil.getPlayersList(i));
						RoundBarriers barriers = new RoundBarriersImpl(loadUtil.getBarriers(i).getX(), loadUtil.getBarriers(i).getY());
						//set current player at the right round.
						if(i==currents.getY()) {
							players.setCurrentPlayer(currents.getX());
						}
						RoundEnvironment environment = new RoundEnvironmentImpl(barriers,players);
						roundEnvironments.add(environment);
					}
					//here i should create the model.
					iterator = roundEnvironments.iterator();
					iterator.next();
					//set the iterator to the current round.
					for(int i=0; i < currents.getY(); i++) {
						iterator.next();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				model = new ModelFactoryImpl().buildFromExisting(roundEnvironments, Model.BOARD_DIMENSION);
			}

			@Override
			public Iterator<RoundEnvironment> getIterator() {
				if(fileExist) {
					getData();
					return iterator;
				}
				return null;
			}

			@Override
			public Model<RoundEnvironment> getModel() {
				if(fileExist) {
					getData();
					return model;
				}
				return null;
			}

			@Override
			public boolean saveExist() {
				return fileExist;
			}
			
		};
	}

	@Override
	public LoadGame<RoundPUpEnvironment> buildPowerup() {
		return new LoadGame<RoundPUpEnvironment>() {
			
			private Iterator<RoundPUpEnvironment> iterator;
			private Model<RoundPUpEnvironment> model;
			private boolean fileExist = loadUtil.fileExistPowerUp();
			
			@Override
			public void getData() {
				List<RoundPUpEnvironment> roundEnvironments = new ArrayList<>();
				try {
					Pair<Player, Integer> currents = loadUtil.getCurrentRoundAndPlayer();
					//now i have to get for each roundEnvironment the things i need
					for(int i=0; i < 3; i++) {
						RoundPlayers players = new RoundPlayersImpl(loadUtil.getPlayersList(i));
						RoundBarriers barriers = new RoundBarriersImpl(loadUtil.getBarriers(i).getX(), loadUtil.getBarriers(i).getY());
						RoundPowerUps powerUps = new RoundPowerUpsImpl(loadUtil.getPowerUpList(i));
						//set current player at the right round.
						if(i==currents.getY()) {
							players.setCurrentPlayer(currents.getX());
						}
						RoundPUpEnvironment environment = new RoundPUpEnvironmentImpl(barriers,players, powerUps);
						roundEnvironments.add(environment);
					}
					//here i should create the model.
					iterator = roundEnvironments.iterator();
					iterator.next();
					//set the iterator to the current round.
					for(int i=0; i < currents.getY(); i++) {
						iterator.next();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				model = new ModelFactoryImpl().buildFromExisting(roundEnvironments, Model.BOARD_DIMENSION);
			}

			@Override
			public Iterator<RoundPUpEnvironment> getIterator() {
				if(fileExist) {
					getData();
					return iterator;
				}
				return null;
			}

			@Override
			public Model<RoundPUpEnvironment> getModel() {
				if(fileExist) {
					getData();
					return model;
				}
				return null;
			}

			@Override
			public boolean saveExist() {
				return fileExist;
			}
			
		};
	}

}