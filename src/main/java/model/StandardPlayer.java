package model;

import java.util.List;

public interface StandardPlayer {
	
	/**
	 * enum describing all possible move direction.
	 * @author luca
	 *
	 */
	public enum Direction{
		LEFT, RIGHT, TOP, DOWN
	}
	
	/**
	 * enum describing possible move types.
	 * @author luca
	 *
	 */
	public enum Type{
		NORMAL, JUMP
	}
	
	/**
	 * 
	 * @return the name of the player.
	 */
	String getNickname();

	/**
	 * 
	 * @return number of the remaining available barriers.
	 */
	Integer getRemainingBarriers();
	
	/**
	 * 
	 * @return the current position of the player.
	 */
	Pair<Integer, Integer> getPosition();
	
	/**
	 * decreases the available barriers number.
	 */
	void placeBarrier();
	
	/**
	 * 
	 * @return true if this player wins.
	 */
	boolean isWinner();
	
	/**
	 * sets the X position of the finish line 
	 */
	//void setXFinishLine(Integer x);
	
	/**
	 * this method at the click of the current player, communicates to the view all possible
	 * moves it can do.
	 * @param positions 
	 */
	void setPossibleMoves(List<Pair<Integer, Integer>> positions);
	
	
}
