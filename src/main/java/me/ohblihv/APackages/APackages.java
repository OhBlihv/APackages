package me.ohblihv.APackages;

import lombok.Getter;
import me.ohblihv.APackages.util.Messages;
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

		PackageManager.reload();
		Messages.reloadMessages();

		getServer().getPluginManager().registerEvents(new CommandListener(), this);
	}

	@Override
	public void onDisable()
	{

	}
	
}
