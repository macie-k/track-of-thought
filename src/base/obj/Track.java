package base.obj;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static base.Scenes.S;

import base.Log;

import static base.Scenes.C;

public class Track extends StackPane {
	
	private final Integer NULL = null;
	
	private String type;
	private Shape track;
	
	private int origin;
	private int end;
	private int endToSwitch;
	private int column;
	private int row;
	
	/* doesn't require endSwitch parameter if not clickable */
	public Track(int [] xy, String type, int origin, int end) {
		this(xy[0], xy[1], type, origin, end, end, false);
	}
	
	/* translates xy array if endSwitch provided -> is clickable */
	public Track(int[] xy, String type, int origin, int end1, int end2) {
		this(xy[0], xy[1], type, origin, end1, end2, true);
	}
	
	public Track(int y, int x, String type, int origin, int end1, int end2, boolean clickable) {
		this.type = type;
		this.origin = origin;
		this.end = end1;
		this.endToSwitch = end2;
		this.column = x/50-1;
		this.row = y/50-1;
		
		setWidth(50);
		setHeight(50);
		setTranslateX(x);
		setTranslateY(y);
		setRotate(calcRotation(origin, end1));
		if(getRotate() == 45) {
			Log.error("Wrong [origin] + [end] combination @[" + column + ", " + row + "]");
			setStyle("-fx-background-color: rgba(255, 0, 0, 0.5);");
		}
		
		if(clickable) {
			getStyleClass().add("clickable");
			setOnMouseClicked(e -> {
				changeType();
				endToSwitch = (endToSwitch == end1) ? end2 : end1;
			});
		}

		track = getTrackShape();		
		Circle bg = new Circle(25, Color.TRANSPARENT);
			bg.getStyleClass().add("bg");
		
		getChildren().addAll(bg, track);
	}
	
	public Shape getTrackAsShape() {
		return track;
	}
		
	public int getColumn() {
		return this.column;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int[] getOriginXY() {
		int originX = getEndpointX(this.origin);
		int originY = getEndpointY(this.origin);
				
		return new int[] {originX, originY};
	}
	
	public int[] getEndXY() {
		int endX = getEndpointX(this.end);
		int endY = getEndpointY(this.end);
				
		return new int[] {endX, endY};
	}
	
	@Override
	public String toString() {
		return String.format(
					"Track@[type=%s, x=%s, y=%s, origin=%s, end=%s]",
					this.type,
					this.column,
					this.row,
					getOriginEndToString(this.origin),
					getOriginEndToString(this.end)
				);
	}
	
	/* return X coordinate of origin/end point */
	private int getEndpointX(int endpoint) {
		int currentX  = (int) getTranslateX();
		switch(endpoint) {
			case 0:
			case 2:
				return currentX + 25;
			case 1:
				return currentX + 50;
			case 3:
				return currentX + 0;
			default:
				Log.error("Wrong endpoint value, expected [0-3]");
				return NULL;
		}
	}
	
	/* return Y coordinate of origin/end point */
	private int getEndpointY(int endpoint) {
		int currentY  = (int) getTranslateY();
		switch(endpoint) {
			case 0:
				return currentY + 0;
			case 2:
				return currentY + 50;
			case 1:
			case 3:
				return currentY + 25;
			default:
				Log.error("Wrong endpoint value, expected [0-3]");
				return NULL;
		}
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
		try {
			switch(type) {
			/* straight track */
			case S:	
				switch(origin + end) {
					case 2: return 0;		// (top <-> bottom)
					case 4: return 90;		// (left <-> right)
					default: return NULL;		// [ERROR]
				}
			/* curved track */
			case C:
				switch(origin + end) {
				case 1: return 0;							// (top -> right)
				case 3: return (origin > end) ? 90 : 270;	// (right -> bottom) or (left -> top)
				case 5: return 180;							// (bottom -> left)
				default: return NULL;							// [ERROR]
			}
			default: return NULL;	
			}
		} catch(Exception e) {
			return 45;
		}
		
		
	}
			
	private void changeType() {
		type = (type == S) ? C : S;
		getChildren().remove(1);
		getChildren().add(getTrackShape());
		setRotate(calcRotation(origin, endToSwitch));
	}
		
	private Shape getTrackShape() {
		if(type == S) {
			Rectangle track =  new Rectangle(15, 0, 20, 50);
				track.getStyleClass().add("track");
			return track;
		} else {
			Rectangle square = new Rectangle(50, 50, Color.GREEN);
			
			Circle small = new Circle(50, 0, 15, Color.RED);
			Shape topRight =  Path.intersect(square, small);
			
			Circle big = new Circle(50, 0, 35, Color.RED);
			Shape mid = Path.intersect(square, big);
			
			Shape track = Path.subtract(mid, topRight);
				track.getStyleClass().add("track");
			setAlignment(track, Pos.TOP_RIGHT);

			return track;
		}
	}
}
