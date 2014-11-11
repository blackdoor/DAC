package blackdoor.util;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

	public static double mean(Number[] population){
		return sum(population)/population.length;
	}
	
	public static double stdDev(Number[] population){
		double mean = mean(population);
		Double[] diffs = new Double[population.length];
		for(int i = 0; i < population.length; i++){
			diffs[i] = Math.pow((population[i].doubleValue() - mean), 2);
		}
		return Math.sqrt(mean(diffs));
	}
	
	public static double sum(Number[] population){
		double sum = 0;
		for(Number point : population){
			sum += point.doubleValue();
		}
		return sum;
	}
	
	public static Number[] discardOutliers(Number[] population, int stdDevs){
		double stdDev = stdDev(population);
		double mean = mean(population);
		List<Number> reduced = new ArrayList<Number>();
		for(Number point : population){
			if(point.doubleValue() < mean + (stdDev * stdDevs) && point.doubleValue() > mean - (stdDev * stdDevs)){
				reduced.add(point);
			}
		}
		return reduced.toArray(new Double[1]);
	}
}
