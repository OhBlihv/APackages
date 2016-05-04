package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.MonthlyPackage;
import me.ohblihv.APackages.PackageManager;
import me.ohblihv.APackages.util.BUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 4/20/2016.
 */
public class TimeLeftCommand extends ACommand
{

	private final List<String> printFormat;

	private final String    printMessage,
							noPackages;
	
	public TimeLeftCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.printFormat = BUtil.translateColours(configurationSection.getStringList("options.print-format"));

		this.printMessage = BUtil.translateColours(configurationSection.getString("options.print-message"));
		this.noPackages = BUtil.translateColours(configurationSection.getString("options.no-packages"));
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

		Map<String, String> optionMap = PackageManager.getPackageOptions(player);
		for(String line : printFormat)
		{
			if(line.equals("{lines}"))
			{
				if(optionMap.isEmpty())
				{
					player.sendMessage(noPackages);
				}
				else
				{
					for(Map.Entry<String, String> entry : optionMap.entrySet())
					{
						MonthlyPackage monthlyPackage = PackageManager.getPackage(entry.getKey());
						if(monthlyPackage == null)
						{
							BUtil.logError("Found expiring package named '" + entry.getKey() + "' that did not exist within this plugin!");
							continue;
						}

						long expiryTime;
						try
						{
							expiryTime = Long.parseLong(entry.getValue());
						}
						catch(NumberFormatException e)
						{
							BUtil.logError("Found invalid activation time: " + entry.getValue() + " on package " + entry.getKey());
							continue;
						}

						player.sendMessage(printMessage.replace("{rank}", monthlyPackage.getDisplayname()).replace("{timeleft}", monthlyPackage.getTimeleft(expiryTime)));
					}
				}

				continue;
			}

			player.sendMessage(line);
		}
		return true;
	}
}
