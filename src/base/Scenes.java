package base;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import base.obj.Ball;
import base.obj.FullTrack;
import base.obj.GridSquare;
import base.obj.LevelPane;
import base.obj.Station;
import base.obj.Track;
import javafx.scene.Scene;
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
	
	public static Scene levels() {
		Pane root = getRootPane();
			
		StackPane titleContainer = new StackPane();
			titleContainer.setPrefWidth(850);
			titleContainer.setTranslateX(0);
			titleContainer.setTranslateY(60);
		Text title = new Text("SELECT LEVEL");
			title.setId("title");
			title.setFont(Font.font("Hind Guntur Bold"));
			
		titleContainer.getChildren().add(title);
			
		for(int i=0; i<12; i++) {
			root.getChildren().add(new LevelPane(i<=5 ? 75+i*120: 75+(i-6)*120, i<=5 ? 270 : 370, i+3));
		}
		
		root.getChildren().add(titleContainer);
		return getSceneWithCSS(root, "levels.css");
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
	
	public static FullTrack game(String level) {
		InputStream stream = Scenes.class.getResourceAsStream("../resources/levels/tutorial.json");
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
			
			if(obj.has("exit")) {
				exit = obj.getInt("exit");
				startStation = new Station(column, row, color, exit);
			} else {
				exit = -1;
			}
			
			stations.add(new Station(column, row, color, exit));
		}

		for(Object track : tracksJson) {
			JSONObject obj = (JSONObject) track;
			
			String type = obj.getString("type");
			int column = obj.getInt("column");
			int row = obj.getInt("row");
			int origin = obj.getInt("origin");
			int end1 = obj.getInt("end-1");
			int end2 = obj.has("end-2") ? obj.getInt("end-2") : -1;
			
			tracks.add(new Track(GRID[column][row].getPos(), type, origin, end1, end2));							
		}
		
		double[] startCoords = startStation.getXY();
		for(Object ball : ballsJson) {
			JSONObject obj = (JSONObject) ball;
			
			int delay = obj.getInt("delay");
			Color color = parseColorName(obj.getString("color"));
			
			balls.add(new Ball(startCoords, color, tracks, delay));
		}

//			new Track(GRID[3][5].getPos(), S, 2, 0),
		
		return new FullTrack(stations, tracks, balls);
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
	
	private static GridSquare[][] getGrid() {
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	private static Color parseColorName(String colorName) {
		switch(colorName) {
			case "BLACK":
				return BLACK;
			case "RED":
				return RED;
			case "GREEN":
				return GREEN;
			case "BLUE":
				return BLUE;
		}
		return Color.CHARTREUSE;
	}
}
