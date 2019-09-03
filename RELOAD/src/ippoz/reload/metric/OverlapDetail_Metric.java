/**
 * 
 */
package ippoz.reload.metric;

import ippoz.reload.commons.support.TimedResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Tommy
 *
 */
public class OverlapDetail_Metric extends BetterMinMetric {

	public OverlapDetail_Metric(boolean validAfter) {
		super(MetricType.OVERLAP_DETAIL, validAfter);
	}

	@Override
	public double evaluateAnomalyResults(List<TimedResult> anomalyEvaluations) {
		List<Double> normalList = new LinkedList<>();
		List<Double> faultyList = new LinkedList<>();
		for(TimedResult tr : anomalyEvaluations){
			if(tr.getInjectedElement() != null)
				faultyList.add(tr.getAlgorithmScore());
			else normalList.add(tr.getAlgorithmScore());
		}
		return calculateOverlapDetail(normalList, faultyList);
	}

	@Override
	public String getMetricName() {
		return "OVERLAP_DETAIL";
	}

	@Override
	public String getMetricShortName() {
		return "OVERLAP_DETAIL";
	}
	
	public static double calculateOverlapDetail(List<Double> normalList, List<Double> faultyList){
		if(normalList.size() == 0 || faultyList.size() == 0)
			return 0.0;
		else {
			Collections.sort(normalList);
			Collections.sort(faultyList);
			boolean normalBeforeFaulty = normalList.get(0) < faultyList.get(0) ? true : false;
			boolean normalEmbedsFaulty = faultyList.get(0) >= normalList.get(0) && 
						faultyList.get(faultyList.size()-1) <= normalList.get(normalList.size()-1);
			boolean faultyEmbedsNormal = normalList.get(0) >= faultyList.get(0) && 
					normalList.get(normalList.size()-1) <= faultyList.get(faultyList.size()-1);
			if(normalBeforeFaulty)
				return overlapFirstBeforeSecond(normalList, faultyList);
			else return overlapFirstBeforeSecond(faultyList, normalList);
		}
	}
	
	private static Map<Double, Integer> toFrequencyMap(List<Double> list){
		Map<Double, Integer> map = new HashMap<>();
		if(list != null && list.size() > 0){
			for(Double d : list){
				if(!map.containsKey(d))
					map.put(d, 1);
				else map.put(d, map.get(d)+1);
			}
		}
		return map;
	}
	
	private static double overlapFirstBeforeSecond(List<Double> first, List<Double> second){
		int good = 0;
		int bad = 0;
		Map<Double, Integer> firstMap = toFrequencyMap(first);
		List<Double> sortedFirstKeys = new ArrayList<Double>(firstMap.keySet());
		Collections.sort(sortedFirstKeys);
		Map<Double, Integer> secondMap = toFrequencyMap(second);
		List<Double> sortedSecondKeys = new ArrayList<Double>(secondMap.keySet());
		Collections.sort(sortedSecondKeys);
		int i = 0, j = 0;
		while(i<sortedFirstKeys.size() && j < sortedSecondKeys.size()){
			while(sortedFirstKeys.get(i) == null){
				i++;
			}
			if(i >= sortedFirstKeys.size())
				break;
			while(sortedSecondKeys.get(j) == null){
				j++;
			}
			if(j >= sortedSecondKeys.size())
				break;
			if(sortedFirstKeys.get(i) > sortedSecondKeys.get(j)){
				good = good + secondMap.get(sortedSecondKeys.get(j));
				j++;
			} else if(sortedFirstKeys.get(i) < sortedSecondKeys.get(j)){
				good = good + firstMap.get(sortedFirstKeys.get(i));
				i++;
			} else if(firstMap.get(sortedFirstKeys.get(i)) > secondMap.get(sortedSecondKeys.get(j))){
				good = good + firstMap.get(sortedFirstKeys.get(i));
				bad = bad + secondMap.get(sortedSecondKeys.get(j));
				j++;
				i++;
			} else {
				bad = bad + firstMap.get(sortedFirstKeys.get(i));
				good = good + secondMap.get(sortedSecondKeys.get(j));
				j++;
				i++;
			}
		}
		while(i<sortedFirstKeys.size()){
			good = good + firstMap.get(sortedFirstKeys.get(i));
			i++;
		}
		while(j<sortedSecondKeys.size()){
			good = good + secondMap.get(sortedSecondKeys.get(j));
			j++;
		}
		
		return 100.0*bad / (good + bad);
	}

}