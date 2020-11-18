package base;

public class Log {
	public static void error(String e) {
		System.err.println("[ERROR] " + getLog(e));
	}
	public static void success(String s) {
		System.out.println("[OK] " + s);
	}
	public static void warning(String w) {
		System.out.println("[!] " + w);
	}
	
	private static String getLog(String log) {
		String[] callerClassSplit = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.");
		String callerClass = callerClassSplit[callerClassSplit.length-1];
		String callerMethod = Thread.currentThread().getStackTrace()[3].getMethodName();

		return String.format("@%s.%s(): %s", callerClass, callerMethod, log);
	}
}

