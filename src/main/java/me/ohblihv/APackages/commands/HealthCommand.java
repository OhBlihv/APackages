package me.ohblihv.APackages.commands;

import com.skytonia.SkyCore.util.RunnableShorthand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.ohblihv.APackages.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by Chris on 4/20/2016.
 */
public class HealthCommand extends ACommand
{

	@AllArgsConstructor
	private class HealthBonus implements Comparable<HealthBonus>
	{

		@Getter
		private final String permission;

		@Getter
		private final int healthBonus;

		@Override
		public int compareTo(HealthBonus o)
		{
			//Reverse the ordering to allow higher values first
			return 0 - (Integer.compare(healthBonus, o.getHealthBonus()));
		}
	}

	private static final int DEFAULT_HEALTH = 20;

	private final List<String> menuMessage;
	private final TreeSet<HealthBonus> healthBonusSet = new TreeSet<>();

	private final String    BONUS_ACTIVE,
							BONUS_INACTIVE,
							BONUS_ALREADY_ACTIVE,
							BONUS_ALREADY_INACTIVE;
	
	public HealthCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.menuMessage = BUtil.translateColours(configurationSection.getStringList("options.menu-message"));
		this.BONUS_ACTIVE = BUtil.translateColours(configurationSection.getString("options.bonus-active"));
		this.BONUS_INACTIVE = BUtil.translateColours(configurationSection.getString("options.bonus-inactive"));
		this.BONUS_ALREADY_ACTIVE = BUtil.translateColours(configurationSection.getString("options.bonus-already-active"));
		this.BONUS_ALREADY_INACTIVE = BUtil.translateColours(configurationSection.getString("options.bonus-already-inactive"));

		for(String permission : configurationSection.getConfigurationSection("options.amount").getKeys(false))
		{
			healthBonusSet.add(new HealthBonus(permission.replace("-", "."), Integer.parseInt(configurationSection.getString("options.amount." + permission, "21"))));
			BUtil.logInfo("Loaded Health Bonus for '" + permission.replace("-", ".") + "' (" + permission + ") at " +
				                                configurationSection.getString("options.amount." + permission, "21"));
		}
	}

	private Integer getHealthBonus(Player player)
	{
		for(HealthBonus healthBonus : healthBonusSet)
		{
			if(player.hasPermission(healthBonus.getPermission()))
			{
				return healthBonus.getHealthBonus();
			}
		}
		return DEFAULT_HEALTH; //Return default health
	}

	private boolean hasHealthBonusActive(Player player)
	{
		return player.getMaxHealth() == getHealthBonus(player);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player) sender;
		}

		if(args.length > 0)
		{
			if(player == null)
			{
				if(args.length < 2)
				{
					sender.sendMessage("§cMissing target player name!");
					return true;
				}
				
				OfflinePlayer offlinePlayer;
				try
				{
					offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
					
					if(offlinePlayer == null)
					{
						sender.sendMessage("§cPlayer " + args[1] + " does not exist.");
						return true;
					}
					else if(!offlinePlayer.isOnline())
					{
						sender.sendMessage("§cPlayer " + args[1] + " is offline.");
						return true;
					}
				}
				catch(Exception e)
				{
					sender.sendMessage("§cPlayer " + args[1] + " does not exist.");
					return true;
				}
				
				player = offlinePlayer.getPlayer();
			}
			
			if(args[0].equalsIgnoreCase("on"))
			{
				if(hasHealthBonusActive(player))
				{
					sender.sendMessage(BONUS_ALREADY_ACTIVE);
					return false;
				}

				player.setMaxHealth(getHealthBonus(player));
				sender.sendMessage(BONUS_ACTIVE.replace("{health}", String.valueOf((int) player.getMaxHealth())));
				return true;
			}
			else if(args[0].equalsIgnoreCase("off"))
			{
				if(!hasHealthBonusActive(player))
				{
					sender.sendMessage(BONUS_ALREADY_INACTIVE);
					return false;
				}
				
				player.setMaxHealth(DEFAULT_HEALTH);
				sender.sendMessage(BONUS_INACTIVE.replace("{health}", String.valueOf(getHealthBonus(player))));
				return true;
			}
		}
		
		if(!(sender instanceof Player))
		{
			sender.sendMessage("§e/health on <player>");
			sender.sendMessage("§e/health off <player>");
		}
		else
		{
			for(String line : menuMessage)
			{
				player.sendMessage(line);
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		player.setMaxHealth(20);
		
		RunnableShorthand.forThis().with(() ->
		{
			double healthBonus = getHealthBonus(player);
			if(healthBonus != player.getMaxHealth())
			{
				BUtil.logInfo("Updating " + player.getName() + "'s health to " + healthBonus);
				player.setMaxHealth(healthBonus);
			}
			
		}).runTaskLater(20L);
	}
	
}
