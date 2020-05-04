/**
 * 
 */
package ippoz.reload.algorithm.meta;

import ippoz.reload.algorithm.DataSeriesNonSlidingAlgorithm;
import ippoz.reload.algorithm.DetectionAlgorithm;
import ippoz.reload.algorithm.configuration.BasicConfiguration;
import ippoz.reload.algorithm.result.AlgorithmResult;
import ippoz.reload.algorithm.type.BaseLearner;
import ippoz.reload.algorithm.type.MetaLearner;
import ippoz.reload.commons.algorithm.AlgorithmType;
import ippoz.reload.commons.dataseries.DataSeries;
import ippoz.reload.commons.knowledge.Knowledge;
import ippoz.reload.commons.support.AppLogger;
import ippoz.reload.decisionfunction.AnomalyResult;
import ippoz.reload.meta.MetaLearnerType;
import ippoz.reload.meta.MetaTrainer;
import ippoz.reload.trainer.AlgorithmTrainer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

/**
 * @author Tommy
 *
 */
public class StackingMetaLearner extends DataSeriesMetaLearner {
	
	public static final String BASE_LEARNERS = "BASE_LEARNERS";
	
	public static final String META_LEARNER = "META_LEARNER";
	
	public static final BaseLearner DEFAULT_META_LEARNER = new BaseLearner(AlgorithmType.ELKI_ODIN);
	
	private DataSeriesNonSlidingAlgorithm metaLearner;

	public StackingMetaLearner(DataSeries dataSeries, BasicConfiguration conf) {
		super(dataSeries, conf, MetaLearnerType.STACKING);
	}
	
	private BaseLearner[] getBaseLearners(){
		return ((MetaLearner)getLearnerType()).getBaseLearners();
	}

	@Override
	protected MetaTrainer trainMetaLearner(List<Knowledge> kList) {
		MetaTrainer mTrainer = new MetaTrainer(data, (MetaLearner)getLearnerType());
		try {
			for(BaseLearner base : getBaseLearners()){
				mTrainer.addTrainer(base, dataSeries, kList, true);
			}
			mTrainer.start();
			mTrainer.join();
			baseLearners = new LinkedList<>();
			for(AlgorithmTrainer at : mTrainer.getTrainers()){
				baseLearners.add((DataSeriesNonSlidingAlgorithm)DetectionAlgorithm.buildAlgorithm(at.getAlgType(), dataSeries, at.getBestConfiguration()));
			}
			
			// Train Meta-Learner
		} catch (InterruptedException e) {
			AppLogger.logException(getClass(), e, "Unable to complete Meta-Training for " + getLearnerType());
		}
		return mTrainer;
	}

	@Override
	public Pair<Double, Object> calculateSnapshotScore(double[] snapArray) {
		int i = 0;
		double[] scores = new double[baseLearners.size()];
		for(DataSeriesNonSlidingAlgorithm alg : baseLearners){
			double score = alg.calculateSnapshotScore(snapArray).getKey();
			scores[i++] = score;
		}
		return new Pair<Double, Object>(metaLearner.calculateSnapshotScore(scores).getKey(), scores);
	}

	@Override
	protected boolean checkCalculationCondition(double[] snapArray) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.reload.algorithm.DetectionAlgorithm#getDefaultParameterValues()
	 */
	@Override
	public Map<String, String[]> getDefaultParameterValues() {
		Map<String, String[]> defPar = new HashMap<String, String[]>();
		return defPar;
	}

}
