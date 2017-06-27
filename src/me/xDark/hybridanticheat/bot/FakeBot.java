package me.xDark.hybridanticheat.bot;

import java.util.concurrent.ThreadLocalRandom;

import javax.sound.midi.Receiver;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;

import me.xDark.hybridanticheat.utils.Timer;

public class FakeBot {

	private final Player packetReceiver, source;

	private boolean spawned, wasAttacked;

	private final Timer ticksExistedTimer = new Timer();

	private final int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) + 150;

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
		WrapperPlayServerNamedEntitySpawn entitySpawn = new WrapperPlayServerNamedEntitySpawn();
		entitySpawn.setEntityID(id);
		entitySpawn.setPosition(packetReceiver.getLocation().toVector().add(new Vector(0, 3, 0)));
		entitySpawn.setPlayerName(source.getName());
		entitySpawn.setPlayerUUID(source.getUniqueId().toString());
		spawned = true;
	}

	public void destory() {
		spawned = false;
	}

	public long getTicksExisted() {
		return ticksExistedTimer.getMSPassed() / 1000L;
	}

	public void onAttack() {
		wasAttacked = true;
	}

	public boolean wasAttacked() {
		return wasAttacked;
	}

	public int getId() {
		return id;
	}

	public boolean isSpawned() {
		return spawned;
	}

}
