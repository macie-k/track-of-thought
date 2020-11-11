package base;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.text.Font;

import static base.Window.saveDirectory;

public class Setup {
		
	static void runSetup() {
		new File(saveDirectory).mkdir();		// create main directory if doesn't exist
		loadFonts();							// load all required fonts
		
		Window.setScene(Scenes.levels());
	}
	
	static void loadFonts() {
		/* list of required font names */
		String[] fontNames = {
				"Poppins-Light.ttf",
				"HindGuntur-Bold.ttf"
		};
		
		/* load each font */
		for(String font : fontNames) {
			Font.loadFont(Setup.class.getResourceAsStream("/resources/fonts/" + font), 20);
		}		
	}
		
	@SuppressWarnings("unused")
	private static void createDirectory(String path) {
		String finalPath = saveDirectory + "/" + path;				// build final path
		
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
		String finalPath = String.format("%s/%s/%s", saveDirectory, dir, filename);
		
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
	
	private static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
