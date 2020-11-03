package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.text.Font;

import static base.Window.saveDirectory;

public class Setup {
		
	static void runSetup() {
		new File(saveDirectory).mkdir();		// create main directory if doesn't exist
		loadFonts();							// load all required fonts
		
		Window.setScene(Scenes.levels());
//		Window.setScene(Scenes.tutorial());
	}
	
	static void loadFonts() {
		createDirectory("fonts");		// create directory if doesn't exist
		String[] fontNames = {			// all fonts required
				"Poppins-Light.ttf",
				"HindGuntur-Bold.ttf"
		};
		
		for(String font : fontNames) {	// download each font
			try {
				String encodedName = URLEncoder.encode(font, "UTF-8").replace("+", "%20%"); 
				downloadFile(
					"https://kazmierczyk.me/--trackOfThought/fonts/" + encodedName,	// dedicated hosting url
					"fonts",														// directory
					font															// fontname
				);
				InputStream IS = new FileInputStream(saveDirectory + "/fonts/" + font);
				Font.loadFont(IS, 20); 
			} catch (Exception e) {
				Log.error(e.toString());
			}
		}		
	}
		
	private static void createDirectory(String path) {
		String finalPath = saveDirectory + "/" + path;		// build final path
		
		if(!fileExists(finalPath)) {								// if directory doesn't exist
			if(new File(finalPath).mkdir()) {						// try to create
				Log.success("Successfully created " + finalPath);	// log success
			} else {												
				Log.error("Could not create " + finalPath);			// print error if failed
			}
		}
	}
	
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
