package net.shortninja.heightmapper.mapping;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object class used for calculating values after a complete heightmapper run.
 * 
 * 2 May 2016 at 4:32 PM
 * 
 * @author Shortninja
 */

public class MapResult
{
	private Set<Integer> heights = new HashSet<Integer>();
	private int highestHeight;
	private double meanHeight;
	private double medianHeight;
	private int modeHeight;
	private int lowestHeight;
	
	public MapResult(Set<Integer> heights)
	{
		this.heights = heights;
		this.highestHeight = Collections.max(heights);
		this.meanHeight = setMeanHeight();
		this.medianHeight = setMedianHeight();
		this.modeHeight = setModeHeight();
		this.lowestHeight = Collections.min(heights);
	}
	
	public int getHeights()
	{
		return heights.size();
	}
	
	public int getHighestHeight()
	{
		return highestHeight;
	}
	
	public double getMeanHeight()
	{
		return meanHeight;
	}
	
	public double getMedianHeight()
	{
		return medianHeight;
	}
	
	public int getModeHeight()
	{
		return modeHeight;
	}
	
	public int getLowestHeight()
	{
		return lowestHeight;
	}
	
	private double setMeanHeight()
	{
		int sumOfHeights = 0;
		
		for(int height : heights)
		{
			sumOfHeights += height;
		}
		
		return Double.parseDouble(new DecimalFormat("#.##").format(sumOfHeights / heights.size()));
	}
	
	private double setMedianHeight()
	{
		double median = 0;
		int middle = 0;
		List<Integer> heightsList = new ArrayList<Integer>();
		
		heightsList.addAll(heights);
		Collections.sort(heightsList);
		middle = (heightsList.size()) / 2;
		
		if(heightsList.size() % 2 == 0)
		{
			int highMedian = heightsList.get(middle);
			int lowMedian = heightsList.get(middle - 1);
			
			median = (highMedian + lowMedian) / 2D;
		}else median = heightsList.get(middle + 1);
		
		return Double.parseDouble(new DecimalFormat("#.##").format(median));
	}
	
	private int setModeHeight()
	{
		HashMap<Integer, Integer> frequents = new HashMap<Integer, Integer>();
		int mode = 0;
		int maxFrequent = 0;
		
		for(int val : heights)
		{
			Integer frequency = frequents.get(val);
			frequents.put(val, (frequency == null ? 1 : frequency + 1));
		}

		for(Map.Entry<Integer, Integer> entry : frequents.entrySet())
		{
			int frequency = entry.getValue();
			
			if(frequency > maxFrequent)
			{
				maxFrequent = frequency;
				mode = entry.getKey();
			}
		}

		return mode;
	}
}