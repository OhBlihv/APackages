package me.ohblihv.APackages.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.ohblihv.APackages.util.BUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
			healthBonusSet.add(new HealthBonus(permission.replace("-", "\\."), Integer.parseInt(configurationSection.getString("options.amount." + permission, "21"))));
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
		if(!(sender instanceof Player))
		{
			sender.sendMessage("§cOnly players are allowed to use this command.");
			return false;
		}

		Player player = (Player) sender;

		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("on"))
			{
				if(hasHealthBonusActive(player))
				{
					player.sendMessage(BONUS_ALREADY_ACTIVE);
					return false;
				}

				player.setMaxHealth(getHealthBonus(player));
				player.sendMessage(BONUS_ACTIVE.replace("{health}", String.valueOf((int) player.getMaxHealth())));
				return true;
			}
			else if(args[0].equalsIgnoreCase("off"))
			{
				if(!hasHealthBonusActive(player))
				{
					player.sendMessage(BONUS_ALREADY_INACTIVE);
					return false;
				}

				player.setMaxHealth(DEFAULT_HEALTH);
				player.sendMessage(BONUS_INACTIVE.replace("{health}", String.valueOf(getHealthBonus(player))));
				return true;
			}
		}

		for(String line : menuMessage)
		{
			player.sendMessage(line);
		}
		return false;
	}
}
