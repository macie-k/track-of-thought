package base;

import base.obj.LevelPane;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class Scenes {
	
	public static final String BACKGROUND = "#1F2227";
	
	public static Pane levels() {
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setId("pane");
		
		root.getChildren().add(new LevelPane(345, 215, 1));
		return root;
	}
	
	public static Pane game() {
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
		return root;
	}
	
	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(Window.class.getResource("/resources/styles/" + cssFile).toExternalForm());
		return scene;
	}
}
