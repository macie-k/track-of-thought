package base.obj;

import base.Scenes;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class LevelPane extends StackPane {
	
	private final Rectangle container;	// rectangle button
	private final Text value;			// level number
		
	public LevelPane(int x, int y, int num) {
		
		setTranslateX(x);
		setTranslateY(y);
		
		container = new Rectangle(110, 70, Color.web(Scenes.BACKGROUND));
		container.getStyleClass().add("container");
		
		value = new Text(String.valueOf(num));
		value.getStyleClass().add("level-number");
		
		getChildren().addAll(container, value);
		
		setOnMouseEntered(event -> this.setHiglight(true));
		setOnMouseExited(event -> this.setHiglight(false));
	}
			
	public void setHiglight(boolean highlight) {
		if(highlight) {
			this.container.setStyle("-fx-cursor: hand;");
			this.container.setFill(Color.WHITE);
			this.value.setFill(Color.web(Scenes.BACKGROUND));
		} else {
			this.container.setStyle("-fx-cursor: default;");
			this.container.setFill(Color.web("#1F2227"));
			this.value.setFill(Color.WHITE);
		}
	}
}
