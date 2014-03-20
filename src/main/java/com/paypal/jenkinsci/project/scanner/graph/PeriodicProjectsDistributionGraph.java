package com.paypal.jenkinsci.project.scanner.graph;
import hudson.util.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;


public class PeriodicProjectsDistributionGraph extends Graph {
	
	private CategoryDataset dataset;
	private String title;

	
	public PeriodicProjectsDistributionGraph(CategoryDataset dataset){
		super(-1,1250,550);
		this.dataset = dataset;
		title = "Periodical Job Counts";
	}
	
	public PeriodicProjectsDistributionGraph(CategoryDataset dataset, String title){
		super(-1,1250,550);
		this.dataset = dataset;
		this.title = title;
	}
	
	
	public JFreeChart createGraph() {
 
        JFreeChart chart = ChartFactory.createBarChart3D
                     (this.title, 
                      "Time",              
                      "Number of Periodical Jobs",
                      dataset,         
                      PlotOrientation.VERTICAL,
                      true,                     
                      true,
                      false
                     );
		
		return chart;
	}


}
