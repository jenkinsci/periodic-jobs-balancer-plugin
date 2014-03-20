package com.paypal.jenkinsci.project.periodic;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProjectFrequencySummaryTest {

    @Test
    public void testCreateInstance() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testCreateInstance()");
        System.out.println("-----------------------------------------------------------------------------------------");

        ProjectFrequencySummary summary = new ProjectFrequencySummary("fileName",
                "Lowx", 10, 30);


        assertNotNull(summary);

        summary.setFrequencyIconFileName("/plugin/projects-scanner/icons/low.png");
        assertEquals(summary.getFrequencyIconFileName(), "/plugin/projects-scanner/icons/low.png");

        summary.setFrequencyLevel("Low");
        assertEquals(summary.getFrequencyLevel(), "Low");


        summary.setBuildCount(15);
        assertEquals(summary.getBuildCount(), 15);

        summary.setJobCount(3);
        assertEquals(summary.getJobCount(), 3);


        assertEquals(summary.getActionString(), "calculateBalancedPeriodicProjectsLoadDistribution");


    }

}
