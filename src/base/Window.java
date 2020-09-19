package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
import javafx.stage.Stage;
import javafx.util.Duration;

public class Window extends Application{
	
	public static Stage window;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	static String saveDirectory;	// directory to save score and fonts
	
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
		
		for(int i=0; i<9; i++) { root.getChildren().addAll(Scenes.GRID[i]);}	// draw the grid
		
		root.getChildren().addAll(tracks);
		root.getChildren().addAll(stations);
		root.getChildren().addAll(ball);
				
		setScene(scene);
			
		AnimationTimer gameTimer = new AnimationTimer() {
			
			private long lastUpdate = 0;
						
			@Override
			public void handle(long now) {		
				
				if(now - lastUpdate >= 20_000_000 ) {					
					if(!animated) {
						boolean beginning = true;

						for(Track track : tracks) {

							if(track.getColumn() == ball.getColumn() && track.getRow() == ball.getRow()) {
								beginning = false;
								
								System.out.println("BALL IN TRACK: " + track);
								
								int[] trackStart = track.getOriginXY();
								int[] trackEnd = track.getEndXY();
								
								MoveTo startPoint = new MoveTo(trackStart[0]+5, trackStart[1]);
								LineTo endPoint = new LineTo(trackEnd[0]+5, trackEnd[1]);
								
								Path path = new Path();
									path.getElements().addAll(startPoint, endPoint);
								
								animated = true;
								PathTransition animation = new PathTransition();
									animation.setDuration(Duration.seconds(10));
									animation.setPath(path);
									animation.setNode(ball);
									animation.setOnFinished(e -> {
										ball.setCenterX(trackEnd[0]);
										ball.setCenterY(trackEnd[1]);
										ball.updateRowsCols();
										animated = false;
									});
									animation.play(); 
									
								if(!animated) {
									break;
								}
							}
						}
						lastUpdate = now;
						if(beginning) {
							ball.moveRight();
						}
					}
					

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
