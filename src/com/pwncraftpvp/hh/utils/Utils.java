package com.pwncraftpvp.hh.utils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.pwncraftpvp.hh.core.Head;
import com.pwncraftpvp.hh.core.Main;
import com.pwncraftpvp.hh.core.MobDrop;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

@SuppressWarnings("deprecation")
public class Utils {
	
	static Main main = Main.getInstance();
	static Random rand = new Random();
	static String yellow = ChatColor.YELLOW + "";
	static String gray = ChatColor.GRAY + "";
	
	static WorldGuardPlugin wgplugin = null;
	
	private static File schematic = new File(main.getDataFolder(), "cannon.schematic");
	
	/**
	 * Set up the plugin's economy for use
	 */
    public static void setupEconomy(){
    	if(main.getServer().getPluginManager().getPlugin("Vault") != null){
        	RegisteredServiceProvider<Economy> rsp = main.getServer().getServicesManager().getRegistration(Economy.class);
        	if(rsp != null){
        		main.econ = rsp.getProvider();
        	}
    	}
    }
    
    /**
     * Set up the mob drops
     */
    public static final void setupDrops(){
    	boolean error = false;
    	ConfigurationSection sect = main.getConfig().getConfigurationSection("drops");
    	if(sect != null){
        	for(String e : sect.getKeys(false)){
        		try{
            		List<String> drops = main.getConfig().getStringList("drops." + e);
        			EntityType entity = EntityType.valueOf(e.toUpperCase());
        			List<MobDrop> mobdrops = new ArrayList<MobDrop>();
        			if(main.drops.containsKey(entity) == true){
        				mobdrops = main.drops.get(entity);
        				main.drops.remove(entity);
        			}
            		for(String d : drops){
            			if(d.contains("money") == false){
                			String[] split = d.split(" ");
                			String matString = null;
                			String amtString = null;
                			String enchantString = null;
                			String chnceString = null;
                			for(int x = 0; x <= (split.length - 1); x++){
                				String s = split[x];
                				if(s.contains("item:") == true){
                					matString = s.replace("item:", "");
                				}else if(s.contains("amount:") == true){
                					amtString = s.replace("amount:", "");
                				}else if(s.contains("enchantments:") == true){
                					enchantString = s.replace("enchantments:", "");
                				}else if(s.contains("chance:") == true){
                					chnceString = s.replace("chance:", "");
                				}
                			}
                			String dataString = null;
                			String[] split4 = matString.split(":");
                			Material material = null;
                			if(split4.length > 1){
                				matString = split4[0];
                				dataString = split4[1];
                				material = Material.getMaterial(Integer.parseInt(matString));
                			}else{
                				material = Material.getMaterial(Integer.parseInt(matString));
                			}
                			int amount = Integer.parseInt(amtString);
                			ItemStack item = new ItemStack(material, amount);
                			if(dataString != null){
                				item = new ItemStack(material, amount, Byte.parseByte(dataString));
                			}
                			if(enchantString != null){
                    			String[] split2 = enchantString.split(",");
                    			for(int x = 0; x <= (split2.length - 1); x++){
                    				String[] split3 = split2[x].split(":");
                    				String enchant = split3[0];
                    				Enchantment enchantment = Utils.getFromString(enchant);
                    				int level = Integer.parseInt(split3[1]);
                    				item.addEnchantment(enchantment, level);
                    			}
                			}
                			int chance = Integer.parseInt(chnceString.replace("%", ""));
                			mobdrops.add(new MobDrop(item, 0, chance));
            			}else{
                			String[] split = d.split(" ");
                			String moneyString = null;
                			String chnceString = null;
                			for(int x = 0; x <= (split.length - 1); x++){
                				String s = split[x];
                				if(s.contains("money:") == true){
                					moneyString = s.replace("money:", "");
                				}else if(s.contains("chance:") == true){
                					chnceString = s.replace("chance:", "");
                				}
                			}
                			int money = Integer.parseInt(moneyString);
                			int chance = Integer.parseInt(chnceString.replace("%", ""));
                			mobdrops.add(new MobDrop(null, money, chance));
            			}
            		}
            		main.drops.put(entity, mobdrops);
        		}catch (Exception ex){
        			error = true;
    				ex.printStackTrace();
    				main.getLogger().info("The above error occured while setting up \"" + e + "\" mob drops. This is likely due to an improper config.");
    			}
        	}
        	if(error == false){
            	main.getLogger().info("Successfully set up mob drops.");
        	}
    	}
    }

    /**
     * Get the WorldGuardPlugin
     * @return - The WorldGuardPlugin
     */
    public static WorldGuardPlugin getWorldGuard() {
    	if(wgplugin == null){
    		Plugin plugin = main.getServer().getPluginManager().getPlugin("WorldGuard");

    		if(plugin == null || !(plugin instanceof WorldGuardPlugin)){
    			return null;
    		}

    		wgplugin = (WorldGuardPlugin) plugin;
    	}
    	return wgplugin;
    }

    /**
     * Check if a player can build at the location	
     * @param player - The player to perform checks on
     * @return - True or false depending on if the player can build at the location or not
     */
    public static boolean canBreakHere(Player player, Location loc){
    	return getWorldGuard().canBuild(player, loc);
    }

    /**
     * Check if a booster is enabled
     * @return True if booster is enabled, false if disabled
     */
    public static final boolean isBoosterEnabled(){
    	return main.getConfig().getBoolean("booster.enabled");
    }

    /**
     * Enable a booster
     * @param time - The time, in milliseconds, for the booster to last
     */
    public static final void enableBooster(long time, double multiplier){
    	main.getConfig().set("booster.enabled", true);
    	main.getConfig().set("booster.multiplier", multiplier);
    	main.getConfig().set("booster.time", System.currentTimeMillis() + time);
    	main.saveConfig();
    }
    
    /**
     * Disable the booster
     */
    public static final void disableBooster(){
    	main.getConfig().set("booster.enabled", false);
    	main.getConfig().set("booster.multiplier", null);
    	main.getConfig().set("booster.time", null);
    	main.saveConfig();
    }
    
    /**
     * Get how much money earned from selling heads should be multiplied by
     * @return The multiplier of money earned from selling heads
     */
    public static final double getBoosterMultiplier(){
    	return main.getConfig().getDouble("booster.multiplier");
    }
    
    /**
     * Get the time, in milliseconds, that the booster should be automatically disabled
     * @return The time, in milliseconds, that the booster should be automatically disabled
     */
    public static final long getBoosterEndTime(){
    	return main.getConfig().getLong("booster.time");
    }
    
    /**
     * Check if the booster can be automatically disabled
     * @return True if it can, false if it cannot
     */
    public static final boolean canAutoDisableBooster(){
    	if((System.currentTimeMillis() - getBoosterEndTime()) > 0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * Get the percent in which the spawner should be dropped if blown up by TNT
     * @return The percent in which the spawner should be dropped if blown up by TNT
     */
    public static final double getSpawnerTNTPercentage(){
    	return main.getConfig().getDouble("silkspawner.tntpercent");
    }
    
    /**
     * Get the percentage to multiply a player's balance by for their head's worth
     * @return The percentage to multiply by
     */
    public static final double getPlayerHeadPercentage(){
    	return (main.getConfig().getDouble("heads.player.percent") * 0.01);
    }
    
    /**
     * Get the price of a level
     * @param level - The level to get the price of
     * @return The price of the level
     */
    public static final double getLevelPrice(int level){
    	return main.getConfig().getDouble("levels." + level + ".buyPrice");
    }
    
    /**
     * Get the required xp of a level
     * @param level - The level to get the required xp of
     * @return The required xp of the level
     */
    public static final double getLevelRequiredXP(int level){
    	return main.getConfig().getDouble("levels." + level + ".xpRequired");
    }
	
	/**
	 * Get a player's head item
	 * @param player - The player to get the head item of
	 * @return The player's head item
	 */
	public static final ItemStack getPlayerHeadItem(String player){
		Head head = Head.PLAYER;
		double price = head.getPlayerSellPrice(player);
		if(price >= 1000){
			ItemStack item = Utils.renameItem(new ItemStack(Material.SKULL_ITEM, 1, head.getData()), ChatColor.AQUA + player, ChatColor.GRAY + "Player Head", 
					yellow + "Sell Price: " + gray + "$" + round(price, 2));
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(player);
			item.setItemMeta(meta);
			return item;
		}else{
			ItemStack item = Utils.renameItem(new ItemStack(Material.SKULL_ITEM, 1, head.getData()), ChatColor.AQUA + player, ChatColor.GRAY + "Player Head", 
					yellow + "Sell Price: " + gray + ChatColor.ITALIC + "Worthless");
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(player);
			item.setItemMeta(meta);
			return item;
		}
	}
	
	/**
	 * Get the price for renaming items
	 * @return The price to rename items
	 */
    public static final double getRenamePrice(){
    	return main.getConfig().getDouble("rename.price");
    }
    
    /**
     * Get the cooldown on repairing items
     * @return The cooldown, in seconds, on repairing items
     */
    public static final int getRepairCooldown(){
    	return main.getConfig().getInt("fixall.cooldown");
    }
    
    /**
     * Get the price for building a cannon
     * @return The price for building a cannon
     */
    public static final double getCannonPrice(){
    	return main.getConfig().getDouble("cannonpaste.price");
    }
    
    /**
     * Get the cooldown for building a cannon
     * @return The cooldown for building a cannon
     */
    public static final int getCannonCooldown(){
    	return main.getConfig().getInt("cannonpaste.cooldown");
    }
    
    /**
     * Get the total regions
     * @return The total regions
     */
    public static final int getTotalRegions(){
    	return main.getConfig().getInt("regions.total");
    }
    
    /**
     * Add a region
     * @param pos1 - The first position
     * @param pos2 - The second position
     * @param level - The required level
     */
    public static final void addRegion(Location pos1, Location pos2, int level){
    	int total = Utils.getTotalRegions() + 1;
    	main.getConfig().set("regions.total", total);
    	main.getConfig().set("regions." + total + ".pos1.x", pos1.getX());
    	main.getConfig().set("regions." + total + ".pos1.z", pos1.getZ());
    	main.getConfig().set("regions." + total + ".pos2.x", pos2.getX());
    	main.getConfig().set("regions." + total + ".pos2.z", pos2.getZ());
    	main.getConfig().set("regions." + total + ".world", pos1.getWorld().getName());
    	main.getConfig().set("regions." + total + ".level", level);
    	main.saveConfig();
    }
    
	/**
	 * Get the enchantment from a string
	 * @param enchant - The string enchantment
	 * @return The enchantment enum from the string
	 */
	public static final Enchantment getFromString(String enchant){
		Enchantment enchantment = null;
		if(enchant.equalsIgnoreCase("protection") == true){
			enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
		}else if(enchant.equalsIgnoreCase("knockback") == true){
			enchantment = Enchantment.KNOCKBACK;
		}else if(enchant.equalsIgnoreCase("punch") == true){
			enchantment = Enchantment.ARROW_KNOCKBACK;
		}else if(enchant.equalsIgnoreCase("fireaspect") == true){
			enchantment = Enchantment.FIRE_ASPECT;
		}else if(enchant.equalsIgnoreCase("sharpness") == true){
			enchantment = Enchantment.DAMAGE_ALL;
		}else if(enchant.equalsIgnoreCase("power") == true){
			enchantment = Enchantment.ARROW_DAMAGE;
		}else if(enchant.equalsIgnoreCase("unbreaking") == true){
			enchantment = Enchantment.DURABILITY;
		}else if(enchant.equalsIgnoreCase("smite") == true){
			enchantment = Enchantment.DAMAGE_UNDEAD;
		}else if(enchant.equalsIgnoreCase("looting") == true){
			enchantment = Enchantment.LOOT_BONUS_MOBS;
		}else if(enchant.equalsIgnoreCase("aquaaffinity") == true){
			enchantment = Enchantment.OXYGEN;
		}else if(enchant.equalsIgnoreCase("blastprotection") == true){
			enchantment = Enchantment.PROTECTION_EXPLOSIONS;
		}else if(enchant.equalsIgnoreCase("projectileprotection") == true){
			enchantment = Enchantment.PROTECTION_PROJECTILE;
		}else if(enchant.equalsIgnoreCase("fireprotection") == true){
			enchantment = Enchantment.PROTECTION_FIRE;
		}else if(enchant.equalsIgnoreCase("fortune") == true){
			enchantment = Enchantment.LOOT_BONUS_BLOCKS;
		}else if(enchant.equalsIgnoreCase("infinity") == true || enchant.equalsIgnoreCase("infinite") == true){
			enchantment = Enchantment.ARROW_INFINITE;
		}else if(enchant.equalsIgnoreCase("flame") == true){
			enchantment = Enchantment.ARROW_FIRE;
		}
		return enchantment;
	}
    
	/**
	 * Check if a player is online
	 * @param player - The name of the player
	 * @return True or false depending on if the player is online or not
	 */
	public static final boolean isOnline(String player){
		if(Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).isOnline() == true){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Check if an item is a tool
	 * @param item - The item to check
	 * @return True if it is a tool, false if it is not
	 */
	public static final boolean isTool(ItemStack item){
		boolean tool = false;
		String name = item.getType().toString().toLowerCase();
		if(name.contains("pick") || name.contains("sword") || name.contains("axe") || name.contains("spade") || name.contains("helmet") || name.contains("chestplate") || name.contains("leggings")
				|| name.contains("boots") || name.contains("bow") || name.contains("shear")){
			tool = true;
		}
		return tool;
	}
	
    /**
	 * Launch a firework at the specified location with the given speed
	 * @param loc - The location to launch the firework at
	 * @param speed - The speed at which to launch the firework
	 */
	public static final Firework launchFirework(Location loc){
		Firework fw = (Firework) loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		meta.addEffect(FireworkEffect.builder().withColor(Color.LIME).withColor(Color.BLUE).flicker(true).with(Type.BALL_LARGE).build());
		fw.setFireworkMeta(meta);
		fw.setVelocity(new Vector(fw.getVelocity().getX(), 0.5, fw.getVelocity().getZ()));
		return fw;
	}
	
	/**
	 * Paste a cannon at the location
	 * @param loc - The location to paste the cannon at
	 * @throws IOException
	 * @throws MaxChangedBlocksException
	 * @throws com.sk89q.worldedit.world.DataException
	 */
	public static boolean pasteCannon(Player player) throws DataException, IOException, MaxChangedBlocksException{
		Location loc = player.getLocation();
		boolean valid = true;
		EditSession es = new EditSession(new BukkitWorld(loc.getWorld()), -1);
        es.setFastMode(true);
        CuboidClipboard cc = SchematicFormat.getFormat(schematic).load(schematic);
	    if(Utils.isLocationValid(player, loc, cc.getWidth(), cc.getHeight(), cc.getLength()) == true){
	    	loc.add(cc.getWidth(), 0, cc.getLength() * -1);
		    cc.paste(es, new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ()), false);
		    player.getLocation().getChunk().unload(true, false);
		    player.getLocation().getChunk().load();
	    }else{
	    	valid = false;
	    }
	    return valid;
	}
	
	/**
	 * Check if the location is valid for the clipboard
	 * @param loc - The location to check
	 * @param cc - The clipboard to check
	 * @return True if the location is valid, false if not
	 */
	public static boolean isLocationValid(Player player, Location loc, int width, int height, int length){
		boolean valid = true;
		Location wideloc = loc.add(width, 0, 0);
		if(Utils.isWilderness(wideloc) == false || Utils.canBreakHere(player, wideloc) == false || wideloc.getBlock().getType() != Material.AIR){
			valid = false;
		}
		Location longloc = loc.add(width * -2, 0, length);
		if(Utils.isWilderness(longloc) == false || Utils.canBreakHere(player, longloc) == false || longloc.getBlock().getType() != Material.AIR){
			valid = false;
		}
		return valid;
	}
	
	/**
	 * Check if the location is wilderness
	 * @param loc - The location to check
	 * @return True if the location is wilderness, false if not
	 */
	public static boolean isWilderness(Location loc){
		String name = ChatColor.stripColor(Board.getFactionAt(new FLocation(loc)).getTag());
		if(name.equalsIgnoreCase("Wilderness") == true){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Check if a string is also an integer
	 * @param isIt - The string to check
	 * @return True or false depending on if the string is an integer or not
	 */
	public static final boolean isInteger(String isIt){
		try{
			Integer.parseInt(isIt);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Check if a string is also a double
	 * @param isIt - The string to check
	 * @return True or false depending on if the string is a double or not
	 */
	public static final boolean isDouble(String isIt){
		try{
			Double.parseDouble(isIt);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Get a random number in the range
	 * @param min - The lowest possible number
	 * @param max - The highest possible number
	 * @return A random number between the minimum and maximum values
	 */
	public static final int randInt(int min, int max){
	    return rand.nextInt((max - min) + 1) + min;
	}
    
	/**
	 * Round a number to two decimal points
	 * @param d - The number to round
	 * @param decimalPlaces - The amount of decimal places to round to
	 * @return The rounded number
	 */
	public static final double round(double d, int decimalPlaces){
        String format = "#.";
        for(int x = 1; x <= decimalPlaces; x++){
        	format = format + "#";
        }
        DecimalFormat form = new DecimalFormat(format);
        return Double.valueOf(form.format(d));
    }
    
    /**
	 * Get the percent of two integers
	 * @param n - The first integer
	 * @param v - The second integer
	 * @return The percent out of 100
	 */
	public static final double getPercent(double n, double v){
		return round(((n * 100) / v), 1);
	}
	
	/**
	 * Rename an itemstack
	 * @param item - The itemstack to rename
	 * @param name - The new name of the itemstack
	 * @param lore - The lore for the itemstack
	 * @return The renamed itemstack
	 */
	public static final ItemStack renameItem(ItemStack item, String name, String... lore){
	    ItemMeta meta = (ItemMeta) item.getItemMeta();
    	meta.setDisplayName(name);
    	List<String> desc = new ArrayList<String>();
    	for(int x = 0; x <= (lore.length - 1); x++){
    		desc.add(lore[x]);
    	}
	    meta.setLore(desc);
	    item.setItemMeta(meta);
	    return item;
	}
	
	/**
	 * Rename an itemstack
	 * @param item - The itemstack to rename
	 * @param name - The new name of the itemstack
	 * @return The renamed itemstack
	 */
	public static final ItemStack renameItem(ItemStack item, String name){
	    ItemMeta meta = (ItemMeta) item.getItemMeta();
    	meta.setDisplayName(name);
	    item.setItemMeta(meta);
	    return item;
	}
}
