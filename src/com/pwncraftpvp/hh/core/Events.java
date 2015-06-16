package com.pwncraftpvp.hh.core;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.pwncraftpvp.hh.utils.ParticleEffect;
import com.pwncraftpvp.hh.utils.ParticleUtils;
import com.pwncraftpvp.hh.utils.Utils;

public class Events implements Listener {
	
	Main main = Main.getInstance();
	Random rand = new Random();
	String yellow = ChatColor.YELLOW + "";
	String gray = ChatColor.GRAY + "";
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		HPlayer hplayer = new HPlayer(player);
		if(hplayer.getLevel() == 0){
			hplayer.setLevel(1);
			hplayer.setXP(0);
			player.setExp(0);
		}
		if(player.hasPermission("joineffect.bandit") == true){
			main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
				public void run(){
					ParticleUtils.sendToLocation(ParticleEffect.LARGE_EXPLODE, player.getEyeLocation(), 1F, 1F, 1F, 1, 6);
				}
			}, 20);
		}
	}
	
	@EventHandler
	public void playerExpChange(PlayerExpChangeEvent event){
		event.setAmount(0);
	}
	
	@EventHandler
	public void blockExp(BlockExpEvent event){
		event.setExpToDrop(0);
	}
	
	@EventHandler
	public void entityTarget(EntityTargetEvent event){
		if(event.getEntity() instanceof ExperienceOrb){
			event.getEntity().remove();
		}
	}
	
	@EventHandler
	public void enchantItem(EnchantItemEvent event){
		event.setCancelled(true);
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if(block.getType() == Material.MOB_SPAWNER && Utils.canBreakHere(player, block.getLocation())){
			if(player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH) == true){
				if(player.hasPermission("silkspawners.silkdrop.*")){
					if(block.getState() instanceof CreatureSpawner){
						CreatureSpawner cs = (CreatureSpawner) block.getState();
						Head head = null;
						for(Head h : Head.values()){
							if(h.getEntityType() == cs.getSpawnedType()){
								head = h;
								break;
							}
						}
						if(head != null){
							event.setCancelled(true);
							block.setType(Material.AIR);
							player.getWorld().dropItem(block.getLocation().add(0.5, 0.25, 0.5), head.getSpawnerItem());
						}
					}
				}
			}
		}else if(block.getType() == Material.SKULL && Utils.canBreakHere(player, block.getLocation())){
			if(block.getState() instanceof Skull){
				try{
					Skull skull = (Skull) block.getState();
					if(skull.getSkullType() == SkullType.PLAYER){
						Head head = null;
						for(Head h : Head.values()){
							if(skull.getOwner().equalsIgnoreCase(h.getSkin())){
								head = h;
								break;
							}
						}
						event.setCancelled(true);
						block.setType(Material.AIR);
						if(head == null){
							player.getWorld().dropItem(block.getLocation().add(0.5, 0.25, 0.5), Utils.getPlayerHeadItem(skull.getOwner()));
						}else{
							player.getWorld().dropItem(block.getLocation().add(0.5, 0.25, 0.5), head.getItem());
						}
					}
				}catch (Exception ex){
					
				}
			}
		}
	}
	
	@EventHandler
	public void blockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		HPlayer hplayer = new HPlayer(player);
		if(event.getBlock().getType() == Material.ENCHANTMENT_TABLE && player.isOp() == false){
			event.setCancelled(true);
		}else if(event.getBlock().getType() == Material.MOB_SPAWNER){
			if(player.getItemInHand() != null && player.getItemInHand().hasItemMeta() == true && player.getItemInHand().getItemMeta().hasDisplayName() == true && 
					player.getItemInHand().getType() == Material.MOB_SPAWNER){
				Head head = null;
				for(Head h : Head.values()){
					if(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName().toLowerCase()).contains(h.getName())){
						head = h;
						break;
					}
				}
				if(head != null){
					if(hplayer.getLevel() < head.getRequiredLevel()){
						event.setCancelled(true);
						hplayer.sendError("You must be level " + head.getRequiredLevel() + "+ to place this spawner!");
					}else{
						if(event.getBlock().getState() instanceof CreatureSpawner){
							CreatureSpawner cs = (CreatureSpawner) event.getBlock().getState();
							cs.setSpawnedType(head.getEntityType());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent event){
		if(event.getEntity().getType() == EntityType.PRIMED_TNT){
			for(Block b : event.blockList()){
				if(b.getType() == Material.MOB_SPAWNER){
					int chance = Utils.randInt(0, 100);
					if(chance <= Utils.getSpawnerTNTPercentage()){
						if(b.getState() instanceof CreatureSpawner){
							CreatureSpawner cs = (CreatureSpawner) b.getState();
							Head head = null;
							for(Head h : Head.values()){
								if(h.getEntityType() == cs.getSpawnedType()){
									head = h;
									break;
								}
							}
							b.getWorld().dropItem(b.getLocation().add(0, 0.5, 0), head.getSpawnerItem());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void signChange(final SignChangeEvent event){
		if(event.getBlock().getState() instanceof Sign){
			final Sign sign = (Sign) event.getBlock().getState();
			if(event.getLine(0).contains("[Purchase]") && event.getLine(1).contains("Mob Spawner")){
				main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
					public void run(){
						Head head = null;
						for(Head h : Head.values()){
							if(ChatColor.stripColor(event.getLine(2).toLowerCase()).equalsIgnoreCase(h.getName())){
								head = h;
								break;
							}
						}
						if(head != null){
							event.setCancelled(true);
							sign.setLine(0, ChatColor.DARK_BLUE + "[Purchase]");
							sign.setLine(1, "Mob Spawner");
							sign.setLine(2, event.getLine(2));
							sign.setLine(3, "$" + head.getSpawnerPrice());
							sign.update();
						}
					}
				}, 5);
			}
		}
	}
	
	@EventHandler
	public void entityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		event.setDroppedExp(0);
		if(entity.getType() != EntityType.PLAYER){
			if(entity.getType() == EntityType.WITHER){
				for(ItemStack i : event.getDrops()){
					if(i.getType() == Material.SKULL_ITEM || i.getType() == Material.SKULL){
						event.getDrops().remove(i);
					}
				}
			}else{
				Head head = null;
				for(Head h : Head.values()){
					if(h.getEntityType() == entity.getType()){
						head = h;
						break;
					}
				}
				if(head != null){
					if(rand.nextFloat() <= head.getChanceToDrop()){
						entity.getWorld().dropItem(entity.getEyeLocation(), head.getItem());
					}
				}
			}
			if(main.op == true && main.drops.containsKey(entity.getType()) == true){
				List<MobDrop> drops = main.drops.get(entity.getType());
				Player player = entity.getKiller();
				for(MobDrop d : drops){
					if(rand.nextInt(100) <= d.getChance()){
						if(d.getItemStack() != null){
							entity.getWorld().dropItem(entity.getEyeLocation(), d.getItemStack());
						}else if(d.getMoney() > 0){
							if(player != null){
								main.econ.depositPlayer(player, d.getMoney());
							}
						}
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		event.setDroppedExp(0);
		event.setKeepLevel(true);
		String name = ChatColor.stripColor(Board.getFactionAt(new FLocation(player.getLocation())).getTag());
		if(name.equalsIgnoreCase("WarZone") == false && name.equalsIgnoreCase("SafeZone") == false){
			player.getWorld().dropItem(player.getEyeLocation(), Utils.getPlayerHeadItem(player.getName()));
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void inventoryClick(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			HPlayer hplayer = new HPlayer((Player)event.getWhoClicked());
			ItemStack item = event.getCurrentItem();
			if(item != null){
				if(item.getType() == Material.EXP_BOTTLE){
					event.setCurrentItem(null);
				}
				InventoryType type = event.getInventory().getType();
				if(item.getType() == Material.SKULL_ITEM && item.getData().getData() != 1){
					int amount = item.getAmount();
					Head head = Head.getFromItemStack(item);
					if(head != Head.PLAYER){
						ItemStack hitem = head.getItem();
						hitem.setAmount(amount);
						event.setCurrentItem(hitem);
					}else{
						if(item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
							try{
								ItemStack hitem = Utils.getPlayerHeadItem(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
								hitem.setAmount(amount);
								event.setCurrentItem(hitem);
							}catch (Exception ex){
								
							}
						}
					}
					/*
					if(head == Head.PLAYER){
						if(item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
							ItemStack hitem = Utils.getPlayerHeadItem(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
							hitem.setAmount(amount);
							event.setCurrentItem(hitem);
						}
					}
					*/
				}else if(item.getType() == Material.MOB_SPAWNER){
					if(item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
						String name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace(" Spawner", "");
						Head head = null;
						for(Head h : Head.values()){
							if(h.getName().equalsIgnoreCase(name)){
								head = h;
								break;
							}
						}
						if(head != null){
							event.setCurrentItem(head.getSpawnerItem());
						}
					}
				}
				if(type == InventoryType.HOPPER || type == InventoryType.DROPPER || type == InventoryType.DISPENSER){
					if(item.getType() == Material.MONSTER_EGG && item.getData().getData() == 50){
						event.setCancelled(true);
						hplayer.sendError("You may not place that item in a " + type.toString().toLowerCase() + "!");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		HPlayer hplayer = new HPlayer(player);
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock() != null){
				if(event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE){
					event.setCancelled(true);
				}else if(event.getClickedBlock().getType() == Material.MOB_SPAWNER){
					if(player.getItemInHand() != null && (player.getItemInHand().getType() == Material.MONSTER_EGG || player.getItemInHand().getType() == Material.MONSTER_EGGS)){
						event.setCancelled(true);
					}
				}else if(event.getClickedBlock().getState() instanceof Sign){
					Sign sign = (Sign) event.getClickedBlock().getState();
					if(sign.getLine(1).contains("Sell") && sign.getLine(2).contains("Head")){
						if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.SKULL_ITEM){
							ItemStack item = player.getItemInHand();
							event.setCancelled(true);
							Head head = Head.getFromItemStack(item);
							if(head != null){
								if(hplayer.getLevel() >= head.getRequiredLevel()){
									if(head != Head.PLAYER){
										double price = head.getSellPrice();
										double xp = head.getXPForSelling();
										boolean shift = false;
										if(player.isSneaking() == false){
											if(item.getAmount() > 1){
												item.setAmount(item.getAmount() - 1);
											}else{
												player.setItemInHand(null);
											}
										}else{
											price = price * item.getAmount();
											xp = xp * item.getAmount();
											player.setItemInHand(null);
											shift = true;
										}
										
										String display = yellow + "a " + gray + head.toString().toLowerCase() + yellow + " head";
										if(shift == true){
											display = gray + item.getAmount() + "x " + head.toString().toLowerCase() + yellow + " heads";
										}
										String plus = gray + "$" + price;
										if(Utils.isBoosterEnabled() == true){
											double increase = (price * Utils.getBoosterMultiplier()) - price;
											plus = plus + ChatColor.DARK_GREEN + " + $" + increase;
											price = price * Utils.getBoosterMultiplier();
										}
										hplayer.sendMessage("You have sold " + display + " for " + plus + yellow + "!");
										
										hplayer.giveMoney(price);
										hplayer.addXP(xp);
									}else{
										String name = "";
										if(item.hasItemMeta() == true && item.getItemMeta().hasDisplayName() == true){
											name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
										}
										if(name.length() > 2){
											if(player.getName().equalsIgnoreCase(name) == false && player.getUniqueId().toString().equalsIgnoreCase(Bukkit.getOfflinePlayer(name).getUniqueId().toString()) == false){
												double price = head.getPlayerSellPrice(name);
												if(item.getAmount() > 1){
													item.setAmount(item.getAmount() - 1);
												}else{
													player.setItemInHand(null);
												}
												double balance = main.econ.getBalance(name);
												String message = yellow + "You have sold " + gray + name + yellow + "'s head for " + gray + "$" + Utils.round(price, 2) + yellow + "!";
												if(price < 1000){
													price = 0;
													message = yellow + "You have sold this worthless head for " + gray + "$" + price + yellow + "!";
												}
												if((balance - price) >= 0){
													main.econ.withdrawPlayer(name, price);
												}else{
													main.econ.withdrawPlayer(name, balance);
												}
												hplayer.sendMessage(message);
												if(price > 0){
													hplayer.giveMoney(price);
													if(Utils.isOnline(name) == true){
														HPlayer htarget = new HPlayer(Bukkit.getPlayer(name));
														htarget.sendMessage(gray + player.getName() + yellow + " has sold your head for " + gray + "$" + Utils.round(price, 2) + yellow + "!");
													}
												}
											}else{
												hplayer.sendError("You may not sell your own head!");
											}
										}
									}
								}else{
									hplayer.sendError("You must be level " + head.getRequiredLevel() + "+ to sell this head!");
								}
							}
						}
					}else if(sign.getLine(0).contains("[Purchase]") && sign.getLine(1).contains("Mob Spawner")){
						Head head = null;
						for(Head h : Head.values()){
							if(ChatColor.stripColor(sign.getLine(2).toLowerCase()).equalsIgnoreCase(h.getName())){
								head = h;
								break;
							}
						}
						if(head != null){
							sign.setLine(0, ChatColor.DARK_BLUE + "[Purchase]");
							sign.setLine(3, "$" + head.getSpawnerPrice());
							sign.update();
							if(hplayer.getLevel() >= head.getRequiredLevel()){
								if(hplayer.getBalance() >= head.getSpawnerPrice()){
									if(hplayer.isInventoryFull() == false){
										hplayer.removeMoney(head.getSpawnerPrice());
										player.getInventory().addItem(head.getSpawnerItem());
										player.updateInventory();
										hplayer.sendMessage("You have purchased a " + gray + head.getName() + yellow + " spawner for " + gray + "$" + head.getSpawnerPrice() + yellow + "!");
									}else{
										hplayer.sendError("Your inventory is full!");
									}
								}else{
									hplayer.sendError("You do not have enough money for this spawner!");
								}
							}else{
								hplayer.sendError("You must be level " + head.getRequiredLevel() + "+ to buy this spawner!");
							}
						}
					}
				}
				if(main.createregion.containsKey(player.getName()) == true){
					Region region = main.createregion.get(player.getName());
					if(region.getStep() == 2){
						region.setPosition(2, event.getClickedBlock().getLocation());
						region.setStep(3);
						hplayer.sendMessage("Type the required level into chat.");
					}
				}
			}
		}
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
			if(player.getItemInHand() != null){
				if(player.getItemInHand().getType() == Material.EXP_BOTTLE){
					event.setCancelled(true);
					player.setItemInHand(null);
				}
			}
		}
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(event.getClickedBlock() != null){
				if(main.createregion.containsKey(player.getName()) == true){
					Region region = main.createregion.get(player.getName());
					if(region.getStep() == 1){
						region.setPosition(1, event.getClickedBlock().getLocation());
						region.setStep(2);
						hplayer.sendMessage("Right click to set the second position.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
		LivingEntity entity = event.getEntity();
		if(entity.getType() == EntityType.SKELETON){
			if(entity.getLocation().getWorld().getEnvironment() == Environment.NETHER && event.getSpawnReason() == SpawnReason.SPAWNER){
				Skeleton skeleton = (Skeleton) entity;
				skeleton.setSkeletonType(SkeletonType.NORMAL);
			}
		}
	}
	
	@EventHandler
	public void asyncPlayerChatEvent(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		HPlayer hplayer = new HPlayer(player);
		if(main.createregion.containsKey(player.getName()) == true){
			Region region = main.createregion.get(player.getName());
			if(region.getStep() == 3){
				event.setCancelled(true);
				if(Utils.isInteger(event.getMessage()) == true){
					int level = Integer.parseInt(event.getMessage());
					region.setLevel(level);
					hplayer.sendMessage("You have created a new region.");
					Utils.addRegion(region.getPosition(1), region.getPosition(2), level);
					main.createregion.remove(player.getName());
					main.regions.add(region);
				}else{
					hplayer.sendError("You must enter a number.");
				}
			}
		}
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent event){
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getX() != to.getX() || from.getZ() != to.getZ()){
			Player player = event.getPlayer();
			HPlayer hplayer = new HPlayer(player);
			Location loc = player.getLocation();
			for(Region r : main.regions){
				if(r.isInRegion(loc) == true){
					if(hplayer.getLevel() < r.getLevel()){
						player.teleport(loc.subtract(loc.getDirection().normalize().multiply(1.5)));
						hplayer.sendError("You must be level " + r.getLevel() + " to enter this area.");
						break;
					}
				}
			}
		}
	}
}
