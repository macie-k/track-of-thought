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

import static base.Scenes.RED;
import static base.Scenes.BLACK;
import static base.Scenes.GREEN;
import static base.Scenes.BLUE;

public class Window extends Application {
	
	public static Stage window;
	public static int points=0;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	static String saveDirectory;	// directory to save score and fonts
	
	private static AnimationTimer gameTimer, secondsCounter;
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
	
	public static void game(FullTrack allNodes) {
		
		final Pane root = Scenes.getRootPane();
		final Scene scene = Scenes.getSceneWithCSS(root, "game.css");
		
		final Track[] tracks = allNodes.getTracks();
		final Station[] stations = allNodes.getStations();
		double[] startCoords = {stations[0].getTranslateX(), stations[0].getTranslateY()};
		
		List<Ball> balls = new ArrayList<Ball>();
			balls.add(new Ball(startCoords, RED, tracks, 1));
			balls.add(new Ball(startCoords, GREEN, tracks, 5));
			balls.add(new Ball(startCoords, RED, tracks, 7));
			balls.add(new Ball(startCoords, BLUE, tracks, 9));
			balls.add(new Ball(startCoords, RED, tracks, 13));
			balls.add(new Ball(startCoords, GREEN, tracks, 15));
			balls.add(new Ball(startCoords, BLUE, tracks, 18));
			balls.add(new Ball(startCoords, RED, tracks, 21));
			balls.add(new Ball(startCoords, GREEN, tracks, 24));
			balls.add(new Ball(startCoords, BLUE, tracks, 26));
			
		final String totalBalls = String.valueOf(balls.size());
		
		final StackPane pointsStack = new StackPane();
			pointsStack.setTranslateX(0);
			pointsStack.setTranslateY(30);
			pointsStack.setPrefSize(850, 30);
		final Text pointsText = new Text("0/" + totalBalls);
			pointsText.setFill(Scenes.COLOR_ACCENT);
			pointsText.setFont(Font.font("Hind Guntur Bold", 23));
		
//		for(int i=0; i<9; i++) { root.getChildren().addAll(Scenes.GRID[i]);}	// draw the grid
		
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
				if(now - lastUpdate >= 15_000_000) {
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
