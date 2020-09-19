package base.obj;

import javafx.scene.paint.Color;

public class Station extends GridSquare {

	private int column;
	private int row;
	
	public Station(int row, int column, Color fill) {
		super(row, column);
		this.column = column;
		this.row = row;
		setFill(fill);
	}
	
	public int[] getPos() {
		return new int[] {this.row, this.column};
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public int getRow() {
		return this.row;
	}
}
