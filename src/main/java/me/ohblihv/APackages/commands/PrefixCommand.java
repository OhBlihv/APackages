package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.util.BUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chris on 4/21/2016.
 */
public class PrefixCommand extends ACommand
{

	private static final Pattern    MAGIC_CODE = Pattern.compile("[&|§]k"),
									COLOUR_CODE = Pattern.compile("[&|§][0-9A-Fa-f]");

	private final int   minLength,
						maxLength;

	private final String    prefixFormat,
							minLengthMessage,
							maxLengthMessage,
							syntaxMessage,
							magicCodeMessage,
							alphanumericCharactersMessage,
							disallowedWordsMessage,
							setPrefixMessage;

	private final boolean   allowMagicCode,
							allowNonAlphanumericCharacters;

	private final List<String> disallowedWords;

	private final Map<String, String> groupColours = new HashMap<>();

	public PrefixCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.minLength = configurationSection.getInt("options.length.min");
		this.maxLength = configurationSection.getInt("options.length.max");

		this.allowMagicCode = configurationSection.getBoolean("options.restrictions.allow-magic-code", false);
		this.allowNonAlphanumericCharacters = configurationSection.getBoolean("options.restrictions.allow-non-alphanumeric-characters", false);

		this.prefixFormat = BUtil.translateColours(configurationSection.getString("options.format"));
		this.minLengthMessage = BUtil.translateColours(configurationSection.getString("options.length.min-message"));
		this.maxLengthMessage = BUtil.translateColours(configurationSection.getString("options.length.max-message"));
		this.syntaxMessage = BUtil.translateColours(configurationSection.getString("options.syntax"));
		this.magicCodeMessage = BUtil.translateColours(configurationSection.getString("options.restrictions.magic-code-message"));
		this.alphanumericCharactersMessage = BUtil.translateColours(configurationSection.getString("options.restrictions.alphanumeric-charcters-message"));
		this.disallowedWordsMessage = BUtil.translateColours(configurationSection.getString("options.restrictions.disallowed-words-message"));
		this.setPrefixMessage = BUtil.translateColours(configurationSection.getString("options.set-prefix"));

		this.disallowedWords = configurationSection.getStringList("options.restrictions.disallowed-words");

		for(String permission : configurationSection.getConfigurationSection("options.group-colours").getKeys(false))
		{
			groupColours.put(permission, BUtil.translateColours(configurationSection.getString("options.group-colours." + permission, "§f")));
		}
	}

	@Override
	public boolean onCommand(Player player, String[] args)
	{
		if(args.length == 0)
		{
			player.sendMessage(syntaxMessage);
			return false;
		}

		String  proposedPrefix = args[0],
				strippedPrefix = BUtil.stripColours(proposedPrefix);

		if(minLength != -1 && strippedPrefix.length() < minLength)
		{
			player.sendMessage(minLengthMessage);
			return false;
		}

		if(maxLength != 0 && strippedPrefix.length() > maxLength)
		{
			player.sendMessage(maxLengthMessage);
			return false;
		}

		if(!allowMagicCode && MAGIC_CODE.matcher(proposedPrefix).find())
		{
			player.sendMessage(magicCodeMessage);
			return false;
		}

		if(!allowNonAlphanumericCharacters)
		{
			for(char character : proposedPrefix.toCharArray())
			{
				if(!Character.isLetterOrDigit(character) && character != '&')
				{
					//TODO: Use regex.
					player.sendMessage(alphanumericCharactersMessage);
					return false;
				}
			}
		}

		if(!disallowedWords.isEmpty())
		{
			for(String disallowedWord : disallowedWords)
			{
				if(strippedPrefix.contains(disallowedWord))
				{
					player.sendMessage(disallowedWordsMessage);
					return false;
				}
			}
		}

		proposedPrefix = BUtil.translateColours(proposedPrefix);

		PermissionUser permissionUser = PermissionsEx.getUser(player);

		Matcher matcher = COLOUR_CODE.matcher(proposedPrefix);
		int pass = 0;
		while(matcher.find())
		{
			if(++pass > 50)
			{
				break;
			}

			//                                             +2 to skip the first code                                        //Yuck
			proposedPrefix = proposedPrefix.substring(0, matcher.start() + (2 * pass)) + "§l" + proposedPrefix.substring(matcher.end() - 2 + (2 * pass), proposedPrefix.length());
		}

		if(pass == 0)
		{
			proposedPrefix = "§l" + proposedPrefix;
		}

		permissionUser.setPrefix(prefixFormat.replace("{prefix}", proposedPrefix).replace("{group-colour}", getGroupColour(permissionUser)), null);

		player.sendMessage(setPrefixMessage.replace("{prefix}", proposedPrefix));
		return true;
	}

	private String getGroupColour(PermissionUser permissionUser)
	{
		for(String groupName : permissionUser.getGroupNames())
		{
			if(groupColours.containsKey(groupName.toLowerCase()))
			{
				return groupColours.get(groupName.toLowerCase());
			}
		}

		return "§7";
	}

}
