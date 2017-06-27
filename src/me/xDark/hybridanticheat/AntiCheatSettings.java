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
		List<CheckType> types = Lists.newArrayList(CheckType.values());
		types.forEach((type) -> {
			values.put(type, getBoolean("checks." + type.name().toLowerCase() + ".enabled"));
			String actions = cfg.getString("checks." + type.name().toLowerCase() + ".action");
			if (actions != null && !actions.trim().isEmpty())
				checkActions.put(type, Lists.newArrayList(actions.split(" ")));
		});
	}

	@Override
	protected void finalize() throws Throwable {
		values.clear();
		actions.clear();
		checkActions.clear();
		immunityReportPlayers.clear();
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
		Flight, HighJump, SpeedHack, Teleport, InvMove, FastLadder, KillAura, Criticals, Exploits, NoClip, InvalidAction, NoSlowDown, NoFall, Freecam, PingSpoof;
	}

	public ArrayList<String> getActionsForCheck(CheckType checkType) {
		return checkActions.get(checkType);
	}

}
