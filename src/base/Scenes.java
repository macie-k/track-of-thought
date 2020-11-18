package base;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import base.obj.Ball;
import base.obj.FullTrack;
import base.obj.GridSquare;
import base.obj.LevelPane;
import base.obj.Station;
import base.obj.Track;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Scenes {
		
	
	public final static Color COLOR_CONTAINER = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
	
	final static Color BLACK = Color.web("#101114");
	final static Color RED = Color.web("#F44241");
	final static Color GREEN = Color.web("#57E669");
	final static Color BLUE = Color.web("#4487F3");
	final static Color CYAN = Color.web("#79FFFA");
	final static Color PINK = Color.web("#AA46F1");
	final static Color YELLOW = Color.web("#EBF14A");
	
	public final static String BACKGROUND = "#363638";
	public final static GridSquare[][] GRID = getGrid();
	
	public final static String S = "straight";
	public final static String C = "curved";
		
	private static double createX, createY, overlayX, overlayY;
	private static String createObjectStr;
	private static JSONObject createObject;
	private static List<String> allProperties;
	private static Map<String, String> currentProperties = new HashMap<String, String>();
	private static ArrayList<Map<String, String>> listMap = new ArrayList<>();
	private static int objectIndex = 0;
	private static Text menuObjectArrowRight, menuObjectArrowLeft;
	
	public static Scene levels() {
		Pane root = getRootPane();
				
		StackPane titleContainer = new StackPane();
			titleContainer.setPrefWidth(850);
			titleContainer.setTranslateX(0);
			titleContainer.setTranslateY(60);
		Text title = new Text("SELECT LEVEL");
			title.setId("title");
			title.setFont(Font.font("Hind Guntur Bold"));
			
		for(int i=0; i<12; i++) {
			root.getChildren().add(new LevelPane(i<=5 ? 75+i*120: 75+(i-6)*120, i<=5 ? 270 : 370, i+3));
		}
		
		titleContainer.getChildren().add(title);
		root.getChildren().add(titleContainer);
		return getSceneWithCSS(root, "levels.css");
	}
	
	public static FullTrack game(String level) {
		int random = new Random().nextInt(3)+1;
		InputStream stream = Scenes.class.getResourceAsStream(String.format("/resources/levels/%s-%d.level", level, random));
		Log.success(String.format("Selected level: %s-%d", level, random));
		
		JSONObject json = new JSONObject(new JSONTokener(stream));
		JSONArray stationsJson = json.getJSONArray("stations");
		JSONArray tracksJson = json.getJSONArray("tracks");
		JSONArray ballsJson = json.getJSONArray("balls");
		
		List<Station> stations = new ArrayList<Station>();
		List<Track> tracks = new ArrayList<Track>();
		List<Ball> balls = new ArrayList<Ball>();
		
		Station startStation = null;
		for(Object station : stationsJson) {
			JSONObject obj = (JSONObject) station;
			
			Color color = parseColorName(obj.getString("color"));
			boolean border = obj.getString("color").contains("+");
			int column = obj.getInt("column");
			int row = obj.getInt("row");
			int exit;
			
			if(obj.get("type").equals("start")) {
				exit = parseDirection(obj.getString("exit"));
				startStation = new Station(column, row, color, exit, border);
			} else {
				exit = -1;
			}
			stations.add(new Station(column, row, color, exit, border));
		}

		for(Object track : tracksJson) {
			JSONObject obj = (JSONObject) track;
			
			boolean switchable = obj.getBoolean("switch");
			String type = obj.getString("type");
			int column = obj.getInt("column");
			int row = obj.getInt("row");
			int origin = parseDirection(obj.getString("start"));
			int end1 = parseDirection(obj.getString("end1"));
			int end2 = switchable ? parseDirection(obj.getString("end2")) : -1;
			
			tracks.add(new Track(GRID[column][row].getPos(), type, origin, end1, end2));							
		}
		
		double[] startCoords = startStation.getXY();
		for(Object ball : ballsJson) {
			JSONObject obj = (JSONObject) ball;
			
			int delay = obj.getInt("delay");
			boolean border = obj.getString("color").contains("+");
			Color color = parseColorName(obj.getString("color"));
			
			balls.add(new Ball(startCoords, color, tracks, delay, border));
		}
		
		return new FullTrack(stations, tracks, balls);
	}
	
	public static void drawPath(Track[] tracks, Pane root) {
		for(Track track : tracks) {
			double[][] path = track.getPath();
			for(int j=0; j<50; j++) {
				Rectangle r = new Rectangle(1, 1);
					r.setTranslateX(path[0][j]);
					r.setTranslateY(path[1][j]);
					r.setFill(Color.rgb(4*j, 3*j, 2*j));
				root.getChildren().add(r);
			}
		}
	}
	
	/*

		- Objects' structure & properties are avaialable at: resources/structure.json
		- Color names that contain '+O' are colors with white border
			The suffix is omitted during conversion @parseColorName() and passed to .level file in json format

	 */
	
	public static Scene createLevel() {
		Pane root = getRootPane();
		
		String[] menuObjects= {"track", "station"};				// available objects as menu "pages"
		JSONObject jsonObjects = getJsonMenuObjects();			// get general object from structure.json
		List<GridSquare> grid = new ArrayList<GridSquare>();	// list containing all gridSquares
		
		createObjectStr = menuObjects[objectIndex];					// current object as string
		createObject = jsonObjects.getJSONObject(createObjectStr);	// current object as json object
		allProperties = getAllKeys(createObject);					// get properties of current obect
		
		/* StackPane containing menu when any square is clicked */
		StackPane menuStack = new StackPane();
			menuStack.setPrefSize(200, 170);
			menuStack.setVisible(false);
			menuStack.setAlignment(Pos.TOP_LEFT);
			
		/* background for menu */
		Rectangle menuBg = new Rectangle(200, 170);
			menuBg.setFill(Color.web("#4B4E54"));
			menuBg.setStroke(COLOR_ACCENT);
			
		/* label "OBJECT" */
		Text menuObjectText = new Text("OBJECT ");
			menuObjectText.setTranslateX(10);
			menuObjectText.setTranslateY(5);
			menuObjectText.getStyleClass().add("menuText");
					
		/* text with currently selected object */
		Text menuObject = new Text(createObjectStr.toUpperCase());
			menuObject.setTranslateX(110);
			menuObject.setTranslateY(5);
			menuObject.getStyleClass().add("menuValue");
			
		/* button for adding the object */
		Text OK = new Text("[ OK ]");
			OK.setTranslateX(0);
			OK.setTranslateY(-10);
			OK.setId("OK");
			OK.setOnMouseClicked(e -> {
				/* save selected row & column */
				currentProperties.put("column", String.valueOf((int)(createX/50-1)));
				currentProperties.put("row", String.valueOf((int)(createY/50-1)));
									
				/* add temporary object to the grid */
				addNewObject(root, new HashMap<String, String>(currentProperties));
				menuStack.toFront();			// bring menu to the front
				menuStack.setVisible(false);	// and hide it
			});	
			StackPane.setAlignment(OK, Pos.BOTTOM_CENTER);
			
		/* StackPane for saving menu */
		StackPane SAVE_MENU = new StackPane();
			SAVE_MENU.setPrefSize(200, 120);
			SAVE_MENU.setTranslateX(325);
			SAVE_MENU.setTranslateY(215);
			SAVE_MENU.setVisible(false);
			
		/* background for saving menu */
		Rectangle SAVE_NAMEbg = new Rectangle(200, 120, Color.web("#4B4E54"));
			SAVE_NAMEbg.setTranslateX(0);
			SAVE_NAMEbg.setStroke(COLOR_ACCENT);
			SAVE_NAMEbg.setStrokeWidth(2);
			
		/* button to close the saving menu */
		Text SAVE_NAMEClose = new Text(" X ");
			StackPane.setAlignment(SAVE_NAMEClose, Pos.TOP_RIGHT);	// align it to top-right corner
			SAVE_NAMEClose.setTranslateX(-5);						// create small X padding
			SAVE_NAMEClose.setTranslateY(5);						// create small Y padding
			SAVE_NAMEClose.setFont(Font.font("Hind Guntur Bold"));
			SAVE_NAMEClose.setCursor(Cursor.HAND);
			SAVE_NAMEClose.setFill(Color.WHITE);
			SAVE_NAMEClose.setOnMouseClicked(e -> {
				root.getChildren().forEach(child -> child.setVisible(!child.equals(menuStack)));	// when [ x ] is clicked show everything except for the menu
				SAVE_MENU.setVisible(false);														// hide saving menu
			});
		
		/* label "NAME:" */
		Text levelNameLabel = new Text("NAME: ");
			levelNameLabel.setTranslateY(-35);
			levelNameLabel.setFont(Font.font("Poppins Light", 17));
			levelNameLabel.setFill(Color.WHITE);
			
		/* input area for entering level name */
		TextField levelName = new TextField();
			levelName.setMaxSize(150, 25);
			levelName.setTranslateY(-5);
			levelName.setStyle("-fx-faint-focus-color: transparent;"
					+ "-fx-focus-color: transparent;"
					+ "-fx-text-box-border: transparent;"
					+ "-fx-highlight-fill: #363638;"
					+ "-fx-highlight-text-fill: #FFF;");
					
		/* confirmation button StackPane -> triggers saving to .level file */
		StackPane CONFIRM = new StackPane();
			CONFIRM.setMaxSize(100, 25);
			CONFIRM.setPrefSize(100, 25);
			CONFIRM.setTranslateY(35);
			CONFIRM.setTranslateX(0);
			CONFIRM.setCursor(Cursor.HAND);
			CONFIRM.setOnMouseClicked(e -> {
				/* when clicked saves level to file and restores view */
				saveToJSON(levelName.getText());
				root.getChildren().forEach(child -> child.setVisible(!child.equals(menuStack)));
				SAVE_MENU.setVisible(false);
			});
		
		/* background and text for the confirmation button */
		Rectangle SAVE_NAMEButton = new Rectangle(100, 25, Color.web("#5beb82"));
		Text SAVE_NAMEButtonText = new Text("CONFIRM");
			SAVE_NAMEButtonText.setFont(Font.font("Poppins Light", 15));
			
		CONFIRM.getChildren().addAll(SAVE_NAMEButton, SAVE_NAMEButtonText);	
		SAVE_MENU.getChildren().addAll(SAVE_NAMEbg, levelName, levelNameLabel, SAVE_NAMEClose);	
		SAVE_MENU.getChildren().add(CONFIRM);		
			
		/* StackPane for save button -> opens saving menu */
		StackPane SAVE = new StackPane();
			SAVE.setTranslateX(375);
			SAVE.setTranslateY(505);
			SAVE.setPrefSize(100, 40);
			SAVE.setCursor(Cursor.HAND);
			SAVE.setOnMouseClicked(e -> {
				/* hide everything & show saving menu */
				root.getChildren().forEach(child -> child.setVisible(false));
				SAVE_MENU.setVisible(true);
			});
		
		/* background and text for the save button */
		Rectangle SAVEbg = new Rectangle(100, 40, Color.web("#5beb82"));
			SAVEbg.setTranslateX(0);
		Text SAVEtext = new Text("SAVE");
			SAVEtext.setFont(Font.font("Poppins Light", 17));
					
		SAVE.getChildren().addAll(SAVEbg, SAVEtext);
					
		/* left arrow for navigating through available objects */
		menuObjectArrowLeft = new Text("<");
			menuObjectArrowLeft.setTranslateX(90);
			menuObjectArrowLeft.setTranslateY(5);
			menuObjectArrowLeft.getStyleClass().add("arrow");
			menuObjectArrowLeft.setOnMouseClicked(e -> {
				objectIndex = (objectIndex != 0) ? objectIndex : menuObjects.length;	// some weird looping
				createObjectStr = menuObjects[--objectIndex];							// save current object as string
				createObject = jsonObjects.getJSONObject(createObjectStr);				// save current object as json object
				menuObject.setText(createObjectStr.toUpperCase());						// update the text value
				
				updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
			});
			
		/* right arrow for navigating through available objects */
		menuObjectArrowRight = new Text(">");
			menuObjectArrowRight.setTranslateX(180);
			menuObjectArrowRight.setTranslateY(5);
			menuObjectArrowRight.getStyleClass().add("arrow");
			menuObjectArrowRight.setOnMouseClicked(e -> {
				/* same here but vice versa */
				objectIndex = (objectIndex < menuObjects.length-1) ? objectIndex : -1;
				createObjectStr = menuObjects[++objectIndex];
				createObject = jsonObjects.getJSONObject(createObjectStr);
				menuObject.setText(createObjectStr.toUpperCase());
				
				updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
			});
								
		/* create visible grid and assign click listener */
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				GridSquare gridSq = new GridSquare(i, j, true);
				gridSq.setOnMouseClicked(e -> {
					/* save X and Y of current square */
					createX = gridSq.getPos()[0];
					createY = gridSq.getPos()[1];
					
					/* calculate X and Y for the menu to avoid partially rendering outside of the window */
					overlayX = gridSq.getPos()[0] - 75;
						if(overlayX == 675) overlayX -= 25;
						if(overlayX == -25) overlayX += 25;
					
					overlayY = gridSq.getPos()[1] + 50;
						if(overlayY >= 400) overlayY -= 220;
					
					/* show menu at calculated coordinates */
					menuStack.setTranslateX(overlayX);
					menuStack.setTranslateY(overlayY);
					menuStack.setVisible(true);
					
					/* css styling for marking currently selected square */
					grid.forEach(gridSqare -> gridSqare.setId(""));
					gridSq.setId("current");
					
					/* refreshes menu each time a square is clicked */
//					updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
				});
				
				grid.add(gridSq);
			}
		}
		
		/* add everything to their stacks and finally to the root Pane */
		menuStack.getChildren().addAll(menuBg, menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, OK);
		updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
		
		root.getChildren().addAll(grid);
		root.getChildren().addAll(SAVE);
		root.getChildren().addAll(SAVE_MENU);
		root.getChildren().add(menuStack);
		
		return getSceneWithCSS(root, "createLevel.css");
	}
	
	/* creates temporary object for createLevel() scene */
	private static void addNewObject(Pane root, Map<String, String> obj) {
		String object = obj.get("object");						// get the object type
		int[] xy = new int[] {(int)createX, (int)createY};		// get the coordinates
		
		boolean success = true;		// assume adding was successful -> overwrite later if otherwise
		switch(object) {
			case "track": {
					/* get all track's properties */
					String type = obj.get("type");
					boolean switchable = obj.get("switch").equals("true");
					int start = parseDirection(obj.get("start"));
					int end1 = parseDirection(obj.get("end1"));
					int end2 = switchable ? parseDirection(obj.get("end2")) : -1;
					
					/* try to create the track -> if error is caught don't add to list */
					try {
						Track t = new Track(xy, type, start, end1, end2);
						t.debugDraw(root);			// comment to hide PATH DRAWING - can get annoying when deleting a lot
						
						/* listener to remove object and change type if clickable */
						t.setOnMouseClicked(e -> {
							/* if scroll is cliked remove else try to change type */
							if(e.getButton() == MouseButton.MIDDLE) {
								t.setVisible(false);
								listMap.remove(obj);
							} else {
								if(t.isClickable()) {
									t.changeType();
								}
							}
						});
						root.getChildren().add(t);
					} catch (Exception e) {
						success = false;										// overwrite success boolean
						Log.error("Could not add object, check parameters");	// log the error
					}
				} break;
				
			case "station": {
					/* same as above */
					String type = obj.get("type");
					boolean start = type.equals("start");
					boolean border = obj.get("color").contains("+");
					int exit = start ? parseDirection(obj.get("exit")) : -1;
					Color color = start ? parseColorName("black") : parseColorName(obj.get("color"));
					
					try {
						Station s = new Station(xy, color, exit, border);
						s.setOnMouseClicked(e -> {
							if(e.getButton() == MouseButton.MIDDLE) {
								s.setVisible(false);
								listMap.remove(obj);
							}
						});
						root.getChildren().add(s);
					} catch (Exception e) {
						success = false;
						Log.error("Could not add object, check parameters");
					}
				} break;
		}
		if(success) {
			listMap.add(obj);
		}
	}
	
	/* saves current level to file in json format*/
	private static void saveToJSON(String levelName) {
		try {
			PrintWriter saver = new PrintWriter(String.format("%s/%s.level", Window.saveDirectory, levelName));
			JSONObject obj = new JSONObject();		// create empty object

			obj.put("tracks", new JSONArray());		// add tracks as object with an empty array of properties
			obj.put("stations", new JSONArray());	// same with stations
			
			/* iterate over all maps in list of maps */
			for(Map<String, String> m : listMap) {
				String name = m.get("object");		// get the object
				boolean startStation = false;		// assume it's not starting station -> overwrite later if otherwise
				
				List<String> keys = new ArrayList<String>();					// initialize list of keys
				m.entrySet().forEach(entry -> keys.add(entry.getKey()));		// fill it with all keys from the map
				keys.removeIf(el -> el.equals(name) || el.equals("object"));	// remove unnecessary information

				JSONObject currObj = new JSONObject();							// initialize object of properties
				for(String key : keys) {										// iterate over all keys
					if(key.equals("type") && m.get(key).equals("start")) {		// overwrite startStation boolean if needed
						startStation = true;
					}
					currObj.put(key, m.get(key));								// put key and its value from map to current json object
				}
				if(startStation) {
					currObj.put("color", "black");			// if its starting station force-change color to black
				}
				obj.getJSONArray(name+"s").put(currObj);	// get json array of current type and add new values
			}
			saver.println(obj);								// print everything to file
			saver.close();									// close PrintWriter
		} catch (FileNotFoundException e1) {
			Log.error("@saveToJSON(): " + e1);
		}
	}

	/* returns scene attached to a .css file */
	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(Window.class.getResource("/resources/styles/" + cssFile).toExternalForm());
		return scene;
	}
		
	/* returns root Pane with constant parameters */
	public static Pane getRootPane() {
		Pane root = new Pane();
		root.setPrefSize(850, 550);
		root.setId("pane");
		
		return root;
	}
	
	/* returns json structure from file */
	private static JSONObject getJsonMenuObjects() {
		InputStream stream = Scenes.class.getResourceAsStream("/resources/structure.json");
		return new JSONObject(new JSONTokener(stream));
	}
	
	/* updates menu in create mode */
	private static void updateMenu(Text ObjectText, Text Object, Text ObjArrowL, Text ObjArrowR, StackPane menuStack, Rectangle menuBg, Text OK) {	
		menuStack.getChildren().clear();														// remove everything from stack
		menuStack.getChildren().addAll(menuBg, ObjectText, Object, ObjArrowL, ObjArrowR, OK);	// add constans
		
		allProperties = getAllKeys(createObject);			// get all properties for current object
		currentProperties.clear();							// clear current properties
		currentProperties.put("object", createObjectStr);	// add current object to current properties

		int y = 1;	// variable for positioning
		/* add all keys with values from available properties to menu stack */
		for(String currentProperty : allProperties) {
			List<Object> values = getAllValues(createObject, currentProperty);
			
			String key = currentProperty.substring(2);				// current key e.g: {color, type, start, ...}
			currentProperties.put(key, values.get(0).toString());	// set current property do it's default value

			/* text contaning the key */
			Text property = new Text(key.toUpperCase());
				property.setTranslateX(10);
				property.setTranslateY(y*20 + 5);
				property.getStyleClass().add("menuText");
				
			/* text contaning the key's default value */
			Text value = new Text(values.get(0).toString().toUpperCase());
				value.setTranslateX(110);
				value.setTranslateY(y*20 + 5);
				value.getStyleClass().add("menuValue");
				
			/* right arrow for navigation */
			Text rightArrow = new Text(">");
				rightArrow.setTranslateX(180);
				rightArrow.setTranslateY(y*20 + 5);
				rightArrow.getStyleClass().add("arrow");
				rightArrow.setOnMouseClicked(e -> {
					/* determine at what index current value is and move index forward */
					int index = 0;
					for(Object v : values) {
						if(v.toString().equals(value.getText().toLowerCase())) {
							index = values.indexOf(v);
							break;
						}
					}
					index = (index < values.size()-1) ? index : -1;						// list looping
					value.setText(String.valueOf(values.get(++index)).toUpperCase());	// update visible value text & increment index
					
					String val = value.getText().toLowerCase();
					currentProperties.put(key, val);	// update current property
				});
				
			/* left arrow for navigation */
			Text leftArrow = new Text("<");
				leftArrow.setTranslateX(90);
				leftArrow.setTranslateY(y*20 + 5);
				leftArrow.getStyleClass().add("arrow");
				leftArrow.setOnMouseClicked(e -> {
					/* vice versa */
					int index = 0;
					for(Object v : values) {
						if(v.toString().equals(value.getText().toLowerCase())) {
							index = values.indexOf(v);
							break;
						}
					}					
					index = (index != 0) ? index : values.size();
					value.setText(String.valueOf(values.get(--index)).toUpperCase());
					
					String val = value.getText().toLowerCase();
					currentProperties.put(key, val);
				});			
			menuStack.getChildren().addAll(property, value, leftArrow, rightArrow);
			y++;
		}	
	}
		
	/* returns full grid */
	private static GridSquare[][] getGrid() {
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	/* returns all keys for given json object */
	private static List<String> getAllKeys(JSONObject json) {
		List<String> keys = new ArrayList<String>();
		Map<String, Object> mapObject = json.toMap();
				
        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
            keys.add(entry.getKey());
        }
        
        Collections.sort(keys);
        return keys;
	}
		
	/* returns all values for a given key from a given object */
	private static List<Object> getAllValues(JSONObject json, String key) {
		return json.getJSONArray(key).toList();
	}
	
	/* returns integer direction from a string value */
	private static int parseDirection(String dir) {
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
	private static Color parseColorName(String colorName) {
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
}
