package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.MonthlyPackage;
import me.ohblihv.APackages.PackageManager;
import me.ohblihv.APackages.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 4/21/2016.
 */
public class AdminCommand extends ACommand
{

	private final String    syntaxMessage,
							invalidTime,
							invalidUnit,
							invalidPackage,
							playerDoesntExist,
							addedTime,
							removedTime,
							setTime;
	
	public AdminCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.syntaxMessage = BUtil.translateColours(configurationSection.getString("options.syntax"));
		this.invalidTime = BUtil.translateColours(configurationSection.getString("options.invalid-time"));
		this.invalidUnit = BUtil.translateColours(configurationSection.getString("options.invalid-unit"));
		this.invalidPackage = BUtil.translateColours(configurationSection.getString("options.invalid-package"));
		this.playerDoesntExist = BUtil.translateColours(configurationSection.getString("options.player-doesnt-exist"));
		this.addedTime = BUtil.translateColours(configurationSection.getString("options.success.added-time"));
		this.removedTime = BUtil.translateColours(configurationSection.getString("options.success.removed-time"));
		this.setTime = BUtil.translateColours(configurationSection.getString("options.success.set-time"));
	}

	@Override
	public boolean onCommand(CommandSender player, String[] args)
	{
		if(args.length > 4)
		{
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
			if(offlinePlayer == null)
			{
				player.sendMessage(playerDoesntExist);
				return false;
			}

			long seconds;
			try
			{
				seconds = Long.parseLong(args[3]);
			}
			catch(NumberFormatException e)
			{
				player.sendMessage(invalidTime);
				return false;
			}

			TimeUnit timeUnit;
			try
			{
				String timeUnitString = args[4].toUpperCase();
				switch(timeUnitString)
				{
					case "SECOND":
					case "MINUTE":
					case "HOUR":
					case "DAY":
						timeUnitString += "s";
						break;
					default:
						break;
				}

				timeUnit = TimeUnit.valueOf(timeUnitString);
			}
			catch(IllegalArgumentException e)
			{
				player.sendMessage(invalidUnit);
				return false;
			}

			seconds = timeUnit.toSeconds(seconds);

			MonthlyPackage monthlyPackage = PackageManager.getPackage(args[2]);
			if(monthlyPackage == null)
			{
				player.sendMessage(invalidPackage);
				return false;
			}

			String successMessage = null;
			switch(args[0])
			{
				case "addtime":
				{
					if(offlinePlayer.isOnline())
					{
						PackageManager.addTime(offlinePlayer.getPlayer(), monthlyPackage.getInternalName(), seconds);
					}
					else
					{
						PackageManager.addTime(offlinePlayer.getName(), monthlyPackage.getInternalName(), seconds);
					}

					successMessage = addedTime;
					break;
				}
				case "removetime":
				{
					if(offlinePlayer.isOnline())
					{
						PackageManager.removeTime(offlinePlayer.getPlayer(), monthlyPackage.getInternalName(), seconds);
					}
					else
					{
						PackageManager.removeTime(offlinePlayer.getName(), monthlyPackage.getInternalName(), seconds);
					}
					successMessage = removedTime;
					break;
				}
				case "settime":
				{
					if(offlinePlayer.isOnline())
					{
						PackageManager.setTime(offlinePlayer.getPlayer(), monthlyPackage.getInternalName(), seconds);
					}
					else
					{
						PackageManager.setTime(offlinePlayer.getName(), monthlyPackage.getInternalName(), seconds);
					}
					successMessage = setTime;
					break;
				}
			}

			if(successMessage != null)
			{
				player.sendMessage(successMessage.replace("{time}", args[3]).replace("{unit}", timeUnit.name().toLowerCase()).replace("{player}", offlinePlayer.getName())
						                   .replace("{package}", monthlyPackage.getDisplayname()));
				return true;
			}
		}

		player.sendMessage(syntaxMessage);
		return true;
	}

}
