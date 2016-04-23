package me.ohblihv.APackages.commands;

import me.ohblihv.APackages.util.BUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Chris on 4/21/2016.
 */
public class LoreCommand extends ACommand
{

	private final boolean restrictToItems;

	private final String    syntaxMessage,
							editedMessage,
							strippedEditedMessage,
							itemRestrictionMessage,
							setLoreMessage,
							setDisplaynameMessage,
							removedLoreMessage,
							removedDisplaynameMessage;

	private final List<String> helpMessages;
	
	public LoreCommand(ConfigurationSection configurationSection)
	{
		super(configurationSection);

		this.restrictToItems = configurationSection.getBoolean("options.restrictions.restrict-to-items", true);
		this.itemRestrictionMessage = BUtil.translateColours(configurationSection.getString("options.restrictions.item-restriction-message", MISSING_STRING));

		this.syntaxMessage = BUtil.translateColours(configurationSection.getString("options.syntax", MISSING_STRING));
		this.editedMessage = BUtil.translateColours(configurationSection.getString("options.edited-message"));
		this.setLoreMessage = BUtil.translateColours(configurationSection.getString("options.set-lore", MISSING_STRING));
		this.setDisplaynameMessage = BUtil.translateColours(configurationSection.getString("options.set-displayname", MISSING_STRING));
		this.removedLoreMessage = BUtil.translateColours(configurationSection.getString("options.removed-lore", MISSING_STRING));
		this.removedDisplaynameMessage = BUtil.translateColours(configurationSection.getString("options.removed-displayname", MISSING_STRING));

		this.helpMessages = BUtil.translateColours(configurationSection.getStringList("options.help"));

		//Strip the player variable and all characters after that
		this.strippedEditedMessage = Pattern.compile("\\{player\\}.*").matcher(editedMessage).replaceAll("");
	}

	@Override
	public boolean onCommand(Player player, String[] args)
	{
		if(args.length == 0)
		{
			player.sendMessage(syntaxMessage);
			return false;
		}

		if(args.length == 1 && args[0].equalsIgnoreCase("help"))
		{
			for(String line : helpMessages)
			{
				player.sendMessage(line);
			}
			return false;
		}

		ItemStack itemStack = player.getItemInHand();
		Material material = itemStack.getType();
		if(restrictToItems && (material.isBlock() || material.getMaxDurability() < 1))
		{
			player.sendMessage(itemRestrictionMessage);
			return false;
		}

		ItemMeta itemMeta = itemStack.getItemMeta();

		String subCommand = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);

		switch(subCommand)
		{
			case "name":
			{
				StringBuilder displayName = new StringBuilder();
				for(String arg : args)
				{
					displayName.append(arg).append(" ");
				}

				itemMeta.setDisplayName(displayName.toString().substring(0, displayName.length() - 1));

				/*if(editedMessage != null && !editedMessage.isEmpty())
				{
					addEditedMessage(itemMeta, player);
				}*/

				player.sendMessage(setDisplaynameMessage);
				break;
			}
			case "lore":
			{
				List<String> lore = new ArrayList<>();
				StringBuilder currentLine = new StringBuilder();
				for(String word : args)
				{
					if(!word.contains("|"))
					{
						currentLine.append(word).append(" ");
					}
					else
					{
						String[] splitWords = word.split("[|]");

						int wordNum = 0;
						for(String splitWord : splitWords)
						{
							if(wordNum++ != splitWords.length)
							{
								//Append the word, add the line to the lore and flush the current line
								currentLine.append(splitWord);
								lore.add(currentLine.toString());
								currentLine = new StringBuilder();
							}
							else
							{
								currentLine.append(word).append(" ");
							}
						}
					}
				}
				lore.add(currentLine.toString());

				itemMeta.setLore(BUtil.translateColours(lore));

				/*if(editedMessage != null && !editedMessage.isEmpty())
				{
					addEditedMessage(itemMeta, player);
				}*/

				player.sendMessage(setLoreMessage);
				break;
			}
			case "removename":
			{
				itemMeta.setDisplayName(null);
				player.sendMessage(removedDisplaynameMessage);
				break;
			}
			case "removelore":
			{
				itemMeta.setLore(null);
				player.sendMessage(removedLoreMessage);
				break;
			}
		}

		itemStack.setItemMeta(itemMeta);
		return true;
	}

	/*private void addEditedMessage(ItemMeta itemMeta, Player player)
	{
		List<String> lore = itemMeta.getLore();
		if(lore == null || lore.isEmpty() || lore.size() == 1)
		{
			lore = new ArrayList<>();
			lore.add(editedMessage.replace("{player}", player.getName()));
		}
		else //We have lines Houston!
		{
			int lineNum = 1; //Start this on 1 so we can easily decide if the pattern was not found

			for(String line : lore)
			{
				if(line.startsWith(strippedEditedMessage))
				{
					break;
				}
				lineNum++;
			}

			//Remove the old 'edited by' line
			if(lineNum < lore.size())
			{
				lore.remove(lineNum);
			}

			lore.add(editedMessage.replace("{player}", player.getName()));
		}

		itemMeta.setLore(lore);
	}*/

}
