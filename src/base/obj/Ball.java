package base.obj;

import base.Log;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle {
	
	private int column;
	private int row;
	private String color;
	private int index = 0;
	
	private int finalCounter = 0;
	private int finalDirection;
	private boolean finalStation = false;

	
	public Ball(double[] xy, int radius, Color fill) {
		this((int)xy[0], (int)xy[1], radius, fill);
	}
	
	public Ball(int x, int y, int radius, Color color) {
		super(radius);
		
		this.column = x/50-1;
		this.row = y/50-1;
		this.color = color.toString();
		
		setCenterX(x + 15);
		setCenterY(y + 25);
		
		setFill(color);
	}
		
	public void update(FullTrack nodes) {
		Track track = nodes.findTrack(column, row);
				
		if(finalStation) {
			moveNextDirection(finalDirection);
				finalCounter++;
			return;
		}
		
		if(track == null) {
			Station startStation = nodes.getStations()[0];
			if(startStation != null) {
				moveNextDirection(startStation.getExit());
			}
			return;
		}
				
		double[][] path = track.getPath();

		if(index == path[0].length-1) {	
			int nextCol = track.getNextTrackColumn();
			int nextRow = track.getNextTrackRow();
			
			track = nodes.findTrack(nextCol, nextRow);
			
			if(track == null) {
				finalStation = true;
				finalDirection = calcFinalDirection(nextCol, nextRow);
				return;
			}			
			path = track.getPath();									
			column = track.getColumn();
			row = track.getRow();
			index = -1;
		}
				
		double x = path[0][index+1];
		double y = path[1][index+1];
		setCenterX(x);
		setCenterY(y);
		index++;
	}
		
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
}
