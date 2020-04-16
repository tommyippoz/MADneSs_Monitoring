/**
 * 
 */
package ippoz.reload.loader;

import java.util.List;

/**
 * @author Tommy
 *
 */
public class LoaderBatch implements Comparable<LoaderBatch>{
	
	private int from;
	
	private int to;

	public LoaderBatch(int from, int to) {
		super();
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	@Override
	public int compareTo(LoaderBatch o) {
		if(from < o.getFrom())
			return -1;
		else if(from == o.getFrom())
			return Integer.compare(to, o.getTo());
		else return 1;
	}
	
	public boolean contains(LoaderBatch o) {
		if(from <= o.getFrom() && to >= o.getTo())
			return true;
		else return false;
	}

	public boolean includesRow(int rowIndex) {
		return rowIndex >= from && rowIndex <= to;
	}

	public int getDataPoints() {
		return Math.abs(to - from) + 1;
	}

	public static List<LoaderBatch> compactBatches(List<LoaderBatch> outList) {
		// TODO Auto-generated method stub
		return outList;
	}

}
