package base.obj;

import base.Log;
import base.Utils;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

public class Station extends GridSquare {

	private int column;
	private int row;
	private boolean start;
	private int exit;
	private Color color;
	private String colorStr;
	private boolean border;
	
	public Station(int[] xy, String fill, int exit, boolean border) {
		this(xy[0]/50-1, xy[1]/50-1, fill, exit, border);
	}
	
	public Station(int column, int row, String fill, boolean border) {
		this(column, row, fill, false, -1, border);
	}
	
	public Station(int column, int row, String fill, int exit, boolean border) {
		this(column, row, fill, exit != -1, exit, border);
	}
	
	public Station(int column, int row, String color, boolean start, int exit, boolean border) {
		super(column, row);
		
		this.column = column;
		this.row = row;
		this.color = Utils.parseColorName(color);
		this.colorStr = color;
		this.start = start;
		this.exit = exit;
		this.border = border;
		
		getStyleClass().add("station");
		if(border && !start) {
			setWidth(getWidth() - 10);
			setHeight(getHeight() - 10);
			setTranslateX(getTranslateX() + 5);
			setTranslateY(getTranslateY() + 5);
			getStyleClass().remove("gridPane");
			setStroke(Color.rgb(255, 255, 255, 0.8));
			setStrokeType(StrokeType.OUTSIDE);
			setStrokeWidth(5);
		}
		setFill(this.color);
	}
	
		
	public String toString() {
		return String.format("Station[XY=(%d, %d), Color=%s, Border=%b, Exit=%d]", column, row, colorStr, border, exit);
	}
		
	public boolean getBorder() {
		return border;
	}
	
	public void setBorder(boolean value) {
		border = value;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getColorStr() {
		return colorStr;
	}
	
	public void setColor(String value) {
		color = Utils.parseColorName(value);
		colorStr = value;
		setFill(color);
	}
	
	public void setColor(Color value) {
		color = value;
		setFill(color);
	}
	
	/* functions below should remain get-only  */
	
	public int[] getFirstTrackColRow() {
		int col = getColumn();
		int row = getRow();
		if(isStart()) {
			switch(exit) {
				case 0:
					return new int[] {col, row-1};
				case 1:
					return new int[] {col+1, row};
				case 2:
					return new int[] {col, row+1};
				case 3:
					return new int[] {col-1, row};
				default:
					Log.error("Wrong station exit");
			}
		} else {
			Log.error("Not the first station");
		}
		return null;
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
