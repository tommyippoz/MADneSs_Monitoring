/**
 * 
 */
package ippoz.reload.decisionfunction;

import ippoz.reload.algorithm.result.AlgorithmResult;

/**
 * The Class StaticThresholdGreaterThanDecision. Data point is anomalous if value > threshold.
 *
 * @author Tommy
 */
public class StaticThresholdGreaterThanDecision extends DecisionFunction {

	/** The threshold. */
	private double threshold;
	
	/**
	 * Instantiates a new static threshold greater than decision.
	 *
	 * @param threshold the threshold
	 */
	public StaticThresholdGreaterThanDecision(double threshold) {
		super("StaticGreaterThanClassifier", DecisionFunctionType.STATIC_THRESHOLD_GREATERTHAN);
		this.threshold = threshold;
	}

	/* (non-Javadoc)
	 * @see ippoz.reload.decisionfunction.DecisionFunction#classify(ippoz.reload.algorithm.result.AlgorithmResult)
	 */
	@Override
	protected AnomalyResult classify(AlgorithmResult value) {
		if(value.getScore() < threshold)
			return AnomalyResult.NORMAL;
		else return AnomalyResult.ANOMALY;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.reload.decisionfunction.DecisionFunction#toCompactString()
	 */
	@Override
	public String toCompactString() {
		return "SGTHR(" + threshold + ") -  {ANOMALY: value > " + threshold + "}";
	}

	/* (non-Javadoc)
	 * @see ippoz.reload.decisionfunction.DecisionFunction#getClassifierTag()
	 */
	@Override
	public String getClassifierTag() {
		return "STATIC_THRESHOLD_GREATERTHAN(" + threshold + ")";
	}

	/* (non-Javadoc)
	 * @see ippoz.reload.decisionfunction.DecisionFunction#getThresholds()
	 */
	@Override
	public double[] getThresholds() {
		return new double[]{threshold};
	}

}
