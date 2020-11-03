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
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Scenes {
	
	public final static Color COLOR_CONTAINER = Color.web("#282d33");
	public final static Color COLOR_ACCENT = Color.web("#C7B59D");
	
	public final static String BACKGROUND = "#363638";
	public final static GridSquare[][] GRID = getGrid();
	
	public final static String S = "straight";
	public final static String C = "curved";
	
	public static Scene levels() {
		Pane root = getRootPane();
			
		Text title = new Text("SELECT LEVEL");
			title.setId("title");
			title.setFont(Font.font("Hind Guntur Bold"));
			title.setTranslateX(97);
			title.setTranslateY(150);
			
		root.getChildren().add(title);
			
		for(int i=0; i<12; i++) {
			root.getChildren().add(new LevelPane(i<=5 ? 55+i*120: 55+(i-6)*120, i<=5 ? 250 : 350, i+3));
		}
		
		return getSceneWithCSS(root, "levels.css");
	}
	
	public static FullTrack tutorial() {
		Station start = new Station(2, 3, Color.BLACK);
		Station redFinish = new Station(6, 9, Color.RED);
		Station blueFinish = new Station(4, 3, Color.BLUE);
		Station greenFinish = new Station(4, 5, Color.GREEN);
			
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
		
	public static Scene game(String level) {
		
		Pane root = new Pane();
			root.setPrefSize(800, 500);
			root.setStyle("-fx-background-color: rgb(14, 14, 14)");
			
		Text lvl = new Text(level);
			lvl.setFont(Font.font("Hind Guntur Bold", 50));
			lvl.setFill(Color.WHITE);
			lvl.setTranslateX((800 - lvl.getLayoutBounds().getWidth())/2);
			lvl.setTranslateY(250);
			
		root.getChildren().add(lvl);
		
		Scene scene = new Scene(root);
		return scene;
	}
	
	public static Scene getSceneWithCSS(Pane root, String cssFile) {
		Scene scene = new Scene(root);
		scene.getStylesheets().addAll(Window.class.getResource("/resources/styles/" + cssFile).toExternalForm());
		return scene;
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
	
	/* return root Pane with constant parameters */
	public static Pane getRootPane() {
		Pane root = new Pane();
		root.setPrefSize(850, 550);
		root.setId("pane");
		
		return root;
	}
}
