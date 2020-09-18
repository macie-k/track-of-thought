package base.obj;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static base.Scenes.S;
import static base.Scenes.C;

public class Track extends StackPane {
	

	private String type;
	private Shape track;
	private int origin;
	private int endToSwitch;
	
	/* doesn't require endSwitch parameter if not clickable */
	public Track(int [] xy, String type, int origin, int end) {
		this(xy[0], xy[1], type, origin, end, end, false);
	}
	
	/* translates xy array if endSwitch provided -> is clickable */
	public Track(int[] xy, String type, int origin, int end1, int end2) {
		this(xy[0], xy[1], type, origin, end1, end2, true);
	}
	
	public Track(int x, int y, String type, int origin, int end1, int end2, boolean clickable) {
		this.type = type;
		this.origin = origin;
		this.endToSwitch = end2;
		
		setWidth(50);
		setHeight(50);
		setTranslateX(x);
		setTranslateY(y);
		setRotate(calcRotation(origin, end1));
		
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
					default: return -1;		// [ERROR]
				}
			/* curved track */
			case C:
				switch(origin + end) {
				case 1: return 0;							// (top -> right)
				case 3: return (origin > end) ? 90 : 270;	// (right -> bottom) or (left -> top)
				case 5: return 180;							// (bottom -> left)
				default: return -1;							// [ERROR]
			}
			default: return -1;	
		}
		
	}
			
	private void changeType() {
		type = (type == S) ? C : S;
		getChildren().remove(1);
		getChildren().add(getTrackShape());
		setRotate(calcRotation(origin, endToSwitch));
	}
	
	public Shape getTrack() {
		return track;
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
