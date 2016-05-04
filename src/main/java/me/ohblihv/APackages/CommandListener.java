package me.ohblihv.APackages;

import lombok.Getter;
import me.ohblihv.APackages.commands.ACommand;
import me.ohblihv.APackages.commands.AdminCommand;
import me.ohblihv.APackages.commands.ExperienceCommand;
import me.ohblihv.APackages.commands.FixCommand;
import me.ohblihv.APackages.commands.HealthCommand;
import me.ohblihv.APackages.commands.LoreCommand;
import me.ohblihv.APackages.commands.PrefixCommand;
import me.ohblihv.APackages.commands.TimeLeftCommand;
import me.ohblihv.APackages.util.FlatFile;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chris on 4/20/2016.
 */
public class CommandListener implements Listener
{

	@Getter
	private static CommandListener instance = null;

	@Getter
	private Set<ACommand> commandSet = new HashSet<>();

	public CommandListener()
	{
		instance = this;

		FlatFile cfg = FlatFile.getInstance();

		commandSet.add(new AdminCommand(cfg.getConfigurationSection("commands.admin")));
		commandSet.add(new ExperienceCommand(cfg.getConfigurationSection("commands.xpclaim")));
		commandSet.add(new FixCommand(cfg.getConfigurationSection("commands.fixclaim")));
		commandSet.add(new HealthCommand(cfg.getConfigurationSection("commands.health")));
		commandSet.add(new LoreCommand(cfg.getConfigurationSection("commands.lore")));
		commandSet.add(new PrefixCommand(cfg.getConfigurationSection("commands.prefix")));
		commandSet.add(new TimeLeftCommand(cfg.getConfigurationSection("commands.timeleft")));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		if(!event.getMessage().startsWith("/"))
		{
			return;
		}

		String[] args = event.getMessage().split(" ");
		String commandName = args[0].substring(1);
		if(args.length > 1)
		{
			args = Arrays.copyOfRange(args, 1, args.length);
		}
		else
		{
			args = new String[]{};
		}

		if(findCommand(event.getPlayer(), commandName, args))
		{
			event.setCancelled(true);
		}
	}

	public boolean findCommand(CommandSender sender, String commandName, String[] args)
	{
		for(ACommand command : commandSet)
		{
			if(command.matchesCommand(commandName))
			{
				command.onInitialCommand(sender, args);
				return true;
			}
		}
		return false;
	}
	
}
