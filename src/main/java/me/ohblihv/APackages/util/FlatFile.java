package me.ohblihv.APackages.util;

import me.ohblihv.APackages.APackages;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

public class FlatFile
{

	protected File saveFile = null;
	protected FileConfiguration save = null;

	volatile static FlatFile instance = null;

	static APackages plugin = null;
	
	static String fileName = "config.yml";
	
	public static FlatFile getInstance()
	{
		if(instance == null)
		{
			instance = new FlatFile();
		}
		return instance;
	}
	
	protected FlatFile()
	{
		plugin = APackages.getInstance();
		saveDefaultConfig();
		getSave();
	}

	public FileConfiguration getSave() 
	{
	    if (save == null)
	    {
	        reloadFile();
	    }
	    return save;
	}
	
	public void reloadFile() 
	{
	    if (saveFile == null) 
	    {
	    	saveFile = new File(plugin.getDataFolder(), fileName);
	    }
	    save = YamlConfiguration.loadConfiguration(saveFile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = null;
		try 
		{
			defConfigStream = new InputStreamReader(plugin.getResource(fileName), "UTF8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	    if (defConfigStream != null)
	    {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        save.setDefaults(defConfig);
	    }
	}
	
	public void saveDefaultConfig() 
	{
	    if (saveFile == null)
	    {
	        saveFile = new File(plugin.getDataFolder(), fileName);
	    }
	    if (!saveFile.exists())
	    {            
	    	plugin.saveResource(fileName, false);
	    }
	}
	
	public void saveToFile() 
	{
	    if (save == null || saveFile == null) 
	    {
	        return;
	    }
	    try
	    {
	        getSave().save(saveFile);
	    }
	    catch (IOException ex) 
	    {
	    	BUtil.logError("Could not save config to " + saveFile);
	    }
	}
	
	public void removeEntry(String path)
	{
		this.getSave().set(path, null);
	}
	
	public void saveEntry(String path, String entry)
	{
		this.getSave().set(path, entry);
		saveToFile();
	}

	public String loadEntry(String path)
	{
		return this.save.getString(path);
	}
	
	public Set<String> getChildren(String path)
	{
		return this.save.getConfigurationSection(path).getKeys(false);
	}
	
	public String getString(String path)
	{
		return this.save.getString(path, "");
	}

	public String getString(String path, String def)
	{
		return this.save.getString(path, def);
	}
	
	public List<String> getStringList(String path)
	{
		return this.save.getStringList(path);
	}
	
	public int getInt(String path)
	{
		return this.save.getInt(path);
	}
	
	public boolean getBoolean(String path)
	{
		return this.save.getBoolean(path);
	}
	
	public long getLong(String path)
	{
		return this.save.getLong(path);
	}
	
	public void saveValue(String path, Object value)
	{
		this.save.set(path, value);
		saveToFile();
	}

	public double getDouble(String path)
	{
		return this.save.getDouble(path);
	}
	
	public float getFloat(String path)
	{
		return (float) this.save.getDouble(path);
	}

	public ConfigurationSection getConfigurationSection(String path)
	{
		return save.getConfigurationSection(path);
	}

}
