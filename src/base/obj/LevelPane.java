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
			
	public LevelPane(int x, int y, int num) {
		
		setTranslateX(x);
		setTranslateY(y);
		
		container = new Rectangle(97, 70, COLOR_CONTAINER);
		container.getStyleClass().add("container");
		
		value = new Text(String.valueOf(num));
		value.getStyleClass().add("level-number");
		value.setFont(Font.font("Poppins Light"));
		
		getChildren().addAll(container, value);
		
		setOnMouseEntered(event -> this.setHiglight(true));
		setOnMouseExited(event -> this.setHiglight(false));
//		setOnMouseClicked(event -> {Window.setScene(Scenes.game(getValue()));});
		setOnMouseClicked(event -> {Window.setScene(Scenes.tutorial());});
	}
	
	public String getValue() {
		return this.value.getText();
	}
			
	public void setHiglight(boolean highlight) {
		if(highlight) {
			this.container.setStyle("-fx-cursor: hand;");
			this.container.setFill(COLOR_ACCENT);
			this.value.setFill(Color.web(BACKGROUND));
		} else {
			this.container.setStyle("-fx-cursor: default;");
			this.container.setFill(COLOR_CONTAINER);
			this.value.setFill(Color.web("#C7B59D"));
		}
	}
}
