package net.shortninja.heightmapper;

import java.text.DecimalFormat;

import net.shortninja.heightmapper.mapping.MapFactory;
import net.shortninja.heightmapper.mapping.MapResult;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Main class for HeightMap plugin.
 * 
 * 1 May 2016 at 4:41 AM
 * 
 * @author Shortninja
 */

//TOprobablyneverDO: Add option for ignoring certain blocks or WorldGuard regions.

public class HeightMapper extends JavaPlugin
{
	private static HeightMapper plugin;
	private static FileConfiguration config;
	public Message message;
	public int interval;
	
	public MapFactory mapFactory;
	private String permission;
	private boolean shouldShowUpdates;
	private long seed;
	private int iterations;
	private int maxX;
	private int minX;
	private int maxZ;
	private int minZ;
	
	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		plugin = this;
		config = getConfig();
		message = new Message();
		
		start();
	}
	
	@Override
	public void onDisable()
	{
		message.sendConsoleMessage("HeightMapper has been disabled!", true);
		
		plugin = null;
		config = null;
		message = null;
	}
	
	public static HeightMapper get()
	{
		return plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(label.equalsIgnoreCase("heightmap"))
		{
			World world = null;
			long seed = this.seed;
			int iterations = this.iterations;
			int maxX = this.maxX;
			int minX = this.minX;
			int maxZ = this.maxZ;
			int minZ = this.minZ;
			
			if(!sender.hasPermission(permission))
			{
				message.sendMessage(sender, "&cYou do not have permission to use this!", true);
				return true;
			}else if(args.length == 0)
			{
				message.sendMessage(sender, "&cInvalid arguments! Use &7/heightmap [world] {seed} {iterations} {max-x} {min-x} {max-z} {min-z}&c!", true);
				return true;
			}
			
			/*
			 * I'm cringing as much as you, trust me. Tell me if you find a better way.
			 */
			if(args.length >= 1)
			{
				String argument = args[0];
				
				if(argument.equalsIgnoreCase("stop"))
				{
					mapFactory.stop();
					message.sendMessage(sender, "&bHeightmapping for &7" + mapFactory.getWorldName() + " &bhas been stopped!", true);
					return true;
				}else world = Bukkit.getWorld(args[0]);
			}else if(args.length >= 2)
			{
				seed = Long.parseLong(args[1]);
			}else if(args.length >= 3)
			{
				iterations = Integer.parseInt(args[2]);
			}else if(args.length >= 4)
			{
				maxX = Integer.parseInt(args[3]);
			}else if(args.length >= 5)
			{
				minX = Integer.parseInt(args[4]);
			}else if(args.length >= 6)
			{
				maxZ = Integer.parseInt(args[5]);
			}else if(args.length >= 7)
			{
				minZ = Integer.parseInt(args[6]);
			}
			
			message.sendMessage(sender, "&bHeightmapping is now beginning for &7" + world.getName() + "&b!", true);
			mapTask(sender, world, seed, iterations, maxX, minX, maxZ, minZ);
			return true;
		}
		
		return false;
	}
	
	private void mapTask(final CommandSender sender, final World world, final long seed, final int iterations, final int maxX, final int minX, final int maxZ, final int minZ)
	{
		mapFactory = new MapFactory(world, seed, iterations, maxX, minX, maxZ, minZ);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double percentage = mapFactory.getProgress();;
				DecimalFormat decimalFormat = new DecimalFormat("#.#");
				
				if(((int) (mapFactory.getProgress() + 0.5)) >= 100 || !mapFactory.shouldContinue())
				{
					MapResult mapResult = mapFactory.getResult();
					
					mapFactory.stop();
					message.sendMessage(sender, message.colorize(message.LONG_LINE), false);
					message.sendMessage(sender, "&bHeight mapping for &7" + mapFactory.getWorldName() + " &bis complete! Here are the results:", true);
					message.sendMessage(sender, "&bTotal different heights: &7" + mapResult.getHeights(), true);
					message.sendMessage(sender, "&bHighest height: &7" + mapResult.getHighestHeight(), true);
					message.sendMessage(sender, "&bMean height: &7" + mapResult.getMeanHeight(), true);
					message.sendMessage(sender, "&bMedian height: &7" + mapResult.getMedianHeight(), true);
					message.sendMessage(sender, "&bMode height: &7" + mapResult.getModeHeight(), true);
					message.sendMessage(sender, "&bLowest height: &7" + mapResult.getLowestHeight(), true);
					message.sendMessage(sender, message.colorize(message.LONG_LINE), false);
					this.cancel();
				}else if(shouldShowUpdates)
				{
					message.sendMessage(sender, "&bHeight mapping for &7" + mapFactory.getWorldName() + " &bis about &7%" + decimalFormat.format(percentage) + " &bcomplete!", true);
				}
			}
		}.runTaskTimer(this, 200L, 200L);
	}
	
	private void start()
	{
		permission = config.getString("permission");
		shouldShowUpdates = config.getBoolean("show-updates");
		seed = config.getLong("seed");
		iterations = config.getInt("iterations");
		maxX = config.getInt("max-x");
		minX = config.getInt("min-x");
		maxZ = config.getInt("max-z");
		minZ = config.getInt("min-Z");
		interval = config.getInt("interval");
	}
}