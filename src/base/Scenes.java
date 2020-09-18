package base;

import base.obj.GridSquare;
import base.obj.LevelPane;
import base.obj.Track;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
	
	public static Scene tutorial() {
		Pane root = getRootPane();
		
		GridSquare start = new GridSquare(3, 2);
			start.setFill(Color.BLACK);
		GridSquare redFinish = new GridSquare(9, 6);
			redFinish.setFill(Color.RED);
		GridSquare blueFinish = new GridSquare(3, 4);
			blueFinish.setFill(Color.BLUE);
		GridSquare greenFinish = new GridSquare(5, 4);
			greenFinish.setFill(Color.GREEN);
			
		GridSquare[] endStations = {start, redFinish, blueFinish, greenFinish};
		root.getChildren().addAll(endStations);
		
//		for(int i=0; i<15; i++) {
//			root.getChildren().addAll(GRID[i]);
//		}
		
		Track[] tracks = {
//			[S=straight, C=curved]
//			[0=top, 1=right, 2=bottom, 3=left]	
//			(xy, type[S, C], origin[0,1,2,3], end1[0,1,2,3], end2[0,1,2,3])
			
			new Track(GRID[4][2].getXY(), S, 3, 1),
			new Track(GRID[5][2].getXY(), S, 3, 1),
			new Track(GRID[6][2].getXY(), S, 3, 1),
			new Track(GRID[7][2].getXY(), S, 3, 1),
			new Track(GRID[8][2].getXY(), S, 3, 1),
			new Track(GRID[9][2].getXY(), S, 3, 1),
			new Track(GRID[10][2].getXY(), S, 3, 1),
			new Track(GRID[11][2].getXY(), C, 3, 2),
			new Track(GRID[11][3].getXY(), S, 0, 2),
			new Track(GRID[11][4].getXY(), C, 0, 3, 2),
			new Track(GRID[11][5].getXY(), S, 3, 2), 	// #10
			new Track(GRID[11][6].getXY(), C, 0, 3),
			new Track(GRID[10][6].getXY(), S, 1, 3),
			new Track(GRID[10][4].getXY(), S, 1, 3),
			new Track(GRID[9][4].getXY(), S, 1, 3),
			new Track(GRID[8][4].getXY(), S, 1, 3),
			new Track(GRID[7][4].getXY(), C, 2, 1),
			new Track(GRID[7][5].getXY(), S, 2, 1),
			new Track(GRID[7][6].getXY(), C, 0, 3),
			new Track(GRID[6][6].getXY(), S, 1, 3),
			new Track(GRID[5][6].getXY(), S, 1, 3, 0),	// #20
			new Track(GRID[5][5].getXY(), S, 2, 0),
			new Track(GRID[4][6].getXY(), S, 1, 3),
			new Track(GRID[3][6].getXY(), C, 1, 0),
			new Track(GRID[3][5].getXY(), S, 2, 0),
		};
		
		
		root.getChildren().addAll(tracks);
		return getSceneWithCSS(root, "game.css");
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
		GridSquare[][] grid = new GridSquare[15][9];
		for(int i=0; i<15; i++) {
			for(int j=0; j<9; j++) {
				grid[i][j] = new GridSquare(i, j);
			}
		}
		return grid;
	}
	
	/* return root Pane with constant parameters */
	private static Pane getRootPane() {
		Pane root = new Pane();
		root.setPrefSize(850, 550);
		root.setId("pane");
		
		return root;
	}
}
