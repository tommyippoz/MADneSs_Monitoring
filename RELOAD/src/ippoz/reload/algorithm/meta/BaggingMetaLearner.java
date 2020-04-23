/**
 * 
 */
package ippoz.reload.algorithm.meta;

import ippoz.reload.algorithm.DataSeriesNonSlidingAlgorithm;
import ippoz.reload.algorithm.DetectionAlgorithm;
import ippoz.reload.algorithm.configuration.BasicConfiguration;
import ippoz.reload.commons.dataseries.DataSeries;
import ippoz.reload.commons.knowledge.Knowledge;
import ippoz.reload.commons.support.AppLogger;
import ippoz.reload.meta.MetaData;
import ippoz.reload.meta.MetaLearnerType;
import ippoz.reload.meta.MetaTrainer;
import ippoz.reload.trainer.AlgorithmTrainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

/**
 * @author Tommy
 *
 */
public class BaggingMetaLearner extends DataSeriesMetaLearner {
	
	public static final String N_SAMPLES = "SAMPLES_NUMBER";
	
	public static final int DEFAULT_SAMPLES = 10;

	public BaggingMetaLearner(DataSeries dataSeries, BasicConfiguration conf, MetaData data) {
		super(dataSeries, conf, MetaLearnerType.BAGGING, data);
	}

	@Override
	protected void trainMetaLearner(List<Knowledge> kList) {
		List<List<Knowledge>> sampledKnowledge = null;
		MetaTrainer mTrainer = new MetaTrainer(data);
		try {
			sampledKnowledge = baggingOf(kList, getSamplesNumber());
			for(List<Knowledge> sKnow : sampledKnowledge){
				mTrainer.addTrainer(getMetaType(), getLearnerType(), dataSeries, sKnow);
			}
			mTrainer.start();
			mTrainer.join();
			baseLearners = new LinkedList<>();
			for(AlgorithmTrainer at : mTrainer.getTrainers()){
				baseLearners.add((DataSeriesNonSlidingAlgorithm)DetectionAlgorithm.buildBaseAlgorithm(getLearnerType(), dataSeries, at.getBestConfiguration()));
			}
		} catch (InterruptedException e) {
			AppLogger.logException(getClass(), e, "Unable to complete Meta-Training for " + getLearnerType());
		}
	}

	private List<List<Knowledge>> baggingOf(List<Knowledge> kList, int samplesNumber) {
		List<List<Knowledge>> outList = new ArrayList<>(samplesNumber);
		for(int i=0;i<samplesNumber;i++){
			List<Knowledge> sList = new ArrayList<>(kList.size());
			for(Knowledge know : kList){
				sList.add(know.sample(200.0/samplesNumber));
			}
			outList.add(sList);
		}
		return outList;
	}

	@Override
	public Pair<Double, Object> calculateSnapshotScore(double[] snapArray) {
		int count = 0;
		double sum = 0;
		for(DataSeriesNonSlidingAlgorithm alg : baseLearners){
			double score = alg.calculateSnapshotScore(snapArray).getKey();
			if(Double.isFinite(score))
				sum = sum + score;
		}
		if(count > 0)
			return new Pair<Double, Object>(sum / count, null);
		else return new Pair<Double, Object>(0.0, null);
	}

	@Override
	protected boolean checkCalculationCondition(double[] snapArray) {
		return true;
	}
	
	@Override
	protected void loadFile(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printFile(File file) {
		// TODO Auto-generated method stub
		
	}
	
	private int getSamplesNumber(){
		return conf != null && conf.hasItem(N_SAMPLES) ? Integer.parseInt(conf.getItem(N_SAMPLES)) : DEFAULT_SAMPLES;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.reload.algorithm.DetectionAlgorithm#getDefaultParameterValues()
	 */
	@Override
	public Map<String, String[]> getDefaultParameterValues() {
		Map<String, String[]> defPar = new HashMap<String, String[]>();
		defPar.put(N_SAMPLES, new String[]{"10"});
		return defPar;
	}

}