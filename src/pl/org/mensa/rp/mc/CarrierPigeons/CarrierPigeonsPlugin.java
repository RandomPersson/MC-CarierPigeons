package pl.org.mensa.rp.mc.CarrierPigeons;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CarrierPigeonsPlugin extends JavaPlugin {
	public static final String prefix = "&3[&bCarrierPigeons&3]&e";
	
	public static boolean enabled = true;
	
	private Material item_used = Material.PAPER;
	private long birb_speed = 20;
	private int instant_distance = 50;
	private long royal_pigeon_delay = 5;
	private String bird_travel_method = "SIMPLE";
	
	private static CarrierPigeonsPlugin instance;
	
	public CarrierPigeonsPlugin() {
		CarrierPigeonsPlugin.instance = this;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdesc = getDescription();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		reloadConfigs(false);
		
		this.getCommand("carrierpigeons").setExecutor(new CommandHandler(this));
		this.getCommand("carrierpigeons").setTabCompleter(new CommandTabCompleter());
		
		Utils.log("&a" + pdesc.getFullName() + " enabled");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdesc = getDescription();
		
		saveConfig();
		
		Utils.log("&c" + pdesc.getFullName() + " disabled");
	}
	
	public static CarrierPigeonsPlugin getInstance() {
		return instance;
	}
	public boolean reloadConfigs() {
		return reloadConfigs(true);
	}
	private boolean reloadConfigs(boolean doLog) {
		super.reloadConfig();
		birb_speed = getConfig().getLong("birb_speed");
		item_used = Material.matchMaterial(getConfig().getString("item_used"));
		instant_distance = getConfig().getInt("instant_distance");
		royal_pigeon_delay = getConfig().getLong("royal_pigeon_delay");
		bird_travel_method = getConfig().getString("bird_travel_method");
		
		if (birb_speed <= 0) {
			birb_speed = getConfig().getLong("bird_speed");
		}
		
		if (!checkConfig()) {
			enabled = false;
			return false;
		}
		enabled = true;
		
		if (doLog) {
			Utils.log(Level.INFO, "&aConfig reloaded");
		}
		
		return true;
	}
	private boolean checkConfig() {
		boolean fine  = true;
		
		if (birb_speed <= 0) {
			Utils.log(Level.SEVERE, "&cConfig error (bird_speed) - has to be positive");
			fine = false;
		}
		if (item_used == null) {
			Utils.log(Level.SEVERE, "&cConfig error (item_used) - material not found");
			fine = false;
		}
		if (instant_distance <= 0) {
			Utils.log(Level.SEVERE, "&cConfig error (instant_distance) - has to be positive");
			fine = false;
		}
		if (royal_pigeon_delay >= birb_speed) {
			Utils.log(Level.SEVERE, "&cConfig error (royal_pigeon_delay) - has to be equal or bigger than bird_speed");
			fine = false;
		}
		if (!bird_travel_method.equalsIgnoreCase("SIMULATED") && !bird_travel_method.equalsIgnoreCase("SIMPLE")) {
			Utils.log(Level.SEVERE, "&cConfig error (bird_travel_method) - has to be one of: SIMULATED, SIMPLE");
			fine = false;
		}
		
		return fine;
	}
	
	public Material getItemUsed() {
		return item_used;
	}
	public long getBirbSpeed() {
		return birb_speed;
	}
	public int getInstantDistance() {
		return instant_distance;
	}
	public long getRoyalPigeonDelay() {
		return royal_pigeon_delay;
	}
	public String getTravelMethod() {
		return bird_travel_method;
	}
}
