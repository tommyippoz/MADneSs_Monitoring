/**
 * 
 */
package ippoz.reload.manager;

import ippoz.madness.commons.datacategory.DataCategory;
import ippoz.reload.algorithm.DetectionAlgorithm;
import ippoz.reload.commons.algorithm.AlgorithmType;
import ippoz.reload.commons.configuration.AlgorithmConfiguration;
import ippoz.reload.commons.dataseries.DataSeries;
import ippoz.reload.commons.knowledge.Knowledge;
import ippoz.reload.commons.knowledge.KnowledgeType;
import ippoz.reload.commons.support.AppLogger;
import ippoz.reload.metric.Metric;
import ippoz.reload.reputation.Reputation;
import ippoz.reload.trainer.AlgorithmTrainer;
import ippoz.reload.trainer.ConfigurationSelectorTrainer;
import ippoz.reload.trainer.FixedConfigurationTrainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Tommy
 *
 */
public class FilterManager extends TrainDataManager {
	
	/** The filtering threshold. */
	private double filteringThreshold;
	
	/**
	 * Instantiates a new filter manager.
	 *
	 */
	public FilterManager(String setupFolder, String dsDomain, String scoresFolder, Map<KnowledgeType, List<Knowledge>> expList, Map<AlgorithmType, List<AlgorithmConfiguration>> confList, Metric metric, Reputation reputation, DataCategory[] dataTypes, double filteringThreshold, double pearsonSimple, double pearsonComplex, int kfold) {
		super(expList, setupFolder, dsDomain, scoresFolder, confList, metric, reputation, dataTypes, defaultFilterAlgorithm(), pearsonSimple, pearsonComplex, true, kfold);
		this.filteringThreshold = filteringThreshold;
	}

	private static List<AlgorithmType> defaultFilterAlgorithm() {
		List<AlgorithmType> list = new LinkedList<AlgorithmType>();
		list.add(AlgorithmType.ELKI_KMEANS);
		return list;
	}

	/**
	 * Starts the train process. 
	 * The scores are saved in a file specified in the preferences.
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<DataSeries> filter(String outFilename){
		LinkedList<DataSeries> filteredSeries = null;
		long start = System.currentTimeMillis();
		try {
			start();
			join();
			Collections.sort((LinkedList<AlgorithmTrainer>)getThreadList());
			AppLogger.logInfo(getClass(), "Filtering executed in " + (System.currentTimeMillis() - start) + "ms");
			filteredSeries = selectDataSeries((LinkedList<AlgorithmTrainer>)getThreadList());
			saveFilteredSeries(filteredSeries, outFilename);
			AppLogger.logInfo(getClass(), "Filtered Checkers Saved");
		} catch (InterruptedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to complete training phase");
		}
		return filteredSeries;
	}
	
	private LinkedList<DataSeries> selectDataSeries(LinkedList<AlgorithmTrainer> atList) {
		LinkedList<DataSeries> result = new LinkedList<DataSeries>();
		for(AlgorithmTrainer at : atList){
			if(at.getMetricAvgScore() <= filteringThreshold){
				if(!result.contains(at.getDataSeries()))
					result.add(at.getDataSeries());
			}
		}
		AppLogger.logInfo(getClass(), "Filtered Data Series are " + result.size() + " out of the possible " + seriesList.size());
		return result;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#initRun()
	 */
	@Override
	protected void initRun(){
		AppLogger.logInfo(getClass(), "Train Started");
		LinkedList<AlgorithmTrainer> trainerList = new LinkedList<AlgorithmTrainer>();
		for(AlgorithmType algType : algTypes){
			if(confList.get(algType) != null){
				KnowledgeType kType = DetectionAlgorithm.getKnowledgeType(algType);
				switch(algType){
					case RCC:
						trainerList.add(new FixedConfigurationTrainer(algType, null, getMetric(), getReputation(), getKnowledge(kType), confList.get(algType).get(0)));
						break;
					case PEA:
						PearsonCombinationManager pcManager;
						File pearsonFile = new File(getSetupFolder() + "pearsonCombinations.csv");
						pcManager = new PearsonCombinationManager(pearsonFile, seriesList, getKnowledge(kType), kfold);
						pcManager.calculatePearsonIndexes(0.9, 0.9);
						trainerList.addAll(pcManager.getTrainers(getMetric(), getReputation(), confList));
						pcManager.flush();
						break;
					default:
						for(DataSeries dataSeries : seriesList){
							if(dataSeries.compliesWith(algType))
								trainerList.add(new ConfigurationSelectorTrainer(algType, dataSeries, getMetric(), getReputation(), getKnowledge(kType), confList.get(algType), kfold));
						}
						break;
				}
			} else AppLogger.logError(getClass(), "UnrecognizedConfiguration", algType + " does not have an associated configuration");	
		}
		setThreadList(trainerList);
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadStart(java.lang.Thread, int)
	 */
	@Override
	protected void threadStart(Thread t, int tIndex) {
		// Empty
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadComplete(java.lang.Thread, int)
	 */
	@Override
	protected void threadComplete(Thread t, int tIndex) {
		AppLogger.logInfo(getClass(), "[" + tIndex + "/" + threadNumber() + "] Found: " + ((AlgorithmTrainer)t).getBestConfiguration().toString());		
		((AlgorithmTrainer)t).flush();
	}
	
	/**
	 * Saves scores related to the executed AlgorithmTrainers.
	 *
	 * @param list the list of algorithm trainers
	 */
	private void saveFilteredSeries(LinkedList<DataSeries> list, String filename) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(getScoresFolder() + filename)));
			writer.write("data_series,algorithm_type,reputation_score,metric_score(" + getMetric().getMetricName() + "),configuration\n");
			for(DataSeries ds : list){
				writer.write(ds.toString() + "\n");			
			}
			writer.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to write series");
		}
	}
	
}