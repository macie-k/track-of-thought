package base.obj;

import javafx.scene.paint.Color;

public class Station extends GridSquare {

	private int column;
	private int row;
	private boolean start;
	private int exit;
	private String color;
	
	public Station(int row, int column, Color fill) {
		this(row, column, fill, false, -1);
	}
	
	public Station(int row, int column, Color fill, int exit) {
		this(row, column, fill, true, exit);
	}
	
	public Station(int row, int column, Color fill, boolean start, int exit) {
		super(row, column);
		
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
