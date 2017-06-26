package me.xDark.hybridanticheat.api;

public class Report {

	private final String sender, target, reason;
	
	private final int id;

	public Report(int id, String sender, String target, String reason) {
		this.id = id;
		this.sender = sender;
		this.target = target;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public String getSender() {
		return sender;
	}

	public String getTarget() {
		return target;
	}

	public int getId() {
		return id;
	}

}
