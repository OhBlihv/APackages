package me.ohblihv.APackages.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Chris on 4/20/2016.
 */
public class ExperienceCommand extends ACommand
{

	private final int claimAmount;

	public ExperienceCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.claimAmount = configurationSection.getInt("options.claim-amount", 20);
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("§cOnly players are allowed to use this command.");
			return false;
		}

		((Player) sender).giveExp(claimAmount);
		return true;
	}
}
