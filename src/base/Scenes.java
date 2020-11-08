package base;

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
		Station start = new Station(2, 3, BLACK, 1);
		Station redFinish = new Station(6, 9, RED);
		Station blueFinish = new Station(4, 3, BLUE);
		Station greenFinish = new Station(4, 5, GREEN);
			
		Station[] stations = {start, redFinish, blueFinish, greenFinish};		
		Track[] tracks = {
				
//			[S=straight, C=curved 0=top, 1=right, 2=bottom, 3=left]	
//			(xy, type[S, C], origin[0,1,2,3], end1[0,1,2,3], end2[0,1,2,3])
			
			new Track(GRID[2][4].getPos(), S, 3, 1),
			new Track(GRID[2][5].getPos(), S, 3, 1),
			new Track(GRID[2][6].getPos(), S, 3, 1),
			new Track(GRID[2][7].getPos(), S, 3, 1),
			new Track(GRID[2][8].getPos(), S, 3, 1),
			new Track(GRID[2][9].getPos(), S, 3, 1),
			new Track(GRID[2][10].getPos(), S, 3, 1),
			new Track(GRID[2][11].getPos(), C, 3, 2),
			new Track(GRID[3][11].getPos(), S, 0, 2),
			new Track(GRID[4][11].getPos(), C, 0, 3, 2),
			new Track(GRID[5][11].getPos(), S, 0, 2), 	// #10
			new Track(GRID[6][11].getPos(), C, 0, 3),
			new Track(GRID[6][10].getPos(), S, 1, 3),
			new Track(GRID[4][10].getPos(), S, 1, 3),
			new Track(GRID[4][9].getPos(), S, 1, 3),
			new Track(GRID[4][8].getPos(), S, 1, 3),
			new Track(GRID[4][7].getPos(), C, 1, 2),
			new Track(GRID[5][7].getPos(), S, 0, 2),
			new Track(GRID[6][7].getPos(), C, 0, 3),
			new Track(GRID[6][6].getPos(), S, 1, 3),
			new Track(GRID[6][5].getPos(), S, 1, 3, 0),	// #20
			new Track(GRID[5][5].getPos(), S, 2, 0),
			new Track(GRID[6][4].getPos(), S, 1, 3),
			new Track(GRID[6][3].getPos(), C, 1, 0),
			new Track(GRID[5][3].getPos(), S, 2, 0),
			
			
		};
		
		return new FullTrack(stations, tracks);
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
		GridSquare[][] grid = new GridSquare[9][15];
		for(int i=0; i<9; i++) {
			for(int j=0; j<15; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
}
