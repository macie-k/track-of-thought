package base.obj;

import static base.Utils.getXYFromRowCol;

import base.Log;
import base.Utils;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

public class Station extends StackPane {

	private int column;
	private int row;
	private boolean start;
	private int exit;
	private Color color;
	private String colorStr;	// english name of the color WITHOUT BORDER INDICATOR
	private boolean border;
	private Shape shape;
	private int x;
	private int y;
	
	private final StackPane fix = new StackPane();
	
	public Station(int[] xy, String fill, int exit) {
		this(xy[0]/50-1, xy[1]/50-1, fill, exit);
	}
	
	public Station(int column, int row, String fill) {
		this(column, row, fill, false, -1);
	}
	
	public Station(int column, int row, String fill, int exit) {
		this(column, row, fill, exit != -1, exit);
	}
	
	public Station(int column, int row, String color, boolean start, int exit) {
		x = getXYFromRowCol(column);
		y = getXYFromRowCol(row);	
		
		setTranslateX(x);
		setTranslateY(y);
		
		this.column = column;
		this.row = row;
		this.color = Utils.parseColorName(color);
		this.colorStr = color;
		this.start = start;
		this.exit = exit;
	}
	
	public void initShape() {
		shape = start ? getStartShape() : getStandardShape();
		shape.getStyleClass().add("station");
		getChildren().addAll(shape);
	}
		
	private Shape getStandardShape() {
		Rectangle rectBg = new Rectangle(50, 50, color);
			rectBg.getStyleClass().remove("gridPane");
			rectBg.setStrokeType(StrokeType.INSIDE);
			rectBg.setFill(color);
		
		if(border) {
			rectBg.setStroke(Color.WHITE);
			rectBg.setStrokeWidth(5);
		} else {
			rectBg.setStroke(Color.web("#B1AD9F"));
			rectBg.setStrokeWidth(1);
		}
		
		return rectBg;
	}
		
	/* utterly bad way to change the station's shape */
	private Shape getStartShape() {
		Pos align = Utils.getDirectionToPos(exit);
		int[] bigCenter = null;
		int[] smallCenter = null;
				
		final int correction = -10;
		switch(exit) {
			case 0:
				bigCenter = new int[] {25, 0-correction};
				smallCenter = new int[] {25, 0};
				break;
			case 1:
				bigCenter = new int[] {50+correction, 25};
				smallCenter = new int[] {50, 25};
				break;
			case 2:
				bigCenter = new int[] {25, 50+correction};
				smallCenter = new int[] {25, 50};
				break;
			case 3:
				bigCenter = new int[] {0-correction, 25};
				smallCenter = new int[] {0, 25};
				break;
		}
				
		final Rectangle coverFix = new Rectangle(20, 20);
			coverFix.setFill(Color.rgb(255, 255, 255, 0.2));
		fix.setTranslateX(x);
		fix.setTranslateY(y);
		fix.getChildren().add(coverFix);
		fix.setAlignment(align);
		fix.setPrefSize(50, 50);

		int xB = bigCenter[0], yB = bigCenter[1];
		int xS = smallCenter[0], yS = smallCenter[1];
		
		Rectangle container = new Rectangle(50, 50);
		Circle bigCircle = new Circle(xB, yB, 40, Color.TRANSPARENT);
		Shape first = Path.intersect(container, bigCircle);
		
		Circle smallCircle = new Circle(xS, yS, 10, Color.TRANSPARENT);
		Shape finalShape = Path.subtract(first, smallCircle);
			finalShape.setFill(Color.web("#262626"));

		setAlignment(align);
		return finalShape;
	}
	
	public StackPane getFix() {
		return fix;
	}
	
	public int[] getPos() {
		return new int[] {x, y};
	}
		
	public String toString() {
		return String.format("Station[XY=(%d, %d), Color=%s, Border=%b, Exit=%d]", column, row, colorStr, border, exit);
	}
		
	public boolean hasBorder() {
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
		colorStr = value.split("\\+")[0].trim();
		shape.setFill(color);
	}
	
	public void setColor(Color value) {
		color = value;
		shape.setFill(color);
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
	
//	public int[] getPos() {
//		return new int[] {row, column};
//	}
	
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
