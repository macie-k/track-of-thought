package base;

import javafx.scene.Scene;

public class Selection {
	
	static void selectLevel() {
		Scene scene = Scenes.getSceneWithCSS(Scenes.levels(), "levels.css");
		Window.window.setScene(scene);
		
		
	}

}
