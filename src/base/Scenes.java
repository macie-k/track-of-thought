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
	
	public final static String BACKGROUND = "#363638";
	public final static GridSquare[][] GRID = getGrid();
	
	public final static String S = "straight";
	public final static String C = "curved";
	
	final static Color RED = Color.web("#8F1114");
	final static Color BLACK = Color.web("#101114");
	final static Color GREEN = Color.web("#105114");
	final static Color BLUE = Color.web("#101193");
	
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
			int column = obj.getInt("column");
			int row = obj.getInt("row");
			int exit;
			
			if(obj.get("type").equals("start")) {
				exit = parseDirection(obj.getString("exit"));
				startStation = new Station(column, row, color, exit);
			} else {
				exit = -1;
			}
			stations.add(new Station(column, row, color, exit));
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
			Color color = parseColorName(obj.getString("color"));
			
			balls.add(new Ball(startCoords, color, tracks, delay));
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
	
	public static Scene createLevel() {
		Pane root = getRootPane();
		
		String[] menuObjects= {"track", "station"};
		JSONObject jsonObjects = getJsonMenuObjects();
		List<GridSquare> grid = new ArrayList<GridSquare>();
		
		createObjectStr = menuObjects[objectIndex];
		createObject = jsonObjects.getJSONObject(createObjectStr);
		allProperties = getAllKeys(createObject);
		
		StackPane menuStack = new StackPane();
			menuStack.setPrefSize(200, 170);
			menuStack.setVisible(false);
			menuStack.setAlignment(Pos.TOP_LEFT);
			
		Rectangle menuBg = new Rectangle(200, 170);
			menuBg.setFill(Color.web("#4B4E54"));
			menuBg.setStroke(COLOR_ACCENT);
			
		Text menuObjectText = new Text("OBJECT ");
			menuObjectText.setTranslateX(10);
			menuObjectText.setTranslateY(5);
			menuObjectText.getStyleClass().add("menuText");
						
		Text menuObject = new Text(createObjectStr.toUpperCase());
			menuObject.setTranslateX(110);
			menuObject.setTranslateY(5);
			menuObject.getStyleClass().add("menuValue");
			
		Text OK = new Text("[ OK ]");
			OK.setTranslateX(0);
			OK.setTranslateY(-10);
			OK.setId("OK");
			OK.setOnMouseClicked(e -> {
				currentProperties.put("column", String.valueOf((int)(createX/50-1)));
				currentProperties.put("row", String.valueOf((int)(createY/50-1)));
									
				addNewObject(root, new HashMap<String, String>(currentProperties));
				menuStack.toFront();
				menuStack.setVisible(false);
			});	
			StackPane.setAlignment(OK, Pos.BOTTOM_CENTER);
			
		StackPane SAVE_NAME = new StackPane();
			SAVE_NAME.setPrefSize(200, 120);
			SAVE_NAME.setTranslateX(325);
			SAVE_NAME.setTranslateY(215);
			SAVE_NAME.setVisible(false);
			
		Text SAVE_NAMEClose = new Text(" X ");
			SAVE_NAMEClose.setTranslateX(-5);
			SAVE_NAMEClose.setTranslateY(5);
			SAVE_NAMEClose.setFont(Font.font("Hind Guntur Bold"));
			SAVE_NAMEClose.setCursor(Cursor.HAND);
			SAVE_NAMEClose.setFill(Color.WHITE);
			StackPane.setAlignment(SAVE_NAMEClose, Pos.TOP_RIGHT);
			SAVE_NAMEClose.setOnMouseClicked(e -> {
				root.getChildren().forEach(child -> {
					child.setVisible(!child.equals(menuStack));
				});
				SAVE_NAME.setVisible(false);
			});
		
		Text levelNameLabel = new Text("NAME: ");
			levelNameLabel.setTranslateY(-35);
			levelNameLabel.setFont(Font.font("Poppins Light", 17));
			levelNameLabel.setFill(Color.WHITE);
			
		TextField levelName = new TextField();
			levelName.setMaxSize(150, 25);
			levelName.setTranslateY(-5);
			levelName.setStyle("-fx-faint-focus-color: transparent;"
					+ "-fx-focus-color: transparent;"
					+ "-fx-text-box-border: transparent;"
					+ "-fx-highlight-fill: #363638;"
					+ "-fx-highlight-text-fill: #FFF;");
			
		Rectangle SAVE_NAMEbg = new Rectangle(200, 120, Color.web("#4B4E54"));
			SAVE_NAMEbg.setTranslateX(0);
			SAVE_NAMEbg.setStroke(COLOR_ACCENT);
			SAVE_NAMEbg.setStrokeWidth(2);
			
		StackPane CONFIRM = new StackPane();
			CONFIRM.setMaxSize(100, 25);
			CONFIRM.setPrefSize(100, 25);
			CONFIRM.setTranslateY(35);
			CONFIRM.setTranslateX(0);
			CONFIRM.setCursor(Cursor.HAND);
			CONFIRM.setOnMouseClicked(e -> {
				saveToJSON(levelName.getText());
				root.getChildren().forEach(child -> {
					child.setVisible(!child.equals(menuStack));
				});
				SAVE_NAME.setVisible(false);
			});
		Rectangle SAVE_NAMEButton = new Rectangle(100, 25, Color.web("#5beb82"));
		Text SAVE_NAMEButtonText = new Text("CONFIRM");
			SAVE_NAMEButtonText.setFont(Font.font("Poppins Light", 15));
			
		CONFIRM.getChildren().addAll(SAVE_NAMEButton, SAVE_NAMEButtonText);

		SAVE_NAME.getChildren().addAll(SAVE_NAMEbg, levelName, levelNameLabel, SAVE_NAMEClose);
		SAVE_NAME.getChildren().add(CONFIRM);		
			
			
		StackPane SAVE = new StackPane();
			SAVE.setTranslateX(375);
			SAVE.setTranslateY(505);
			SAVE.setPrefSize(100, 40);
			SAVE.setCursor(Cursor.HAND);
			SAVE.setOnMouseClicked(e -> {
				root.getChildren().forEach(child -> child.setVisible(false));
				SAVE_NAME.setVisible(true);
			});
		Rectangle SAVEbg = new Rectangle(100, 40, Color.web("#5beb82"));
			SAVEbg.setTranslateX(0);
		Text SAVEtext = new Text("S A V E");
			SAVEtext.setFont(Font.font("Poppins Light", 17));
					
		SAVE.getChildren().addAll(SAVEbg, SAVEtext);
					
		menuObjectArrowLeft = new Text("<");
			menuObjectArrowLeft.setTranslateX(90);
			menuObjectArrowLeft.setTranslateY(5);
			menuObjectArrowLeft.getStyleClass().add("arrow");
			menuObjectArrowLeft.setOnMouseClicked(e -> {
				objectIndex = (objectIndex != 0) ? objectIndex : menuObjects.length;
				createObjectStr = menuObjects[--objectIndex];
				createObject = jsonObjects.getJSONObject(createObjectStr);
				menuObject.setText(createObjectStr.toUpperCase());
				
				updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
			});
			
		menuObjectArrowRight = new Text(">");
			menuObjectArrowRight.setTranslateX(180);
			menuObjectArrowRight.setTranslateY(5);
			menuObjectArrowRight.getStyleClass().add("arrow");
			menuObjectArrowRight.setOnMouseClicked(e -> {
				objectIndex = (objectIndex < menuObjects.length-1) ? objectIndex : -1;
				createObjectStr = menuObjects[++objectIndex];
				createObject = jsonObjects.getJSONObject(createObjectStr);
				menuObject.setText(createObjectStr.toUpperCase());
				
				updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
			});
														
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				GridSquare gridSq = new GridSquare(i, j, true);
				gridSq.setOnMouseClicked(e -> {
					createX = gridSq.getPos()[0];
					createY = gridSq.getPos()[1];
					
					overlayX = gridSq.getPos()[0] - 75;
						if(overlayX == 675) overlayX -= 25;
						if(overlayX == -25) overlayX += 25;
					
					overlayY = gridSq.getPos()[1] + 50;
						if(overlayY >= 400) overlayY -= 220;
					
					menuStack.setTranslateX(overlayX);
					menuStack.setTranslateY(overlayY);
					menuStack.setVisible(true);
					
					grid.forEach(gridSqare -> gridSqare.setId(""));
					gridSq.setId("current");
//					updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
				});
				
				grid.add(gridSq);
			}
		}
		
		menuStack.getChildren().addAll(menuBg, menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, OK);
		updateMenu(menuObjectText, menuObject, menuObjectArrowLeft, menuObjectArrowRight, menuStack, menuBg, OK);
		
		root.getChildren().addAll(grid);
		root.getChildren().addAll(SAVE);
		root.getChildren().addAll(SAVE_NAME);
		root.getChildren().add(menuStack);
		
		return getSceneWithCSS(root, "createLevel.css");
	}
	
	private static void addNewObject(Pane root, Map<String, String> obj) {
		String object = obj.get("object");
		int[] xy = new int[] {(int)createX, (int)createY};
		
		boolean success = true;
		switch(object) {
			case "track":
				{
					String type = obj.get("type");
					boolean switchable = obj.get("switch").equals("true");
					int start = parseDirection(obj.get("start"));
					int end1 = parseDirection(obj.get("end1"));
					int end2 = switchable ? parseDirection(obj.get("end2")) : -1;
					try {
						Track t = new Track(xy, type, start, end1, end2);
						t.debugDraw(root);
						t.setOnMouseClicked(e -> {
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
						success = false;
						Log.error("Could not add object, check parameters");
					}
				}
				break;
				
			case "station":
				{
					String type = obj.get("type");
					boolean start = type.equals("start");
					Color color = start ? parseColorName("black") : parseColorName(obj.get("color"));
					int exit = start ? parseDirection(obj.get("exit")) : -1; 
					
					try {
						Station s = new Station(xy, color, exit);
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
				}
				break;
		}
		if(success) {
			listMap.add(obj);
		}
	}
	
	private static void saveToJSON(String levelName) {
		try {
			PrintWriter saver = new PrintWriter(String.format("%s/%s.level", Window.saveDirectory, levelName));
			JSONObject obj = new JSONObject();

			obj.put("tracks", new JSONArray());
			obj.put("stations", new JSONArray());

			for(Map<String, String> m : listMap) {
				String name = m.get("object");
				boolean startStation = false;
				
				List<String> keys = new ArrayList<String>();
				m.entrySet().forEach(entry -> keys.add(entry.getKey()));
				keys.removeIf(el -> el.equals(name) || el.equals("object"));

				JSONObject currObj = new JSONObject();
				for(String key : keys) {
					if(name.equals("station") && key.equals("type") && m.get(key).equals("start")) {
						startStation = true;
					}
					currObj.put(key, m.get(key));
				}
				if(startStation) {
					currObj.put("color", "black");
				}
				obj.getJSONArray(name+"s").put(currObj);
			}
			saver.println(obj);
			saver.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(Window.class.getResource("/resources/styles/" + cssFile).toExternalForm());
		return scene;
	}
		
	/* return root Pane with constant parameters */
	public static Pane getRootPane() {
		Pane root = new Pane();
		root.setPrefSize(850, 550);
		root.setId("pane");
		
		return root;
	}
	
	private static JSONObject getJsonMenuObjects() {
		String json = "{\"track\":{\"1-type\":[\"straight\",\"curved\"],\"5-switch\":[false,true],\"2-start\":[\"top\",\"right\",\"bottom\",\"left\"],\"3-end1\":[\"top\",\"right\",\"bottom\",\"left\"],\"4-end2\":[\"top\",\"right\",\"bottom\",\"left\"]},\"station\":{\"1-type\":[\"normal\",\"start\"],\"2-color\":[\"red\",\"green\",\"blue\",\"black\"],\"3-exit\":[\"top\",\"right\",\"bottom\",\"left\"]}}";		
		return new JSONObject(json);
	}
	
	private static void updateMenu(Text ObjectText, Text Object, Text ObjArrowL, Text ObjArrowR, StackPane menuStack, Rectangle menuBg, Text OK) {	
		menuStack.getChildren().clear();
		menuStack.getChildren().addAll(menuBg, ObjectText, Object, ObjArrowL, ObjArrowR, OK);
		allProperties = getAllKeys(createObject);
		
		currentProperties.clear();
		currentProperties.put("object", createObjectStr);

		int y = 1;
		for(String cp : allProperties) {
			List<Object> values = getAllValues(createObject, cp);
			
			String key = cp.substring(2);
			currentProperties.put(key, values.get(0).toString());

			Text property = new Text(key.toUpperCase());
				property.setTranslateX(10);
				property.setTranslateY(y*20 + 5);
				property.getStyleClass().add("menuText");
				
			Text value = new Text(values.get(0).toString().toUpperCase());
				value.setTranslateX(110);
				value.setTranslateY(y*20 + 5);
				value.getStyleClass().add("menuValue");
				
			Text rightArrow = new Text(">");
				rightArrow.setTranslateX(180);
				rightArrow.setTranslateY(y*20 + 5);
				rightArrow.getStyleClass().add("arrow");
				rightArrow.setOnMouseClicked(e -> {
					
					int index = 0;
					for(Object v : values) {
						if(v.toString().equals(value.getText().toLowerCase())) {
							index = values.indexOf(v);
							break;
						}
					}
					index = (index < values.size()-1) ? index : -1;
					value.setText(String.valueOf(values.get(++index)).toUpperCase());
					
					String val = value.getText().toLowerCase();
					currentProperties.put(key, val);
				});
				
			Text leftArrow = new Text("<");
				leftArrow.setTranslateX(90);
				leftArrow.setTranslateY(y*20 + 5);
				leftArrow.getStyleClass().add("arrow");
				leftArrow.setOnMouseClicked(e -> {
					
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
		
	private static GridSquare[][] getGrid() {
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	private static List<String> getAllKeys(JSONObject json) {
		List<String> keys = new ArrayList<String>();
		Map<String, Object> mapObject = json.toMap();
				
        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
            keys.add(entry.getKey());
        }
        
        Collections.sort(keys);
        return keys;
	}
		
	private static List<Object> getAllValues(JSONObject json, String key) {
		return json.getJSONArray(key).toList();
	}
	
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
	
	private static Color parseColorName(String colorName) {
		switch(colorName.toUpperCase()) {
			case "BLACK":
				return BLACK;
			case "RED":
				return RED;
			case "GREEN":
				return GREEN;
			case "BLUE":
				return BLUE;
			default:
				return Color.FUCHSIA;
		}
	}
}
