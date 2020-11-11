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
	
	public static Stage window;
	public static int points = 0;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	static String saveDirectory;	// directory to save score and fonts
	
	private static AnimationTimer gameTimer;
	private static int seconds = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		saveDirectory = System.getenv(OS.equals("linux") ? "HOME" : "APPDATA") + "/Track of thought";
		
		window = primaryStage;
		window.setTitle("Track of thought");
		window.setResizable(false);
		Setup.runSetup();
		window.show();
	}
	
	public static void createLevel(Scene scene) {
		setScene(scene);
	}
	
	public static void game(FullTrack allNodes) {
		
		final Pane root = Scenes.getRootPane();
		final Scene scene = Scenes.getSceneWithCSS(root, "game.css");
		
		final List<Track> tracks = allNodes.getTracks();
		final List<Station> stations = allNodes.getStations();
		final List<Ball> balls = allNodes.getBalls();
		
		final String totalBalls = String.valueOf(balls.size());
		
		final StackPane pointsStack = new StackPane();
			pointsStack.setTranslateX(0);
			pointsStack.setTranslateY(30);
			pointsStack.setPrefSize(850, 30);
		final Text pointsText = new Text("0/" + totalBalls);
			pointsText.setFill(Scenes.COLOR_ACCENT);
			pointsText.setFont(Font.font("Hind Guntur Bold", 23));
		
		for(int i=0; i<15; i++) { root.getChildren().addAll(Scenes.GRID[i]);}	// draw the grid
		
		root.getChildren().addAll(tracks);
		root.getChildren().addAll(balls);
		root.getChildren().addAll(stations);
		pointsStack.getChildren().add(pointsText);
		root.getChildren().add(pointsStack);
		
		setScene(scene);
									
		gameTimer = new AnimationTimer() {
			private long lastUpdate = 0;
			private long secondsUpdate = 0;

			@Override
			public void handle(long now) {		
				if(now - lastUpdate >= 16_000_000) {
					List<Ball> toRemove = new ArrayList<Ball>();
					for(Ball ball : balls) {
						if(seconds >= ball.getDelay()) {
							ball.update(allNodes);
							
							if(ball.getCounter() == 25) {
								Station finalStation = allNodes.findStation(ball.getColumn(), ball.getRow());
								if(finalStation.getColor().equals(ball.getColor())) {
									pointsText.setText(String.valueOf(++points) + "/" + totalBalls);
								}
								root.getChildren().remove(ball);
								toRemove.add(ball);
							}
						}
					}
					balls.removeAll(toRemove);
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
