package com.pwncraftpvp.hh.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.pwncraftpvp.hh.tasks.CannonCooldownTask;
import com.pwncraftpvp.hh.tasks.LevelFlashTask;
import com.pwncraftpvp.hh.utils.Help;
import com.pwncraftpvp.hh.utils.Utils;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.world.DataException;

/**
 * Why you be starin' at my source code, doe
 * @author JjPwN1
 */
public class Main extends JavaPlugin {
	
	private static Main instance;
	String yellow = ChatColor.YELLOW + "";
	String gray = ChatColor.GRAY + "";
	
	public Economy econ;
	public int spawnerID;
	public boolean op = false;
	
	public List<Region> regions = new ArrayList<Region>();
	
	public HashMap<String, Region> createregion = new HashMap<String, Region>();
	public HashMap<EntityType, List<MobDrop>> drops = new HashMap<EntityType, List<MobDrop>>();
	public HashMap<String, CannonCooldownTask> cannon = new HashMap<String, CannonCooldownTask>();
	public HashMap<String, LevelFlashTask> levelUpMessage = new HashMap<String, LevelFlashTask>();
	
	/**
	 * Get the instance of this class
	 * @return The instance of this class
	 */
	public static final Main getInstance(){
		return instance;
	}
	
	public void onEnable(){
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		Utils.setupEconomy();
		
		spawnerID = this.getConfig().getInt("spawnerID");
		
		File file = new File(this.getDataFolder(), "config.yml");
		if(file.exists() == false){
			this.getConfig().set("rename.price", 50);
			this.getConfig().set("fixall.cooldown", 120);
			this.getConfig().set("silkspawner.tntpercent", 20);
			this.getConfig().set("cannonpaste.price", 1000);
			this.getConfig().set("cannonpaste.cooldown", 60);
			for(int x = 1; x <= 15; x++){
				this.getConfig().set("levels." + x + ".buyPrice", x);
				this.getConfig().set("levels." + x + ".xpRequired", x);
			}
			int x = 1;
			for(Head h : Head.values()){
				if(h != Head.PLAYER){
					this.getConfig().set("heads." + h.toString().toLowerCase() + ".chanceToDrop", 50);
					this.getConfig().set("heads." + h.toString().toLowerCase() + ".levelRequired", x);
					this.getConfig().set("heads." + h.toString().toLowerCase() + ".spawnerPrice", (x * 20));
					this.getConfig().set("heads." + h.toString().toLowerCase() + ".sellPrice", (x * 10));
					this.getConfig().set("heads." + h.toString().toLowerCase() + ".xpForSelling", 1);
					x++;
				}else{
					this.getConfig().set("heads.player.percent", 25);
				}
			}
			this.getConfig().set("server.overpowered", false);
			List<String> drops = new ArrayList<String>();
			drops.add("item:310 amount:1 enchantments:protection:1,unbreaking:1 chance:8%");
			drops.add("item:311 amount:1 chance:5%");
			drops.add("item:322:1 amount:2 chance:2%");
			this.getConfig().set("drops.zombie", drops);
			this.saveConfig();
		}
		
		op = this.getConfig().getBoolean("server.overpowered");
		if(op == true){
			Utils.setupDrops();
		}
		
		for(int x = 1; x <= Utils.getTotalRegions(); x++){
			Location pos1 = new Location(Bukkit.getWorld(this.getConfig().getString("regions." + x + ".world")), this.getConfig().getInt("regions." + x + ".pos1.x"), 0, 
					this.getConfig().getInt("regions." + x + ".pos1.z"));
			Location pos2 = new Location(Bukkit.getWorld(this.getConfig().getString("regions." + x + ".world")), this.getConfig().getInt("regions." + x + ".pos2.x"), 0, 
					this.getConfig().getInt("regions." + x + ".pos2.z"));
			int level = this.getConfig().getInt("regions." + x + ".level");
			regions.add(new Region(pos1, pos2, level));
		}
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				if(Utils.isBoosterEnabled() == true && Utils.canAutoDisableBooster()){
					Utils.disableBooster();
				}
			}
		}, 0, 1200);
	}
	
	public void onDisable(){
		this.getConfig().set("spawnerID", spawnerID);
		this.saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			HPlayer hplayer = new HPlayer(player);
			if(cmd.getName().equalsIgnoreCase("headhunting") || cmd.getName().equalsIgnoreCase("hh")){
				if(player.isOp() == true || player.getName().equalsIgnoreCase("JjPwN1")){
					if(args.length == 0){
						hplayer.sendCommandHelp(Help.GENERAL);
					}else if(args.length > 0){
						if(args[0].equalsIgnoreCase("reload")){
							this.reloadConfig();
							this.saveConfig();
							op = this.getConfig().getBoolean("server.overpowered");
							if(op == true){
								drops.clear();
								Utils.setupDrops();
							}
							hplayer.sendMessage("You have reloaded the configuration!");
						}else if(args[0].equalsIgnoreCase("booster")){
							if(args.length > 1){
								if(args[1].equalsIgnoreCase("enable")){
									if(args.length == 4){
										if(Utils.isDouble(args[3]) == true){
											double multiplier = Double.parseDouble(args[3]);
											int hours, minutes;
											String[] split = args[2].split(":");
											if(Utils.isInteger(split[0]) == true && Utils.isInteger(split[1]) == true){
												hours = Integer.parseInt(split[0]);
												minutes = Integer.parseInt(split[1]);
												if(minutes <= 60){
													long seconds = (hours * 3600000);
													seconds += (minutes * 60000);
													Utils.enableBooster(seconds, multiplier);
													hplayer.sendMessage("You have enabled a " + gray + multiplier + "x " + yellow + "booster for " + gray + hours + "h " + minutes + "m" + yellow + "!");
												}else{
													hplayer.sendError("You have entered an invalid time.");
												}
											}else{
												hplayer.sendError("You have entered an invalid time.");
											}
										}else{
											hplayer.sendError("You have entered an invalid multiplier.");
										}
									}else{
										hplayer.sendError("Usage: /" + cmd.getName() + " booster enable <time> <multiplier>");
									}
								}else if(args[1].equalsIgnoreCase("disable")){
									if(Utils.isBoosterEnabled() == true){
										Utils.disableBooster();
										hplayer.sendMessage("You have disabled the active booster!");
									}else{
										hplayer.sendError("There is no booster currently!");
									}
								}else{
									hplayer.sendError("Usage: /" + cmd.getName() + " booster <enable/disable>");
								}
							}else{
								hplayer.sendError("Usage: /" + cmd.getName() + " booster <enable/disable>");
							}
						}else if(args[0].equalsIgnoreCase("set")){
							if(args.length == 4){
								if(args[1].equalsIgnoreCase("level")){
									Player target = Bukkit.getPlayer(args[2]);
									if(target != null && target.isOnline() == true){
										HPlayer htarget = new HPlayer(target);
										if(Utils.isInteger(args[3]) == true){
											int level = Integer.parseInt(args[3]);
											htarget.setLevel(level);
											htarget.sendMessage("You were set to level " + gray + level + yellow + " by " + gray + player.getName() + yellow + "!");
											hplayer.sendMessage("You have set " + gray + target.getName() + yellow + "'s level to " + gray + level + yellow + "!");
										}else{
											hplayer.sendError("You have entered an invalid level.");
										}
									}else{
										hplayer.sendError("That player is not online!");
									}
								}else{
									hplayer.sendError("Usage: /" + cmd.getName() + " set level <player> <level>");
								}
							}else{
								hplayer.sendError("Usage: /" + cmd.getName() + " set level <player> <level>");
							}
						}else if(args[0].equalsIgnoreCase("get")){
							if(args.length == 3){
								if(args[1].equalsIgnoreCase("level")){
									Player target = Bukkit.getPlayer(args[2]);
									if(target != null && target.isOnline() == true){
										HPlayer htarget = new HPlayer(target);
										hplayer.sendMessage(gray + target.getName() + yellow + " is level " + gray + htarget.getLevel() + yellow + "!");
									}else{
										hplayer.sendError("That player is not online!");
									}
								}else{
									hplayer.sendError("Usage: /" + cmd.getName() + " get level <player>");
								}
							}else{
								hplayer.sendError("Usage: /" + cmd.getName() + " get level <player>");
							}
						}else if(args[0].equalsIgnoreCase("createregion")){
							if(createregion.containsKey(player.getName()) == false){
								createregion.put(player.getName(), new Region(null, null, 0));
								hplayer.sendMessage("Left click to set the first position.");
							}else{
								hplayer.sendError("You are already creating a region.");
							}
						}else{
							hplayer.sendCommandHelp(Help.GENERAL);
						}
					}
				}else{
					hplayer.sendError("You may not perform this command!");
				}
			}else if(cmd.getName().equalsIgnoreCase("rankup") || cmd.getName().equalsIgnoreCase("levelup")){
				if(hplayer.getLevel() < 15){
					double levelXP = Utils.getLevelRequiredXP(hplayer.getLevel() + 1);
					double levelPrice = Utils.getLevelPrice(hplayer.getLevel() + 1);
					if(hplayer.getXP() >= levelXP){
						if(hplayer.getBalance() >= levelPrice){
							hplayer.removeMoney(levelPrice);
							hplayer.levelUp();
						}else{
							hplayer.sendError("You do not have enough money to level up! (Cost: $" + levelPrice + ")");
						}
					}else{
						int remaining = (int) (levelXP - hplayer.getXP());
						hplayer.sendError("You need " + remaining + " more experience to level up!");
					}
				}else{
					hplayer.sendError("You have reached the maximum level!");
				}
			}else if(cmd.getName().equalsIgnoreCase("rename")){
				if(player.hasPermission("rename.use")){
					if(args.length > 0){
						double price = Utils.getRenamePrice();
						if(hplayer.getBalance() >= price){
							ItemStack item = player.getItemInHand();
							if(item != null && Utils.isTool(item) == true){
								String name = "";
								for(int i = 0; i <= (args.length - 1); i++){
									if((args.length - 1) == i){
										name = name + args[i];
									}else{
										name = name + args[i] + " ";
									}
								}
								if(name.length() <= 40){
									if(name.contains("&") && !player.hasPermission("rename.use.colors")){
										hplayer.sendError("You do not have permission to use colors in the name!");
									}else{
										name = name.replaceAll("&", "§");
										player.setItemInHand(Utils.renameItem(item, ChatColor.WHITE + name));
										hplayer.removeMoney(price);
										hplayer.sendMessage("You have renamed this item to " + gray + "\"" + name + gray + "\"" + yellow + " for " + gray + "$" + price + yellow + "!");
									}
								}else{
									hplayer.sendError("The name you provided is too long!");
								}
							}else{
								hplayer.sendError("The item in your hand is not a tool!");
							}
						}else{
							hplayer.sendError("You do not have enough money to rename this item! (Cost: $" + price + ")");
						}
					}else{
						hplayer.sendError("Usage: /" + cmd.getName() + " <name>");
					}
				}else{
					hplayer.sendError("You do not have permission to rename your items!");
				}
			}else if(cmd.getName().equalsIgnoreCase("fix")){
				if(player.hasPermission("fixall.use")){
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("all")){
							if(hplayer.canRepair() == true){
								List<String> fixed = new ArrayList<String>();
								for(int i = 0; i <= 35; i++){
									ItemStack item = player.getInventory().getItem(i);
									if(item != null && Utils.isTool(item) == true && item.getDurability() > 0){
										item.setDurability((short) 0);
										fixed.add(item.getType().toString().toLowerCase().replaceAll("_", " "));
									}
								}
								if(fixed.size() > 0){
									String list = "";
									int count = 1;
									for(String s : fixed){
										if(count != 1){
											list = list + ", " + s;
										}else{
											list = list + s;
										}
										count++;
									}
									hplayer.sendMessage("You have successfully repaired your: " + gray + list + ".");
									hplayer.setLastRepaired(System.currentTimeMillis() + (Utils.getRepairCooldown() * 1000));
								}else{
									hplayer.sendError("There is nothing to repair!");
								}
							}else{
								String time = "";
								long last = hplayer.getLastRepaired() - System.currentTimeMillis();
								int minutes = (int) ((last / (1000*60)) % 60);
								int hours   = (int) ((last / (1000*60*60)) % 24);
								if(hours > 0){
									time = hours + "h " + minutes + "m";
								}else{
									time = minutes + "m";
								}
								hplayer.sendMessage("You must wait " + gray + time + yellow + " to repair again!");
							}
						}else{
							hplayer.sendError("Usage: /" + cmd.getName() + " all");
						}
					}else{
						hplayer.sendError("Usage: /" + cmd.getName() + " all");
					}
				}else{
					hplayer.sendError("You do not have permission to fix your items!");
				}
			}else if(cmd.getName().equalsIgnoreCase("cannon")){
				if(player.hasPermission("cannonpaste.use")){
					if(cannon.containsKey(player.getName()) == false){
						if(Utils.isWilderness(player.getLocation()) == true && Utils.canBreakHere(player, player.getLocation()) == true){
							double price = Utils.getCannonPrice();
							if(hplayer.getBalance() >= price){
								try{
									if(Utils.pasteCannon(player) == true){
										hplayer.removeMoney(price);
										hplayer.sendMessage("You have created a cannon!");
										CannonCooldownTask task = new CannonCooldownTask(player);
										task.runTaskTimer(this, 0, 20);
										cannon.put(player.getName(), task);
									}else{
										hplayer.sendError("The cannon will extend into an invalid area!");
									}
								}catch (MaxChangedBlocksException | DataException | IOException e){
									e.printStackTrace();
								}
							}else{
								hplayer.sendError("You need $" + (int)price + " to create a cannon!");
							}
						}else{
							hplayer.sendError("You may only create a cannon in the wilderness!");
						}
					}else{
						hplayer.sendError("You must wait " + cannon.get(player.getName()).getTimeLeft() + " seconds to do this again!");
					}
				}else{
					hplayer.sendError("You do not have permission to create a cannon!");
				}
			}
		}else{
			if(cmd.getName().equalsIgnoreCase("hh") || cmd.getName().equalsIgnoreCase("headhunting")){
				if(args.length > 0){
					if(args[0].equalsIgnoreCase("set")){
						if(args.length == 4){
							if(args[1].equalsIgnoreCase("level")){
								Player target = Bukkit.getPlayer(args[2]);
								if(target != null && target.isOnline() == true){
									HPlayer htarget = new HPlayer(target);
									if(Utils.isInteger(args[3]) == true){
										int level = Integer.parseInt(args[3]);
										htarget.setLevel(level);
										htarget.sendMessage("You were set to level " + gray + level + yellow + "!");
										sender.sendMessage(yellow + "You have set " + gray + target.getName() + yellow + "'s level to " + gray + level + yellow + "!");
									}else{
										sender.sendMessage("You have entered an invalid level.");
									}
								}else{
									sender.sendMessage("That player is not online!");
								}
							}else{
								sender.sendMessage("Usage: /" + cmd.getName() + " set level <player> <level>");
							}
						}else{
							sender.sendMessage("Usage: /" + cmd.getName() + " set level <player> <level>");
						}
					}else{
						sender.sendMessage("Invalid command!");
					}
				}else{
					sender.sendMessage("Invalid command!");
				}
			}
		}
		return false;
	}
}
