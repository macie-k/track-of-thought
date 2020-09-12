package base;

public class Log {
	public static void error(String e) {
		System.err.println("[ERROR] " + e);
	}
	public static void success(String s) {
		System.out.println("[OK] " + s);
	}
	public static void warning(String w) {
		System.out.println("[!] " + w);
	}
}
