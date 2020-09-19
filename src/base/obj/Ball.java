package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle {
	
	private int column;
	private int row;
	
	public Ball(int[] xy, int radius, Color fill) {
		this(xy[0], xy[1], radius, fill);
	}
	
	public Ball(int row, int column, int radius, Color fill) {
		super(radius);
		
		this.column = column;
		this.row = row;
		
		setCenterX((column+1) * 50 + 15);
		setCenterY((row+1) * 50 + 25);
		
		setFill(fill);
	}
	
	public void updateRowsCols() {
		if((int)(getCenterX() / 50) - 1 > column) {
			column++;
		}
		if((int)(getCenterX() / 50) - 1 < column) {
			column--;
		}
		if((int)(getCenterY() / 50) - 1 > row) {
			row++;
		}
		if((int)(getCenterY() / 50) - 1 < row) {
			row--;
		}
	}
	
	public void moveRight() {
		setCenterX(getCenterX() + 1);
		updateRowsCols();
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public int getRow() {
		return this.row;
	}
}
