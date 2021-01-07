package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

import base.obj.GridSquare;
import base.obj.Track;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Utils {
	
	public static final String OS = System.getProperty("os.name").toLowerCase();	// get current operating system
	public static final boolean WINDOWS = !OS.equals("linux");
	
	public static final String PATH_ROOT = WINDOWS ? (System.getenv("APPDATA") + "/track_of_thought/") : (System.getenv("HOME") + "/.local/share/track_of_thought/");
	public static final String PATH_LEVELS = PATH_ROOT + "levels/";
	public static final String PATH_LEVELS_CUSTOM = PATH_LEVELS + "custom/";
	public static final String PATH_TEMPLATE_DATA = "/resources/data/data";
	public static final String PATH_PUBLIC_DATA = PATH_ROOT + "data";
	public static final String[] PATHS_TO_LOAD = {PATH_ROOT, PATH_LEVELS, PATH_LEVELS_CUSTOM};

	public final static Color COLOR_LEVEL = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
	public final static Color COLOR_BACKGROUND = Color.web("#2D2E37");
//	public final static Color COLOR_BACKGROUND = Color.web("#363638");

	public final static Color BLACK = Color.web("#101114");
	public final static Color RED = Color.web("#F44241");
	public final static Color GREEN = Color.web("#57E669");
	public final static Color BLUE = Color.web("#3F6DE0");
	public final static Color CYAN = Color.web("#79FFFA");
	public final static Color PINK = Color.web("#AA46F1");
	public final static Color YELLOW = Color.web("#F5EF42");
	public final static Color ORANGE = Color.web("#FFAA00");
	
	public final static List<Color> COLORS_BASE = Arrays.asList(new Color[] {
			RED, GREEN, BLUE, CYAN, PINK, YELLOW, ORANGE
	});
	public final static List<String> COLORS_STR = Arrays.asList(new String[] {
			"red", "green", "blue", "cyan", "yellow", "pink", "orange",
			"red + o", "green + o", "blue + o", "cyan + o", "yellow + o", "pink + o", "orange + o"
	});
	
	
	public static int checkArgumentWithValue(String name, String nargs, String[] args, int counter) {
		final String[] required = nargs.split(" ");
		
		if(counter+required.length >= args.length) {
			Log.error(String.format("%s: %d values required", name, required.length), true);
			return args.length - (counter+required.length);
		}
		
		for(String val : required) {
			try {
				switch(val) {
					case "int":
						Integer.parseInt(args[++counter]);
						break;
					case "boolean":
						Boolean.parseBoolean(args[++counter]);
						break;
				}
			} catch(NumberFormatException e) {
				Log.error(String.format("%s: Value must be type of {%s}", name, nargs), true);
				return 0;
			}
		}
		
		return required.length;
	}

	
	/* fades in given node */
	public static void fadeIn(Node node, int duration) {
		FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
			ft.setFromValue(node.getOpacity());
		    ft.setToValue(1);
		    ft.play();
	}
	
	/* animates color change */
	public static void fadeColors(Shape shape, int duration, Color from, Color to) {
		FillTransition ft = new FillTransition(Duration.millis(duration), shape, from, to);
			ft.play();
	}
	
	public static void resetLevel() {
		try {
			setDataProgress(3);
			Log.success("Succesfully reset levels");
		} catch (Exception e) {
			Log.error("Could not unlock level: " + e);
		}
	}
	
	/* for developement only */
	public static void forceUnlockLevel(int level) {
		try {
			setDataProgress(level);
		} catch (Exception e) {
			Log.error("Could not unlock level: " + e);
		}
	}
	
	/* returns true only if level was unlocked */
	public static boolean unlockLevel(boolean unlock, int currentLevel) {
		if(unlock) {
			try {
				if((currentLevel < 14) && (getProgress() == currentLevel)) {
					final int nextLevel = currentLevel + 1;
					setDataProgress(nextLevel);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Log.error("Could not unlock level: " + e);
				return false;
			}
		} else {
			return false;
		}
	}
		
	/* creates data file and sets new key */
	public static void createData() {		
		try {
			/* copy template data to byte array */
			InputStream privateData = Setup.class.getResourceAsStream(PATH_TEMPLATE_DATA);
				byte[] privateBytes = new byte[privateData.available()];
				privateData.read(privateBytes);
				privateData.close();

			/* write byte array to data file in game folder */
			FileOutputStream publicData = new FileOutputStream(PATH_PUBLIC_DATA);
				publicData.write(privateBytes);
				publicData.close();
				
			/* set new key */
			if(setDataKey(getNewKey())) {
				Log.success("Successfully created data file");
			}
		} catch (IOException e) {
			Log.error("Could not create data file: " + e);
		}
	}
	
	/* returns true only if data key was read and is correct, false otherwise */
	public static boolean isCorrectKey() {
		try {
			final String key = getDataResults()[0];						// get stored key
			final int x = Character.getNumericValue(key.charAt(0));		// extract prefix
			final String encodedKey = key.substring(1, key.length());	// extract encoded key
			
			final long modTime = new File(PATH_PUBLIC_DATA).lastModified();					// get current modification time
			final String decodedKey = new String(Base64.getDecoder().decode(encodedKey));	// get currently saved key
			final double decodedTime = Double.valueOf(decodedKey)/Math.pow(Math.E, x);		// decode the key

			return modTime == decodedTime;	// check if flags are the same
		} catch (Exception e) {
			Log.error("Could not verify data key: " + e);
			return false;
		}
	}
	
	/* generate new key for data file */
	public static String getNewKey() {
		Random r = new Random();
		
		final long modTime = new File(PATH_PUBLIC_DATA).lastModified();		// get current modification time
		final int x = r.nextInt(9)+1;										// get random exponent
		final double calculatedKey = modTime*Math.pow(Math.E, x);			// calculate key
		
		return (x + Base64.getEncoder().encodeToString(String.valueOf(calculatedKey).getBytes()));	// return key in base64
	}
	
	/* sets data progress value */
	public static boolean setDataProgress(int level) {
		final File data = new File(PATH_PUBLIC_DATA);	// get data file
		final long modTime = data.lastModified();		// get modification time
		final String sql = "UPDATE progress SET level=?";

		try {
			/* execute query to set the level value */
			Statement st = getDataConnection(PATH_PUBLIC_DATA).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
			
			ps.setInt(1, level);
			ps.executeUpdate();
				ps.close();
				st.close();
			
			data.setLastModified(modTime);	// restore  modification time
			return true;
		} catch (SQLException e) {
			Log.error("Could not set progress: " + e);
			return false;
		}
	}
	
	/* sets data key value */
	public static boolean setDataKey(String value) {
		final File data = new File(PATH_PUBLIC_DATA);	// get data file
		final long modTime = data.lastModified();		// get modification time
		final String sql = "UPDATE progress SET safekey=?";
		
		try {
			/* execute query to set the level value */
			Statement st = getDataConnection(PATH_PUBLIC_DATA).createStatement();
			PreparedStatement ps = st.getConnection().prepareStatement(sql);
			
			ps.setString(1, value);
			ps.executeUpdate();
				ps.close();
				st.close();
			
			data.setLastModified(modTime);	// restore  modification time
			return true;
		} catch (SQLException e) {
			Log.error("Could not set key: " + e);
			return false;
		}
	}
	
	/* returns progress value */
	public static int getProgress() throws Exception {
		return Integer.valueOf(getDataResults()[1]);
	}
	
	/* returns key & progress values */
	public static String[] getDataResults() throws Exception {
		try {
			Statement st = getDataConnection(PATH_PUBLIC_DATA).createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM progress");
				rs.next();

			final String key = rs.getString("safekey");
			final String level = rs.getString("level");
			
			st.close();
			return new String[] {key, level};
		} catch (SQLException e) {
			throw new Exception("Could not get results from data file: " + e.getMessage());
		}
	}
	
	/* returns connection for data reading & writing */
	public static Connection getDataConnection(String path) throws SQLException {
		return DriverManager.getConnection("jdbc:ucanaccess://" + path);
	}
	
	/* returns centered alignment position from given int */ 
	public static Pos getDirectionToPos(int exit) {
		switch(exit) {
			case 0: 	
				return Pos.TOP_CENTER; 		// 0 -> top
			case 1:
				return Pos.CENTER_RIGHT;	// 1 -> right
			case 2:
				return Pos.BOTTOM_CENTER;	// 2 -> bottom
			case 3:
				return Pos.CENTER_LEFT;		// 3 -> left
			default:
				return null;
		}
	}
	
	/* creates directory with feedback */
	public static void createDirectory(String path) {
		String finalPath = PATH_ROOT + "/" + path;					// build final path
		
		if(!fileExists(finalPath)) {								// if directory doesn't exist
			if(new File(finalPath).mkdir()) {						// try to create
				Log.success("Successfully created " + finalPath);	// log success
			} else {												
				Log.error("Could not create " + finalPath);			// print error if failed
			}
		}
	}
	
	/* downloads the file from given url */
	public static void downloadFile(String url, String dir, String filename) {
		String finalPath = String.format("%s%s/%s", PATH_ROOT, dir, filename);
		
		if(!fileExists(finalPath)) {
			try {
				URL link = new URL(url);
				InputStream IS = link.openStream();
				Files.copy(IS, Paths.get(finalPath));
				Log.success("Successfully downloaded: {" + filename + "}");
				IS.close();
			} catch (Exception e) {
				Log.error("Could not download file: " + e);
			}
		} else {
			Log.success("{" + filename + "} already downloaded");
		}
	}
		
	/* randomly switches tracks */
	public static void randomSwitchTracks(List<Track> tracks) {
		Random r = new Random();
		for(Track t : tracks) {
			if(r.nextBoolean()) {
				try {
					t.changeType();
				} catch (Exception e) {}
			}
		}
	}
	
	/* returns list of colors for stations with borders */
	public static List<String> getRandomBorderColors(int amount) {
		final List<String> newColors = new ArrayList<>();
		final List<String> allColors = new ArrayList<>(COLORS_STR);
			allColors.removeIf(color -> !color.contains("+"));
		
		Random r = new Random();
		while(newColors.size() < amount) {
			final int index = r.nextInt(allColors.size());
			newColors.add(allColors.get(index));
			allColors.remove(index);
		}
		
		return newColors;
	}
			
	/* returns list of colors for stations */
	public static List<String> getRandomColors(int amount, List<String> toExclude, boolean prioritizeBase) {
		final List<String> newColors = new ArrayList<>();
		
		/* create a list of all remaining colors */
		final List<String> allColors = new ArrayList<>(COLORS_STR);
			allColors.removeAll(toExclude);
			
		/* create list of only base colors */
		final List<String> baseColors = new ArrayList<>(allColors);
			baseColors.removeIf(color -> color.contains("+"));
			
		Random r = new Random();
		if(prioritizeBase) {
			final List<String> copyOfBaseColors = new ArrayList<>(baseColors);
			while(newColors.size() < amount && copyOfBaseColors.size() > 0) {
				final int index = r.nextInt(copyOfBaseColors.size());
				newColors.add(copyOfBaseColors.get(index));
			}
		}
		if(newColors.size() < amount) {
			allColors.removeAll(baseColors);
			
			while(newColors.size() < amount) {
				final int index = r.nextInt(allColors.size());
				newColors.add(allColors.get(index));
				allColors.remove(index);
			}
		}
		return newColors;
	}
	
	/* fallback for different arguments */
	
	public static List<String> getRandomColors(int amount, boolean prioritizeBase) {
		return getRandomColors(amount, new ArrayList<String>(), prioritizeBase);
	}
	
	public static List<String> getRandomColors(int amount, List<String> toExclude) {
		return getRandomColors(amount, toExclude, false);
	}
	
	public static List<String> getRandomColors(int amount) {
		return getRandomColors(amount, new ArrayList<String>(), false);
	}
			
	/* returns integer direction from a string value */
	public static int parseDirectionToInt(String dir) {
		switch(dir) {
			case "top":
				return 0;
			case "right":
				return 1;
			case "bottom":
				return 2;
			case "left":
				return 3;
			default:
				Log.error("Wrong direction");
				return -1;
		}
	}
	
	/* returns array with color name and boolean value of border */
	public static Object[] parseColorWithBorder(String colorName) {
		Color color = parseColorName(colorName);
		return new Object[] {color, colorName.contains("+")};
	}
	
	/* returns color from a string value */
	public static Color parseColorName(String colorName) {
		/* if the given color string has a border information, strip it to colorname-only string */
		if(colorName == null) {
			return null;
		}
		if(colorName.contains("+")) {
			colorName = colorName.split("\\+")[0].trim();
		}
		switch(colorName.toUpperCase()) {
			case "BLACK":
				return BLACK;
			case "RED":
				return RED;
			case "GREEN":
				return GREEN;
			case "BLUE":
				return BLUE;
			case "CYAN":
				return CYAN;
			case "PINK":
				return PINK;
			case "YELLOW":
				return YELLOW;
			case "ORANGE":
				return ORANGE;
			default:
				return Color.TRANSPARENT;
		}
	}
	
	/* returns (x from column) or (y from row) */
	public static int getXYFromRowCol(int rowcol) {
		return (rowcol + 1)*50;
	}
	
	/* returns (column from x) or (row from y) */
	public static int getColRowFromXY(int xy) {
		return xy/50-1;
	}
	
	/* same for double argument */
	public static int getColRowFromXY(double xy) {
		return getColRowFromXY((int) xy);
	}
	
	/* returns list of files in resources folder + relative path */
	public static List<String> getAllResourceFiles(String path) {
		List<String> filenames = new ArrayList<>();

		try {
            InputStream in = Scenes.class.getResourceAsStream("/resources/" + path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
	        String resource;
	        while ((resource = br.readLine()) != null) {
	            filenames.add(resource);
	        }
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	    return filenames;
	}
	
	/* returns full grid */
	public static GridSquare[][] getGrid() {
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	/* returns all keys for given json object */
	public static List<String> getAllJsonKeys(JSONObject json) {
		List<String> keys = new ArrayList<String>();
		Map<String, Object> mapObject = json.toMap();
				
        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
            keys.add(entry.getKey());
        }
        
        Collections.sort(keys);
        return keys;
	}
	
	/* returns json structure from file */
	public static JSONObject getJsonFromFile(String path) {
		InputStream stream = Scenes.class.getResourceAsStream(path);
		return new JSONObject(new JSONTokener(stream));
	}
	
	/* returns all values for a given key from a given object */
	public static List<Object> getAllJsonValues(JSONObject json, String key) {
		return json.getJSONArray(key).toList();
	}

	public static void createFolder(String path) {
		new File(path).mkdir();
	}
	
	public static boolean fileExists(String name) {
		return new File(name).exists();
	}
}
