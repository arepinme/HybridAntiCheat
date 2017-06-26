package me.xDark.hybridanticheat.wrapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {

	/**
	 * Returns the greatest integer less than or equal to the double argument
	 */
	public static int floor_double(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

	public static double round(double value, int places) {
		if (places < 0)
			return 0D;
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
