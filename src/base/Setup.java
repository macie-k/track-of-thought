package base;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.text.Font;

import static base.Utils.PATHS_TO_LOAD;
import static base.Utils.PATH_ROOT;
import static base.Utils.fileExists;
import static base.Utils.createFolder;

public class Setup {
		
	static void runSetup() {	
		Log.success("Detected OS: " + Utils.OS);
		loadFonts();							// load all required fonts
		for(String dir : PATHS_TO_LOAD) {		// create all necessary folders if dont exist
			createFolder(dir);
		}
		
		Window.setScene(Scenes.levels());
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
				Font.loadFont(Setup.class.getResourceAsStream("/resources/fonts/" + font), 20);
			} catch (Exception e) {
				Log.error(String.format("Unable to load font {%s}: {%s}", font, e));
			}
		}		
	}
		
	@SuppressWarnings("unused")
	private static void createDirectory(String path) {
		String finalPath = PATH_ROOT + "/" + path;				// build final path
		
		if(!fileExists(finalPath)) {								// if directory doesn't exist
			if(new File(finalPath).mkdir()) {						// try to create
				Log.success("Successfully created " + finalPath);	// log success
			} else {												
				Log.error("Could not create " + finalPath);			// print error if failed
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void downloadFile(String url, String dir, String filename) {
		String finalPath = String.format("%s%s/%s", PATH_ROOT, dir, filename);
		
		if(!fileExists(finalPath)) {
			try {
				URL link = new URL(url);
				InputStream IS = link.openStream();
				Files.copy(IS, Paths.get(finalPath));
				Log.success("Successfully downloaded: {" + filename + "}");
				IS.close();
			} catch (Exception e) {
				Log.error(e.toString());
			}
		} else {
			Log.success("{" + filename + "} already downloaded");
		}
	}
	
}
