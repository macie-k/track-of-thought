package base.obj;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static base.Utils.COLOR_ACCENT;
import static base.Utils.COLOR_LEVEL;

import base.Log;
import base.Scenes;
import base.Window;

public class LevelPane extends StackPane {
	
	private final Rectangle container;	// rectangle button
	private final Text value;			// level number
			
	public LevelPane(int x, int y, int lvl) {
		
		setTranslateX(x);
		setTranslateY(y);
		
		container = new Rectangle(97, 70, COLOR_LEVEL);
		container.getStyleClass().add("container");
		
		String level = String.valueOf(lvl);
		value = new Text(level);
		value.getStyleClass().add("level-number");
		value.setFont(Font.font("Poppins Light"));
		
		getChildren().addAll(container, value);
		
		setOnMouseEntered(event -> setHiglight(true));
		setOnMouseExited(event -> setHiglight(false));
		setOnMouseClicked(e -> {
			if(Window.levelCreator) {
				Window.setScene(Scenes.createLevel());
			} else {
				try { Window.game(Scenes.game(level.charAt(0))); } catch (Exception ex) {
					Log.error("Selected level is incorrect: " + ex);
				}
			}
		});
	}
	
	public String getValue() {
		return this.value.getText();
	}
			
	public void setHiglight(boolean highlight) {
		if(highlight) {
			container.setStyle("-fx-cursor: hand;");
			container.setFill(COLOR_ACCENT);
			value.setFill(COLOR_LEVEL);
		} else {
			container.setStyle("-fx-cursor: default;");
			container.setFill(COLOR_LEVEL);
			value.setFill(COLOR_ACCENT);
		}
	}
}
