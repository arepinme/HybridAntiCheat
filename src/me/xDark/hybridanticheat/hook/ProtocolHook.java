package me.xDark.hybridanticheat.hook;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerAction;

import me.xDark.hybridanticheat.AntiCheatSettings.CheckType;
import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.events.ValidateEvent;
import me.xDark.hybridanticheat.utils.MathUtil;

public class ProtocolHook {

	public static final HashMap<Player, Long> channelRegisterMap = new HashMap<>();

	public static final HashMap<Player, Location> safetyLocations = new HashMap<>();

	public static final HashMap<Player, AtomicInteger> teleportAttempts = new HashMap<>();

	public static void hook() {
		try {
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Teleport))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.POSITION }) {
					@Override
					public void onPacketReceiving(PacketEvent event) {
						if (event.getPlayer().hasPermission("hac.bypass.teleport"))
							return;
						Player p = event.getPlayer();
						if (p.getAllowFlight() && p.isFlying())
							return;
						User user = HybridAPI.getUser(p);
						if (user == null)
							return;
						if (user.initTime() <= 5000L)
							return;
						PacketContainer container = event.getPacket();
						double x = container.getDoubles().read(0);
						double y = container.getDoubles().read(1);
						double z = container.getDoubles().read(2);
						double xDiff = MathUtil.diff(x, p.getLocation().getX());
						double yDiff = MathUtil.diff(y, p.getLocation().getY());
						double zDiff = MathUtil.diff(z, p.getLocation().getZ());
						if (xDiff > 6D || yDiff > 4D || zDiff > 6D) {
							if (teleportAttempts.get(p).incrementAndGet() == 10) {
								p.teleport(safetyLocations.get(p));
								Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.Teleport));
							}
						} else {
							teleportAttempts.get(p).set(0);
							safetyLocations.put(p, p.getLocation());
						}
					}
				});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Exploits))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.CUSTOM_PAYLOAD }) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Player p = e.getPlayer();
						if (p == null)
							return;
						if (p.hasPermission("hac.bypass.books"))
							return;
						User user = HybridAPI.getUser(p);
						if (user.initTime() <= 3000L)
							return;
						PacketContainer container = e.getPacket();
						String channelName = container.getStrings().read(0).trim();
						if (channelName.equalsIgnoreCase("MC|BEdit") || channelName.equalsIgnoreCase("MC|BSign")) {
							if (!channelRegisterMap.containsKey(p))
								channelRegisterMap.put(p, System.currentTimeMillis());
							else {
								if ((System.currentTimeMillis() - channelRegisterMap.get(p)) <= 50L) {
									e.setCancelled(true);
									HybridAPI.performActions(user, CheckType.Exploits);
								} else
									channelRegisterMap.put(p, System.currentTimeMillis());
							}
						}
					}
				});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Exploits))
				ProtocolLibrary.getProtocolManager()
						.addPacketListener(new PacketAdapter(HybridAntiCheat.instance(), ListenerPriority.LOWEST,
								new PacketType[] { PacketType.Play.Client.POSITION,
										PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.HELD_ITEM_SLOT,
										PacketType.Play.Client.USE_ITEM }) {
							@Override
							public void onPacketReceiving(PacketEvent e) {
								Player p = e.getPlayer();
								if (p == null)
									return;
								if (p.hasPermission("hac.bypass.flood"))
									return;
								User user = HybridAPI.getUser(p);
								if (user == null)
									return;
								user.incrementSentPackets();
								if (user.isFlooding())
									HybridAPI.performActions(HybridAPI.getUser(p), CheckType.Exploits);
							}
						});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.InvalidAction))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.BLOCK_DIG }) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Player p = e.getPlayer();
						if (p == null)
							return;
						if (p.hasPermission("hac.bypass.block"))
							return;
						User user = HybridAPI.getUser(p);
						if (user == null)
							return;
						if (HybridAPI.getBlockInfront(p, 10).getType() == Material.AIR)
							return;
						PacketContainer container = e.getPacket();
						BlockPosition blockPos = container.getBlockPositionModifier().read(0);
						Location blockLocation = new Location(p.getWorld(), blockPos.getX(), blockPos.getY(),
								blockPos.getZ());
						if ((p.getLocation()
								.distance(blockLocation) > (p.getGameMode() == GameMode.CREATIVE ? 7D : 5.5D))
								|| (HybridAPI.isInvalidMaterial(blockLocation.getBlock().getType()))) {
							e.setCancelled(true);
							Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.InvalidAction));
						}
					}
				});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.NoSlowDown))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.ENTITY_ACTION }) {
					@SuppressWarnings("deprecation")
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Player p = e.getPlayer();
						if (p == null)
							return;
						if (p.hasPermission("hac.bypass.block"))
							return;
						User user = HybridAPI.getUser(p);
						if (user == null)
							return;
						PacketContainer container = e.getPacket();
						PlayerAction action = container.getPlayerActions().read(0);
						if (user.isFloodingSneak() && (action == PlayerAction.STOP_SNEAKING) && p.isOnGround()) {
							e.setCancelled(true);
							Bukkit.getPluginManager().callEvent(new ValidateEvent(user, CheckType.NoSlowDown));
							user.resetSneak();
						}
					}
				});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.InvMove))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.ENTITY_ACTION }) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Player p = e.getPlayer();
						if (p == null)
							return;
						if (p.hasPermission("hac.bypass.invmove"))
							return;
						User user = HybridAPI.getUser(p);
						if (user == null)
							return;
						if (e.getPacket().getPlayerActions().read(0) == PlayerAction.OPEN_INVENTORY)
							user.setInventoryOpen(true);
					}
				});
			if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.Freecam))
				ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(HybridAntiCheat.instance(),
						ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.POSITION }) {
					@Override
					public void onPacketReceiving(PacketEvent e) {
						Player p = e.getPlayer();
						if (p == null)
							return;
						if (p.hasPermission("hac.bypass.freecam"))
							return;
						User user = HybridAPI.getUser(p);
						if (user == null)
							return;
						user.updateLastUpdatePacket();
					}
				});
			// if (HybridAntiCheat.instance().getSettings().isEnabled(CheckType.KillAura))
			// ProtocolLibrary.getProtocolManager().addPacketListener(new
			// PacketAdapter(HybridAntiCheat.instance(),
			// ListenerPriority.LOWEST, new PacketType[] { PacketType.Play.Client.USE_ENTITY
			// }) {
			// @Override
			// public void onPacketReceiving(PacketEvent e) {
			// Player p = e.getPlayer();
			// if (p == null)
			// return;
			// if (p.hasPermission("hac.bypass.killaura"))
			// return;
			// User user = HybridAPI.getUser(p);
			// if (user == null)
			// return;
			// FakeBot bot = user.getBot();
			// if (bot.getId() == e.getPacket().getIntegers().read(0))
			// bot.onAttack();
			// }
			// });

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
	}

}
