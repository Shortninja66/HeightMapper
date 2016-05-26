package net.shortninja.heightmapper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class that handles all of the message formatting and sending.
 * 
 * May 1 2016 at 4:13 AM
 * 
 * @author Shortninja
 */

public class Message
{
	public final String LONG_LINE = "&7&m------------------------------------------------";
	private final String PREFIX = "&7[&dHeight&5Mapper&7] ";
	
	public Message()
	{
		sendConsoleMessage("HeightMapper has been enabled!", false);
		sendConsoleMessage("Plugin created by Shortninja.", false);
	}
	
	public String colorize(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
	
	public String format(String message)
	{
		return colorize(PREFIX + message);
	}
	
	public void sendMessage(Player player, String message, boolean prefix)
	{
		if(player == null)
		{
			return;
		}
		
		if(prefix)
		{
			player.sendMessage(format(message));
		}else player.sendMessage(message);
	}
	
	public void sendMessage(CommandSender sender, String message, boolean prefix)
	{
		if(sender == null)
		{
			return;
		}
		
		if(prefix)
		{
			sender.sendMessage(format(message));
		}else sender.sendMessage(message);
	}
	
	public void sendConsoleMessage(String message, boolean isError)
	{
		String prefix = isError ? "&4[HeightMap] &c" : "&2[HeightMap] &a";
		
		Bukkit.getServer().getConsoleSender().sendMessage(colorize(prefix + message));
	}
	
	public String build(String[] args, int index)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int i = index; i < args.length; i++)
		{
			builder.append(args[i] + " ");
		}
		
		return builder.toString().trim();
	}
}