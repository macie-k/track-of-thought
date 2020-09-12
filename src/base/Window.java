package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javafx.application.Application;
import javafx.stage.Stage;

public class Window extends Application{
	
	public static Stage window;
	public static final String OS = System.getProperty("os.name").toLowerCase();

	static String saveDirectory;	// directory to save score and fonts

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		saveDirectory = System.getenv(OS.equals("linux") ? "HOME" : "APPDATA") + "/trackOfThought";
		
		window = primaryStage;
		window.setTitle("Track of thought");
		window.setResizable(false);
		Setup.runSetup();
		window.show();
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
