/**
 * 
 */
package ippoz.reload.metric;

/**
 * The Class BetterMaxMetric. Identifies a metric that is better if it is high.
 *
 * @author Tommy
 */
public abstract class BetterMaxMetric extends ScoringMetric {

	public BetterMaxMetric(MetricType mType, boolean validAfter) {
		super(mType, validAfter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ippoz.multilayer.detector.metric.Metric#compareResults(double,
	 * double)
	 */
	@Override
	public int compareResults(double currentMetricValue, double bestMetricValue) {
		if(!Double.isFinite(bestMetricValue))
			return 1;
		else if(!Double.isFinite(currentMetricValue))
			return -1;
		else return Double.valueOf(currentMetricValue).compareTo(bestMetricValue);
	}

}
