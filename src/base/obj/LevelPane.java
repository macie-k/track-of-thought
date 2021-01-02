package base.obj;

import javafx.animation.FillTransition;  
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static base.Utils.COLOR_ACCENT;
import static base.Utils.COLOR_LEVEL;

import base.Scenes;
import base.Window;

public class LevelPane extends StackPane {
	
	private final Rectangle container;	// rectangle button
	private final Text value;			// level number
			
	public LevelPane(int x, int y, String lvl, boolean premade, boolean enabled) {
		setTranslateX(x);
		setTranslateY(y);
		
		container = new Rectangle(97, 70, COLOR_LEVEL);
		container.getStyleClass().add("container");		
		
		String level = String.valueOf(lvl);
		value = new Text(level);
		value.getStyleClass().add("level-number");
		value.setFont(Font.font("Poppins Light"));
				
		if(enabled) {
			container.setCursor(Cursor.HAND);
			value.setCursor(Cursor.HAND);
			setOnMouseEntered(event -> fadeHighlight(true));
			setOnMouseExited(event -> fadeHighlight(false));
			setOnMouseClicked(event -> {
				if(Window.levelCreator) {
					Window.setScene(Scenes.createLevel());
				} else {
					Window.game(Scenes.gameTrack(lvl, premade));
				}
			});
		} else {
			container.getStyleClass().add("container-disabled");
			value.getStyleClass().add("level-number-disabled");
		}
		getChildren().addAll(container, value);
	}
		
	public String getValue() {
		return value.getText();
	}
			
	public void fadeHighlight(boolean highlight) {
		final int duration = 200;
		if(highlight) {
			fadeColors(container, duration, COLOR_LEVEL, COLOR_ACCENT);
			fadeColors(value, duration, COLOR_ACCENT, COLOR_LEVEL);
		} else {
			fadeColors(container, duration, COLOR_ACCENT, COLOR_LEVEL);
			fadeColors(value, duration, COLOR_LEVEL, COLOR_ACCENT);
		}
	}
	
	private void fadeColors(Shape shape, int duration, Color from, Color to) {
		FillTransition ft = new FillTransition(Duration.millis(duration), shape, from, to);
		ft.setOnFinished(e -> {
			shape.setFill(to);
		});
		ft.play();
	}
}
