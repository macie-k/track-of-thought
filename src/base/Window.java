package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import base.obj.Ball;
import base.obj.FullTrack;
import base.obj.Station;
import base.obj.Track;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Window extends Application {
	
	public static Stage window;		// main stage
	public static int points = 0;	// points counter
	public static final String OS = System.getProperty("os.name").toLowerCase();	// get current operating system
	public static boolean levelCreator = false;	// temporary variable for level creation

	static String saveDirectory;				// directory to save score and fonts
	
	private static AnimationTimer gameTimer;	// main game timer
	private static int seconds = 0;				// seconds counter for ball releasing

	@Override
	public void start(Stage primaryStage) throws Exception {
		saveDirectory = System.getenv(OS.equals("linux") ? "HOME" : "APPDATA") + "/Track of thought";
		
		window = primaryStage;
		window.setTitle("Track of thought");
		window.setResizable(false);
		Setup.runSetup();
		window.show();
	}
		
	public static void game(FullTrack allNodes) {
		final Pane root = Scenes.getRootPane();							// game root pane
		final Scene scene = Scenes.getSceneWithCSS(root, "game.css");	// main game scene
		
		final List<Track> tracks = allNodes.getTracks();		// list of all tracks
		final List<Station> stations = allNodes.getStations();	// list of all stations
		final List<Ball> balls = allNodes.getBalls();			// list of all balls
		
		final String totalBalls = String.valueOf(balls.size());	// amount of balls
		
		/* StackPane for points counter */
		final StackPane pointsStack = new StackPane();
			pointsStack.setTranslateX(0);
			pointsStack.setTranslateY(30);
			pointsStack.setPrefSize(850, 30);
			
		/* text with points value */
		final Text pointsText = new Text("0/" + totalBalls);
			pointsText.setFill(Utils.COLOR_ACCENT);
			pointsText.setFont(Font.font("Hind Guntur Bold", 23));
			
		pointsStack.getChildren().add(pointsText);

		/* grid drawing */
//		for(int i=0; i<15; i++) { root.getChildren().addAll(Scenes.GRID[i]);}
		
		/* add everything to the roott pane */
		root.getChildren().addAll(tracks);
		root.getChildren().addAll(balls);
		root.getChildren().addAll(stations);
		root.getChildren().add(pointsStack);
		
		setScene(scene);	// set scene with all elements
									
		gameTimer = new AnimationTimer() {
			private long lastUpdate = 0;
			private long secondsUpdate = 0;

			@Override
			public void handle(long now) {		
				if(now - lastUpdate >= 16_000_000) {
					List<Ball> toRemove = new ArrayList<Ball>();	// list of balls that should be removed
					
					for(Ball ball : balls) {
						/* update ball only if should be, or already is 'released' */
						if(seconds >= ball.getDelay()) {
							ball.update(allNodes);
							
							/* if ball finished 'parking' */
							if(ball.getCounter() == 25) {
								Station finalStation = allNodes.findStation(ball.getColumn(), ball.getRow());	// get final station
								/* if the station is correct update points value */
								if(finalStation.getColor().equals(ball.getColor()) && (finalStation.getBorder() == ball.getBorder())) {
									pointsText.setText(String.valueOf(++points) + "/" + totalBalls);
								}
								root.getChildren().remove(ball);	// remove the ball from root pane when 'parked'
								toRemove.add(ball);					// add to removal list
							}
						}
					}
					balls.removeAll(toRemove);	// remove balls that finished track
					lastUpdate = now;
				}
				
				/* timer for counting seconds */
				if(now - secondsUpdate >= 1_000_000_000) {
					secondsUpdate = now;
					seconds++;
				}
			}
		}; gameTimer.start();
				
	}
	
	/* sets the main scene */
	public static void setScene(Scene scene) {
		window.setScene(scene);
	}
	
	
	public static void main (String[] args) throws FileNotFoundException {
		/* parses arguments */
		if(args.length>0) {
			/* 
			 	currently available arguments:
			 		- log: enables logging to file for debugging 
			*/
			for(String arg : args) {
				switch(arg) {
					case "--log":
						System.out.println("[OK] Logging enabled");
						PrintStream outputLog = new PrintStream(new FileOutputStream(new File("log.txt")));
							System.setOut(outputLog);
							System.setErr(outputLog);
					break;
				}
			}
		}
		launch(args);
	}
	

}
