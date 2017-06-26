package me.xDark.hybridanticheat.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.hook.ProtocolHook;
import me.xDark.hybridanticheat.utils.ClassUtil;
import me.xDark.hybridanticheat.utils.ReflectionUtil;

public class HybridAPI {

	private static final String craftVersion, nmsVersion;

	private static final Class<?> craftPlayerClass, entityPlayerClass;

	private static final Method getHandleMethod;

	private static final Field pingField;

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
	}

	public static void stop() {
		users.values().forEach((user) -> {
			user.gc();
		});
		users.clear();
	}

	public static void registerPlayer(Player player) {
		users.put(player, new User(player));
	}

	public static void unregisterPlayer(Player player) {
		User user = users.get(player);
		if (user == null)
			return;
		user.gc();
		users.remove(player);
		ProtocolHook.channelRegisterMap.remove(player);
	}

	public static void disconnectUser(User user, CheckType checkType) {
		if (user == null)
			return;
		if (user.isVerbose())
			return;
		users.remove(user.getHandle());
		user.getHandle().kickPlayer(HybridAntiCheat.getPrefix()
				+ "\n�cYou has been kicked from the server!\n�fKicked for: �c" + checkType.name());
		user.gc();
	}

	public static User getUser(Object o) {
		return users.get((Player) o);
	}

	public static int getPing(Player handle) {
		return ReflectionUtil.getValue(pingField,
				ReflectionUtil.invoke(getHandleMethod, craftPlayerClass.cast(handle)));
	}

	public static HashMap<Player, User> getUsers() {
		return users;
	}

}