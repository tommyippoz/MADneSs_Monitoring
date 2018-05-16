/**
 * 
 */
package ippoz.madness.detector.commons.knowledge.sliding;

import ippoz.madness.detector.commons.support.WeightedIndex;

import java.util.List;

/**
 * @author Tommy
 *
 */
public class FIFOPolicy extends SlidingPolicy {

	@Override
	public int canReplace(List<WeightedIndex> indexList, WeightedIndex wi) {
		return 0;
	}

	@Override
	public boolean canEnter(WeightedIndex wi) {
		return true;
	}

}
