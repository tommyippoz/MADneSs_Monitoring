/**
 * 
 */
package ippoz.reload.metric;

import ippoz.reload.algorithm.result.AlgorithmResult;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class FalseNegativeRate_Metric. Implements a metric dependent on the
 * false negative rate FN/(TP+FN)
 *
 * @author Tommy
 */
public class FalseNegativeRate_Metric extends BetterMinMetric {

	public FalseNegativeRate_Metric(boolean validAfter) {
		super(MetricType.FNR, validAfter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.
	 * multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(List<AlgorithmResult> anomalyEvaluations) {
		double tp = new TN_Metric(true, isValidAfter()).evaluateAnomalyResults(anomalyEvaluations);
		double fn = new FP_Metric(true, isValidAfter()).evaluateAnomalyResults(anomalyEvaluations);
		if (tp + fn > 0)
			return 1.0 * fn / (tp + fn);
		else
			return 0.0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "False Negative Rate";
	}

	@Override
	public String getMetricShortName() {
		return "FNR";
	}

}
