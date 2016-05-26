package net.shortninja.heightmapper.mapping;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.shortninja.heightmapper.HeightMapper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Class that will take care each iteration during a heightmap task.
 * 
 * 1 May 2016 at 5:02 AM
 * 
 * @author Shortninja
 */

public class MapFactory
{
	private Set<Location> usedLocations = new HashSet<Location>();
	private Set<Integer> heights = new HashSet<Integer>();
	private double progressPercentage;
	private double progressIncrement;
	private World world;
	private long seed;
	private int iterations;
	private int maxX;
	private int minX;
	private int maxZ;
	private int minZ;
	private boolean shouldContinue = true;
	private long interval = HeightMapper.get().interval;
	
	public MapFactory(World world, long seed, int iterations, int maxX, int minX, int maxZ, int minZ)
	{
		this.progressPercentage = 0;
		this.progressIncrement = 100.0 / iterations;
		this.world = world;
		this.seed = seed;
		this.iterations = iterations;
		this.maxX = maxX;
		this.minX = minX;
		this.maxZ = maxZ;
		this.minZ = minZ;
		
		mapHeights();
	}
	
	public double getProgress()
	{
		return progressPercentage;
	}
	
	public MapResult getResult()
	{
		return new MapResult(heights);
	}
	
	public String getWorldName()
	{
		return world.getName();
	}
	
	public boolean shouldContinue()
	{
		return shouldContinue;
	}
	
	public void stop()
	{
		shouldContinue = false;
	}
	
	private void mapHeights()
	{
		new BukkitRunnable()
		{
			Random random = new Random(seed);
			Location currentLocation;
			int count = 1;
			
			@Override
			public void run()
			{
				if(count < iterations && shouldContinue)
				{
					currentLocation = new Location(world, random.nextInt(maxX + Math.abs(minX)) + minX, 1, random.nextInt(maxZ + Math.abs(minZ)) + minZ);
					
					if(!usedLocations.contains(currentLocation))
					{
						heights.add(getGround(currentLocation));
						progressPercentage += progressIncrement;
						count++;
					}
					
					usedLocations.add(currentLocation);
				}else this.cancel();
			}
		}.runTaskTimer(HeightMapper.get(), interval, interval);
	}
	
	/*
	 * Midair collisions with blocks may occur, which can cause false ground levels!
	 */
	private int getGround(Location location)
	{
		Location groundLocation = null;
		
		location.setY(255);
		
		for(int i = 255; i > 0; i--)
		{
			groundLocation = new Location(location.getWorld(), location.getBlockX(), i, location.getBlockZ());
			
			if(groundLocation.getBlock().getType() != Material.AIR)
			{
				groundLocation = new Location(location.getWorld(), location.getBlockX(), i + 1, location.getBlockZ());
				break;
			}
		}
		
		return groundLocation.getBlockY();
	}
}