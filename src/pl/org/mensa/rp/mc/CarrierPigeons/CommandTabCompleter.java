package pl.org.mensa.rp.mc.CarrierPigeons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandTabCompleter implements TabCompleter {
	
	private static final List<String> args1 = new ArrayList<String>(2);
	private static final List<String> args2 = new ArrayList<String>(2);
	static {
		args1.add("help");
		args1.add("send");
		args2.add("info");
		args2.add("reload");
	}

	@Override
	public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String alias, @Nonnull String[] args) {
		List<String> tabs = new ArrayList<String>();
		
		for (int i=0; i<args.length; ++i) {
			args[i] = args[i].toLowerCase();
		}
		
		switch (args.length) {
			case 1: {
				tabs.addAll(args1.stream().filter(arg -> arg.startsWith(args[0])).collect(Collectors.toList()));
				if (sender.hasPermission("carrierpigeons.admin")) {
					tabs.addAll(args2.stream().filter(arg -> arg.startsWith(args[0])).collect(Collectors.toList()));
				}
				
				if (tabs.isEmpty()) {
					tabs.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> p.getDisplayName().toLowerCase().startsWith(args[0])).map(p -> p.getDisplayName()).collect(Collectors.toList()));
				}
			} break;
			case 2: {
				if (args[0].equalsIgnoreCase("send")) {
					tabs.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> p.getDisplayName().toLowerCase().startsWith(args[1])).map(p -> p.getDisplayName()).collect(Collectors.toList()));
				}
			} break;
			default: {}
		}
		
		return tabs;
	}
}
