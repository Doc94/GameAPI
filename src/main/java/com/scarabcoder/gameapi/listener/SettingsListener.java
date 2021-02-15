package com.scarabcoder.gameapi.listener;

import com.scarabcoder.gameapi.game.ArenaSettings;
import com.scarabcoder.gameapi.game.Game;
import com.scarabcoder.gameapi.game.GamePlayer;
import com.scarabcoder.gameapi.manager.ArenaManager;
import com.scarabcoder.gameapi.manager.PlayerManager;
import com.scarabcoder.gameapi.util.ScarabUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Random;

public class SettingsListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteract(PlayerInteractEvent e){
		GamePlayer p = PlayerManager.getGamePlayer(e.getPlayer());
		ArenaSettings settings = ArenaManager.getActiveSettings(p);
		if(settings != null){
			if(p.isInGame()){
				if(!settings.canPlayerInteract()){
					e.setCancelled(true);
					return;
				}
				if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					//CHEST ACCESS
					if(e.getClickedBlock().getType().equals(Material.CHEST) || e.getClickedBlock().getType().equals(Material.TRAPPED_CHEST)){
						if(!settings.canChestAccess()){
							e.setCancelled(true);
						}
					}
					//FLINT & STEEL
					if(e.getItem() != null){
						if(e.getItem().getType().equals(Material.FLINT_AND_STEEL)){
							if(!settings.canFlintAndSteel()) e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		GamePlayer player = PlayerManager.getGamePlayer(e.getPlayer());
		ArenaSettings settings = ArenaManager.getActiveSettings(player);
		if(settings != null){
			if(!settings.allowChat()) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getWorld().getSpawnLocation());
		if(settings != null){
			if(!settings.isAllowWeatherChange()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void explosionEvent(EntityExplodeEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getLocation());
		if(settings != null){
			if(e.getEntityType().equals(EntityType.CREEPER)){
				if(!settings.canCreeperExplosion()){
					e.setCancelled(true);
				}
			}else if(e.getEntityType().equals(EntityType.FIREBALL)){
				if(!settings.canGhastFireballExplosion()) e.setCancelled(true);
			}else if(e.getEntityType().equals(EntityType.PRIMED_TNT)){
				if(!settings.canTNTExplosion()) e.setCancelled(true);
			}else{
				if(!settings.canOtherExplosion()) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void enderpearlEvent(ProjectileLaunchEvent e){
		if(e.getEntity().getShooter() != null){
			if(e.getEntity().getShooter() instanceof Player){
				GamePlayer player = PlayerManager.getGamePlayer((Player) e.getEntity().getShooter());
				ArenaSettings settings = ArenaManager.getActiveSettings(player);
				if(settings != null){
					if(!ArenaManager.getActiveSettings(player).canEnderpearl()){
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void entityGrief(EntityChangeBlockEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			//EnderDragon
			if(!settings.canEnderDragonDestroy() && e.getEntityType().equals(EntityType.ENDER_DRAGON)) e.setCancelled(true);
			//Enderman
			if(!settings.canEndermanGrief() && e.getEntityType().equals(EntityType.ENDERMAN)) e.setCancelled(true);
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void expDrop(EntityDeathEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getEntity().getLocation());
		if(settings != null){
			if(!settings.canExpDrop()){
				e.setDroppedExp(0);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockSpread(BlockSpreadEvent e){

		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(e.getNewState().getType().equals(Material.FIRE)){
				if(!settings.canFireSpread()){
					e.setCancelled(true);
				}
			} else if(e.getNewState().getType().equals(Material.GRASS)){
				if(!settings.canGrassGrowth()) e.setCancelled(true);
			} else if(e.getNewState().getType().equals(Material.BROWN_MUSHROOM) || e.getNewState().getType().equals(Material.RED_MUSHROOM)){
				if(!settings.canMushroomGrowth()) e.setCancelled(true);
			} else if(e.getNewState().getType().equals(Material.MYCELIUM)){
				if(!settings.canMyceliumSpread()) e.setCancelled(true);
			} else if(e.getNewState().getType().equals(Material.VINE)){
				if(!settings.canVineGrowth()) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockForm(BlockFormEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(e.getNewState().equals(Material.ICE)){
				if(!settings.canIceForm()) e.setCancelled(true);
				
			}else if(e.getNewState().equals(Material.SNOW)){
				if(!settings.canSnowCollection()) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void itemDrop(PlayerDropItemEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(PlayerManager.getGamePlayer(e.getPlayer()));
		if(settings != null){
			if(!settings.canItemDrop()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDamage(EntityDamageEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getEntity().getLocation());
		if(settings != null){
			if(e.getEntity().getType().equals(EntityType.ITEM_FRAME)){
				if(!settings.canItemFrameDamage()) e.setCancelled(true);
			}else if(e.getEntity().getType().equals(EntityType.PAINTING)){
				if(!settings.canPaintingDamage()) e.setCancelled(true);
			}else if(e.getEntity().getType().equals(EntityType.PLAYER)){
				GamePlayer player = PlayerManager.getGamePlayer((Player) e.getEntity());
				ArenaSettings pSettings = ArenaManager.getActiveSettings(player);
				if(pSettings != null){
					if(pSettings.canPlayerInvincibility()) e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void igniteEvent(BlockIgniteEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(e.getIgnitingBlock().getType().equals(Material.LAVA) || e.getIgnitingBlock().getType().equals(Material.LAVA)){
				if(!settings.canLavaFire()) e.setCancelled(true);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void fluidFlow(BlockFromToEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(e.getBlock().getType().equals(Material.LAVA)){
				if(!settings.canLavaFlow()) e.setCancelled(true);
			}else if(e.getBlock().getType().equals(Material.WATER)){
				if(!settings.canWaterFlow()) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void leafDecay(LeavesDecayEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(!settings.canLeafDecay()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void lightningStrike(LightningStrikeEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getLightning().getLocation());
		if(settings != null){
			if(!settings.canLightning()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void allowMobSpawn(CreatureSpawnEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getEntity().getLocation());
		if(settings != null){
			if(!settings.canMobSpawn() && e.getSpawnReason().equals(SpawnReason.NATURAL)) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void pistonExtend(BlockPistonExtendEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(!settings.canPistons()) e.setCancelled(true);
		}
	}
	

	@EventHandler(priority = EventPriority.LOWEST)
	public void pistonRetract(BlockPistonRetractEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(!settings.canPistons()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerEnterBed(PlayerBedEnterEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(PlayerManager.getGamePlayer(e.getPlayer()));
		if(settings != null){
			if(!settings.canPlayerSleep()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockMelt(BlockFadeEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getBlock().getLocation());
		if(settings != null){
			if(e.getBlock().getType().equals(Material.SNOW)){
				if(!settings.canSnowMelt()) e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void vehicleDestroy(VehicleDestroyEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(e.getVehicle().getLocation());
		if(settings != null){
			if(!settings.canVehicleDestroy()) e.setCancelled(true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockPlace(BlockPlaceEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(PlayerManager.getGamePlayer(e.getPlayer()));
		if(settings != null){
			if(!settings.isCanBuild()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockBreak(BlockBreakEvent e){
		ArenaSettings settings = ArenaManager.getActiveSettings(PlayerManager.getGamePlayer(e.getPlayer()));
		if(settings != null){
			if(!settings.isCanDestroy()){
				e.setCancelled(true);
			}else if(!settings.getAllowBlockDrop()){
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onFoodchange(FoodLevelChangeEvent e){
		GamePlayer player = PlayerManager.getGamePlayer((Player)e.getEntity());
		ArenaSettings settings = ArenaManager.getActiveSettings(player);
		if(settings != null){
			if(!settings.isAllowFoodLevelChange()){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDeath(PlayerDeathEvent e){
		GamePlayer player = PlayerManager.getGamePlayer(e.getEntity());
		ArenaSettings settings = ArenaManager.getActiveSettings(player);
		if(settings != null){
			if(settings.isKeepInventory()){
				e.setKeepInventory(true);
			}
			if(!settings.isAllowDropOnPlayerDie()) {
				e.getDrops().clear();
			}
			if(player.getGame().getGameSettings().shouldDisableVanillaDeathMessages()) e.setDeathMessage("");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDurabilityChange(PlayerItemDamageEvent e){
		GamePlayer player = PlayerManager.getGamePlayer(e.getPlayer());
		ArenaSettings settings = ArenaManager.getActiveSettings(player);
		if(settings != null){
			e.setCancelled(!settings.isAllowDurabilityChange());
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e){
		GamePlayer player = PlayerManager.getGamePlayer((OfflinePlayer) e.getWhoClicked());
		ArenaSettings settings = ArenaManager.getActiveSettings(player);
		if(settings != null){
			if(!settings.isAllowInventoryChange()) e.setCancelled(true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerRespawn(PlayerRespawnEvent e){
		GamePlayer player = PlayerManager.getGamePlayer(e.getPlayer());
		if(player.isOnline() && player.isInGame()){
			Game game = player.getGame();
			if(game.getGameSettings().shouldUseTeams()){
				if(player.getTeam() != null){
					e.setRespawnLocation((player.getTeam().getTeamSpawns().get(new Random().nextInt(player.getTeam().getTeamSpawns().size()))));
				}
			}
			
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDamagePlayer(EntityDamageByEntityEvent e){
		if(ScarabUtil.isPlayerDamager(e) && e.getEntity() instanceof Player){
			GamePlayer player = PlayerManager.getGamePlayer((Player) e.getEntity());
			GamePlayer damager = ScarabUtil.getDamager(e);
			if(damager.isInGame()){
				ArenaSettings settings = ArenaManager.getActiveSettings(damager);
				if(settings != null){
					if(!settings.isCanPvP()){
						e.setCancelled(true);
					}else{
						if(player.isInGame()){
							if(player.getGame().getID().equals(damager.getGame().getID())){
								if(player.getTeam() != null && damager.getTeam() != null){
									if(player.getTeam().getName().equals(damager.getTeam().getName()) && !player.getTeam().allowTeamDamage()) e.setCancelled(true);
								}
							}
						}
					}
					
				}
			}
			
		}
	}
	
	
	
}
