package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import base.obj.Ball;
import base.obj.FullTrack;
import base.obj.Station;
import base.obj.Track;
import javafx.animation.AnimationTimer;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Window extends Application{
	
	public static Stage window;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	static String saveDirectory;	// directory to save score and fonts
	
	private static AnimationTimer gameTimer;
	private static boolean animated = false;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		saveDirectory = System.getenv(OS.equals("linux") ? "HOME" : "APPDATA") + "/trackOfThought";
		
		window = primaryStage;
		window.setTitle("Track of thought");
		window.setResizable(false);
		Setup.runSetup();
		window.show();
	}
	
	public static void game(FullTrack allNodes) {
		
		final Pane root = Scenes.getRootPane();
		final Scene scene = Scenes.getSceneWithCSS(root, "game.css");
		
		final Track[] tracks = allNodes.getTracks();
		final Station[] stations = allNodes.getStations();
		final Ball ball = new Ball(stations[0].getPos(), 10, Color.WHITE);
		
//		for(int i=0; i<9; i++) { root.getChildren().addAll(Scenes.GRID[i]);}	// draw the grid
		
		root.getChildren().addAll(tracks);
		root.getChildren().addAll(stations);
		root.getChildren().addAll(ball);
				
		setScene(scene);
					
		gameTimer = new AnimationTimer() {
			
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {		
				
				if(now - lastUpdate >= 2_000_000) {	
					lastUpdate = now;
//					path = track.getPath();
//					for(int j=0; j<50; j++) {
//						Rectangle r = new Rectangle(1, 1);
//							r.setTranslateX(path[0][j]);
//							r.setTranslateY(path[1][j]);
//							r.setFill(Color.rgb(4*j, 3*j, 2*j));
//						root.getChildren().add(r);
//					}	
					ball.update(allNodes);
				}
				
			}
		}; gameTimer.start();
	}
	
	public static void setScene(Scene scene) {
		window.setScene(scene);
	}
	
	public static void main (String[] args) throws FileNotFoundException {
		
		if(args.length>0) {
			for(String arg : args) {
				switch(arg) {
					case "--log":
						System.out.println("[OK] Logging enabled");
						
						PrintStream outputLog = new PrintStream(new FileOutputStream(new File("log.txt")));
							System.setOut(outputLog);
							System.setErr(outputLog);
					break;
					
					default: break;
				}
			}
		}
		launch(args);
	}
	

}
