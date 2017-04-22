package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.MonthlyPackage;
import me.ohblihv.APackages.PackageManager;
import me.ohblihv.APackages.util.BUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
	
	private final Pattern OTHER_PLAYER_PATTERN = Pattern.compile("([ ]?You[ ]+)");

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		final String targetPlayer;
		if(args.length > 0)
		{
			targetPlayer = args[0];
		}
		else
		{
			 targetPlayer = player.getName();
		}
		
		Map<String, String> optionMap = PackageManager.getPackageOptions(targetPlayer);
		for(String line : printFormat)
		{
			if(line.contains("{player}"))
			{
				line = line.replace("{player}", targetPlayer);
			}
			if(line.equals("{lines}"))
			{
				if(optionMap.isEmpty())
				{
					String noPackagesMessage = noPackages;
					if(!targetPlayer.equals(player.getName()))
					{
						noPackagesMessage = OTHER_PLAYER_PATTERN.matcher(noPackagesMessage).replaceAll(targetPlayer);
					}
					
					player.sendMessage(noPackagesMessage);
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
						
						boolean hasRank = true;
						if(expiryTime < System.currentTimeMillis())
						{
							hasRank = false;
							
							String packageInternalName = monthlyPackage.getInternalName().toLowerCase();
							
							PermissionUser user = PermissionsEx.getUser(player);
							for(String groupName : user.getGroupNames())
							{
								if(groupName.toLowerCase().equals(packageInternalName))
								{
									hasRank = true;
									break;
								}
							}
						}
						
						if(!hasRank)
						{
							continue;
						}
						
						player.sendMessage(printMessage
							                   .replace("{rank}", monthlyPackage.getDisplayname())
							                   .replace("{timeleft}", monthlyPackage.getTimeleft(expiryTime)));
					}
				}
				
				continue;
			}
			
			player.sendMessage(line);
		}
		return true;
	}
}
