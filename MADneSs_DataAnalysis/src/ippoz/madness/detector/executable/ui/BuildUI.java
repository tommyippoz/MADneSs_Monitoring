/**
 * 
 */
package ippoz.madness.detector.executable.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ippoz.madness.detector.commons.algorithm.AlgorithmType;
import ippoz.madness.detector.commons.knowledge.sliding.SlidingPolicy;
import ippoz.madness.detector.commons.knowledge.sliding.SlidingPolicyType;
import ippoz.madness.detector.commons.support.AppLogger;
import ippoz.madness.detector.commons.support.PreferencesManager;
import ippoz.madness.detector.executable.DetectorMain;
import ippoz.madness.detector.loader.CSVPreLoader;
import ippoz.madness.detector.loader.Loader;
import ippoz.madness.detector.loader.MySQLLoader;
import ippoz.madness.detector.manager.DetectionManager;
import ippoz.madness.detector.manager.InputManager;
import ippoz.madness.detector.metric.MetricType;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Tommy
 *
 */
public class BuildUI {
	
	private static final String SETUP_LABEL_PREFFILE = "Preferences File";
	
	private static final String SETUP_LABEL_METRIC = "Target Metric";
	
	private static final String SETUP_LABEL_OUTPUT = "Output Format";
	
	private static final String SETUP_LABEL_FILTERING = "Filtering";
	
	private static final String SETUP_LABEL_FILTERING_THRESHOLD = "FPR Threshold";
	
	private static final String SETUP_LABEL_TRAINING = "Training";
	
	private static final String SETUP_LABEL_SLIDING_POLICY = "Sliding Policy";
	
	private static final String SETUP_LABEL_WINDOW_SIZE = "Window Size";
	
	private static final String PATH_LABEL_INPUT_FOLDER = "Input Folder";
	
	private static final String PATH_LABEL_OUTPUT_FOLDER = "Output Folder";
	
	private static final String PATH_LABEL_CONF_FOLDER = "Configiuration Folder";
	
	private static final String PATH_LABEL_SETUP_FOLDER = "Setup Folder";
	
	private static final String PATH_LABEL_SCORES_FOLDER = "Scores Folder";
	
	private static final String PATH_LABEL_DETECTION_PREFERENCES = "Detection Preferences";
	
	private JPanel headerPanel, setupPanel, pathPanel, dataAlgPanel, footerPanel;
	
	private Map<String, JPanel> setupMap, pathMap, dataAlgMap;

	private JFrame frame;
	
	private InputManager iManager;

	public BuildUI(InputManager iManager){
		this.iManager = iManager;
		setupMap = new HashMap<String, JPanel>();
		pathMap = new HashMap<String, JPanel>();
		dataAlgMap = new HashMap<String, JPanel>();
		buildFrame();
	}
	
	private void buildFrame(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame();
		frame.setTitle("MADneSs Framework");
		frame.setIconImage(new ImageIcon("img/Logo_Icon.png").getImage());
		if(screenSize.getWidth() > 1600)
			frame.setBounds(0, 0, (int)(screenSize.getWidth()*0.75), (int)(screenSize.getHeight()*0.75));
		else frame.setBounds(0, 0, 800, 480);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
	}
	
	private void reload() {
		frame.setVisible(false);
		setupMap = new HashMap<String, JPanel>();
		pathMap = new HashMap<String, JPanel>();
		dataAlgMap = new HashMap<String, JPanel>();
		frame = buildJFrame();
		frame.setVisible(true);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	private String panelToPreference(String textName) {
		switch(textName){
			case SETUP_LABEL_PREFFILE:
				return null;
			case SETUP_LABEL_METRIC:
				return InputManager.METRIC;
			case SETUP_LABEL_OUTPUT:
				return InputManager.OUTPUT_FORMAT;
			case SETUP_LABEL_FILTERING:
				return InputManager.FILTERING_NEEDED_FLAG;
			case SETUP_LABEL_FILTERING_THRESHOLD:
				return InputManager.FILTERING_TRESHOLD;
			case SETUP_LABEL_TRAINING:
				return InputManager.TRAIN_NEEDED_FLAG;
			case SETUP_LABEL_SLIDING_POLICY:
				return InputManager.SLIDING_POLICY;
			case SETUP_LABEL_WINDOW_SIZE:
				return InputManager.SLIDING_WINDOW_SIZE;
			case PATH_LABEL_INPUT_FOLDER:
				return InputManager.INPUT_FOLDER;
			case PATH_LABEL_OUTPUT_FOLDER:
				return InputManager.OUTPUT_FOLDER;
			case PATH_LABEL_CONF_FOLDER:
				return InputManager.CONF_FILE_FOLDER;
			case PATH_LABEL_SETUP_FOLDER:
				return InputManager.SETUP_FILE_FOLDER;
			case PATH_LABEL_SCORES_FOLDER:
				return InputManager.SCORES_FILE_FOLDER;
			case PATH_LABEL_DETECTION_PREFERENCES:
				return InputManager.DETECTION_PREFERENCES_FILE;
		}
		return null;
	}
	
	public JFrame buildJFrame(){
		headerPanel = new JPanel();
		frame.getContentPane().add(buildHeaderTab());
		
		setupPanel = new JPanel();
		frame.getContentPane().add(buildSetupTab(headerPanel.getHeight()));
		
		pathPanel = new JPanel();
		frame.getContentPane().add(buildPathsTab(headerPanel.getHeight()));
		
		dataAlgPanel = new JPanel();
		frame.getContentPane().add(buildAlgorithmsDatasetsTab(headerPanel.getHeight()));
		
		footerPanel = new JPanel();
		frame.getContentPane().add(buildFooterTab(headerPanel.getHeight() + Math.max(setupPanel.getHeight(), Math.max(pathPanel.getHeight(), dataAlgPanel.getHeight()))));
		
		frame.setBounds(0, 0, frame.getWidth(), headerPanel.getHeight() + Math.max(setupPanel.getHeight(), Math.max(pathPanel.getHeight(), dataAlgPanel.getHeight())) + footerPanel.getHeight());
		frame.setLocationRelativeTo(null);
		
		return frame;
	}
	
	private JPanel buildHeaderTab(){
		headerPanel.setBounds(0, 0, frame.getWidth(), 145);
		headerPanel.setLayout(null);
		
		ImageIcon ii = new ImageIcon("img/Logo_Transparent.png");
		JLabel lblMadness = new JLabel(new ImageIcon(ii.getImage().getScaledInstance(400, 125, Image.SCALE_DEFAULT)));
		lblMadness.setBounds(0, 10, frame.getWidth(), 125);
		lblMadness.setHorizontalAlignment(SwingConstants.CENTER);
		headerPanel.add(lblMadness);
		
		return headerPanel;
	}
	
	private JPanel buildFooterTab(int tabY){
		footerPanel.setBounds(frame.getWidth()/10, tabY, frame.getWidth()*4/5, 100);
		footerPanel.setLayout(null);
		
		ImageIcon ii = new ImageIcon("img/reload.png");
		JButton button = new JButton("", new ImageIcon(ii.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)));
		button.setBounds(footerPanel.getWidth()*2/5, 0, 40, 40);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				reload();
			}
		} );
		footerPanel.add(button);
		
		button = new JButton("Run MADneSs");
		button.setBounds(footerPanel.getWidth()*2/5 + 65, 0, footerPanel.getWidth()/5 - 65, 40);
		button.setFont(new Font("Times", Font.BOLD, 15));
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				runExperiments();
			} } );
		footerPanel.add(button);
		
		JLabel lblFooter = new JLabel("Authors' Information and References");
		lblFooter.setBounds(0, 40, footerPanel.getWidth(), 20);
		lblFooter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFooter.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	JOptionPane.showMessageDialog(frame, "Multi-Layer Anomaly Detection for Complex Dynamic Systems (MADneSs) Framework\n"
		    			+ "For further information, please refer to the Resilient Computing Lab @ University of Florence, Italy\n"
		    			+ "Website: http://rcl.dsi.unifi.it/");
		    }  
		}); 
		footerPanel.add(lblFooter);
		
		return footerPanel;
	}

	protected void runExperiments() {
		ProgressBar pBar = new ProgressBar(frame, "Experiments Progress", 0, DetectorMain.getMADneSsIterations(iManager));
		new Thread(new Runnable() {
			public void run() {
				pBar.showBar();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				List<DetectionManager> dmList;
				try {
					dmList = new LinkedList<DetectionManager>();
					for(PreferencesManager loaderPref : DetectorMain.readLoaders(iManager)){
						for(List<AlgorithmType> aList : DetectorMain.readAlgorithmCombinations(iManager)){
							if(DetectorMain.hasSliding(aList)){
								for(Integer windowSize : DetectorMain.readWindowSizes(iManager)){
									for(SlidingPolicy sPolicy : DetectorMain.readSlidingPolicies(iManager)){
										dmList.add(new DetectionManager(iManager, aList, loaderPref, windowSize, sPolicy));
									}
								}
							} else {
								dmList.add(new DetectionManager(iManager, aList, loaderPref));
							}
						}
					}
					AppLogger.logInfo(DetectorMain.class, dmList.size() + " MADneSs instances found.");
					for(int i=0;i<dmList.size();i++){
						AppLogger.logInfo(DetectorMain.class, "Running MADneSs [" + (i+1) + "/" + dmList.size() + "]: '" + dmList.get(i).getTag() + "'");
						DetectorMain.runMADneSs(dmList.get(i));
						pBar.moveNext();
					}
					pBar.deleteFrame();
				} catch(Exception ex) {
					AppLogger.logException(DetectorMain.class, ex, "");
				}
			}
		}).start();
	}
	
	private void printOptions(JPanel panel, String[] options, int fromX, int fromY, int space){
		JLabel lbl;
		int i = 0;
		if(options != null){
			for(String option : options){
				lbl = new JLabel(option);
				lbl.setBounds(fromX, fromY + i*space, panel.getWidth()-fromX, 20);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				panel.add(lbl);
				i++;
			}
		}
	}
	
	private void addToPanel(JPanel root, String tag, JPanel panel, Map<String, JPanel> refMap){
		root.add(panel);
		refMap.put(tag, panel);
	}
	
	private JPanel buildAlgorithmsDatasetsTab(int tabY){
		int labelSpacing = 30;
		
		TitledBorder tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Data Analysis", TitledBorder.RIGHT, TitledBorder.CENTER, new Font("Times", Font.BOLD, 20), Color.DARK_GRAY);
		dataAlgPanel.setBounds(frame.getWidth()*2/3 + 10, tabY, frame.getWidth()/3 - 20, 80 + labelSpacing*(getDatasets().length + getAlgorithms().length + 2) + 15);
		dataAlgPanel.setBorder(tb);
		dataAlgPanel.setLayout(null);
		
		JLabel mainLabel = new JLabel("Datasets");
		mainLabel.setBounds(dataAlgPanel.getWidth()/4, labelSpacing, dataAlgPanel.getWidth()/2, 25);
		mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainLabel.setFont(new Font("Times", Font.BOLD, 20));
		dataAlgPanel.add(mainLabel);
		
		printOptions(dataAlgPanel, getDatasets(), 20, 2*labelSpacing, labelSpacing);
		
		tabY = labelSpacing*(getDatasets().length + 1) + 40;
		
		mainLabel = new JLabel("Algorithms");
		mainLabel.setBounds(dataAlgPanel.getWidth()/4, tabY, dataAlgPanel.getWidth()/2, 25);
		mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainLabel.setFont(new Font("Times", Font.BOLD, 20));
		dataAlgPanel.add(mainLabel);
		
		printOptions(dataAlgPanel, getAlgorithms(), 20, tabY + 40, labelSpacing);
		
		JPanel seePrefPanel = new JPanel();
		seePrefPanel.setBounds((int) (dataAlgPanel.getWidth()*0.01), 80 + labelSpacing*(getDatasets().length + getAlgorithms().length + 1), (int) (dataAlgPanel.getWidth()*0.98), labelSpacing + 1);
		
		JButton button = new JButton("Open Algorithms");
		button.setVisible(true);
		button.setBounds(0, 0, pathPanel.getWidth()*2/5, 25);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				try {
					Desktop.getDesktop().open(new File(iManager.getSetupFolder() + "algorithmPreferences.preferences"));
				} catch (IOException e1) {
					AppLogger.logException(getClass(), e1, "");
				}
			} } );
		seePrefPanel.add(button);
		dataAlgPanel.add(seePrefPanel);
		
		return dataAlgPanel;
	}
	
	private String[] getDatasets() {
		int i = 0;
		List<PreferencesManager> lList = DetectorMain.readLoaders(iManager);
		String[] dsStrings = new String[lList.size()];
		for(PreferencesManager lPref : lList){
			if(lPref.getPreference(Loader.LOADER_TYPE).equals("MYSQL"))
				dsStrings[i++] = "MySQL - " + lPref.getPreference(MySQLLoader.DB_NAME);
			else {
				dsStrings[i++] = "CSV - " + lPref.getPreference(CSVPreLoader.TRAIN_CSV_FILE);
			}
		}
		return dsStrings;
	}

	private String[] getAlgorithms(){
		int i = 0;
		List<List<AlgorithmType>> aComb = DetectorMain.readAlgorithmCombinations(iManager);
		String[] algStrings = new String[aComb.size()];
		for(List<AlgorithmType> aList : aComb){
			algStrings[i++] = aList.toString().substring(1, aList.toString().length()-1);
		}
		return algStrings;
	}
	
	private JPanel buildPathsTab(int tabY){
		int labelSpacing = 35;
		
		TitledBorder tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Paths", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Times", Font.BOLD, 20), Color.DARK_GRAY);
		pathPanel.setBounds(frame.getWidth()/3 + 10, tabY, frame.getWidth()/3 - 20, 8*labelSpacing + 10);
		pathPanel.setBorder(tb);
		pathPanel.setLayout(null);
		
		addToPanel(pathPanel, PATH_LABEL_INPUT_FOLDER, createFCHPanel(PATH_LABEL_INPUT_FOLDER, pathPanel, labelSpacing, iManager.getInputFolder(), true), pathMap);
		addToPanel(pathPanel, PATH_LABEL_OUTPUT_FOLDER, createFCHPanel(PATH_LABEL_OUTPUT_FOLDER, pathPanel, 2*labelSpacing, iManager.getOutputFolder(), true), pathMap);
		addToPanel(pathPanel, PATH_LABEL_CONF_FOLDER, createFCHPanel(PATH_LABEL_CONF_FOLDER, pathPanel, 3*labelSpacing, iManager.getConfigurationFolder(), true), pathMap);
		addToPanel(pathPanel, PATH_LABEL_SETUP_FOLDER, createFCHPanel(PATH_LABEL_SETUP_FOLDER, pathPanel, 4*labelSpacing, iManager.getSetupFolder(), true), pathMap);
		addToPanel(pathPanel, PATH_LABEL_SCORES_FOLDER, createFCHPanel(PATH_LABEL_SCORES_FOLDER, pathPanel, 5*labelSpacing, iManager.getScoresFolder(), true), pathMap);
		addToPanel(pathPanel, PATH_LABEL_DETECTION_PREFERENCES, createFCHPanel(PATH_LABEL_DETECTION_PREFERENCES, pathPanel, 6*labelSpacing, iManager.getDetectionPreferencesFile(), false), pathMap);
		
		JPanel seePrefPanel = new JPanel();
		seePrefPanel.setBounds((int) (setupPanel.getWidth()*0.01), 7*labelSpacing, (int) (setupPanel.getWidth()*0.98), labelSpacing+1);
		
		JButton button = new JButton("Open Scoring Preferences");
		button.setVisible(true);
		button.setBounds(0, 0, pathPanel.getWidth()*2/5, 25);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				try {
					Desktop.getDesktop().open(new File(iManager.getInputFolder() + iManager.getDetectionPreferencesFile()));
				} catch (IOException e1) {
					AppLogger.logException(getClass(), e1, "");
				}
			} } );
		seePrefPanel.add(button);
		pathPanel.add(seePrefPanel);
		
		return pathPanel;
	}
	
	private JPanel buildSetupTab(int tabY){
		int labelSpacing = 30;
		
		TitledBorder tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Setup", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Times", Font.BOLD, 20), Color.DARK_GRAY);
		setupPanel.setBounds(10, tabY, frame.getWidth()/3 - 20, 10*labelSpacing + 15);
		setupPanel.setBorder(tb);
		setupPanel.setLayout(null);
		
		addToPanel(setupPanel, SETUP_LABEL_PREFFILE, createLPanel(SETUP_LABEL_PREFFILE, setupPanel, labelSpacing, DetectorMain.DEFAULT_PREF_FILE), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_METRIC, createLCBPanel(SETUP_LABEL_METRIC, setupPanel, 2*labelSpacing, MetricType.values(), iManager.getMetricType()), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_OUTPUT, createLCBPanel(SETUP_LABEL_OUTPUT, setupPanel, 3*labelSpacing, new String[]{"null", "TEXT", "IMAGE"}, iManager.getOutputFormat()), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_FILTERING, createLCKPanel(SETUP_LABEL_FILTERING, setupPanel, 4*labelSpacing, iManager.getFilteringFlag()), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_FILTERING_THRESHOLD, createLTPanel(SETUP_LABEL_FILTERING_THRESHOLD, setupPanel, 5*labelSpacing, Double.toString(iManager.getFilteringTreshold())), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_TRAINING, createLCKPanel(SETUP_LABEL_TRAINING, setupPanel, 6*labelSpacing, iManager.getTrainingFlag()), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_SLIDING_POLICY, createLTPanel(SETUP_LABEL_SLIDING_POLICY, setupPanel, 7*labelSpacing, iManager.getSlidingPolicies()), setupMap);
		addToPanel(setupPanel, SETUP_LABEL_WINDOW_SIZE, createLTPanel(SETUP_LABEL_WINDOW_SIZE, setupPanel, 8*labelSpacing, iManager.getSlidingWindowSizes()), setupMap);
		
		JPanel seePrefPanel = new JPanel();
		seePrefPanel.setBounds((int) (setupPanel.getWidth()*0.01), 9*labelSpacing, (int) (setupPanel.getWidth()*0.98), labelSpacing+1);
		
		JButton button = new JButton("Open Preferences");
		button.setVisible(true);
		button.setBounds(0, 0, setupPanel.getWidth()*2/5, 25);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				try {
					Desktop.getDesktop().open(new File(DetectorMain.DEFAULT_PREF_FILE));
				} catch (IOException e1) {
					AppLogger.logException(getClass(), e1, "");
				}
			} } );
		seePrefPanel.add(button);
		setupPanel.add(seePrefPanel);
		
		return setupPanel;
	}
	
	private JPanel createLPanel(String textName, JPanel root, int panelY, String textFieldText){
		JPanel panel = new JPanel();
		panel.setBounds((int) (root.getWidth()*0.01), panelY, (int) (root.getWidth()*0.98), 25);
		panel.setLayout(null);
		
		JLabel lbl = new JLabel(textName);
		lbl.setBounds(root.getWidth()/10, 0, root.getWidth()*2/5, 20);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		JLabel lbldata = new JLabel(textFieldText);
		lbldata.setBounds(root.getWidth()/2, 0, root.getWidth()*2/5, 25);
		lbldata.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbldata);
		
		return panel;
	}
	
	private JPanel createLTPanel(String textName, JPanel root, int panelY, String textFieldText){
		JPanel panel = new JPanel();
		panel.setBounds((int) (root.getWidth()*0.01), panelY, (int) (root.getWidth()*0.98), 25);
		panel.setLayout(null);
		
		JLabel lbl = new JLabel(textName);
		lbl.setBounds(root.getWidth()/10, 0, root.getWidth()*2/5, 20);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		JTextField textField = new JTextField();
		textField.setText(textFieldText);
		textField.setBounds(root.getWidth()/2, 0, root.getWidth()*2/5, 25);
		panel.add(textField);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			  
			public void changedUpdate(DocumentEvent e) {
				workOnUpdate();
			}
			  
			public void removeUpdate(DocumentEvent e) {
				workOnUpdate();
			}
			  
			public void insertUpdate(DocumentEvent e) {
				workOnUpdate();
			}

			public void workOnUpdate() {
				if (textField.getText() != null && textField.getText().length() > 0){
	        		iManager.updatePreference(panelToPreference(textName), textField.getText());
	        	}
			}
		});
		
		root.add(panel);
		
		return panel;
	}
	
	private JPanel createFCHPanel(String textName, JPanel root, int panelY, String textFieldText, boolean folderFlag){
		JPanel panel = new JPanel();
		panel.setBounds((int) (root.getWidth()*0.01), panelY, (int) (root.getWidth()*0.98), 25);
		panel.setLayout(null);
		
		JLabel lbl = new JLabel(textName);
		lbl.setBounds(root.getWidth()/10, 0, root.getWidth()*2/5, 20);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		JButton button = new JButton(textFieldText);
		button.setVisible(true);
		button.setBounds(root.getWidth()/2, 0, root.getWidth()*2/5, 25);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				JFileChooser jfc = new JFileChooser(new File("").getAbsolutePath());
				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					Path pathAbsolute = Paths.get(selectedFile.getAbsolutePath());
			        Path pathBase = Paths.get(new File("").getAbsolutePath());
					if(!folderFlag || selectedFile.isDirectory()){
						button.setText(pathBase.relativize(pathAbsolute).toString());
						iManager.updatePreference(panelToPreference(textName), pathBase.relativize(pathAbsolute).toString());
					} else JOptionPane.showMessageDialog(frame, "'" + pathBase.relativize(pathAbsolute).toString() + "' is not a folder");
				}
			} } );
		panel.add(button);
		
		root.add(panel);
		
		return panel;
	}
	
	private JPanel createLCKPanel(String textName, JPanel root, int panelY, boolean checked){
		JPanel panel = new JPanel();
		panel.setBounds((int) (root.getWidth()*0.01), panelY, (int) (root.getWidth()*0.98), 25);
		panel.setLayout(null);
		
		JCheckBox cb = new JCheckBox(textName);
		cb.setSelected(checked);
		cb.setBounds(root.getWidth()/4, 0, root.getWidth()/2, 20);
		cb.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(cb);
		
		root.add(panel);
		
		return panel;
	}
	
	private JPanel createLCBPanel(String textName, JPanel root, int panelY, Object[] itemList, Object selected){
		JPanel panel = new JPanel();
		panel.setBounds((int) (root.getWidth()*0.01), panelY, (int) (root.getWidth()*0.98), 25);
		panel.setLayout(null);
		
		JLabel lbl = new JLabel(textName);
		lbl.setBounds(root.getWidth()/10, 0, root.getWidth()*2/5, 20);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		JComboBox<Object> comboBox = new JComboBox<Object>();
		comboBox.setBounds(root.getWidth()/2, 0, root.getWidth()*2/5, 25);
		if(itemList != null){
			for(Object ob : itemList){
				comboBox.addItem(ob);
			}
		}
		if(selected != null)
			comboBox.setSelectedItem(selected);
		panel.add(comboBox);
		
		root.add(panel);
		
		return panel;
	}

}
