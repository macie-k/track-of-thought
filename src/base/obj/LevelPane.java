package base.obj;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static base.Scenes.BACKGROUND;
import static base.Scenes.COLOR_ACCENT;
import static base.Scenes.COLOR_CONTAINER;

import base.Scenes;
import base.Window;

public class LevelPane extends StackPane {
	
	private final Rectangle container;	// rectangle button
	private final Text value;			// level number
			
	public LevelPane(int x, int y, int lvl) {
		
		setTranslateX(x);
		setTranslateY(y);
		
		container = new Rectangle(97, 70, COLOR_CONTAINER);
		container.getStyleClass().add("container");
		
		String level = String.valueOf(lvl);
		value = new Text(level);
		value.getStyleClass().add("level-number");
		value.setFont(Font.font("Poppins Light"));
		
		getChildren().addAll(container, value);
		
		setOnMouseEntered(event -> setHiglight(true));
		setOnMouseExited(event -> setHiglight(false));
		setOnMouseClicked(event -> {Window.game(Scenes.game(level));});
	}
	
	public String getValue() {
		return this.value.getText();
	}
			
	public void setHiglight(boolean highlight) {
		if(highlight) {
			container.setStyle("-fx-cursor: hand;");
			container.setFill(COLOR_ACCENT);
			value.setFill(Color.web(BACKGROUND));
		} else {
			container.setStyle("-fx-cursor: default;");
			container.setFill(COLOR_CONTAINER);
			value.setFill(Color.web("#C7B59D"));
		}
	}
}
