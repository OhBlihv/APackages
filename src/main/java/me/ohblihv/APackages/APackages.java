package me.ohblihv.APackages;

import lombok.Getter;
import me.ohblihv.APackages.util.FlatFile;
import me.ohblihv.APackages.util.Messages;
import me.ohblihv.APackages.util.SaveFlatFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Chris on 4/20/2016.
 */
public class APackages extends JavaPlugin
{

	@Getter
	private static APackages instance = null;

	@Override
	public void onEnable()
	{
		instance = this;

		FlatFile.getInstance(); //Init FlatFile
		SaveFlatFile.getInstance(); //Init SaveFlatFile

		PackageManager.reload();
		Messages.reloadMessages();

		getServer().getPluginManager().registerEvents(new CommandListener(), this);
	}

	@Override
	public void onDisable()
	{
		SaveFlatFile.getInstance().save(); //Save all pending actions
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("apackages"))
		{
			CommandListener.getInstance().findCommand(sender, FlatFile.getInstance().getString("commands.admin.command"), args);
		}
		return true;
	}
}
