package com.trackofthought;

import static com.trackofthought.Utils.PATHS_TO_LOAD;
import static com.trackofthought.Utils.PATH_PUBLIC_DATA;
import static com.trackofthought.Utils.createData;
import static com.trackofthought.Utils.createFolder;
import static com.trackofthought.Utils.isCorrectKey;

import java.io.File;
import javafx.scene.text.Font;

public class Setup {

    static void runSetup() {
        Log.success("Detected OS: " + Utils.OS);

        for (String dir : PATHS_TO_LOAD) { // create all necessary folders if they don't exist
            createFolder(dir);
        }

        loadFonts(); // load all required fonts
        checkProgress(); // check progress data

        Window.setScene(Window.levelCreator ? Scenes.createLevel() : Scenes.intro());
    }

    private static void loadFonts() {
        /* list of required font names */
        String[] fontNames = {"Poppins-Light.ttf", "HindGuntur-Bold.ttf"};

        /* load each font */
        for (String font : fontNames) {
            try {
                Font.loadFont(Setup.class.getResourceAsStream("/data/fonts/" + font), 20);
            } catch (Exception e) {
                Log.error(String.format("Unable to load font {%s}: {%s}", font, e.getMessage()));
            }
        }
    }

    private static void checkProgress() {
        if (!new File(PATH_PUBLIC_DATA).exists()) {
            Log.warning("Progress data file doesn't exist, creating ...");
            createData();
        } else {
            if (isCorrectKey()) {
                Log.success("Data key is correct");
            } else {
                Log.warning("Data key is incorrect, resetting ...");
                createData();
            }
        }
    }
}
