/**
 * 
 */
package ippoz.reload.commons.dataseries;

import ippoz.reload.commons.algorithm.AlgorithmType;
import ippoz.reload.commons.datacategory.DataCategory;
import ippoz.reload.commons.knowledge.data.Observation;
import ippoz.reload.commons.knowledge.snapshot.SnapshotValue;
import ippoz.reload.commons.layers.LayerType;
import ippoz.reload.commons.service.ServiceCall;
import ippoz.reload.commons.service.ServiceStat;
import ippoz.reload.commons.service.StatPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Tommy
 *
 */
public class MultipleDataSeries extends DataSeries {
	
	private List<DataSeries> dsList; 

	public MultipleDataSeries(List<? extends DataSeries> pList) {
		super(aggregateSeriesName(pList), DataCategory.PLAIN);
		dsList = new ArrayList<DataSeries>(pList.size());
		Collections.sort(pList);
		for(DataSeries is : pList){
			dsList.add(is);
		}
		if(dsList.size() == 0)
			dsList = null;
	}
	
	private static String aggregateSeriesName(List<? extends DataSeries> pList){
		String aggName = "";
		if(pList != null && pList.size() >= 2){
			Collections.sort(pList);
			for(DataSeries id : pList){
				aggName = aggName + id.toString() + "@";
			}
			return aggName.substring(0, aggName.length()-1);
		}	
		else return null;
	}

	public void add(IndicatorDataSeries is){
		dsList.add(is);
	}

	@Override
	public LayerType getLayerType() {
		return LayerType.COMPOSITION;
	}
	
	@Override
	public boolean compliesWith(AlgorithmType algType) {
		if(dsList != null && dsList.size() > 0){
			return true;
		} else return false;
	}

	@Override
	protected SnapshotValue getPlainSeriesValue(Observation obs) {
		return null;
	}

	@Override
	protected SnapshotValue getDiffSeriesValue(Observation obs) {
		return null;
	}

	@Override
	public StatPair getSeriesServiceStat(Date timestamp, ServiceCall sCall, ServiceStat sStat) {
		return null;
	}

	public DataSeries[] getSeriesArray() {
		return dsList.toArray(new DataSeries[dsList.size()]);
	}
	
	public List<DataSeries> getSeriesList() {
		return dsList;
	}

	public String getSeriesString() {
		String string = "";
		for(DataSeries ds : dsList){
			string = string + ds.toString() + ";";
		}
		return string.substring(0, string.length()-1);
	}

	@Override
	public int size() {
		return dsList.size();
	}

	public DataSeries getSeries(int j) {
		return dsList.get(j);
	}

	@Override
	public String toCompactString() {
		String string = "";
		if(dsList!= null && dsList.size() > 0){
			for(DataSeries ds : dsList){
				string = string + ds.getName() + ds.getDataCategory().name().substring(0, 1) + ";";
			}
			return string.substring(0, string.length()-1);
		} else return "";
	}
	
	

}
