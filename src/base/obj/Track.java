package base.obj;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import base.Log;

import static base.Utils.getColRowFromXY;
import static base.Utils.getXYFromRowCol;

public class Track extends StackPane {
	
	public final static Integer NULL = null;
	
	private final static double PI = Math.PI;
	private final static double fi0 = PI/100;
	private final static String S = "straight";
	private final static String C = "curved";
	
	private String type;
	private Shape track;
	
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
	public Track(int [] xy, String type, int origin, int end) {
		this(xy[0], xy[1], type, origin, end, end, false);
	}
	
	/* translates xy array if endSwitch provided -> is clickable */
	public Track(int[] xy, String type, int origin, int end1, int end2) {
		this(xy[0], xy[1], type, origin, end1, (end2 != -1) ? end2 : end1, end2 != -1);
	}
	
	public Track(int x, int y, String type, int origin, int end1, int end2, boolean clickable) {
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
		
		setWidth(50);
		setHeight(50);
		setTranslateX(x);
		setTranslateY(y);
		setRotate(calcRotation(origin, end1));
		if(getRotate() == 45) {
			setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
		}
		
		if(clickable) {
			getStyleClass().add("clickable");
			setOnMouseClicked(e -> {
				changeType();
			});
		}

		track = getTrackShape();		
		Circle bg = new Circle(24, Color.TRANSPARENT);
			bg.getStyleClass().add("bg");
		
		getChildren().addAll(bg, track);
	}
	
	@Override
	public String toString() {
		return String.format(
					"Track[xy=(%d, %d), type=%s, origin=%s, end=%s, switch=%b]",
					column,
					row,
					type,
					getOriginEndToString(origin),
					getOriginEndToString(currentEnd),
					clickable
				);
	}
	
	public double[][] getPath() {
		double[][] pathXY = new double[2][50];
		final int originX = getOriginXY()[0];
		final int originY = getOriginXY()[1];
		
		if(type.equals(S)) {
			switch(origin + currentEnd) {
				case 2:
					for(int i=0; i<50; i++) {
						double x = originX + 25;
						double y = originY + i;
						
						pathXY[0][i] = x;
						pathXY[1][i] = y;
					}
					if(origin == 2) {
						reverseRow(pathXY, 1);
					}
					break;
				case 4:
					for(int i=0; i<50; i++) {
						double x = originX + i;
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

			for(int i=0; i<50; i++) {
				double x = originX + 25*Math.cos(startFi + i*fi);
				double y = originY - 25*Math.sin(startFi + i*fi);
				
				pathXY[0][i] = x;
				pathXY[1][i] = y;
			}
		}	
		return pathXY;
	}
	
	/* draws track's direction white -> red */
	public void debugDraw(Pane root) {
		double[][] path = getPath();
		for(int j=0; j<path[0].length; j++) {
			Rectangle r = new Rectangle(1, 1);
				r.setTranslateX(path[0][j]);
				r.setTranslateY(path[1][j]);
				r.setFill(Color.rgb(255, 5*(path[0].length-j), 5*(path[0].length-j)));
			root.getChildren().add(r);
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
				Log.error("Wrong 'currentEnd' value");
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
				Log.error("Wrong 'currentEnd' value");
				return -1;
		}
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
		int x=getXYFromRowCol(this.column), y=getXYFromRowCol(this.row);
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
	
	private int calcRotation(int origin, int end) {
		
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
						Log.error("Wrong 'origin'+'end' combination for straight track");
						return NULL;
				}
			/* curved track */
			case C:
				switch(origin + end) {
					case 1: return 0;							// (top -> right)
					case 3:
						return (origin == 1 || origin == 2) ? 90 : 270;
					case 5: return 180;							// (bottom -> left)
					default:
						Log.error("Wrong 'origin'+'end' combination for curved track");
						return NULL;
				}
			default:
				Log.error("Wrong track type");
				return NULL;	
		}
	}
		
	public void changeType() {
		type = (type.equals(S)) ? C : S;		
		
		getChildren().remove(1);
		getChildren().add(getTrackShape());
		setRotate(calcRotation(origin, endToSwitch));
		
		endToSwitch = (endToSwitch == end1) ? end2 : end1;
		currentEnd = (currentEnd == end1) ? end2 : end1;
		quarter = calcQuarter();
	}
		
	private Shape getTrackShape() {
		if(type.equals(S)) {
			Rectangle track =  new Rectangle(15, 0, 20, 50);
				track.getStyleClass().add("track");
			return track;
		} else {
			Rectangle square = new Rectangle(50, 50, Color.TRANSPARENT);
			
			Circle small = new Circle(50, 0, 15, Color.TRANSPARENT);
			Shape topRight = Path.intersect(square, small);
			
			Circle big = new Circle(50, 0, 35, Color.TRANSPARENT);
			Shape mid = Path.intersect(square, big);
			
			Shape track = Path.subtract(mid, topRight);
				track.getStyleClass().add("track");
			setAlignment(track, Pos.TOP_RIGHT);

			return track;
		}
	}
}
