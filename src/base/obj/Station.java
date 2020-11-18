package base.obj;

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class Station extends GridSquare {

	private int column;
	private int row;
	private boolean start;
	private int exit;
	private String color;
	private boolean border;
	
	public Station(int[] xy, Color fill, int exit, boolean border) {
		this(xy[0]/50-1, xy[1]/50-1, fill, exit, border);
	}
	
	public Station(int column, int row, Color fill, boolean border) {
		this(column, row, fill, false, -1, border);
	}
	
	public Station(int column, int row, Color fill, int exit, boolean border) {
		this(column, row, fill, exit != -1, exit, border);
	}
	
	public Station(int column, int row, Color fill, boolean start, int exit, boolean border) {
		super(column, row);
		
		this.column = column;
		this.row = row;
		this.color = fill.toString();
		this.start = start;
		this.exit = exit;
		this.border = border;
		
		getStyleClass().add("station");
		if(border) {
			setWidth(getWidth() - 10);
			setHeight(getHeight() - 10);
			setTranslateX(getTranslateX() + 5);
			setTranslateY(getTranslateY() + 5);
			getStyleClass().remove("gridPane");
			setStroke(Color.rgb(255, 255, 255, 0.8));
			setStrokeType(StrokeType.OUTSIDE);
			setStrokeWidth(5);
		}
		setFill(fill);
	}
	
	public boolean getBorder() {
		return border;
	}
	
	public String toString() {
		return String.format("Station[XY=(%d, %d), Color=%s, Border=%b]", column, row, color, border);
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
