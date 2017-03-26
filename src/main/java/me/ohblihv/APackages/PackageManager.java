package me.ohblihv.APackages;

import me.ohblihv.APackages.util.BUtil;
import me.ohblihv.APackages.util.FlatFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 4/20/2016.
 */
public class PackageManager
{

	public static final String PERMISSIONSEX_OPTION_PREFIX = "APACKAGES_";

	private static final ConcurrentHashMap<String, MonthlyPackage> packageMap = new ConcurrentHashMap<>();

	public static void reload()
	{
		FlatFile cfg = FlatFile.getInstance();

		for(String configName : cfg.getChildren("packages"))
		{
			ConfigurationSection configurationSection = cfg.getSave().getConfigurationSection("packages." + configName);

			String internalName = configurationSection.getString("group-name", null);
			if(internalName == null)
			{
				BUtil.logError("Invalid internal package name: '" + internalName + "'.");
				continue;
			}

			TimeUnit timeUnit;
			try
			{
				timeUnit = TimeUnit.valueOf(configurationSection.getString("expiry.unit"));
			}
			catch(IllegalArgumentException e)
			{
				BUtil.logError("Invalid Time Unit: '" + configurationSection.getString("expiry.unit") + "'.");
				continue;
			}

			packageMap.put(internalName, new MonthlyPackage(BUtil.translateColours(configurationSection.getString("displayname")),
			                                                internalName,
			                                                timeUnit,
			                                                configurationSection.getInt("expiry.time")));
		}
	}

	public static MonthlyPackage getPackage(String internalName)
	{
		return packageMap.get(internalName);
	}

	public static long getTimeLeft(Player player, MonthlyPackage monthlyPackage)
	{
		PermissionUser permissionUser = PermissionsEx.getUser(player);

		long timeLeft = 0;

		String optionValue = permissionUser.getOption(PERMISSIONSEX_OPTION_PREFIX + monthlyPackage.getInternalName());
		try
		{
			timeLeft = Long.parseLong(optionValue);
		}
		catch(NumberFormatException e)
		{
			BUtil.logError("Attempted to parse an invalid time from " + player.getName() + " from the group " + monthlyPackage.getInternalName() + ".");
		}

		return timeLeft - (System.currentTimeMillis() / 1000L);
	}

	public static Map<String, String> getPackageOptions(Player player)
	{
		return getPackageOptions(PermissionsEx.getUser(player));
	}
	
	public static Map<String, String> getPackageOptions(String playerName)
	{
		return getPackageOptions(PermissionsEx.getUser(playerName));
	}
	
	public static Map<String, String> getPackageOptions(PermissionUser permissionUser)
	{
		Map<String, String> applicableOptionMap = new HashMap<>();
		for(Map.Entry<String, String> entry : permissionUser.getOptions(null).entrySet())
		{
			if(entry.getKey().startsWith(PERMISSIONSEX_OPTION_PREFIX))
			{
				applicableOptionMap.put(entry.getKey().replace(PERMISSIONSEX_OPTION_PREFIX, ""), entry.getValue());
			}
		}

		return applicableOptionMap;
	}

	public static void addTime(Player player, String internalName, long seconds)
	{
		addTime(PermissionsEx.getUser(player), internalName, seconds);
	}

	public static void addTime(String playerName, String internalName, long seconds)
	{
		addTime(PermissionsEx.getUser(playerName), internalName, seconds);
	}

	public static void addTime(PermissionUser permissionUser, String internalName, long seconds)
	{
		long secondsTilExpiry = System.currentTimeMillis() / 1000L;
		String optionString = permissionUser.getOption(PERMISSIONSEX_OPTION_PREFIX + internalName);

		//NFEs handle null pointers, so we're safe here.
		try
		{
			secondsTilExpiry = Long.parseLong(optionString);
		}
		catch(NumberFormatException e)
		{
			//Use current time
		}

		secondsTilExpiry += seconds;

		permissionUser.setOption(PERMISSIONSEX_OPTION_PREFIX + internalName, String.valueOf(secondsTilExpiry));
	}

	public static void removeTime(Player player, String internalName, long seconds)
	{
		removeTime(PermissionsEx.getUser(player), internalName, seconds);
	}

	public static void removeTime(String playerName, String internalName, long seconds)
	{
		removeTime(PermissionsEx.getUser(playerName), internalName, seconds);
	}

	public static void removeTime(PermissionUser permissionUser, String internalName, long seconds)
	{
		long secondsTilExpiry = System.currentTimeMillis() / 1000L;
		String optionString = permissionUser.getOption(PERMISSIONSEX_OPTION_PREFIX + internalName);

		//NFEs handle null pointers, so we're safe here.
		try
		{
			secondsTilExpiry = Long.parseLong(optionString);
		}
		catch(NumberFormatException e)
		{
			//Use current time
		}

		secondsTilExpiry -= seconds;

		permissionUser.setOption(PERMISSIONSEX_OPTION_PREFIX + internalName, String.valueOf(secondsTilExpiry));
	}

	public static void setTime(Player player, String internalName, long seconds)
	{
		setTime(PermissionsEx.getUser(player), internalName, seconds);
	}

	public static void setTime(String playerName, String internalName, long seconds)
	{
		setTime(PermissionsEx.getUser(playerName), internalName, seconds);
	}

	public static void setTime(PermissionUser permissionUser, String internalName, long seconds)
	{
		permissionUser.setOption(PERMISSIONSEX_OPTION_PREFIX + internalName, String.valueOf((System.currentTimeMillis() / 1000L) + seconds));
	}

}
