package base.obj;

import base.Log;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/* object used only to return the starting coordinates of each grid pane for easier positioning */

public class GridSquare extends Rectangle {
	
	private int x;
	private int y;
	private boolean hoverable;
	
	public GridSquare(int column, int row) {
		this(column, row, false);
	}
		
	public GridSquare(int column, int row, boolean hoverable) {
		x = (column + 1)*50;
		y = (row + 1)*50;
		this.hoverable = hoverable;
		
		if(column < 15 && row < 9) {	// leave 1 block border
			setWidth(50);
			setHeight(50);
			setTranslateX(x);
			setTranslateY(y);
			getStyleClass().add("gridPane");
			setFill(Color.TRANSPARENT);
		} else {
			Log.error("Value exceeding grid size");
		}
		
		if(hoverable) {
			getStyleClass().add("grid");
		}
	}
	
	public boolean getHoverable() {
		return hoverable;
	}
	
	public int[] getPos() {
		return new int[] {this.x, this.y};
	}
}
