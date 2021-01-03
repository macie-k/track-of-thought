package base.obj;

import base.Log;
import base.Utils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import static base.Utils.getColRowFromXY;

public class Ball extends Circle {
	
	private int index = 0;
	private int column;
	private int row;
	private int delay;
	private boolean border;
	private boolean active = false;
	private Color color;
	private String colorStr;		// english name of the color (optionally with border indicator '+O')
	
	private double[][] finalTrackPath;
	private Track finalTrack;	
	private int finalCounter = 0;
	private int finalDirection;
	private boolean finalStation = false;
	private boolean dontChange;
	
	
	public Ball(double[] xy, int delay) {
		this((int)xy[0], (int)xy[1], 10, delay, false);
	}
	
	public Ball(int x, int y, int radius, int delay, boolean active) {
		super(radius);
		
		this.column = getColRowFromXY(x);
		this.row = getColRowFromXY(y);
		this.delay = delay;
		
		setCenterX(x + 15);
		setCenterY(y + 25);
		setFill(Color.TRANSPARENT);
		getStyleClass().add("ball");
		setStrokeType(StrokeType.INSIDE);
		
		setMouseTransparent(true);
	}
		
	/* all the logic behind the ball's movement */
	public void update(FullTrack nodes) {
		Track track = nodes.findTrack(column, row);		// get the track that ball is on
				
		/* if the station is next track */
		if(finalStation) {
			moveNextDirection(finalDirection);	// calc where ball should move
				finalCounter++;					// move it 15px total into station
			return;
		}
		
		/* start of the game */
		if(track == null) {
			Station startStation = nodes.getStartStation();	// get starting station
			if(startStation != null) {
				moveNextDirection(startStation.getExit());		// move towards its exit
			}
			return;
		}
								
		double[][] path = track.getPath();	// get path xy coordinates from current track
		
		/* 10 last pixels of path are unchangeable - so set the final values at 11th pixel */
		if(index == path[0].length-11) {
			dontChange = true;
			finalTrackPath = path;
			try {
				finalTrack = new Track(track);
			} catch (Exception e) {}
		}

		/* if the ball is on last pixel of the path */
		if(index == path[0].length-1) {	
			int nextCol = finalTrack.getNextTrackColumn();	// get next column
			int nextRow = finalTrack.getNextTrackRow();		// get next row
			
			Track newTrack = nodes.findTrack(nextCol, nextRow);		// get a corresponding track
			
			/* if there is no next track available */
			if(newTrack == null) {
				finalStation = true;									// set the flag
				finalDirection = calcFinalDirection(nextCol, nextRow);	// calculate direction
				return;
			}
			path = newTrack.getPath();			// overwrite path with new one					
			column = newTrack.getColumn();		// overwrite ball column
			row = newTrack.getRow();			// overwrite ball row
			index = -1;							// reset index
			dontChange = false;
		}
		
		/* get new ball coordinates and move to them */
		double x = dontChange ? finalTrackPath[0][index+1] : path[0][index+1];
		double y = dontChange ? finalTrackPath[1][index+1] : path[1][index+1];
		setCenterX(x);
		setCenterY(y);
		index++;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive() {
		return active;
	}
			
	public void setColor(String colorName) {
		Object[] parsedColor = Utils.parseColorWithBorder(colorName);
		color = (Color) parsedColor[0];
		colorStr = colorName;
		boolean border = (boolean) parsedColor[1];
		
		setFill(color);
		setBorder(border);
	}
	
	public void setBorder(boolean border) {
		this.border = border;
	}
	
	/* show border at launch to avoid a halo under the start station */
	public void showBorder() {
		if(border) {
			setStroke(Color.WHITE);
			setStrokeWidth(4);
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getColorStr() {
		return colorStr;
	}
	
	public boolean hasBorder() {
		return border;
	}
	
	public int getCounter() {
		return this.finalCounter;
	}
	
	public int getColumn() {
		return getColRowFromXY(getCenterX());
	}
	
	public int getRow() {
		return getColRowFromXY(getCenterY());
	}
	
	public int getDelay() {
		return delay;
	}
	
	public String toString() {
		return String.format(
				"Ball[xy=(%d, %d), color=%s, border=%b]",
				column,
				row,
				colorStr,
				border
			);
	}
	
	/* returns a direction the ball should move at the beginning */
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
				Log.error("Wrong direction: " + direction);
		}
		column = getColumn();
		row = getRow();
	}
	
	/* returns a final direction the ball should move when 'parking' on the station */
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
}
