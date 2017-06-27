package me.xDark.hybridanticheat.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.bot.FakeBot;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.checks.impl.FlightCheck;
import me.xDark.hybridanticheat.checks.impl.NoFallCheck;
import me.xDark.hybridanticheat.checks.impl.NoSlowDownCheck;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.hook.ProtocolHook;
import me.xDark.hybridanticheat.utils.ClassUtil;
import me.xDark.hybridanticheat.utils.MathUtil;
import me.xDark.hybridanticheat.utils.ReflectionUtil;

public class HybridAPI {

	private static final String craftVersion, nmsVersion;

	private static final Class<?> craftPlayerClass, entityPlayerClass;

	private static final Method getHandleMethod;

	private static final Field pingField;

	private static final File reportFile = new File(HybridAntiCheat.instance().getDataFolder(), "reports.yml");

	private static YamlConfiguration reportYaml = YamlConfiguration.loadConfiguration(reportFile);

	private static final TreeMap<Integer, Report> reports = new TreeMap<>();

	public static final HashSet<String> actionsPerformed = new HashSet<>();

	static {
		craftVersion = Bukkit.getServer().getClass().getPackage().getName();
		// http://rubukkit.org/threads/wip-code-snippets-poleznye-kuski-koda-i-nekotorye-neochevidnye-veschi-likbez.134693/page-2
		nmsVersion = craftVersion.replace("org.bukkit.craftbukkit", "net.minecraft.server");
		HybridAntiCheat.instance().getLogger()
				.info("Detected bukkit version: " + craftVersion.replace('.', ',').split(",")[3]);
		craftPlayerClass = ClassUtil.findClass(craftVersion + ".entity.CraftPlayer");
		entityPlayerClass = ClassUtil.findClass(nmsVersion + ".EntityPlayer");
		getHandleMethod = ReflectionUtil.findMethod(craftPlayerClass, "getHandle", new Class<?>[0]);
		pingField = ReflectionUtil.findField(entityPlayerClass, "ping");
	}

	private static final HashMap<Player, User> users = new HashMap<>();

	public static void start() {
		users.clear();
		Bukkit.getOnlinePlayers().forEach((player) -> {
			registerPlayer(player);
		});
		loadReports();
	}

	public static void stop() {
		users.values().forEach((user) -> {
			user.gc();
		});
		users.clear();
		FlightCheck.floatingTime.clear();
		ProtocolHook.teleportAttempts.clear();
		ProtocolHook.channelRegisterMap.clear();
		ProtocolHook.safetyLocations.clear();
		NoFallCheck.fallDistance.clear();
		actionsPerformed.clear();
		saveReports();

	}

	private static void loadReports() {
		reportYaml.getKeys(false).forEach((reportId) -> {
			int id = Integer.parseInt(reportId);
			String sender = reportYaml.getString(reportId + ".sender");
			String target = reportYaml.getString(reportId + ".target");
			String reason = reportYaml.getString(reportId + ".reason");
			reports.put(id, new Report(id, sender, target, reason));
		});
	}

	private static void saveReports() {
		reportFile.delete();
		try {
			reportFile.createNewFile();
		} catch (IOException e) {
		}
		reportYaml = new YamlConfiguration();
		reports.forEach((id, report) -> {
			reportYaml.set(String.valueOf(id) + ".sender", report.getSender());
			reportYaml.set(String.valueOf(id) + ".target", report.getTarget());
			reportYaml.set(String.valueOf(id) + ".reason", report.getReason());
		});
		try {
			reportYaml.save(reportFile);
		} catch (IOException e) {
		}
		reports.clear();
	}

	public static void registerPlayer(Player player) {
		users.put(player, new User(player));
		FlightCheck.floatingTime.put(player, new AtomicInteger(0));
		ProtocolHook.teleportAttempts.put(player, new AtomicInteger(0));
		NoSlowDownCheck.attempts.put(player, new AtomicInteger(0));
	}

	public static void unregisterPlayer(Player player) {
		User user = users.get(player);
		if (user == null)
			return;
		user.gc();
		users.remove(player);
		ProtocolHook.channelRegisterMap.remove(player);
		FlightCheck.floatingTime.remove(player);
		ProtocolHook.teleportAttempts.remove(player);
		NoSlowDownCheck.attempts.remove(player);
	}

	public static void performActions(User user, CheckType checkType) {
		if (user == null)
			return;
		if (user.isVerbose())
			return;
		if (!user.getHandle().isOnline())
			return;
		if (actionsPerformed.contains(user.getHandle().getName()))
			return;
		HybridAntiCheat.callSyncMethod(() -> {
			ArrayList<String> actions = HybridAntiCheat.instance().getSettings().getActionsForCheck(checkType);
			if (actions != null && !actions.isEmpty())
				actions.forEach((action) -> {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
							HybridAntiCheat.instance().getSettings().getActions().get(action)
									.replace("%player%", user.getHandle().getPlayer().getName())
									.replace("%checktype%", checkType.name()));
				});
			if (!user.getHandle().isOnline()) {
				user.gc();
				users.remove(user.getHandle());
			}
			return (Void) null;
		});
		actionsPerformed.add(user.getHandle().getName());
	}

	public static User getUser(Object o) {
		return users.get((Player) o);
	}

	public static int getPing(Player handle) {
		return ReflectionUtil.getValue(pingField,
				ReflectionUtil.invoke(getHandleMethod, craftPlayerClass.cast(handle)));
	}

	public static boolean isInvalidMaterial(Material type) {
		return (type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LAVA
				|| type == Material.STATIONARY_LAVA);
	}

	public static Block getBlockInfront(Entity entity, float distance) {
		double yaw = entity.getLocation().getYaw();
		yaw = Math.toRadians(yaw);
		double dX = -Math.sin(yaw) * distance;
		double dZ = Math.cos(yaw) * distance;
		return entity.getWorld().getBlockAt(new Location(entity.getWorld(), entity.getLocation().getX() + dX,
				entity.getLocation().getY(), entity.getLocation().getZ() + dZ));
	}

	public static void clearVLs() {
		users.values().forEach(user -> {
			user.resetVL();
		});
	}

	public static void showReports(CommandSender s, int page) {
		int pages = (int) (reports.size() / 8F);
		if (pages == 0) {
			for (Entry<Integer, Report> entry : reports.entrySet()) {
				Report report = entry.getValue();
				s.sendMessage("-----------------------------");
				s.sendMessage(HybridAntiCheat.getPrefix() + "Номер жалобы: " + report.getId());
				s.sendMessage(HybridAntiCheat.getPrefix() + "Отправитель жалобы: " + report.getSender());
				s.sendMessage(HybridAntiCheat.getPrefix() + "Нарушитель: " + report.getTarget());
				s.sendMessage(HybridAntiCheat.getPrefix() + "Причина: " + report.getReason());
				s.sendMessage("-----------------------------");
				s.sendMessage("");
			}
			return;
		}
		if (page > pages || page < 1) {
			s.sendMessage(HybridAntiCheat.getPrefix() + "Страница задана не верно.");
			return;
		}
		int i = 0;
		for (Entry<Integer, Report> entry : reports.entrySet()) {
			i++;
			if (i <= (page - 1) * 8)
				continue;
			if (i > page * 8)
				break;
			Report report = entry.getValue();
			s.sendMessage("-----------------------------");
			s.sendMessage(HybridAntiCheat.getPrefix() + "Номер жалобы: " + report.getId());
			s.sendMessage(HybridAntiCheat.getPrefix() + "Отправитель жалобы: " + report.getSender());
			s.sendMessage(HybridAntiCheat.getPrefix() + "Нарушитель: " + report.getTarget());
			s.sendMessage(HybridAntiCheat.getPrefix() + "Причина: " + report.getReason());
			s.sendMessage("-----------------------------");
			s.sendMessage("");
		}
	}

	public static int performReport(String sender, String target, String reason) {
		int nextReport = reports.size() + 1;
		reports.put(nextReport, new Report(nextReport, sender, target, reason));
		return nextReport;
	}

	public static boolean hasReportImmunity(String player) {
		Player target = Bukkit.getPlayer(player);
		return HybridAntiCheat.instance().getSettings().getImmunityReportPlayers().contains(player.toLowerCase())
				|| (target != null && HybridAntiCheat.checkPermission(target, "command.report.immunity"));
	}

	public static boolean removeReport(int reportId) {
		return reports.remove(reportId) != null;
	}

	public static void spawnRandomBots() {
		if (users.isEmpty())
			return;
		users.values().forEach((user) -> {
			if (!user.getBot().isSpawned())
				user.getBot().spawn();
		});
	}

	public static void checkBots() {
		if (users.isEmpty())
			return;
		users.values().forEach((user) -> {
			FakeBot bot = user.getBot();
			if (bot.isSpawned()) {
				if (bot.getTicksExisted() >= 8000L) {
					if (bot.wasAttacked()) {
						Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.KillAura));
						performActions(user, CheckType.KillAura);
					}
					user.getBot().destory();
				} else
					bot.runTick();
			}
		});
	}

	public static void checkFrozen() {
		if (!HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Freecam))
			return;
		if (users.isEmpty())
			return;
		users.values().forEach((user) -> {
			if (user.isFrozen()) {
				HybridAntiCheat.instance().notify(
						"§fИгрок §6" + user.getHandle().getName() + " §fне посылает пакетов. §cFreecam§f?",
						"hac.notify.staff");
			}
		});
	}

	public static void updateAndCheckPing() {
		if (!HybridAntiCheat.instance().getSettings().isEnabled(CheckType.PingSpoof))
			return;
		if (users.isEmpty())
			return;
		users.values().forEach((user) -> {
			if (user.shouldUpdatePing()) {
				user.updatePing();
				int difference = (int) MathUtil.diff(user.getOldPing(), user.getNewPing());
				if (difference >= 250)
					HybridAntiCheat.instance()
							.notify("§fУ игрока §6" + user.getHandle().getName()
									+ "§f сильно изменился пинг. Предыдущий пинг: §6 " + user.getOldPing()
									+ "§f, нынешний пинг: §6" + user.getNewPing(), "hac.notify.staff");
				else if ((user.getOldPing() <= 1) || (user.getNewPing() <= 1))
					HybridAntiCheat.instance().notify("§fУ игрока §6" + user.getHandle().getName()
							+ "§f не валидный пинг: §6" + user.getOldPing(), "hac.notify.staff");
			}
		});
	}

	public static HashMap<Player, User> getUsers() {
		return users;
	}

}
