package me.xDark.hybridanticheat.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.xDark.hybridanticheat.HybridAntiCheat;
import me.xDark.hybridanticheat.api.HybridAPI;
import me.xDark.hybridanticheat.api.User;
import me.xDark.hybridanticheat.utils.ParseUtil;

public class ReportCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String str, String[] args) {
		if (s.equals(Bukkit.getConsoleSender()))
			return true;
		if (!HybridAntiCheat.checkPermission(s, "commands.report.use")) {
			s.sendMessage(HybridAntiCheat.getPrefix() + " �c� ��� ��� ���� �� ���������� ������ �������.");
			return true;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("list")) {
				if (!HybridAntiCheat.checkPermission(s, "commands.report.list")) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c� ��� ��� ���� �� ���������� ������ �������.");
					return true;
				}
				if (args.length == 1) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c������� ��������.");
					return true;
				}
				if (!ParseUtil.parseInt(args[1])) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c�������� ������ �� �����.");
					return true;
				}
				HybridAPI.showReports(s, Integer.parseInt(args[1]));
				return true;
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (!HybridAntiCheat.checkPermission(s, "commands.report.remove")) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c� ��� ��� ���� �� ���������� ������ �������.");
					return true;
				}
				if (args.length == 1) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c������� ����� ������.");
					return true;
				}
				if (!ParseUtil.parseInt(args[1])) {
					s.sendMessage(HybridAntiCheat.getPrefix() + " �c����� ������ ����� �� �����.");
					return true;
				}
				int reportId = Integer.parseInt(args[1]);
				s.sendMessage(HybridAntiCheat.getPrefix()
						+ (HybridAPI.removeReport(reportId) ? "�a������ ���� �������" : "�c������ �� �������"));
				return true;
			}
		}
		if (args.length < 2) {
			s.sendMessage(HybridAntiCheat.getPrefix() + " �c������� ������ � ������� ������.");
			return true;
		}
		User user = HybridAPI.getUser(s);
		if (!user.hasReportTimePassed()) {
			s.sendMessage(HybridAntiCheat.getPrefix() + " �c�� ������� ����� ����������� ������� /report.");
			return true;
		}
		String player = args[0];
		if (player.equalsIgnoreCase(s.getName())) {
			s.sendMessage(HybridAntiCheat.getPrefix() + " �c�� �� ������ ��������� ������ �� ������ ����.");
			return true;
		}
		if (HybridAPI.hasReportImmunity(player)) {
			s.sendMessage(HybridAntiCheat.getPrefix() + " �c�� �� ������ ������������ �� ������� ������.");
			return true;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			builder.append(args[i]).append(" ");
		String reason = builder.toString().trim();
		int id = HybridAPI.performReport(s.getName(), player, reason);
		user.getReportTimer().reset();
		s.sendMessage(HybridAntiCheat.getPrefix() + "�a������ ���� ����������. �� �����: " + id);
		HybridAntiCheat.instance().notify("����� " + s.getName() + " �������� ������. �� �����: " + id,
				"hac.notify.staff");
		return true;
	}

}
