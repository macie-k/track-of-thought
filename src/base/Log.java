package base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
		
	public static void error(String e) {
		System.err.println(getTime() + " [ERROR] " + getLog(e));
	}
	
	public static void success(String s) {
		System.out.println(getTime() + " [OK] " + s);
	}
	public static void warning(String w) {
		System.out.println(getTime() + " [!] " + w);
	}
		
	private static String getLog(String log) {	   
		String[] callerClassSplit = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.");
		String callerClass = callerClassSplit[callerClassSplit.length-1];
		String callerMethod = Thread.currentThread().getStackTrace()[3].getMethodName();

		return String.format("@%s.%s(): %s", callerClass, callerMethod, log);
	}
	
	/* returns current time, the • symbol (\u2022) may not be displayed properly in windows cmd */
	private static String getTime() {
		return String.format("[%s] \u2022", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
	}
}

