package me.ohblihv.APackages.commands;

import lombok.Getter;
import me.ohblihv.APackages.util.BUtil;
import me.ohblihv.APackages.util.FlatFile;
import me.ohblihv.APackages.util.SaveFlatFile;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chris on 4/20/2016.
 */
public abstract class ACommand
{

	static final String MISSING_STRING = "§cThis message is missing from the config. Ensure your config is up to date.";

	@Getter
	final boolean enabled;

	@Getter
	final String command;

	@Getter
	final String permission;

	@Getter
	final String noPermissionMessage;

	@Getter
	final int expiryTime;

	@Getter
	final TimeUnit expiryUnit;

	@Getter
	final String onCooldownMessage;

	@Getter
	final String onUseMessage;

	public ACommand(ConfigurationSection configurationSection)
	{
		this.enabled = configurationSection.getBoolean("enabled", true);
		this.command = configurationSection.getString("command", "");
		this.permission = configurationSection.getString("permission", null);
		this.noPermissionMessage = BUtil.translateColours(configurationSection.getString("no-permission", null));

		if(configurationSection.contains("cooldown"))
		{
			expiryTime = configurationSection.getInt("cooldown.time");
			TimeUnit tempTimeUnit;
			try
			{
				tempTimeUnit = TimeUnit.valueOf(configurationSection.getString("cooldown.unit"));
			}
			catch(IllegalArgumentException e)
			{
				BUtil.logError("Found invalid time unit on command " + configurationSection.getName() + ": '" + configurationSection.getString("cooldown.unit") + "'");
				tempTimeUnit = TimeUnit.DAYS;
			}
			expiryUnit = tempTimeUnit;

			this.onCooldownMessage = BUtil.translateColours(configurationSection.getString("cooldown.on-cooldown",
			                                                FlatFile.getInstance().getString("messages.commands.default.on-cooldown")));
		}
		else
		{
			this.expiryTime = -1;
			this.expiryUnit = null;

			this.onCooldownMessage = BUtil.translateColours(FlatFile.getInstance().getString("messages.commands.default.on-cooldown"));
		}

		this.onUseMessage = BUtil.translateColours(configurationSection.getString("on-use", null));
	}

	public boolean isCommand(String enteredCommand)
	{
		return command.equalsIgnoreCase(enteredCommand);
	}

	public boolean hasPermission(Player sender)
	{
		return permission == null || sender.hasPermission(permission);
	}

	public boolean matchesCommand(String command)
	{
		return enabled && isCommand(command);
	}

	public long checkCooldown(UUID uuid)
	{
		if(expiryTime == 0)
		{
			return -1;
		}

		Map<UUID, Long> commandCooldownMap = SaveFlatFile.getSaveMap().get(command);
		if(commandCooldownMap == null)
		{
			return -1;
		}

		return commandCooldownMap.getOrDefault(uuid, -1L);
	}

	public void onInitialCommand(CommandSender sender, String[] args)
	{
		boolean isPlayer = sender instanceof Player;
		if(isPlayer)
		{
			if(!hasPermission((Player) sender))
			{
				if(noPermissionMessage != null)
				{
					sender.sendMessage(noPermissionMessage);
				}
				return;
			}

			long cooldown = checkCooldown(((Player) sender).getUniqueId());
			if(cooldown > 0 /*&& !sender.isOp()*/ && (cooldown - System.currentTimeMillis()) > 0)
			{
				sender.sendMessage(onCooldownMessage);
				return;
			}
		}

		//Only enter if the command completed successfully
		if(onCommand(sender, args))
		{
			if(onUseMessage != null)
			{
				sender.sendMessage(onUseMessage);
			}

			//Add the cooldown if required
			if(isPlayer && expiryTime > 0)
			{
				addCooldown(((Player) sender).getUniqueId());
			}
		}
	}

	private void addCooldown(UUID uuid)
	{
		Map<UUID, Long> commandCooldownMap = SaveFlatFile.getSaveMap().get(command);
		if(commandCooldownMap == null)
		{
			commandCooldownMap = new HashMap<>();
			SaveFlatFile.getSaveMap().put(command, commandCooldownMap);
		}

		commandCooldownMap.put(uuid, System.currentTimeMillis() + expiryUnit.toMillis(expiryTime));
	}

	/**
	 *
	 * @param player
	 * @param args
	 * @return True if the command completed successfully, else False.
	 */
	public abstract boolean onCommand(CommandSender player, String[] args);

}
