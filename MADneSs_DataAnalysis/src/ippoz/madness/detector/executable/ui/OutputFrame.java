/**
 * 
 */
package ippoz.madness.detector.executable.ui;

import ippoz.madness.detector.manager.InputManager;
import ippoz.madness.detector.metric.Metric;
import ippoz.madness.detector.output.DetectorOutput;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @author Tommy
 *
 */
public class OutputFrame {
	
	private JFrame outFrame;
	
	private JTabbedPane tabbedPane;
	
	private static final int labelSpacing = 25;
	
	private int panelNumber; 
	
	private InputManager iManager;
	
	public OutputFrame(InputManager iManager, int panelNumber) {
		this.panelNumber = panelNumber;
		this.iManager = iManager;
		buildFrame();
		buildTabbedPanel();
	}

	private void buildTabbedPanel() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(0, labelSpacing + 40, outFrame.getWidth() - 10, outFrame.getHeight() - labelSpacing - 50);		
	}

	public void addOutput(DetectorOutput dOut) {
		JPanel outPanel = buildOutputPanel(dOut);
		tabbedPane.addTab("DB: " + dOut.getDataset() + " - Alg: " + dOut.getAlgorithm().replace("[", "").replace("]", ""), outPanel);
	}

	public void setVisible(boolean b) {
		if(outFrame != null){
			outFrame.getContentPane().setBackground(Color.WHITE);
			outFrame.getContentPane().add(tabbedPane);
			if(panelNumber > 4){
				outFrame.setBounds(outFrame.getX(), outFrame.getY(), outFrame.getWidth(), outFrame.getHeight() + (panelNumber / 4)*labelSpacing);
			}
			tabbedPane.setForeground(Color.WHITE);
			outFrame.setLocationRelativeTo(null);
			outFrame.setVisible(b);
		}
	}

	private void buildFrame(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		outFrame = new JFrame();
		outFrame.setTitle("Summary");
		if(screenSize.getWidth() > 1600)
			outFrame.setBounds(0, 0, (int)(screenSize.getWidth()*0.5), (int)(screenSize.getHeight()*0.5));
		else outFrame.setBounds(0, 0, 800, 480);
		outFrame.setBackground(Color.WHITE);
		outFrame.setResizable(false);
	}
	
	public void buildSummaryPanel(List<DetectorOutput> dOutList){
		JPanel summaryPanel = new JPanel();
		summaryPanel.setBackground(Color.WHITE);
		summaryPanel.setBounds(0, 0, tabbedPane.getWidth() - 10, tabbedPane.getHeight() - 10);
		summaryPanel.setLayout(null);
		
		JPanel fPanel = new JPanel();
		fPanel.setBackground(Color.WHITE);
		TitledBorder tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Common Setups", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Times", Font.BOLD, 16), Color.DARK_GRAY);
		fPanel.setBounds(summaryPanel.getWidth()/4, 0, summaryPanel.getWidth()/2, labelSpacing + 30);
		fPanel.setBorder(tb);
		fPanel.setLayout(null);
		fPanel.add(BuildUI.createLPanel(true, "Metric", fPanel, (int) (0.01*fPanel.getWidth()), 20, dOutList.get(0).getReferenceMetric().getMetricName()));			
		summaryPanel.add(fPanel);
		
		summaryPanel.add(buildOutputSummaryPanel(null, summaryPanel, 0));
		int i = 1;
		for(DetectorOutput dOut : dOutList){
			summaryPanel.add(buildOutputSummaryPanel(dOut, summaryPanel, i++));
		}
		tabbedPane.add("Summary", summaryPanel);
	}
	
	private JPanel buildOutputSummaryPanel(DetectorOutput dOut, JPanel root, int i){
		int elements = 7;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds((int) (root.getWidth()*0.01), labelSpacing*(i+1) + 40, (int) (root.getWidth()*0.98), labelSpacing);
		panel.setLayout(null);
		
		JLabel lbl = new JLabel(dOut != null ? dOut.getDataset() : "Dataset");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(0, 0, root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		lbl = new JLabel(dOut != null ? dOut.getAlgorithm().replace("[", "").replace("]", "") : "Algorithm");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(root.getWidth()/elements, 0, 2*root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		lbl = new JLabel(dOut != null ? dOut.getBestSetup() : "Best Configuration");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(root.getWidth()*3/elements, 0, root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		lbl = new JLabel(dOut != null ? dOut.getBestRuns() : "Best Runs");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(root.getWidth()*4/elements, 0, root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		lbl = new JLabel(dOut != null ? dOut.getFaultsRatioString() : "Attacks Ratio");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(root.getWidth()*5/elements, 0, root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		lbl = new JLabel(dOut != null ? String.valueOf(dOut.getFormattedBestScore()) : "Best Score");
		lbl.setFont(new Font(lbl.getFont().getName(), dOut == null ? Font.BOLD : Font.PLAIN, 12));
		lbl.setBounds(root.getWidth()*6/elements, 0, root.getWidth()/elements, labelSpacing);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lbl);
		
		return panel;
	}
	
	private JPanel buildOutputPanel(DetectorOutput dOut) {	
		JPanel containerPanel = new JPanel();
		containerPanel.setBackground(Color.WHITE);
		containerPanel.setBounds(0, 0, tabbedPane.getWidth() - 10, tabbedPane.getHeight() - 10);
		containerPanel.setLayout(null);
		
		JPanel miscPanel = new JPanel();
		miscPanel.setBackground(Color.WHITE);
		TitledBorder tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Setup", TitledBorder.LEFT, TitledBorder.CENTER, new Font("Times", Font.BOLD, 16), Color.DARK_GRAY);
		miscPanel.setBounds(5, 10, containerPanel.getWidth()/2 - 10, labelSpacing*3+30);
		miscPanel.setBorder(tb);
		miscPanel.setLayout(null);
		miscPanel.add(BuildUI.createLPanel(true, "Dataset", miscPanel, (int) (0.01*miscPanel.getWidth()), 20, dOut.getDataset()));
		miscPanel.add(BuildUI.createLPanel(true, "Algorithm", miscPanel, (int) (0.01*miscPanel.getWidth()), labelSpacing + 20, dOut.getAlgorithm().replace("[", "").replace("]", "")));
		miscPanel.add(BuildUI.createLPanel(true, "Metric", miscPanel, (int) (0.01*miscPanel.getWidth()), labelSpacing*2 + 20, dOut.getReferenceMetric().getMetricName()));			
		containerPanel.add(miscPanel);
		
		miscPanel = new JPanel();
		miscPanel.setBackground(Color.WHITE);
		tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Details", TitledBorder.RIGHT, TitledBorder.CENTER, new Font("Times", Font.BOLD, 16), Color.DARK_GRAY);
		miscPanel.setBounds(containerPanel.getWidth()/2 + 5, 10, containerPanel.getWidth()/2 - 10, labelSpacing*3+30);
		miscPanel.setBorder(tb);
		miscPanel.setLayout(null);
		miscPanel.add(BuildUI.createLPanel(true, "Best Setup", miscPanel, (int) (0.01*miscPanel.getWidth()), 20, dOut.getBestSetup()));
		miscPanel.add(BuildUI.createLPanel(true, "Runs", miscPanel, (int) (0.01*miscPanel.getWidth()), labelSpacing + 20, dOut.getBestRuns()));
		miscPanel.add(BuildUI.createLPanel(true, "Best Score (" + dOut.getReferenceMetric().getMetricShortName() + ")", miscPanel, (int) (0.01*miscPanel.getWidth()), labelSpacing*2 + 20, String.valueOf(dOut.getBestScore())));			
		containerPanel.add(miscPanel);
		   
        String[] columnNames = new String[dOut.getEvaluationMetrics().length + 3];
        columnNames[0] = "Voter";
        columnNames[1] = "Anomaly";
        columnNames[2] = "Checkers";
		int i = 3;
        for(Metric met : dOut.getEvaluationMetrics()){
			columnNames[i++] = met.getMetricType() != null ? met.getMetricShortName() : "AUC";
		}	 
        
        JTable table = new JTable(dOut.getEvaluationGrid(), columnNames);
        table.setFillsViewportHeight(true);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        for(int x=0;x<table.getColumnCount();x++){
        	table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        	table.getColumnModel().getColumn(x).setHeaderRenderer(centerRenderer);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resizeColumnWidth(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(5, miscPanel.getHeight() + 20, containerPanel.getWidth()-10, (int)table.getPreferredSize().getHeight() + 50);
        containerPanel.add(scroll);
        
        JPanel fPanel = new JPanel();
        fPanel.setBackground(Color.WHITE);
		tb = new TitledBorder(new LineBorder(Color.DARK_GRAY, 2), "Additional Files", TitledBorder.CENTER, TitledBorder.CENTER, new Font("Times", Font.BOLD, 16), Color.DARK_GRAY);
		fPanel.setBounds(outFrame.getWidth()/4, miscPanel.getHeight() + scroll.getHeight() + 20, outFrame.getWidth()/2, labelSpacing + 30);
		fPanel.setBorder(tb);
		fPanel.setLayout(null);
		
		JButton button = new JButton("Open Output Folder");
		button.setVisible(true);
		button.setBounds(miscPanel.getWidth()/4, 20, miscPanel.getWidth()/2, labelSpacing);
		button.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				Desktop desktop = Desktop.getDesktop();
		        File dirToOpen = new File(dOut.buildPath(iManager.getOutputFolder()));
		        try {
		            desktop.open(dirToOpen);
		        } catch (IOException ex) {
		        	JOptionPane.showMessageDialog(outFrame, "ERROR: Unable to open '" + dOut.buildPath(iManager.getOutputFolder()) + "'");
				}
			} } );	
		fPanel.add(button);
		containerPanel.add(fPanel);
		
		return containerPanel;
	}
	
	public void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    int width = 60; // Min width
	    
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 100)
	            width = 100;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
}
