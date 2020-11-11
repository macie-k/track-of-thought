package base.obj;

import javafx.scene.paint.Color;

public class Station extends GridSquare {

	private int column;
	private int row;
	private boolean start;
	private int exit;
	private String color;
	
	public Station(int[] xy, Color fill, int exit) {
		this(xy[0]/50-1, xy[1]/50-1, fill, exit);
	}
	
	public Station(int column, int row, Color fill) {
		this(column, row, fill, false, -1);
	}
	
	public Station(int column, int row, Color fill, int exit) {
		this(column, row, fill, exit != -1, exit);
	}
	
	public Station(int column, int row, Color fill, boolean start, int exit) {
		super(column, row);
		
		this.column = column;
		this.row = row;
		this.color = fill.toString();
		this.start = start;
		this.exit = exit;
		
		setFill(fill);
	}
	
	public String toString() {
		return String.format("Station@[%d, %d]", column, row);
	}
	
	public String getColor() {
		return color;
	}
	
	public double[] getXY() {
		return new double[] {getTranslateX(), getTranslateY()};
	}
	
	public int[] getPos() {
		return new int[] {row, column};
	}
	
	public boolean isStart() {
		return start;
	}
	
	public int getExit() {
		return exit;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRow() {
		return row;
	}
}
