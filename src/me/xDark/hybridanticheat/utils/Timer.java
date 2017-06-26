package me.xDark.hybridanticheat.utils;

public class Timer {

	private long startMS;

	public Timer() {
		reset();
	}

	public void reset() {
		startMS = System.currentTimeMillis();
	}

	public long getMSPassed() {
		return (System.currentTimeMillis() - startMS);
	}

	public boolean hasMSPassed(long time) {
		return (getMSPassed() >= time);
	}
}
