package me.xDark.hybridanticheat.hook;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.MathUtil;

public class ProtocolHook {

	public static final HashMap<Player, Long> channelRegisterMap = new HashMap<>();

	public static void hook() {
		try {
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
					ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.POSITION }) {
				@Override
				public void onPacketReceiving(PacketEvent event) {
					if (event.getPlayer().hasPermission("hac.bypass.teleport"))
						return;
					PacketContainer container = event.getPacket();
					double x = container.getDoubles().read(0);
					double y = container.getDoubles().read(1);
					double z = container.getDoubles().read(2);
					Player p = event.getPlayer();
					double xDiff = MathUtil.diff(x, p.getLocation().getX());
					double yDiff = MathUtil.diff(y, p.getLocation().getY());
					double zDiff = MathUtil.diff(z, p.getLocation().getZ());
					if (xDiff > 10D || yDiff > 10D || zDiff > 10D) {
						HybridAntiCheat.callSyncMethod(() -> {
							User user;
							Bukkit.getPluginManager()
									.callEvent(new ValidateEvent(user = HybridAPI.getUser(p), CheckType.Teleport));
							HybridAPI.disconnectUser(user, CheckType.Teleport);
							return (Void) null;
						}

						);
					}
				}
			});
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
					ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.CUSTOM_PAYLOAD }) {
				@Override
				public void onPacketReceiving(PacketEvent e) {
					PacketContainer container = e.getPacket();
					Player p = e.getPlayer();
					if (p.hasPermission("hac.bypass.books"))
						return;
					String channelName = container.getStrings().read(0).trim();
					if (channelName.equalsIgnoreCase("MC|BEdit") || channelName.equalsIgnoreCase("MC|BSign")) {
						if (!channelRegisterMap.containsKey(p))
							channelRegisterMap.put(p, System.currentTimeMillis());
						else {
							if ((System.currentTimeMillis() - channelRegisterMap.get(p)) <= 50L) {
								e.setCancelled(true);
								HybridAntiCheat.callSyncMethod(() -> {
									HybridAPI.disconnectUser(HybridAPI.getUser(p), CheckType.Exploits);
									return (Void) null;
								});
							} else
								channelRegisterMap.put(p, System.currentTimeMillis());
						}
					}
				}
			});
			ProtocolLibrary.getProtocolManager()
					.addPacketListener(new PacketAdapter(HybridAntiCheat.instance(), ListenerPriority.LOWEST,
							new PacketType[] { PacketType.Play.Client.POSITION, PacketType.Play.Client.ARM_ANIMATION,
									PacketType.Play.Client.HELD_ITEM_SLOT }) {
						@Override
						public void onPacketReceiving(PacketEvent e) {
							Player p = e.getPlayer();
							if (p.hasPermission("hac.bypass.flood"))
								return;
							User user = HybridAPI.getUser(p);
							if (user == null)
								return;
							user.incrementSentPackets();
							if (user.isFlooding()) {
								HybridAntiCheat.callSyncMethod(() -> {
									HybridAPI.disconnectUser(HybridAPI.getUser(p), CheckType.Exploits);
									return (Void) null;
								});
							}
						}
					});
		} catch (Exception exc) {
			HybridAntiCheat.instance().getLogger().log(Level.SEVERE,
					"Error hooking in ProtocolLib. Some checks has been disabled.", exc);
		}
	}

	public static void unhook() {
		try {
			ProtocolLibrary.getProtocolManager().removePacketListeners(HybridAntiCheat.instance());
		} catch (Exception exc) {
			HybridAntiCheat.instance().getLogger().log(Level.SEVERE, "Error unhooking from ProtocolLib.", exc);
		}
		channelRegisterMap.clear();
	}

}
