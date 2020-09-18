package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/* object used only to return the starting coordinates of each grid pane for easier positioning */

public class GridSquare extends Rectangle {
	
	private int x;
	private int y;
		
	public GridSquare(int column, int row) {
		x = (column + 1)*50;
		y = (row + 1)*50;
		
		if(column < 15 && row < 9) {	// leave 1 block border
			setWidth(50);
			setHeight(50);
			setTranslateX(x);
			setTranslateY(y);
			getStyleClass().add("gridPane");
			setFill(Color.TRANSPARENT);
			setOpacity(0.5);
		}
	}
	
	public int[] getXY() {
		return new int[] {this.x, this.y};
	}
}
