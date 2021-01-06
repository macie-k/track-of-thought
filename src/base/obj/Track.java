package base.obj;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import base.Log;

import static base.Utils.getColRowFromXY;
import static base.Utils.getXYFromRowCol;

public class Track extends StackPane {
		
	private final static double PI = Math.PI;
	private final static double fi0 = PI/100;
	private final static String S = "straight";
	private final static String C = "curved";
	
	private Shape track;
	private Rectangle[] debugPath;
	
	private String type;
	private int origin;
	private int end1;
	private int end2;
	private int currentEnd;
	private int endToSwitch;
	private int column;
	private int row;
	private int quarter;
	private boolean clickable;
		
		
	/* doesn't require endSwitch parameter if not clickable */
	public Track(int [] xy, String type, int origin, int end) throws Exception {
		this(xy[0], xy[1], type, origin, end, end, false);
	}
	
	/* translates xy array if endSwitch provided -> is clickable */
	public Track(int[] xy, String type, int origin, int end1, int end2) throws Exception {
		this(xy[0], xy[1], type, origin, end1, (end2 != -1) ? end2 : end1, end2 != -1);
	}
	
	/* for creating a copy */
	public Track(Track t) throws Exception {		
		this(getXYFromRowCol(t.getColumn()), getXYFromRowCol(t.getRow()), t.getType(), t.getOrigin(), t.getEnd1(), t.getEnd2(), t.isClickable());
	}
	
	public Track(int x, int y, String type, int origin, int end1, int end2, boolean clickable) throws Exception {
		this.type = type;
		this.origin = origin;
		this.end1 = end1;
		this.currentEnd = end1;
		this.end2 = end2;
		this.endToSwitch = end2;
		this.column = getColRowFromXY(x);
		this.row = getColRowFromXY(y);
		this.quarter = calcQuarter();
		this.clickable = clickable;
		this.debugPath = getDebugPath();
		setRotate(calcRotation(origin, end1));
		
		setWidth(50);
		setHeight(50);
		setTranslateX(x);
		setTranslateY(y);

		track = getTrackShape();	
		Circle bg = new Circle(24, Color.TRANSPARENT);
			bg.getStyleClass().add("bg");

		getChildren().addAll(bg, track);
		
		if(clickable) {
			getStyleClass().add("clickable");
			
			/* Change the track only when mouse was released on the its square */
			setOnMouseReleased(e -> {
				final double dropColumn = getColRowFromXY(e.getSceneX());
				final double dropRow = getColRowFromXY(e.getSceneY());
				
				if(dropColumn == getColumn() && dropRow == getRow()) {
					try {
						changeType();
					} catch (Exception e1) {}
				}
			});
		}
	}
		
	@Override
	public String toString() {
		return String.format(
					"Track[xy=(%d, %d), type=%s, origin=%s, end=%s, end2=%s, switch=%b]",
					column,
					row,
					type,
					getOriginEndToString(origin),
					getOriginEndToString(currentEnd),
					getOriginEndToString(endToSwitch),
					clickable
				);
	}
	
	public double[][] getPath(boolean creator) {
		final int size = creator ? 50 : 35;
		final double ratio = 50.0/size;
		
		double[][] pathXY = new double[2][size];
		final int originX = getOriginXY()[0];
		final int originY = getOriginXY()[1];
		
		if(type.equals(S)) {
			switch(origin + currentEnd) {
				case 2:
					for(int i=0; i<size; i++) {
						double x = originX + 25;
						double y = originY + ratio*i;
						
						pathXY[0][i] = x;
						pathXY[1][i] = y;
					}
					if(origin == 2) {
						reverseRow(pathXY, 1);
					}
					break;
				case 4:
					for(int i=0; i<size; i++) {
						double x = originX + ratio*i;
						double y = originY + 25;
						
						pathXY[0][i] = x;
						pathXY[1][i] = y;
					}

					if(origin == 1) {						
						reverseRow(pathXY, 0);
					}
					break;
			}			
		} else {			
			double startFi = getAngles()[0];
			double endFi = getAngles()[1];
			
			double fi = fi0;
			if((endFi - startFi) < 0) {
				fi *= -1;
			}

			for(int i=0; i<size; i++) {
				double x = originX + 25*Math.cos(startFi + ratio*i*fi);
				double y = originY - 25*Math.sin(startFi + ratio*i*fi);
				
				pathXY[0][i] = x;
				pathXY[1][i] = y;
			}
		}	
		return pathXY;
	}
	
	/* default getPath for game */
	public double[][] getPath() {
		return getPath(false);
	}
	
	/* draws track's direction white -> red */
	public Rectangle[] getDebugPath() {
		double[][] path = getPath(true);
		Rectangle[] tab = new Rectangle[path[0].length];
		
		for(int j=0; j<path[0].length; j++) {
			Rectangle r = new Rectangle(1, 1);
				r.setTranslateX(path[0][j]);
				r.setTranslateY(path[1][j]);
				r.setFill(Color.rgb(255, 5*(path[0].length-j), 5*(path[0].length-j)));
				r.setMouseTransparent(true);
				tab[j] = r;
		}	
		return tab;
	}
	
	public void removeDebugPath(Pane root) {
		for(Rectangle r : debugPath) {
			root.getChildren().remove(r);
		}
	}
	
	public void addDebugPath(Pane root) {
		debugPath = getDebugPath();
		for(Rectangle r : debugPath) {
			root.getChildren().add(r);
		}
	}
	
	public void hideDebugPath() {
		for(Rectangle r : debugPath) {
			r.setVisible(false);
		}
	}
	
	public void showDebugPath() {
		for(Rectangle r : debugPath) {
			r.setVisible(true);
		}
	}
	
	public int getNextTrackColumn() {
		switch(currentEnd) {
			case 0:
			case 2:
				return column;
			case 1:
				return column+1;
			case 3:
				return column-1;
			default:
				return -1;
		}
	}
	
	public int getNextTrackRow() {
		switch(currentEnd) {
			case 1:
			case 3:
				return row;
			case 2:
				return row+1;
			case 0:
				return row-1;
			default:
				return -1;
		}
	}
	
	public int getOrigin() {
		return this.origin;
	}
	
	public int getEnd1() {
		return this.currentEnd;
	}
	
	public int getEnd2() {
		return this.endToSwitch;
	}
		
	public boolean isClickable() {
		return clickable;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public String getType() {
		return this.type;
	}

	public Shape getTrackAsShape() {
		return track;
	}

//	------------------------------------------------
		
	private int calcQuarter() {
		if(type.equals(S)) {
			return 0;
		} else {
			int quarter = 0;
			switch(origin + currentEnd) {
				case 1: quarter=3;									// 3rd quarter
					break;
				case 3: quarter=(origin == 0 || currentEnd == 0) ? 4 : 2;	// 2nd or 4th quarter
					break;
				case 5: quarter=1;									// 1st quarter
					break;
				default:
					Log.error("Wrong parameters");
					break;
			}
			return quarter;
		}
	}
	
	private int[] getOriginXY() {		
		int x = getXYFromRowCol(this.column), y = getXYFromRowCol(this.row);
		switch(quarter) {
			case 1:
				y += 50;
				break;
			case 2:
				x += 50;
				y += 50;
				break;
			case 3:
				x += 50;
				break;
			case 4:
			default: break;
		}
		
		return new int[] {x, y};
	}
	
	private double[] getAngles() {
		
		double startAngle = 0;
		double endAngle = 0;
		
		switch(quarter) {
			case 1:
				if(origin == 2) {
					startAngle = 0;
					endAngle = PI/2;
				} else {
					startAngle = PI/2;
					endAngle = 0;
				}
				break;
			case 2:
				if(origin == 1) {
					startAngle = PI/2;
					endAngle = PI;
				} else {
					startAngle = PI;
					endAngle = PI/2;
				}
				break;
			case 3:
				if(origin == 0) {
					startAngle = PI;
					endAngle = 3*PI/2;
				} else {
					startAngle = 3*PI/2;
					endAngle = PI;
				}
				break;
			case 4:
				if(origin == 3) {
					startAngle = 3*PI/2;
					endAngle = 2*PI;
				} else {
					startAngle = 2*PI;
					endAngle = 3*PI/2;
				}
				break;
			default:
				Log.error("Wrong parameters");
				break;
		}
		return new double[] {startAngle, endAngle};
	}
		
	private double[][] reverseRow(double[][] pathXY, int row) {
		int rowlen = pathXY[row].length;
		for(int i=0; i<rowlen/2; i++) {
			double x = pathXY[row][i];
			
			pathXY[row][i] = pathXY[row][rowlen-i-1];
			pathXY[row][rowlen-i-1] = x;
		}
		
		return pathXY;
	}
	
	private String getOriginEndToString(int originOrEnd) {
		switch(originOrEnd) {
			case 0:
				return "top";
			case 1:
				return "right";
			case 2:
				return "bottom";
			case 3:
				return "left";
			default:
				return "[ERROR]";
		}
	}
	
	private int calcRotation(int origin, int end) throws Exception {
		
//		0 -> top
//		1 -> right
//		2 -> bottom
//		3 -> left
		
		switch(type) {
			/* straight track */
			case S:	
				switch(origin + end) {
					case 2: return 0;		// (top <-> bottom)
					case 4: return 90;		// (left <-> right)
					default:
						throw new Exception("Wrong 'origin'+'end' combination for straight track");
				}
			/* curved track */
			case C:
				switch(origin + end) {
					case 1: return 0;							// (top -> right)
					case 3:
						return (origin == 1 || origin == 2) ? 90 : 270;
					case 5: return 180;							// (bottom -> left)
					default:
						throw new Exception("Wrong 'origin'+'end' combination for curved track");
				}
			default:
				throw new Exception("Wrong track type");	
		}
	}
		
	public void changeType() throws Exception {
		type = (type.equals(S)) ? C : S;		
		
		getChildren().removeIf(node -> node.getStyleClass().contains("track"));
		getChildren().add(getTrackShape());
		setRotate(calcRotation(origin, endToSwitch));
				
		endToSwitch = (endToSwitch == end1) ? end2 : end1;
		currentEnd = (currentEnd == end1) ? end2 : end1;
		quarter = calcQuarter();
		
		debugPath = getDebugPath();
	}
		
	private Shape getTrackShape() {
		Shape track = null;
		if(type.equals(S)) {
			track = new Rectangle(15, 0, 20, 50);
		} else {
			Rectangle square = new Rectangle(50, 50, Color.TRANSPARENT);
			
			Circle small = new Circle(50, 0, 15, Color.TRANSPARENT);
			Shape topRight = Shape.intersect(square, small);
			
			Circle big = new Circle(50, 0, 35, Color.TRANSPARENT);
			Shape mid = Shape.intersect(square, big);
			
			track = Shape.subtract(mid, topRight);
			setAlignment(track, Pos.TOP_RIGHT);	
		}
		track.getStyleClass().add("track");
		return track;
	}
}
