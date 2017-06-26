package me.xDark.hybridanticheat;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.checks.CheckManager;
import me.xDark.hybridanticheat.hook.ProtocolHook;
import me.xDark.hybridanticheat.listener.CheckListener;
import me.xDark.hybridanticheat.listener.UserListener;
import me.xDark.hybridanticheat.listener.ValidateListener;
import me.xDark.hybridanticheat.utils.ClassUtil;

public class HybridAntiCheat extends JavaPlugin {

	private static HybridAntiCheat instance;

	private AntiCheatSettings settings;

	private static int updateTaskId;

	private static final Listener userListener = new UserListener(), validateListener = new ValidateListener(),
			moveListener = new CheckListener();

	public HybridAntiCheat() {
		instance = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		(settings = new AntiCheatSettings(getConfig())).apply();
		if (settings.isAutoUpdateEnabled())
			updateTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
				reload(null);
			}, 1L, 20L * 60L * 5L);
		ClassUtil.init();
		HybridAPI.start();
		getServer().getPluginManager().registerEvents(userListener, this);
		getServer().getPluginManager().registerEvents(validateListener, this);
		getServer().getPluginManager().registerEvents(moveListener, this);
		CheckManager.init();
		ProtocolHook.hook();
		getLogger().info("enabled");
	}

	private void reload(CommandSender s) {
		AntiCheatSettings old = settings;
		try {
			reloadConfig();
			(settings = new AntiCheatSettings(getConfig())).apply();
			if (!settings.isAutoUpdateEnabled())
				getServer().getScheduler().cancelTask(updateTaskId);
			if (s != null)
				s.sendMessage(getPrefix() + "§aConfiguration reloaded.");
		} catch (Exception exc) {
			if (s != null)
				s.sendMessage(getPrefix() + "§cFailed to reload configuration. Using old settings.");
			else
				notify("§cReload failed. Check console for errors. Using old settings.", "hac.notify.admins");
			settings = old;
			settings.apply();
		}
	}

	public void notify(Object message, String permission) {
		StringBuilder builder = new StringBuilder().append(getPrefix()).append(message);
		if ((permission == null) || (permission.trim().equals("")))
			Bukkit.broadcastMessage(builder.toString());
		else {
			HybridAPI.getUsers().values().forEach((user) -> {
				if (user.getHandle().hasPermission(permission) || user.isVerbose())
					user.getHandle().sendMessage(builder.toString());
			});
		}
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String str, String[] args) {
		if (s.equals(Bukkit.getConsoleSender()))
			return true;
		if (!checkPermission(s, "commands.use")) {
			s.sendMessage(getPrefix() + " §cNo permission to perform this action.");
			return true;
		}
		if (args.length == 0) {
			s.sendMessage(getPrefix() + "§f/hac verbose §3- toggles verbose off/on");
			s.sendMessage(getPrefix() + "§f/hac reload §3- reloads configuration");
			return true;
		}
		if (args[0].equalsIgnoreCase("verbose")) {
			if (!checkPermission(s, "commands.verbose")) {
				s.sendMessage(getPrefix() + " §cNo permission to perform this action.");
				return true;
			}
			boolean verbose = HybridAPI.getUser(s).updateVerbose();
			s.sendMessage(getPrefix() + "§fVerbose turned " + (verbose ? "§aon" : "§coff"));
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (!checkPermission(s, "commands.reload")) {
				s.sendMessage(getPrefix() + " §cNo permission to perform this action.");
				return true;
			}
			reload(s);
		} else
			s.sendMessage(getPrefix() + "§cCommand not found.");
		return true;
	}

	private static boolean checkPermission(CommandSender s, String permission) {
		return s.hasPermission("hac." + permission);
	}

	public static void callSyncMethod(Callable<?> call) {
		Bukkit.getScheduler().callSyncMethod(instance, call);
	}

	@Override
	public void onDisable() {
		HybridAPI.stop();
		getServer().getScheduler().cancelTasks(this);
		ProtocolHook.unhook();
		getLogger().info("disabled");
	}

	public AntiCheatSettings getSettings() {
		return settings;
	}

	public static String getPrefix() {
		return "§f[§cHybrid AC§f] §r";
	}

	public static HybridAntiCheat instance() {
		return instance;
	}

}
