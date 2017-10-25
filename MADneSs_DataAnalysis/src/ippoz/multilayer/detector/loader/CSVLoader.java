/**
 * 
 */
package ippoz.multilayer.detector.loader;

import ippoz.madness.commons.indicator.Indicator;
import ippoz.madness.commons.layers.LayerType;
import ippoz.madness.commons.support.AppLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public abstract class CSVLoader extends SimpleLoader {

	protected File csvFile;
	protected int labelCol;
	protected int experimentRows;
	protected LinkedList<Indicator> header;

	protected CSVLoader(LinkedList<Integer> runs, File csvFile, Integer[] skip, int labelCol, int experimentRows) {
		super(runs);
		this.csvFile = csvFile;
		this.labelCol = labelCol;
		this.experimentRows = experimentRows;
		loadHeader(skip);
	}
	
	private void loadHeader(Integer[] skip){
		BufferedReader reader = null;
		String readLine = null;
		try {
			header = new LinkedList<Indicator>();
			if(csvFile != null && csvFile.exists()){
				reader = new BufferedReader(new FileReader(csvFile));
				while(reader.ready() && readLine == null){
					readLine = reader.readLine();
					if(readLine != null){
						readLine = readLine.trim();
						if(readLine.length() == 0)
							readLine = null;
					}
				}
				for(String splitted : readLine.split(",")){
					if(!occursIn(skip, header.size()))
						header.add(new Indicator(splitted, LayerType.NO_LAYER, Double.class));
					else header.add(null);
				}
				reader.close();
			}
		} catch (IOException ex){
			AppLogger.logException(getClass(), ex, "unable to parse header");
		}
		
	}	

	@Override
	public boolean canRead(int index) {
		return super.canRead(getRun(index));
	}

	protected int getRun(int rowIndex){
		return rowIndex / experimentRows;
	}
	
	private static boolean occursIn(Integer[] skip, int item){
		for(int i=0;i<skip.length;i++){
			if(skip[i] == item)
				return true;
		}
		return false;
	}

}