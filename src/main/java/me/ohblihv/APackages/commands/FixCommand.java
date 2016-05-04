package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.util.BUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris on 4/21/2016.
 */
public class FixCommand extends ACommand
{

	private final String    notRepairableMessage,
							alreadyRepairedMessage;
	
	public FixCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.notRepairableMessage = BUtil.translateColours(configurationSection.getString("options.not-repairable"));
		this.alreadyRepairedMessage = BUtil.translateColours(configurationSection.getString("options.already-repaired"));
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("§cOnly players are allowed to use this command.");
			return false;
		}

		ItemStack itemStack = ((Player) sender).getItemInHand();
		Material material = itemStack.getType();
		if(material.isBlock() || material.getMaxDurability() < 1)
		{
			sender.sendMessage(notRepairableMessage);
			return false;
		}

		if(itemStack.getDurability() == 0)
		{
			sender.sendMessage(alreadyRepairedMessage);
			return false;
		}

		itemStack.setDurability((short) 0);
		return true;
	}
}
