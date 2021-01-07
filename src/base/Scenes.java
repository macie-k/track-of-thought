package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import base.Log;
import base.obj.Ball;
import base.obj.FullTrack;
import base.obj.GridSquare;
import base.obj.LevelPane;
import base.obj.Station;
import base.obj.Track;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import static base.Utils.fadeColors;
import static base.Utils.PATH_LEVELS_CUSTOM;
import static base.Utils.COLOR_ACCENT;
import static base.Utils.COLOR_BACKGROUND;

/* Functions are returning Panes to allow fade in effect */

public class Scenes {
		
	public final static GridSquare[][] GRID = Utils.getGrid();
			
	private static double createX, createY, overlayX, overlayY;
	private static String createObjectStr;
	private static JSONObject createObject;
	private static List<String> allProperties;
	private static Map<String, String> currentProperties = new HashMap<>();
	private static ArrayList<Map<String, String>> listMap = new ArrayList<>();
	private static int objectIndex = 0;
	private static Text menuObjectArrowRight, menuObjectArrowLeft;
	
	private static String FONT_TITLE = "Hind Guntur Bold";
	private static String FONT_TEXT = "Poppins Light";

		
	public static Pane intro() {
		Pane root = getRootPane();
		
		StackPane titleContainer = new StackPane();
			titleContainer.setPrefWidth(850);
			titleContainer.setTranslateX(0);
			titleContainer.setTranslateY(209);	// real y += 35
		
		Text title = new Text("TRACK OF THOUGHT");
			title.setId("title");
			title.setFont(Font.font(FONT_TITLE, 85));
			title.setOpacity(0);
			
		StackPane startContainer = new StackPane();
			startContainer.setPrefSize(850, 45);
			startContainer.setOpacity(0);
			startContainer.setScaleX(0.7);
			startContainer.setScaleY(0.7);
			startContainer.setTranslateY(350);
			
		Text startText = new Text("START");
			startText.setId("startText");
			startText.setFont(Font.font(FONT_TEXT, 25));
			startText.setMouseTransparent(true);
			startText.setTranslateY(-1);	// weird padding fix
			
		Rectangle startBg = new Rectangle(175, 45, COLOR_BACKGROUND);
			startBg.setId("startBg");
			startBg.setOnMouseEntered(e -> {
				fadeColors(startText, 200, COLOR_ACCENT, COLOR_BACKGROUND);
				fadeColors(startBg, 200, COLOR_BACKGROUND, COLOR_ACCENT);
			});
			startBg.setOnMouseExited(e -> {
				fadeColors(startText, 200, COLOR_BACKGROUND, COLOR_ACCENT);
				fadeColors(startBg, 200, COLOR_ACCENT, COLOR_BACKGROUND);
			});	

		final Interpolator EASE_OUT = new Interpolator() {
			@Override
			protected double curve(double t) {
				return (t == 1.0) ? 1.0 : 1 - Math.pow(1.8, -7*t);
			}
		};
								
		Timeline titleFade = new Timeline(
				new KeyFrame(Duration.seconds(.7),
						new KeyValue(title.opacityProperty(), 1, Interpolator.EASE_IN)
				)
		);
		
		Timeline titleMove = new Timeline(
			new KeyFrame(Duration.seconds(1.5),
					new KeyValue(titleContainer.translateYProperty(), 100, EASE_OUT),
					new KeyValue(titleContainer.scaleXProperty(), 0.95, EASE_OUT),
					new KeyValue(titleContainer.scaleYProperty(), 0.95, EASE_OUT)
			)
		);
		
		Timeline buttonAnimation = new Timeline(
			new KeyFrame(Duration.seconds(1.5),
					new KeyValue(startContainer.opacityProperty(), 1, EASE_OUT),
					new KeyValue(startContainer.scaleXProperty(), 1, EASE_OUT),
					new KeyValue(startContainer.scaleYProperty(), 1, EASE_OUT)
			)
		);
		
		Timeline moveAway = new Timeline(
			new KeyFrame(Duration.seconds(0.5), e -> Window.setScene(levels())),
			new KeyFrame(Duration.seconds(1),
					new KeyValue(titleContainer.translateYProperty(), -150, EASE_OUT),
					new KeyValue(startContainer.translateYProperty(), 650, EASE_OUT)
			)
		);
		
//		moveAway.setOnFinished(e -> Window.setScene(levels()));
		startBg.setOnMouseClicked(e -> {
			moveAway.play();
		});
		
		titleMove.setDelay(Duration.seconds(.5));		
		buttonAnimation.setDelay(Duration.seconds(.5));
		
		titleFade.setDelay(Duration.seconds(.5));
		titleFade.setOnFinished(e -> {
			titleMove.play();
			buttonAnimation.play();
		});
		titleFade.play();
						        
		titleContainer.getChildren().add(title);
		startContainer.getChildren().addAll(startBg, startText);
		root.getChildren().addAll(titleContainer, startContainer);
		
		getSceneWithCSS(root, "intro.css");
		return root;
	}
	
	public static Pane levels() {
		Pane root = getRootPane();
				
		StackPane titleContainer = new StackPane();
			titleContainer.setPrefWidth(850);
			titleContainer.setTranslateX(0);
			titleContainer.setTranslateY(60);
		Text title = new Text("SELECT LEVEL");
			title.setId("title");
			title.setFont(Font.font(FONT_TITLE));
			
		int unlocked = 3;
		try {
			unlocked = Utils.getProgress();
		} catch (Exception e) {
			Log.error("Could not get progress: " + e.getMessage());
			Log.warning("Defaults progress to 3");
		}
		
		for(int i=0; i<12; i++) {
			String level = String.valueOf(i+3);
			root.getChildren().add(new LevelPane((i<=5 ? 75+i*120: 75+(i-6)*120), (i<=5 ? 270 : 370), level, true, (i+3) <= unlocked));
		}
		
		titleContainer.getChildren().add(title);
		root.getChildren().add(titleContainer);
		
		getSceneWithCSS(root, "levels.css");
		return root;
	}
	
	public static FullTrack gameTrack(String level, boolean premade) {
		
		final String path = premade ? "/resources/data/levels/" : PATH_LEVELS_CUSTOM;
		
		int levelCounter = level.equals("3") ? 3 : 5;	// all levels except 3rd have 5 versions
		if(Integer.valueOf(level) > 8) levelCounter = 2;	// temporary restriction for new levels
		
		final int random = new Random().nextInt(levelCounter)+1;
		final String levelName = String.format("%s-%d", level, random);
//		final String levelName = "8-5";
		final InputStream stream = Scenes.class.getResourceAsStream(path + levelName + ".level");
		
		Log.success("Selected level: " + levelName);

		final List<Station> stations = new ArrayList<>();
		final List<Track> tracks = new ArrayList<>();
		final List<Ball> balls = new ArrayList<>();
		final List<String> userColors = new ArrayList<>();

		int bordersAmount = 0;
		
		final JSONObject json = new JSONObject(new JSONTokener(stream));
		final JSONArray stationsJson = json.getJSONArray("stations");
		final JSONArray tracksJson = json.getJSONArray("tracks");
		final JSONObject ballsJson = json.getJSONObject("balls");
		final int ballsAmount = ballsJson.getInt("amount");
		
		Station startStation = null;
		for(Object station : stationsJson) {
			final JSONObject obj = (JSONObject) station;
			
			final boolean start = obj.get("type").equals("start");
			final boolean border = obj.getString("color").contains("+");
			final String color = start ? "black" : obj.getString("color");
			final int column = obj.getInt("column");
			final int row = obj.getInt("row");
			int exit;
			
			if(start) {
				exit = Utils.parseDirectionToInt(obj.getString("exit"));
				startStation = new Station(column, row, color, exit);
			} else {
				exit = -1;
				
				String onlyColor = obj.getString("color").split("\\+")[0].trim();
				userColors.add(onlyColor);
				
				if(border) {
					bordersAmount++;
				}
			}
			stations.add(new Station(column, row, color, exit));
		}
		
						
		final double[] startCoords = startStation.getXY();		
		final List<String> stationColors = new ArrayList<>();
		final int lvl = stations.size()-1;
		
		/* Remove duplicates from stations */
		final Set<String> set = new HashSet<>(userColors);
		userColors.clear();
		userColors.addAll(set);
		
		int totalColors = userColors.size() + bordersAmount;
				
		/* check if there is enough colors */
		if(lvl > totalColors) {
			Log.warning("Duplicated station colors, generating new ...");
			
			final int diff = stations.size() - totalColors;
			final List<String> newColors = Utils.getRandomColors(diff, userColors, true);
			userColors.addAll(newColors);
		}
		
		userColors.addAll(Utils.getRandomBorderColors(bordersAmount));
		
		Random r = new Random();
		for(Station s : stations) {
			if(!s.isStart()) {
				final int index = r.nextInt(userColors.size());
				final String colorStr = userColors.get(index);
				
				userColors.remove(index);
				stationColors.add(colorStr);
				
				s.setBorder(colorStr.contains("+"));
				s.initShape();
				s.setColor(colorStr);
			} else {
				s.initShape();
			}
		}
					
		ArrayList<Track> clickableTracks = new ArrayList<>();	// smaller list for ball interactions & random switching
		for(Object track : tracksJson) {
			final JSONObject obj = (JSONObject) track;
			
			final boolean switchable = obj.getBoolean("switch");
			final String type = obj.getString("type");
			final int column = obj.getInt("column");
			final int row = obj.getInt("row");
			final int origin = Utils.parseDirectionToInt(obj.getString("start"));
			final int end1 = Utils.parseDirectionToInt(obj.getString("end1"));
			final int end2 = switchable ? Utils.parseDirectionToInt(obj.getString("end2")) : -1;
			
			try {
				Track t = new Track(GRID[column][row].getXY(), type, origin, end1, end2);
				tracks.add(t);		
				if(switchable) {
					clickableTracks.add(t);
				}
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}
				
		int avarageDelay = 120 / ballsAmount;
		int globalDelay = 2;
		balls.add(new Ball(startCoords, globalDelay));
		globalDelay += (14 - lvl)/2;
		for(int i=1; i<ballsAmount; i++) {
			final int delay = avarageDelay + r.nextInt(3);
			globalDelay += delay;
			balls.add(new Ball(startCoords, globalDelay));
		}
		
		Utils.randomSwitchTracks(clickableTracks);
		return new FullTrack(stations, tracks, balls);
	}
		
	public static void drawFullPath(Track[] tracks, Pane root) {
		for(Track track : tracks) {
			final double[][] path = track.getPath();
			for(int j=0; j<50; j++) {
				final Rectangle r = new Rectangle(1, 1);
					r.setTranslateX(path[0][j]);
					r.setTranslateY(path[1][j]);
					r.setFill(Color.rgb(4*j, 3*j, 2*j));
				root.getChildren().add(r);
			}
		}
	}
	
	/*
		- Objects' structure & properties are avaialable at: resources/structure.json
		- In order for the premade level to be accessible, it needs to be placed in: bin/resources/levels
		- Color names that contain '+O' are colors with white border
			The suffix is omitted during a conversion @Utils.parseColorName() and boolean border value is passed to constructor
	*/
	
	public static Pane createLevel() {
		final Pane root = getRootPane();
		
		final String[] menuObjects= {"track", "station"};										// available objects as menu "pages"
		final JSONObject jsonObjects = Utils.getJsonFromFile("/resources/utils/structure.json");	// get general object from structure.json
		final List<GridSquare> grid = new ArrayList<GridSquare>();							// list containing all gridSquares
		
		createObjectStr = menuObjects[objectIndex];					// current object as string
		createObject = jsonObjects.getJSONObject(createObjectStr);	// current object as json object
		allProperties = Utils.getAllJsonKeys(createObject);			// get properties of current obect
		
		/* StackPane containing menu when any square is clicked */
		StackPane menuStack = new StackPane();
			menuStack.setPrefSize(200, 170);
			menuStack.setVisible(false);
			menuStack.setAlignment(Pos.TOP_LEFT);
			menuStack.setAccessibleText("MENU");
			
		/* background for menu */
		Rectangle menuBg = new Rectangle(200, 170);
			menuBg.setFill(Color.web("#4B4E54"));
			menuBg.setStroke(Utils.COLOR_ACCENT);
			
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
				addNewObject(root, new HashMap<String, String>(currentProperties), menuStack);
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
			SAVE_NAMEbg.setStroke(Utils.COLOR_ACCENT);
			SAVE_NAMEbg.setStrokeWidth(2);
			
		/* button to close the saving menu */
		Text SAVE_NAMEClose = new Text(" X ");
			StackPane.setAlignment(SAVE_NAMEClose, Pos.TOP_RIGHT);	// align it to top-right corner
			SAVE_NAMEClose.setTranslateX(-5);						// create small X padding
			SAVE_NAMEClose.setTranslateY(5);						// create small Y padding
			SAVE_NAMEClose.setFont(Font.font(FONT_TITLE));
			SAVE_NAMEClose.setCursor(Cursor.HAND);
			SAVE_NAMEClose.setFill(Color.WHITE);
			SAVE_NAMEClose.setOnMouseClicked(e -> {
				root.getChildren().forEach(child -> {
					child.setVisible(!child.equals(menuStack)); // when [ x ] is clicked show everything except for the menu
				});	
				SAVE_MENU.setVisible(false);					// hide saving menu
			});
		
		/* label "NAME:" */
		Text levelNameLabel = new Text("NAME: ");
			levelNameLabel.setTranslateY(-35);
			levelNameLabel.setFont(Font.font(FONT_TEXT, 17));
			levelNameLabel.setFill(Color.WHITE);
			
		final Color COLOR_DISABLED = Color.web("#939598");
		final Color COLOR_ENABLED = Color.web("#5beb82");
			
		TextField levelName = new TextField();
		StackPane CONFIRM_CREATE = new StackPane();
		Rectangle SAVE_NAMEButton = new Rectangle(100, 25, levelName.getText().length() > 0 ? COLOR_ENABLED : COLOR_DISABLED);
			
		/* input area for entering level name */
		levelName.setId("level-name");
		levelName.setMaxSize(150, 25);
		levelName.setTranslateY(-5);
		levelName.setOnKeyReleased(e -> {	
			if(levelName.getText().length() == 0) {
				CONFIRM_CREATE.setDisable(true);
				SAVE_NAMEButton.setFill(COLOR_DISABLED);
				return;
			}
			CONFIRM_CREATE.setDisable(false);
			SAVE_NAMEButton.setFill(COLOR_ENABLED);
		});
					
		/* confirmation button StackPane -> triggers saving to .level file */
		
		CONFIRM_CREATE.setMaxSize(100, 25);
		CONFIRM_CREATE.setPrefSize(100, 25);
		CONFIRM_CREATE.setTranslateY(35);
		CONFIRM_CREATE.setTranslateX(0);
		CONFIRM_CREATE.setCursor(Cursor.HAND);
		CONFIRM_CREATE.setDisable(levelName.getText().length() == 0);
		CONFIRM_CREATE.setOnMouseClicked(e -> {
			/* when clicked saves level to file and restores view */
			saveLevelToJSON(levelName.getText());
			root.getChildren().forEach(child -> child.setVisible(!child.equals(menuStack)));
			SAVE_MENU.setVisible(false);
		});
		
		/* background and text for the confirmation button */
		Text SAVE_NAMEButtonText = new Text("CONFIRM");
			SAVE_NAMEButtonText.setFont(Font.font(FONT_TEXT, 15));
			
		CONFIRM_CREATE.getChildren().addAll(SAVE_NAMEButton, SAVE_NAMEButtonText);	
		SAVE_MENU.getChildren().addAll(SAVE_NAMEbg, levelName, levelNameLabel, SAVE_NAMEClose);	
		SAVE_MENU.getChildren().add(CONFIRM_CREATE);	
		
		/* StackPane for save button -> opens saving menu */
		StackPane SAVE = new StackPane();
			SAVE.setTranslateX(445);
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
			SAVEtext.setFont(Font.font(FONT_TEXT, 17));
					
		SAVE.getChildren().addAll(SAVEbg, SAVEtext);
		
		/* StackPane for clear button -> removes all elements */
		StackPane CLEAR = new StackPane();
			CLEAR.setTranslateX(305);
			CLEAR.setTranslateY(505);
			CLEAR.setPrefSize(100, 40);
			CLEAR.setCursor(Cursor.HAND);
			
		/* background and text for the save button */
		Rectangle CLEARbg = new Rectangle(100, 40, Color.web("#eb5b5b"));
			CLEARbg.setTranslateX(0);
		Text CLEARtext = new Text("CLEAR");
			CLEARtext.setFont(Font.font(FONT_TEXT, 17));
					
		CLEAR.getChildren().addAll(CLEARbg, CLEARtext);

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
				final GridSquare gridSq = new GridSquare(i, j, true);
				gridSq.setOnMouseClicked(e -> {
					/* save X and Y of current square */
					createX = gridSq.getXY()[0];
					createY = gridSq.getXY()[1];
					
					/* calculate X and Y for the menu to avoid partially rendering outside of the window */
					overlayX = gridSq.getXY()[0] - 75;
						if(overlayX == 675) overlayX -= 25;
						if(overlayX == -25) overlayX += 25;
					
					overlayY = gridSq.getXY()[1] + 50;
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
		root.getChildren().add(SAVE);
		root.getChildren().add(CLEAR);
		root.getChildren().add(SAVE_MENU);
		root.getChildren().add(menuStack);
		
		CLEAR.setOnMouseClicked(e -> {
			root.getChildren().removeIf(child -> shouldClear(child));
			listMap.clear();
		});
		
		getSceneWithCSS(root, "createLevel.css");
		return root;
	}
	
	public static String getNextBallColor(FullTrack track) {	
		final List<Pair<String, Double>> pairs = new ArrayList<>();						// create list of pairs
		final List<String> usedColors = track.getUsedColors();							// create list of all used colors
		final List<String> activeColors = track.getActiveBallColors();					// create list of currently used colors
		final List<String> activeColorsWithoutBorders = new ArrayList<>(activeColors);	// create list of currently used colors but without borders
			activeColorsWithoutBorders.forEach(color -> {
				if(color.contains("+")) {
					color = color.split("\\+")[0].trim();
				}
			});
		
		/* get most recent ball(s) & its color */
		final List<Ball> mostRecentBalls = track.getMostRecentBalls(1);
		final String mostRecentColor = mostRecentBalls.isEmpty() ? "" : mostRecentBalls.get(0).getColorStr();
				
		for(String color : usedColors) {
			final String colorWithoutBorder = color.split("\\+")[0].trim();					// get current color without border
			final String prevColorWithoutBorder = mostRecentColor.split("\\+")[0].trim();	// get previous color without border
			
			final int occurence = Collections.frequency(activeColorsWithoutBorders, colorWithoutBorder);	// check for occurence without border
			
			double probability = Math.pow((1.0/usedColors.size()), occurence);								// calculate probability
			/* if previous color was the same or (current end-station has this color & level is above 5th) set probability to 0 */
			if(colorWithoutBorder.equals(prevColorWithoutBorder) ||
					(track.getCurrentEndStation().getColorStr().equals(color) && activeColors.size() > 5)) {
				probability = 0;
			}
			pairs.add(new Pair<String, Double>(color, probability));	
		}
		
		return new EnumeratedDistribution<>(pairs).sample();
	}
		
	/* checks if given child should be cleared from level creator */
	private static boolean shouldClear(Node child) {
		String aH = child.getAccessibleHelp();
		/* clear only Tracks, Stations, and Tracks' paths */
		return 	(child instanceof Track) ||
				(child instanceof Station) ||
				(aH != null) && (aH.equals("debugdraw"));
	}
		
	/* creates temporary object for createLevel() scene */
	private static void addNewObject(Pane root, Map<String, String> obj, StackPane menu) {
		final String object = obj.get("object");				// get the object type
		final int[] xy = {(int)createX, (int)createY};		// get the coordinates
		
		boolean success = true;		// assume adding was successful -> overwrite later if otherwise
		switch(object) {
			case "track": {
				/* get all track's properties */
				final String type = obj.get("type");
				final boolean switchable = obj.get("switch").equals("true");
				final int start = Utils.parseDirectionToInt(obj.get("start"));
				final int end1 = Utils.parseDirectionToInt(obj.get("end1"));
				final int end2 = switchable ? Utils.parseDirectionToInt(obj.get("end2")) : -1;
				
				/* try to create the track -> if error is caught don't add to list */
				try {
					Track t = new Track(xy, type, start, end1, end2);
					
					/* test second type */
					if(switchable) {
						t.changeType();
						t.changeType();
					}

					t.addDebugPath(root);	// comment to hide PATH DRAWING - can get annoying when deleting a lot
					
					/* listener to remove object and change type if clickable */
					t.setOnMouseReleased(e -> {
						/* if scroll is cliked remove else try to change type */
						if(e.getButton() == MouseButton.MIDDLE) {
							t.removeDebugPath(root);
							root.getChildren().remove(t);
							listMap.remove(obj);
						} else {
							if(t.isClickable()) {
								try {
									t.removeDebugPath(root);
									t.changeType();
									t.addDebugPath(root);
								} catch (Exception e1) {}
								menu.toFront();
							}
						}
					});
					root.getChildren().add(t);
				} catch (Exception e) {
					success = false;											// overwrite success boolean
					Log.warning("Could not add object: " + e.getMessage());		// log the error
				}
			} break;
			
			case "station": {
				/* same as above */
				String type = obj.get("type");
				boolean start = type.equals("start");
				int exit = start ? Utils.parseDirectionToInt(obj.get("exit")) : -1;
				String color = start ? "black" : obj.get("color");
				try {
					Station s = new Station(xy, color, exit);
					s.setBorder(color.contains("+"));
					s.initShape();
					s.setColor(color);
					s.setOnMouseClicked(e -> {
						if(e.getButton() == MouseButton.MIDDLE) {
							root.getChildren().remove(s);
							listMap.remove(obj);
						}
					});
					root.getChildren().add(s);
				} catch (Exception e) {
					success = false;
					Log.warning("Could not add object: " + e.getMessage());
				}
			} break;
		}
		if(success) {
			listMap.add(obj);
		}
	}
	
	/* saves current level to file in json format*/
	private static void saveLevelToJSON(String levelName) {
		try {
			String filePath = String.format("%s%s.level", PATH_LEVELS_CUSTOM, levelName);
			int counter = 0;
			while(new File(filePath).exists()) {
				Log.warning("Level already exists, changing name ...");
				filePath = String.format("%s%s-%d.level", PATH_LEVELS_CUSTOM, levelName, ++counter);
			}
			if(counter != 0) {
				levelName += "-" + counter;
			}
			PrintWriter saver = new PrintWriter(filePath);
			JSONObject obj = new JSONObject();		// create empty object
			obj.put("name", levelName);				// save level name
			obj.put("tracks", new JSONArray());		// add tracks as object with an empty array of properties
			obj.put("stations", new JSONArray());	// same with stations
			
			/* iterate over all maps in list of maps */
			for(Map<String, String> m : listMap) {
				final String name = m.get("object");	// get the object
				boolean startStation = false;			// assume it's not starting station -> overwrite later if otherwise
				
				List<String> keys = new ArrayList<String>();					// initialize list of keys
				m.entrySet().forEach(entry -> keys.add(entry.getKey()));		// fill it with all keys from the map
				keys.removeIf(el -> el.equals(name) || el.equals("object"));	// remove unnecessary information

				final JSONObject currObj = new JSONObject();					// initialize object of properties
				for(String key : keys) {										// iterate over all keys
					if(key.equals("type") && m.get(key).equals("start")) {		// overwrite startStation boolean if needed
						startStation = true;
					}
					if(key.equals("column") || key.equals("row")) {
						currObj.put(key, Integer.valueOf(m.get(key)));
					} else {
						currObj.put(key, m.get(key));							// put key and its value from map to current json object
					}
				}
				if(startStation) {
					currObj.put("color", "black");			// if its starting station force-change color to black
				}
				obj.getJSONArray(name+"s").put(currObj);	// get json array of current type and add new values
			}
			saver.println(obj);								// print everything to file
			saver.close();									// close PrintWriter
		} catch (FileNotFoundException e) {
			Log.error(e.getMessage());
		}
	}

	/* returns scene attached to a .css file */
	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
			scene.setFill(COLOR_BACKGROUND);
			scene.getStylesheets().addAll(Window.class.getResource("/resources/data/styles/" + cssFile).toExternalForm());
		return scene;
	}
		
	/* returns root Pane with constant parameters */
	public static Pane getRootPane() {
		Pane root = new Pane();
		root.setPrefSize(850, 550);
		root.setId("pane");
		
		return root;
	}
		
	/* updates menu in create mode */
	private static void updateMenu(Text ObjectText, Text Object, Text ObjArrowL, Text ObjArrowR, StackPane menuStack, Rectangle menuBg, Text OK) {	
		menuStack.getChildren().clear();														// remove everything from stack
		menuStack.getChildren().addAll(menuBg, ObjectText, Object, ObjArrowL, ObjArrowR, OK);	// add constans
		
		allProperties = Utils.getAllJsonKeys(createObject);			// get all properties for current object
		currentProperties.clear();							// clear current properties
		currentProperties.put("object", createObjectStr);	// add current object to current properties

		int y = 1;	// variable for positioning
		/* add all keys with values from available properties to menu stack */
		for(String currentProperty : allProperties) {
			List<Object> values = Utils.getAllJsonValues(createObject, currentProperty);
			
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
					

}
