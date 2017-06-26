package me.xDark.hybridanticheat.utils;

public class ParseUtil {

	public static boolean parseInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseShort(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseByte(String s) {
		try {
			Byte.parseByte(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean parseBoolean(String s) {
		try {
			Boolean.parseBoolean(s);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}
}
