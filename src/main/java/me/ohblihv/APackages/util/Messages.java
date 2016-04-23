package me.ohblihv.APackages.util;

import java.util.List;

/**
 * Created by Chris on 4/20/2016.
 */
public class Messages
{

	public static String    CMD_TIMELEFT_FORMAT,
							CMD_TIMELEFT_FORMAT_DAYS,
							CMD_TIMELEFT_FORMAT_HOURS,
							CMD_TIMELEFT_FORMAT_MINUTES,
							CMD_TIMELEFT_FORMAT_SECONDS,
							CMD_TIMELEFT_FORMAT_EXPIRING;

	public static void reloadMessages()
	{
		cfg = FlatFile.getInstance();

		CMD_TIMELEFT_FORMAT = loadString("commands.timeleft.options.timeleft.format");
		CMD_TIMELEFT_FORMAT_DAYS = loadString("commands.timeleft.options.timeleft.days");
		CMD_TIMELEFT_FORMAT_HOURS = loadString("commands.timeleft.options.timeleft.hours");
		CMD_TIMELEFT_FORMAT_MINUTES = loadString("commands.timeleft.options.timeleft.minutes");
		CMD_TIMELEFT_FORMAT_SECONDS = loadString("commands.timeleft.options.timeleft.seconds");
		CMD_TIMELEFT_FORMAT_EXPIRING = loadString("commands.timeleft.options.timeleft.expiring-soon");
	}

	private static FlatFile cfg;

	private static String loadString(String path)
	{
		return BUtil.translateColours(cfg.getString(path));
	}

	private static List<String> loadStringList(String path)
	{
		return BUtil.translateColours(cfg.getStringList(path));
	}

}
