package pl.org.mensa.rp.mc.CarrierPigeons;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler implements CommandExecutor {
	private final CarrierPigeonsPlugin plugin;
	public static final String message_plugin_disabled = "&cPlugin is disabled at the moment (config error)";
	public static final String message_no_permission = "&cNo permission";
	
	public CommandHandler(CarrierPigeonsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
		if (args.length == 0) {
			Utils.sendMessage(sender, "&eType &b/" + alias + " help&e to see command list.");
		}
		else {
			switch (args[0]) {
				case "help": case "h": {
					printHelp(sender, alias);
				} break;
				case "info": case "i": {
					if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) {
						printInfo(sender, alias);
					}
					else {
						Utils.sendMessage(sender, message_no_permission);
					}
				} break;
				case "send": case "s": {
					if (!consoleCheck(sender)) return true;
					
					if (CarrierPigeonsPlugin.enabled) {
						birbCommand((Player)sender, args, 2, false);
					}
					else {
						Utils.sendMessage(sender, message_plugin_disabled);
					}
				} break;
				case "reload": case "r": {
					if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) {
						if (plugin.reloadConfigs()) {
							Utils.sendMessage(sender, "&aConfig reloaded");
						}
						else {
							Utils.sendMessage(sender, "&cError in config - check console");
						}
					}
					else {
						Utils.sendMessage(sender, message_no_permission);
					}
				} break;
				case "sendroyal": case "royal": case "broadcast": {
					if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) {
						if (args.length < 2) {
							Utils.sendMessage(sender, "&cYou need to write a message");
							return true;
						}
						royalBirbCommand(sender, Utils.mergeArray(args, 1));
					}
					else {
						Utils.sendMessage(sender, message_no_permission);
					}
				} break;
				case "debug": {
					if (sender.hasPermission("carrierpigeons.admin")) {
						if (args.length < 2) {
							Utils.sendMessage(sender, "&cYou need to write a message");
							return true;
						}
						
						if (CarrierPigeonsPlugin.enabled) {
							birbCommand((Player)sender, args, 2, true);
						}
						else {
							Utils.sendMessage(sender, message_plugin_disabled);
						}
					}
					else {
						Utils.sendMessage(sender, message_no_permission);
					}
				}
				default: {
					if (!consoleCheck(sender)) return true;
					
					if (CarrierPigeonsPlugin.enabled) {
						birbCommand((Player)sender, args, 1, false);
					}
					else {
						Utils.sendMessage(sender, message_plugin_disabled);
					}
				}
			}
		}
		
		return true;
	}
	private boolean consoleCheck(CommandSender sender) {
		if (!(sender instanceof Player)) {
			Utils.sendMessage(sender, "&cCommand is misspellt or player-only");
			return false;
		}
		
		return true;
	}
	
	
	private void printHelp(CommandSender sender, String alias) {
		Utils.sendMessage(sender, "&3==================[&bCarrierPigeons&3]===================");
		Utils.sendMessage(sender, "&e/" + alias + " help - shows command list.");
		if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) Utils.sendMessage(sender, "&e/" + alias + " info - shows plugin info.");
		Utils.sendMessage(sender, "&e/" + alias + " send &b<player> <message>&e - send a pigeon with a written letter.");
		if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) Utils.sendMessage(sender, "&e/" + alias + " royal &b<message>&e - broadbast a message to all players (" + plugin.getRoyalPigeonDelay() + "s delay).");
		Utils.sendMessage(sender, "&e/" + alias + " &b<player> <message>&e - send a pigeon with a written letter.");
		if (sender.hasPermission("carrierpigeons.admin") || !(sender instanceof Player)) Utils.sendMessage(sender, "&e/" + alias + " reload - reloads plugin config.");
	}
	
	private void printInfo(CommandSender sender, String alias) {
		Utils.sendMessage(sender, "&3==================[&bCarrierPigeons&3]===================");
		Utils.sendMessage(sender, "&eVersion: &b" + plugin.getDescription().getVersion());
		Utils.sendMessage(sender, "&eAuthor: &bRandomPersson");
		Utils.sendMessage(sender, "&eCommand aliases: &bcpigeons&e, &bpigeon&e, &bbird&e, &bbirb");
		Utils.sendMessage(sender, "&ePermissions: &bcarrierpigeons.command.carrierpigeons&e, &bcarrierpigeons.admin");
	}
	
	private void birbCommand(final Player player, String[] args, int x, boolean debug) {
		ItemStack itemInHand = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
		if (itemInHand == null || itemInHand.getType() != (plugin.getItemUsed())) {
			Utils.sendMessage(player, "&cYou need to hold &6" + plugin.getItemUsed().name().toLowerCase() + "&c to write your message on");
			return;
		}
		if (args.length < x) {
			Utils.sendMessage(player, "&cYou need to specify a recipient");
			return;
		}
		
		final Player target = Bukkit.getPlayer(args[x-1]);
		if (target == null) {
			Utils.sendMessage(player, "&cThat player does not exist");
			return;
		}
		if (!target.isOnline()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (player != null && player.isOnline()) {
						Utils.sendMessage(player, "&bNot able to find &o" + target.getDisplayName() + "&b, the pigeon returned.");
						if (player.getInventory().firstEmpty() == -1 && player.getInventory().contains(plugin.getItemUsed())) {
							player.getLocation().getWorld().dropItem(player.getLocation(), new ItemStack(plugin.getItemUsed(), 1));
						}
						else {
							player.getInventory().addItem(new ItemStack(plugin.getItemUsed(), 1));
						}
					}
				}
			}, 40L);
			Utils.sendMessage(player, "&cThat player is not online");
			return;
		}
		if (args.length < x+1) {
			Utils.sendMessage(player, "&cYou need to write a message");
			return;
		}
		
		itemInHand.setAmount(itemInHand.getAmount()-1);
		
		sendBirb(player, target, Utils.mergeArray(args, x), debug);
		Utils.sendMessage(player, "&aMessage sent to &o" + target.getDisplayName());
	}
	private void sendBirb(Player sender, Player target, String message, boolean debug) {
		switch (plugin.getTravelMethod().toUpperCase()) {
			case "SIMULATED": {
				Pigeon pigeon = debug ? new DebugPigeon(sender, target, message) : new Pigeon(sender, target, message);
				final int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, pigeon, 0L, 20L);
				pigeon.setId(id);
			} break;
			case "SIMPLE": {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						if (!target.isOnline()) {
							if (sender != null && sender.isOnline()) {
								Utils.sendMessage(sender, "&bNot able to find &o" + target.getDisplayName() + "&b, the pigeon returned.");
								if (sender.getInventory().firstEmpty() == -1 && sender.getInventory().contains(plugin.getItemUsed())) {
									sender.getLocation().getWorld().dropItem(sender.getLocation(), new ItemStack(plugin.getItemUsed(), 1));
								}
								else { // sender NOT online
									sender.getInventory().addItem(new ItemStack(plugin.getItemUsed(), 1));
								}
							}
						}
						else { // target IS online
							Utils.sendMessage(target, "&bA carrier pigeon from &o" + sender.getDisplayName() + "&b lands on your shoulder:");
							Utils.sendMessage(target, "&7" + message.replaceAll("&.", ""));
						}
					}
				}, (long) sender.getLocation().distance(target.getLocation())*plugin.getBirbSpeed()*20L);
			} break;
			default: {}
		}
	}
	
	private void royalBirbCommand(CommandSender sender, String message) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Bukkit.getOnlinePlayers().stream().forEach(p -> {
					Utils.sendMessage(p, "&bA royal carrier pigeon lands on your shoulder:");
					Utils.sendMessage(p, "&6" + message);
				});
			}
		}, plugin.getRoyalPigeonDelay()*20L);
		Utils.sendMessage(sender, "&aThe pigeon flew away carrying your royal decree.");
	}
}
