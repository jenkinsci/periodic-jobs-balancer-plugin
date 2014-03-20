package com.paypal.jenkinsci.project.scanner.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hudson.util.DataSetBuilder;

import org.jfree.chart.JFreeChart;
import org.junit.Test;

public class PeriodicProjectsDistributionGraphTest {
	
	@Test
	public void testCreateInstance(){
		
		System.out.println("-----------------------------------------------------------------------------------------");
		System.out.println("Test Cases for testCreateInstance()");
		System.out.println("-----------------------------------------------------------------------------------------");
		
		long maxValue = 0;
        //First iteration just to get scale of the y-axis
        
        
        int floor = 1000; 
        double base = Math.pow(1024, floor);

        DataSetBuilder<String, Integer> dsb = new DataSetBuilder<String, Integer>();

        for(int i = 0 ; i < 24; i++){
        	String label = i+":00";
        	int val = ((i+1)*2);
        	 dsb.add((Integer)val, "count", i);
        	
        }
        
        
        PeriodicProjectsDistributionGraph g = new PeriodicProjectsDistributionGraph(dsb.build());
        
        JFreeChart chart = g.createGraph();
        
        assertNotNull(g);
        
        assertNotNull(chart);
		
	}
	
	
	@Test
	public void testCreateInstance2(){
		
		System.out.println("-----------------------------------------------------------------------------------------");
		System.out.println("Test Cases for testCreateInstance()");
		System.out.println("-----------------------------------------------------------------------------------------");
		
		long maxValue = 0;
        //First iteration just to get scale of the y-axis
        
        
        int floor = 1000; 
        double base = Math.pow(1024, floor);

        DataSetBuilder<String, Integer> dsb = new DataSetBuilder<String, Integer>();

        for(int i = 0 ; i < 24; i++){
        	String label = i+":00";
        	int val = ((i+1)*2);
        	 dsb.add((Integer)val, "count", i);
        	
        }
        
        
        PeriodicProjectsDistributionGraph g = new PeriodicProjectsDistributionGraph(dsb.build(), 
        		"Chart Title");
        
        JFreeChart chart = g.createGraph();
        
        assertNotNull(g);
        
        assertNotNull(chart);
		
	}
	
}