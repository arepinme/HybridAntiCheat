package me.xDark.hybridanticheat.bot;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;

public class FakeBot {

	private final WrapperPlayServerNamedEntitySpawn entitySpawnPacket = new WrapperPlayServerNamedEntitySpawn();

	private final WrapperPlayServerEntityDestroy entityDestroyPacket = new WrapperPlayServerEntityDestroy();

	private final Player packetReceiver, source;

	public FakeBot(Player receiver, Player source) {
		this.packetReceiver = receiver;
		this.source = source;
	}

	public Player getPacketReceiver() {
		return packetReceiver;
	}

	public Player getSource() {
		return source;
	}

	public void spawn() {
		Location loc = packetReceiver.getLocation();
		entitySpawnPacket.setPlayerUUID(source.getUniqueId());
		entitySpawnPacket.setEntityID(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
		entitySpawnPacket.receivePacket(packetReceiver);
	}

}
