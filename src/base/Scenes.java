package base;

import base.obj.LevelPane;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Scenes {
	
	public final static String BACKGROUND = "#363638";
	public final static Color COLOR_CONTAINER = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
	
	public static Pane levels() {
		
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setId("pane");
			
		Text title = new Text("SELECT LEVEL");
			title.setId("title");
			title.setFont(Font.font("Hind Guntur Bold"));
			title.setTranslateX(97);
			title.setTranslateY(150);
			
		root.getChildren().add(title);
			
		for(int i=0; i<12; i++) {
			root.getChildren().add(new LevelPane(i<=5 ? 55+i*120: 55+(i-6)*120, i<=5 ? 250 : 350, i+3));
		}
		
		return root;
	}
	
	public static Pane game(String level) {
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
			
		Text lvl = new Text(level);
			lvl.setFont(Font.font("Hind Guntur Bold", 50));
			lvl.setFill(Color.WHITE);
			lvl.setTranslateX((800 - lvl.getLayoutBounds().getWidth())/2);
			lvl.setTranslateY(250);
			
		root.getChildren().add(lvl);
		return root;
	}
	
	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(Window.class.getResource("/resources/styles/" + cssFile).toExternalForm());
		return scene;
	}
}
