package me.xDark.hybridanticheat.utils;

public class CastUtil {

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	public static boolean canBeCasted(Object from, Class<?> to) {
		try {
			to.cast(from);
			return true;
		} catch (Exception exc) {
			return false;
		}
	}
}
