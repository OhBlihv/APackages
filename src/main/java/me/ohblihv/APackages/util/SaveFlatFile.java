package me.ohblihv.APackages.util;

import lombok.Getter;
import me.ohblihv.APackages.APackages;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Chris Brown (OhBlihv) on 5/9/2016.
 */
public class SaveFlatFile extends FlatFile
{

	private static SaveFlatFile instance = null;
	public static SaveFlatFile getInstance()
	{
		if(instance == null)
		{
			instance = new SaveFlatFile();
		}
		return instance;
	}

	@Getter
	private static ConcurrentHashMap<String, Map<UUID, Long>> saveMap = new ConcurrentHashMap<>();

	private SaveFlatFile()
	{
		super("save.yml");

		load();

		Bukkit.getScheduler().runTaskTimerAsynchronously(APackages.getInstance(), this::save, 36000L, 36000L);
	}

	public void load()
	{
		saveMap.clear();

		for(String subCommand : save.getKeys(false))
		{
			Map<UUID, Long> subCommandSave = new HashMap<>();
			for(String serialisedEntry : save.getStringList(subCommand))
			{
				String[] serialisedSplit = serialisedEntry.split("~");

				subCommandSave.put(BUtil.deCompressUUID(serialisedSplit[0]), Long.parseLong(serialisedSplit[1]));
			}

			saveMap.put(subCommand, subCommandSave);
		}
	}

	public void save()
	{
		for(Map.Entry<String, Map<UUID, Long>> saveEntry : saveMap.entrySet())
		{
			List<String> saveList = new ArrayList<>();
			for(Map.Entry<UUID, Long> entry : saveEntry.getValue().entrySet())
			{
				saveList.add(BUtil.compressUUID(entry.getKey()) + "~" + entry.getValue());
			}

			save.set(saveEntry.getKey(), saveList);
		}

		saveToFile();
	}

}
