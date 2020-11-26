package base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import base.obj.GridSquare;
import javafx.scene.paint.Color;

public class Utils {
	
	public final static Color COLOR_LEVEL = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
//	public final static Color COLOR_BACKGROUND = Color.web("#363638");
	public final static Color COLOR_BACKGROUND = Color.web("#2D2E37");

	public final static Color BLACK = Color.web("#101114");
	public final static Color RED = Color.web("#F44241");
	public final static Color GREEN = Color.web("#57E669");
	public final static Color BLUE = Color.web("#4487F3");
	public final static Color CYAN = Color.web("#79FFFA");
	public final static Color PINK = Color.web("#AA46F1");
	public final static Color YELLOW = Color.web("#EBF14A");
	
	
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
				Log.error("Wrong direction @getDirectionToInt");
				return -1;
		}
	}
	
	/* returns color from a string value */
	public static Color parseColorName(String colorName) {
		/* if the given color string has a border information, strip it to colorname-only string */
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
            InputStream in = Utils.class.getResourceAsStream("/resources/" + path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
	        String resource;
	        while ((resource = br.readLine()) != null) {
	            filenames.add(resource);
	        }
		} catch (Exception e) {
			Log.error(e.toString());
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
}
