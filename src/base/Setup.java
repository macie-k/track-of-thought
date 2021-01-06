package base;

import java.io.File;

import javafx.scene.text.Font;

import static base.Utils.PATHS_TO_LOAD;
import static base.Utils.PATH_PUBLIC_DATA;
import static base.Utils.createFolder;
import static base.Utils.createData;
import static base.Utils.isCorrectKey;

public class Setup {
			
	static void runSetup() {	
		Log.success("Detected OS: " + Utils.OS);
		
		for(String dir : PATHS_TO_LOAD) {		// create all necessary folders if dont exist
			createFolder(dir);
		}
		
		loadFonts();			// load all required fonts
		checkProgress();		// check progress data

		Window.setScene(Window.levelCreator ? Scenes.createLevel() : Scenes.intro());
	}
	
	private static void loadFonts() {
		/* list of required font names */
		String[] fontNames = {
				"Poppins-Light.ttf",
				"HindGuntur-Bold.ttf"
		};
		
		/* load each font */
		for(String font : fontNames) {
			try {
				Font.loadFont(Setup.class.getResourceAsStream("/resources/data/fonts/" + font), 20);
			} catch (Exception e) {
				Log.error(String.format("Unable to load font {%s}: {%s}", font, e.getMessage()));
			}
		}		
	}
	
	private static void checkProgress() {
		if(!new File(PATH_PUBLIC_DATA).exists()) {
			Log.warning("Progress data file doesn't exist, creating ...");
			createData();
		} else {
			if(isCorrectKey()) {
				Log.success("Data key is correct");
			} else {
				Log.warning("Data key is incorrect, reseting ...");
				createData();
			}
		}
	}
}
