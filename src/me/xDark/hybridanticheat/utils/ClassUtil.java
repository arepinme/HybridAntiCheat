package me.xDark.hybridanticheat.utils;

public class ClassUtil {
	
	public static void init() {}

	public static Class<?> findClass(String className) {
		try {
			return Class.forName(className);
		} catch (Throwable t) {
			return null;
		}
	}

}
