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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

public class Window extends Application {
	
	public static Stage window;		// main stage
	public static int points = 0;	// points counter
	public static boolean levelCreator = false;	// temporary variable for level creation

	private static AnimationTimer gameTimer;	// main game timer
	
	private static int level;
	private static int seconds = 0;				// seconds counter for ball releasing
	private static int finishedBalls = 0;
	private static boolean skip = false;
	private static double timeD = 1;

	@Override
	public void start(Stage primaryStage) {
		window = primaryStage;
		window.setTitle("Track of thought");
		window.getIcons().add(new Image("/resources/icon.png"));
		window.setResizable(false);
		
		Setup.runSetup();
		window.show();
	}
		
	public static void game(FullTrack allNodes) {
		seconds = 0;
		finishedBalls = 0;
		points = 0;
		timeD = 1;
		
		final Pane root = Scenes.getRootPane();							// game root pane
		final Scene scene = Scenes.getSceneWithCSS(root, "game.css");	// main game scene
		
		final List<Ball> balls = new ArrayList<>(allNodes.getBalls());			// list of all balls
		final List<Track> tracks = new ArrayList<>(allNodes.getTracks());		// list of all tracks
		final List<Station> stations = new ArrayList<>(allNodes.getStations()); // list of all stations
						
		/* StackPane for points counter */
		final StackPane pointsStack = new StackPane();
			pointsStack.setTranslateX(10);
			pointsStack.setTranslateY(0);
			pointsStack.setPrefWidth(70);
			pointsStack.setPrefHeight(30);
			pointsStack.setBackground(new Background(new BackgroundFill(Color.rgb(145, 139, 119, 0.3), new CornerRadii(0), new Insets(0))));
			
		/* text with points value */
		final Text pointsText = new Text("0/0");
			pointsText.setFill(Utils.COLOR_ACCENT);
			pointsText.setFont(Font.font("Hind Guntur Bold", 20));
			pointsText.setBoundsType(TextBoundsType.VISUAL);

		/* add everything to the root pane */
		StackPane.setAlignment(pointsText, Pos.CENTER);
		StackPane.setMargin(pointsText, new Insets(0));
		
		pointsStack.getChildren().addAll(pointsText);
		
		root.getChildren().addAll(tracks);
		root.getChildren().add(allNodes.getStartStation().getFix());
		root.getChildren().addAll(balls);
		root.getChildren().addAll(stations);
		root.getChildren().add(pointsStack);
		
//		for(int i=0; i<15; i++) { root.getChildren().addAll(Scenes.GRID[i]);}	// grid drawing

		level = stations.size()-1;
		setScene(root);	// set scene with all elements
		
		
		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.SPACE) {
				timeD = 0.25;
			}
		});
		
		scene.setOnKeyReleased(e -> {
			if(e.getCode() == KeyCode.SPACE) {
				timeD = 1;
			}
		});
									
		gameTimer = new AnimationTimer() {
			private long lastUpdate = 0;
			private long secondsUpdate = 0;
						
			@Override
			public void handle(long now) {
				
				try {
					/* game timer */
					if(now - lastUpdate >= 34_000_000*timeD) {
						if(balls.size() > 0) {
							gameHandle(root, balls, pointsText, allNodes);
							lastUpdate = now;
						} else {
							gameTimer.stop();							
							if(Utils.unlockLevel((finishedBalls - points) <= 3, level)) {
								Log.success("Next level unlocked");
							}
							Window.setScene(Scenes.levels());
						}
					}
					
				} catch (Exception e) {
					Log.error(e.getMessage());
				}
				
				/* timer for counting seconds & managing new balls */
				if(now - secondsUpdate >= 1_000_000_000*timeD) {
					seconds++;
					secondsHandle(root, balls, allNodes);
					secondsUpdate = now;
				}
			}
		}; gameTimer.start();
				
	}
	
	/* main game */
	private static void gameHandle(Pane root, List<Ball> balls, Text pointsText, FullTrack allNodes) {
		List<Ball> toRemove = new ArrayList<Ball>();	// list of balls that should be removed
		for(Ball ball : balls) {
			/* update ball only if should be, or already is 'released' */
			if(seconds >= ball.getDelay()) {
				ball.update(allNodes);
				
				/* if ball finished 'parking' */
				if(ball.getCounter() == 15) {
					finishedBalls++;
					allNodes.removeActiveBall(ball);
					
					Station finalStation = allNodes.findStation(ball.getColumn(), ball.getRow());	// get final station				
					/* if the station is correct update points value */
					if(finalStation.getColor() == ball.getColor() && (finalStation.hasBorder() == ball.hasBorder())) {
						points++;
					} else {
						skip = true;
					}
					pointsText.setText(String.valueOf(points) + "/" + String.valueOf(finishedBalls));
					root.getChildren().remove(ball);	// remove the ball from root pane when 'parked'
					toRemove.add(ball);					// add to removal list
				}
			}
		}
		balls.removeAll(toRemove);	// remove balls that finished track
	}
	
	/* updates seconds and sets colors of the new balls */
	private static void secondsHandle(Pane root, List<Ball> balls, FullTrack allNodes) {
		for(Ball ball : balls) {
			if(seconds == ball.getDelay()) {
				
				/* allow skipping only if there is enough active balls */
				if(skip) {
					skip = false;
					if(allNodes.getActiveBalls().size() > level) {
						balls.remove(ball);
						root.getChildren().remove(ball);
						break;
					}
				}
				
				String newColor = Scenes.getNextBallColor(allNodes);
				ball.setColor(newColor);
				ball.showBorder();
			
				allNodes.addActiveBall(ball);
				break;
			}
		}
	}
			
	/* sets the main scene with fade in effect */
	public static void setScene(Pane root) {
			root.setOpacity(0);
		window.setScene(root.getScene());
			Utils.fadeIn(root, 500);
	}
		
	public static void main (String[] args) throws FileNotFoundException {		
		/* 
		 	currently available arguments:
		 		- ide: disables color logging
		 		- log: enables logging to file for debugging 
		 		- create: allows to create new levels
		*/
		if(args.length>0) {
			for(String arg : args) {
				switch(arg) {
					case "--nocolors":
						Log.IDE = true;
						Log.success("Colored logging is disabled");
						break;
					case "--create":
						levelCreator = true;
						Log.success("Creator mode enabled");
						break;
					case "--log":
						Log.success("Logging enabled");
						PrintStream outputLog = new PrintStream(new FileOutputStream(new File("log.txt")));
							System.setOut(outputLog);
							System.setErr(outputLog);
					break;
					default:
						Log.warning("Unknown argument: " + arg);
				}
			}
		}
		launch(args);
	}
}
