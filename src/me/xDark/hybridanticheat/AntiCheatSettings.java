package me.xDark.hybridanticheat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;

public class AntiCheatSettings {

	private final FileConfiguration cfg;

	private final HashMap<CheckType, Boolean> values = new HashMap<>();

	private final boolean autoUpdateEnabled;

	private final int maxVL;

	private final long reportDelay;

	private final HashMap<String, String> actions = new HashMap<>();

	private final HashMap<CheckType, ArrayList<String>> checkActions = new HashMap<>();
	
	private final List<String> immunityReportPlayers;

	public AntiCheatSettings(FileConfiguration fileConfiguration) {
		cfg = fileConfiguration;
		autoUpdateEnabled = getBoolean("config.update");
		maxVL = getInt("checks.maxVL");
		immunityReportPlayers = cfg.getStringList("report.immunity");
		cfg.getConfigurationSection("actions").getKeys(false).forEach((name) -> {
			actions.put(name, cfg.getString("actions." + name));
		});
		reportDelay = cfg.getLong("report.delay") * 1000L;
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
		values.put(CheckType.InvalidAction, getBoolean("checks.invalidaction.enabled"));
		values.put(CheckType.Exploits, getBoolean("checks.exploits.enabled"));
		List<CheckType> types = Lists.newArrayList(CheckType.values());
		types.forEach((type) -> {
			values.put(type, getBoolean("checks." + type.name().toLowerCase() + ".enabled"));
		});
		types.forEach((type) -> {
			checkActions.put(type,
					Lists.newArrayList(cfg.getString("checks." + type.name().toLowerCase() + ".action").split(" ")));
		});
	}

	@Override
	protected void finalize() throws Throwable {
		values.clear();
		actions.clear();
		super.finalize();
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

	public int getMaxVL() {
		return maxVL;
	}

	public boolean isAutoUpdateEnabled() {
		return autoUpdateEnabled;
	}

	public HashMap<String, String> getActions() {
		return actions;
	}

	public long getReportDelay() {
		return reportDelay;
	}
	
	public List<String> getImmunityReportPlayers() {
		return immunityReportPlayers;
	}

	public static enum CheckType {
		Flight, HighJump, SpeedHack, Teleport, InvMove, FastLadder, KillAura, Criticals, Exploits, NoClip, InvalidAction;
	}

	public ArrayList<String> getActionsForCheck(CheckType checkType) {
		return checkActions.get(checkType);
	}

}
