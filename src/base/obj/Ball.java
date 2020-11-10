package base.obj;

import java.util.List;

import base.Log;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle {
	
	private final static Track NULL_TRACK = null;
	
	private int index = 0;
	private int column;
	private int row;
	private int delay;
	private String color;
	private List<Track> tracks;
	private Track currentTrack;
	
	private double[][] finalTrackPath;
	private int finalCounter = 0;
	private int finalDirection;
	private boolean finalStation = false;
	
	
	public Ball(double[] xy, Color fill, List<Track> tracks, int delay) {
		this((int)xy[0], (int)xy[1], 10, fill, tracks, delay);
	}
	
	public Ball(int x, int y, int radius, Color color, List<Track> tracks, int delay) {
		super(radius-1);	// r - padding for border
		
		this.column = x/50-1;
		this.row = y/50-1;
		this.color = color.toString();
		this.tracks = tracks;
		this.delay = delay;
		
		setCenterX(x + 15);
		setCenterY(y + 25);
		setFill(color);
		getStyleClass().add("ball");
		
		/* event listeners to change track when ball above it is clicked */
		setOnMouseEntered(e -> { modifyTrackOnHover(); });
		setOnMouseMoved(e -> { modifyTrackOnHover(); });
		setOnMouseClicked(e -> {
			if(currentTrack != null && currentTrack.isClickable()) {
				currentTrack.changeType();
			}
		});
		setOnMouseExited(e -> {
			setCursor(Cursor.DEFAULT);
			if(currentTrack != null) {
				currentTrack.setId("");
			}
			this.currentTrack = null;
		});
	}
		
	
	public void update(FullTrack nodes) {
		Track track = nodes.findTrack(column, row);		// get the track that ball is on
				
		/* if the station is next track */
		if(finalStation) {
			moveNextDirection(finalDirection);	// calc where ball should move
				finalCounter++;					// move it 25px total into station
			return;
		}
		
		/* start of the game */
		if(track == null) {
			Station startStation = nodes.getStations().get(0);	// get starting station
			if(startStation != null) {
				moveNextDirection(startStation.getExit());	// move towards its exit
			}
			return;
		}
				
		double[][] path = track.getPath();	// get path xy coordinates from current track

		/* if the ball is on last pixel of the path */
		if(index == path[0].length-1) {	
			int nextCol = track.getNextTrackColumn();	// get next column
			int nextRow = track.getNextTrackRow();		// get next row
			
			track = nodes.findTrack(nextCol, nextRow);	// get a corresponding track
			
			/* if there is no next track available */
			if(track == null) {
				finalStation = true;									// set the flag
				finalDirection = calcFinalDirection(nextCol, nextRow);	// calculate direction
				return;
			}
			path = track.getPath();			// overwrite path with new one					
			column = track.getColumn();		// overwrite ball column
			row = track.getRow();			// overwrite ball row
			index = -1;						// reset index
		}
		
		/* get new ball coordinates and move to them */
		double x = path[0][index+1];
		double y = path[1][index+1];
		setCenterX(x);
		setCenterY(y);
		index++;
	}
	
	/* setters & getters */
		
	public String getColor() {
		return color;
	}
	
	public int getCounter() {
		return this.finalCounter;
	}
	
	public int getColumn() {
		return (int)(getCenterX()/50-1);
	}
	
	public int getRow() {
		return (int)(getCenterY()/50-1);
	}
	
	public int getDelay() {
		return delay;
	}
	
	/* Check if ball is currently on clickable track */
	private Track getCurrentTrack() {
		for(Track track : tracks) {
			if(track.getColumn() == getColumn() && track.getRow() == getRow()) {
				return track;
			}
		} return NULL_TRACK;
	}
	
	private void moveNextDirection(int direction) {
		switch(direction) {
			case 0:
				setCenterY(getCenterY() - 1);
				break;
			case 1:
				setCenterX(getCenterX() + 1);
				break;
			case 2:
				setCenterY(getCenterY() + 1);
				break;
			case 3:
				setCenterX(getCenterX() - 1);
				break;
			default:
				Log.error("@moveNextDirection: Wrong direction");
		}
		column = getColumn();
		row = getRow();
	}
	
	private int calcFinalDirection(int nextCol, int nextRow) {
		if(nextCol > column) {
			return 1;
		}
		if(nextCol < column) {
			return 3;
		}
		if(nextRow > row) {
			return 2;
		}
		if(nextRow < row) {
			return 0;
		}
		return -1;
	}
	
	private void modifyTrackOnHover() {
		Track currentTrack = getCurrentTrack();
		this.currentTrack = currentTrack;
		
		/* if the ball is over a clickable track allow to change it */
		if(currentTrack != null && currentTrack.isClickable()) {
			setCursor(Cursor.HAND);
			currentTrack.setId("ball-hovered");
		}
	}
}
