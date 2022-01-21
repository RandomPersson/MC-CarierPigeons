package pl.org.mensa.rp.mc.CarrierPigeons;

import org.bukkit.entity.Player;

public class DebugPigeon extends Pigeon {
	
	public DebugPigeon(Player sender, Player target, String message) {
		super(sender, target, message);
	}
	
	@Override
	public void run() {
		super.run();
		
		Utils.sendMessage(sender, "&8[&bDebug&8]&e Pigeon location: &b" + this.pigeonLocation.getX() + " " + this.pigeonLocation.getZ());
		Utils.sendMessage(sender, "&8[&bDebug&8]&e Target location: &b" + this.target.getLocation().getX() + " " + this.target.getLocation().getZ());
		Utils.sendMessage(sender, "&8[&bDebug&8]&c Distance: " + this.target.getLocation().distance(pigeonLocation));
	}
	
	@Override
	public void arrive() {
		super.arrive();
		
		Utils.sendMessage(sender, "&8[&bDebug&8]&6 Pigeon arrived!");
		Utils.sendMessage(sender, "&8[&bDebug&8]&e Pigeon location: &b" + this.pigeonLocation.getX() + " " + this.pigeonLocation.getZ());
		Utils.sendMessage(sender, "&8[&bDebug&8]&e Target location: &b" + this.target.getLocation().getX() + " " + this.target.getLocation().getZ());
		Utils.sendMessage(sender, "&8[&bDebug&8]&e Distance: &3" + this.target.getLocation().distance(pigeonLocation));
	}
}
