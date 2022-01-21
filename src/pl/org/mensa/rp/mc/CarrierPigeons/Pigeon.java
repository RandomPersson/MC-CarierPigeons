package pl.org.mensa.rp.mc.CarrierPigeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Pigeon implements Runnable {
	int id;
	Player sender;
	Player target;
	String message;
	Location pigeonLocation;
	
	public Pigeon(Player sender, Player target, String message) {
		this.sender = sender;
		this.target = target;
		this.message = message;
		pigeonLocation = sender.getLocation();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		if (target == null || !target.isOnline()) {
			returnToSender();
			return;
		}
		
		if (pigeonLocation.getWorld().getName().equals(target.getWorld().getName())) {
			pigeonLocation.setWorld(target.getWorld());
		}
		
		if (pigeonLocation.distance(target.getLocation()) < CarrierPigeonsPlugin.getInstance().getInstantDistance()) {
			arrive();
			return;
		}
		
		Vector step = target.getLocation().toVector().subtract(pigeonLocation.toVector()).normalize().multiply(CarrierPigeonsPlugin.getInstance().getBirbSpeed());
		if (step.length() >= pigeonLocation.distance(target.getLocation())) {
			arrive();
			return;
		}
		
		pigeonLocation.add(step);
	}
	
	public void arrive() {
		Utils.sendMessage(target, "&bA carrier pigeon from &o" + sender.getDisplayName() + "&b lands on your shoulder:");
		Utils.sendMessage(target, "&7" + message.replaceAll("&.", ""));
		Bukkit.getScheduler().cancelTask(id);
	}
	
	public void returnToSender() {
		CarrierPigeonsPlugin plugin = CarrierPigeonsPlugin.getInstance();
		
		if (sender != null && sender.isOnline()) {
			Utils.sendMessage(sender, "&bNot able to find &o" + target.getDisplayName() + "&b, the pigeon returned.");
			if (sender.getInventory().firstEmpty() == -1 && sender.getInventory().contains(plugin.getItemUsed())) {
				sender.getLocation().getWorld().dropItem(sender.getLocation(), new ItemStack(plugin.getItemUsed(), 1));
			}
			else {
				sender.getInventory().addItem(new ItemStack(plugin.getItemUsed(), 1));
			}
		}
		Bukkit.getScheduler().cancelTask(id);
	}
}
