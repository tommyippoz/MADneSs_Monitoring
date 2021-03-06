/**
 * 
 */
package ippoz.reload.commons.knowledge.data;

import ippoz.reload.commons.datacategory.DataCategory;
import ippoz.reload.commons.indicator.Indicator;
import ippoz.reload.commons.layers.LayerType;
import ippoz.reload.commons.support.AppLogger;
import ippoz.reload.commons.support.AppUtility;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class Observation.
 * STores data related to observations of all the indicator at a given time timestamp.
 *
 * @author Tommy
 */
public class Observation {
	
	/** The timestamp. */
	private Date timestamp;
	
	/** The observed indicators. */
	private HashMap<Indicator, IndicatorData> observedIndicators;
	
	/**
	 * Instantiates a new observation.
	 *
	 * @param timestamp the timestamp
	 */
	public Observation(String timestamp){
		this.timestamp = AppUtility.convertStringToDate(timestamp);
		observedIndicators = new HashMap<Indicator, IndicatorData>();
	}
	
	/**
	 * Instantiates a new observation.
	 *
	 * @param timestamp the timestamp
	 */
	public Observation(long timestampMs){
		timestamp = new Date(timestampMs);
		observedIndicators = new HashMap<Indicator, IndicatorData>();
	}
	
	/**
	 * Adds the indicator.
	 *
	 * @param newInd the new indicator
	 * @param newValue the new value of the indicator
	 */
	public void addIndicator(Indicator newInd, IndicatorData newValue){
		observedIndicators.put(newInd, newValue);
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Gets the indicators of the current observation.
	 *
	 * @return the indicators
	 */
	public Indicator[] getIndicators(){
		return observedIndicators.keySet().toArray(new Indicator[observedIndicators.keySet().size()]);
	}

	/**
	 * Gets the value of an indicator for this specific observation.
	 *
	 * @param indicator the indicator
	 * @param categoryTag the data category (plain, diff)
	 * @return the indicator value
	 */
	public String getValue(Indicator indicator, DataCategory categoryTag) {
		return observedIndicators.get(indicator).getCategoryValue(categoryTag);
	}
	
	/**
	 * Gets the value of an indicator for this specific observation.
	 *
	 * @param indicator the indicator
	 * @param categoryTag the data category (plain, diff)
	 * @return the indicator value
	 */
	public String getValue(String indicatorName, DataCategory categoryTag) {
		for(Indicator ind : getIndicators()){
			if(ind.getName().equals(indicatorName.trim()))
				return getValue(ind, categoryTag);
		}
		AppLogger.logError(getClass(), "NoSuchIndicator", "Unable to find Indicator '" + indicatorName + "'");
		return null;
	}
	
	public boolean hasIndicator(String indicatorName, DataCategory categoryTag) {
		for(Indicator ind : getIndicators()){
			if(ind.getName().equals(indicatorName.trim()))
				return true;
		}
		return false;
	}
	
	/**
	 * Gets the number of observed indicators.
	 *
	 * @return the number of indicators
	 */
	public int getIndicatorNumber(){
		return observedIndicators.size();
	}
	
	public void addIndicatorData(String indName, String indData, DataCategory dataTag){
		if(indName != null && !hasIndicator(indName, dataTag)){
			observedIndicators.put(new Indicator(indName, LayerType.NO_LAYER, String.class), new IndicatorData(indData, dataTag));
		} else AppLogger.logError(getClass(), "ObservationUpdateError", "Unable to add indicator '" + indName + "'");
	}

}
