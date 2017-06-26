package me.xDark.hybridanticheat;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

public class AntiCheatSettings {

	private final FileConfiguration cfg;

	private final HashMap<CheckType, Boolean> values = new HashMap<>();


	private final boolean autoUpdateEnabled;

	private final int maxVL;

	public AntiCheatSettings(FileConfiguration fileConfiguration) {
		cfg = fileConfiguration;
		autoUpdateEnabled = getBoolean("config.update");
		maxVL = getInt("checks.maxVL");
	}

	public void apply() {
		values.put(CheckType.Flight, getBoolean("checks.flight.enabled"));
		values.put(CheckType.HighJump, getBoolean("checks.highjump.enabled"));
		values.put(CheckType.SpeedHack, getBoolean("checks.speedhack.enabled"));
		values.put(CheckType.Teleport, getBoolean("checks.teleport.enabled"));
		values.put(CheckType.InvMove, getBoolean("checks.invmove.enabled"));
		values.put(CheckType.FastLadder, getBoolean("checks.fastladder.enabled"));
		values.put(CheckType.KillAura, getBoolean("checks.killaura.enabled"));
		values.put(CheckType.Criticals, getBoolean("checks.criticals.enabled"));
		values.put(CheckType.NoClip, getBoolean("checks.noclip.enabled"));
	}

	@Override
	protected void finalize() throws Throwable {
		values.clear();
		super.finalize();
	}

	public int getMaxVL() {
		return maxVL;
	}

	public boolean isAutoUpdateEnabled() {
		return autoUpdateEnabled;
	}

	private int getInt(String path) {
		return cfg.getInt(path);
	}

	private boolean getBoolean(String path) {
		return cfg.getBoolean(path);
	}

	public boolean isEnabled(CheckType check) {
		return values.get(check);
	}

	public static enum CheckType {
		Flight, HighJump, SpeedHack, Teleport, InvMove, FastLadder, KillAura, Criticals, Exploits, NoClip;
	}

}
