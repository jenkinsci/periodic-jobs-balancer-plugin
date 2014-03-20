package com.paypal.jenkinsci.project.scanner.thread;


import com.paypal.jenkinsci.project.periodic.PeriodicProjectCronExpressionAnalyzer;
import com.paypal.jenkinsci.project.periodic.PeriodicProjectSpecification;
import com.paypal.jenkinsci.project.scanner.load.balancing.PeriodicProjectLoadBalancingStrategy;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.tasks.Shell;
import hudson.triggers.TimerTrigger;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeriodicProjectsScannerThreadTest extends HudsonTestCase {

//	 

    private HashMap<Integer, Integer> periodicJobSchedule;
    private HashMap<Integer, List<PeriodicProjectSpecification>> projectsTimeSchedule;
    private List<PeriodicProjectSpecification> periodicJobList;

    @Test
    public void testPeriodicJobsAnalysisAndBalancing() throws Exception {
        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = Hudson.getInstance().createProject(FreeStyleProject.class, "job-1");
        //createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("@hourly"));

        project.save();


        int len = Hudson.getInstance().getAllItems(AbstractProject.class).size();
        int specLen = project.getTriggers().size();


        //	String configXml = this.fetchProjectXml("job-1", true, configFileUrl);
        System.out.println("##Number of Jobs in the Hudson Instance " + len);
        System.out.println("##Number of triggers " + specLen);

        // Run the Build (Since the Project is buildable)
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        System.out.println(build.getDisplayName() + " completed");

        // Assert that the project is active in the Hudson instance 
        assertNotNull(project);

        // Create the Periodic Jobs Scanner object (Note that the scanner will utilize the Hudson instance provided by HudsonTestCase)
        PeriodicProjectsScannerThread projectScannerThread = new PeriodicProjectsScannerThread();

        System.out.println("Initial Delay: " + projectScannerThread.getInitialDelay());
        System.out.println("Recurrece Period: " + projectScannerThread.getRecurrencePeriod());


        // Test the Execute Method  
        projectScannerThread.execute(null);

        // Test the Periodic Jobs Analysis 
        projectScannerThread.analyzePeriodicJobs();

        // Test that the list Periodic Job specifications 
        assertNotNull(projectScannerThread.getPeriodicJobList());

        periodicJobList = projectScannerThread.getPeriodicJobList();

        analyzePeriodicJobSchedule();

        doCalculateBalancedPeriodicProjectsLoadDistribution("6", "Hot-Spots-Plus-Midnight", "50%", "50%");

        doCalculateBalancedPeriodicProjectsLoadDistribution("9", "Midnight-Only", "25%", "25%");

        doCalculateBalancedPeriodicProjectsLoadDistribution("9", "Hot-Spots-Only", "75%", "100%");

        doCalculateBalancedPeriodicProjectsLoadDistribution("9", "xxxx", "75%", "100%");


        // update Url of the for the job specification

//		projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList);


        System.out.println("Root Url : " + super.getURL());

        updatePeriodicJobUrl(periodicJobList, super.getURL().toString());

        projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList, "http://localhost", 56823);


        System.out.println("Result: " + projectScannerThread.getResult());

    }


    public void testPeriodicJobsBalancingAndReversion() throws Exception {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = Hudson.getInstance().createProject(FreeStyleProject.class, "job-1");
        //createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("#comment\n" + "@hourly"));

        project.save();


        int len = Hudson.getInstance().getAllItems(AbstractProject.class).size();
        int specLen = project.getTriggers().size();


//		String configXml = this.fetchProjectXml("job-1", true, configFileUrl);
        System.out.println("##Number of Jobs in the Hudson Instance " + len);
        System.out.println("##Number of triggers " + specLen);

        // Assert that the project is active in the Hudson instance
        assertNotNull(project);

        // Create the Periodic Jobs Scanner object (Note that the scanner will utilize the Hudson instance provided by HudsonTestCase)
        PeriodicProjectsScannerThread projectScannerThread = new PeriodicProjectsScannerThread();

        System.out.println("Initial Delay: " + projectScannerThread.getInitialDelay());
        System.out.println("Recurrece Period: " + projectScannerThread.getRecurrencePeriod());


        // Test the Execute Method
        projectScannerThread.execute(null);

        // Test the Periodic Jobs Analysis
        projectScannerThread.analyzePeriodicJobs();

        // Test that the list Periodic Job specifications
        assertNotNull(projectScannerThread.getPeriodicJobList());

        periodicJobList = projectScannerThread.getPeriodicJobList();

        analyzePeriodicJobSchedule();

        doCalculateBalancedPeriodicProjectsLoadDistribution("6", "Hot-Spots-Plus-Midnight", "50%", "50%");


        // update Url of the for the job specification

//		projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList);


        System.out.println("Root Url : " + super.getURL());

        updatePeriodicJobUrl(periodicJobList, super.getURL().toString());

        projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList, "http://localhost", 56823);


        System.out.println("Result: " + projectScannerThread.getResult());


        // Perform Reversion

        projectScannerThread.revertOldTimerTriggerSpec(periodicJobList, "http://localhost", 56823);


    }

    @Test
    public void testPeriodicJobsBalancingAndReversionNegativeScenario() throws Exception {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = Hudson.getInstance().createProject(FreeStyleProject.class, "job-1");
        //createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("#comment\n" + "H H(0-9) * * *"));

        project.save();


        int len = Hudson.getInstance().getAllItems(AbstractProject.class).size();
        int specLen = project.getTriggers().size();


//		String configXml = this.fetchProjectXml("job-1", true, configFileUrl);
        System.out.println("##Number of Jobs in the Hudson Instance " + len);
        System.out.println("##Number of triggers " + specLen);

        // Assert that the project is active in the Hudson instance
        assertNotNull(project);

        // Create the Periodic Jobs Scanner object (Note that the scanner will utilize the Hudson instance provided by HudsonTestCase)
        PeriodicProjectsScannerThread projectScannerThread = new PeriodicProjectsScannerThread();

        System.out.println("Initial Delay: " + projectScannerThread.getInitialDelay());
        System.out.println("Recurrece Period: " + projectScannerThread.getRecurrencePeriod());


        // Test the Execute Method
        projectScannerThread.execute(null);

        // Test the Periodic Jobs Analysis
        projectScannerThread.analyzePeriodicJobs();

        // Test that the list Periodic Job specifications
        assertNotNull(projectScannerThread.getPeriodicJobList());

        periodicJobList = projectScannerThread.getPeriodicJobList();

        analyzePeriodicJobSchedule();

        doCalculateBalancedPeriodicProjectsLoadDistribution("6", "Hot-Spots-Plus-Midnight", "50%", "50%");


        // update Url of the for the job specification

//		projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList);


        System.out.println("Root Url : " + super.getURL());

        updatePeriodicJobUrl(periodicJobList, super.getURL().toString());

        projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList, "http://localhost", 8080);


        System.out.println("Result: " + projectScannerThread.getResult());


        // Perform Reversion

        projectScannerThread.revertOldTimerTriggerSpec(periodicJobList, "http://localhost", 8080);


    }


    public void testPeriodicJobsBalancingwithLogging() throws Exception {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = Hudson.getInstance().createProject(FreeStyleProject.class, "job-1");

        //createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("#comment\n" + "@hourly"));

        project.save();


        int len = Hudson.getInstance().getAllItems(AbstractProject.class).size();
        int specLen = project.getTriggers().size();


        //	String configXml = this.fetchProjectXml("job-1", true, configFileUrl);
        System.out.println("##Number of Jobs in the Hudson Instance " + len);
        System.out.println("##Number of triggers " + specLen);

        // Run the Build (Since the Project is buildable)
//        FreeStyleBuild build = project.scheduleBuild2(0).get();
//        System.out.println(build.getDisplayName()+" completed");

        // Assert that the project is active in the Hudson instance 
        assertNotNull(project);

        // Create the Periodic Jobs Scanner object (Note that the scanner will utilize the Hudson instance provided by HudsonTestCase)
        PeriodicProjectsScannerThread projectScannerThread = new PeriodicProjectsScannerThread();

        System.out.println("Initial Delay: " + projectScannerThread.getInitialDelay());
        System.out.println("Recurrece Period: " + projectScannerThread.getRecurrencePeriod());


        // Test the Execute Method  
        projectScannerThread.execute(null);

        // Test the Periodic Jobs Analysis 
        projectScannerThread.analyzePeriodicJobs();

        // Test that the list Periodic Job specifications 
        assertNotNull(projectScannerThread.getPeriodicJobList());

        periodicJobList = projectScannerThread.getPeriodicJobList();

        analyzePeriodicJobSchedule();

        doCalculateBalancedPeriodicProjectsLoadDistribution("6", "Hot-Spots-Plus-Midnight", "50%", "50%");


        // update Url of the for the job specification

//		projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList);


        System.out.println("Root Url : " + super.getURL());

        updatePeriodicJobUrl(periodicJobList, super.getURL().toString());

        projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList, new StringBuffer(), "http://localhost", 56823);


        System.out.println("Result: " + projectScannerThread.getResult());


    }

    public void testPeriodicJobsBalancingAndReversionAndLogging() throws Exception {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = Hudson.getInstance().createProject(FreeStyleProject.class, "job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("#comment\n" + "@hourly"));

        project.save();


        int len = Hudson.getInstance().getAllItems(AbstractProject.class).size();
        int specLen = project.getTriggers().size();


//	String configXml = this.fetchProjectXml("job-1", true, configFileUrl);
        System.out.println("##Number of Jobs in the Hudson Instance " + len);
        System.out.println("##Number of triggers " + specLen);

        // Assert that the project is active in the Hudson instance
        assertNotNull(project);

        // Create the Periodic Jobs Scanner object (Note that the scanner will utilize the Hudson instance provided by HudsonTestCase)
        PeriodicProjectsScannerThread projectScannerThread = new PeriodicProjectsScannerThread();

        System.out.println("Initial Delay: " + projectScannerThread.getInitialDelay());
        System.out.println("Recurrece Period: " + projectScannerThread.getRecurrencePeriod());


        // Test the Execute Method
        projectScannerThread.execute(null);

        // Test the Periodic Jobs Analysis
        projectScannerThread.analyzePeriodicJobs();

        // Test that the list Periodic Job specifications
        assertNotNull(projectScannerThread.getPeriodicJobList());

        periodicJobList = projectScannerThread.getPeriodicJobList();

        analyzePeriodicJobSchedule();

        doCalculateBalancedPeriodicProjectsLoadDistribution("6", "Hot-Spots-Plus-Midnight", "50%", "50%");


        // update Url of the for the job specification

//	projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList);


        System.out.println("Root Url : " + super.getURL());

        updatePeriodicJobUrl(periodicJobList, super.getURL().toString());

        projectScannerThread.updateBalancedTimerTriggerSpec(periodicJobList, new StringBuffer(), "http://localhost", 56823);


        System.out.println("Result: " + projectScannerThread.getResult());


        // Perform Reversion

        projectScannerThread.revertOldTimerTriggerSpec(periodicJobList, new StringBuffer(), "http://localhost", 56823);

        System.out.println("Completed" + projectScannerThread.isCompleted());


    }


    private void analyzePeriodicJobSchedule() {

        if (null == periodicJobSchedule) {
            periodicJobSchedule = new HashMap<Integer, Integer>();
        } else {
            // Clear the collection in order to avoid memory leak.
            periodicJobSchedule.clear();
        }


        if (null == projectsTimeSchedule) {
            projectsTimeSchedule = new HashMap<Integer, List<PeriodicProjectSpecification>>();
            for (int i = 0; i < 24; i++) {
                projectsTimeSchedule.put(i, new ArrayList<PeriodicProjectSpecification>());
            }
        } else {
            for (Integer key : projectsTimeSchedule.keySet()) {
                projectsTimeSchedule.get(key).clear();
            }
        }

        // pass the it into the following method

        PeriodicProjectCronExpressionAnalyzer.analyzePeriodicJobSchedule(periodicJobSchedule, periodicJobList);

        // Now Calculate the Balanced Schedule


    }


// The following method will calculate the balanced schedule


    private void doCalculateBalancedPeriodicProjectsLoadDistribution(
            String balancingWindow, String hotspotsAvoidance, String minutesBalancingFactor,
            String hoursBalancingFactor) throws ServletException, IOException {

        // Get the list of Periodic Projects Specs

        for (PeriodicProjectSpecification projectSpec : this.periodicJobList) {

            String balanedSpec = PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule
                    (periodicJobSchedule, "Midnight:Hourly", projectSpec.getSpec(), Integer.parseInt(balancingWindow),
                            translateHostSpotsAvoidancePolicy(hotspotsAvoidance), translateBalancingFactor(minutesBalancingFactor),
                            translateBalancingFactor(hoursBalancingFactor));

            System.out.println("#### New Balanced Spec : " + balanedSpec + " Old Spec: " + projectSpec.getSpec());
            if (balanedSpec != null) {
                System.out.println("-New Balanced Spec : " + balanedSpec);
                projectSpec.setBalancedSpec(balanedSpec);
            }
        }

        //analyzeBalancedPeriodicJobSchedule();
        // recalculate the distribution according to the new schedule

    }

    private String translateHostSpotsAvoidancePolicy(String hotspot) {
        if (hotspot.contains("plus")) {
            return "Hot-Spots-Plus-Midnight";
        } else if (hotspot.contains("Midnight")) {
            return "Midnight-Only";
        } else if (hotspot.contains("HotSpots")) {
            return "Hot-Spots-Only";
        }

        return null;

    }

    private double translateBalancingFactor(String factor) {

        if (factor.contains("25%")) {
            return 1.32;
        } else if (factor.contains("50%")) {
            return 2.0;
        } else if (factor.contains("75%")) {
            return 4.0;
        }

        return 1.0;
    }


    private void updatePeriodicJobUrl(List<PeriodicProjectSpecification> periodicJobList, String rootURL) {
        for (PeriodicProjectSpecification projectSpec : periodicJobList) {

            System.out.println("Old Project Url:" + projectSpec.getUrl());
            projectSpec.setUrl(
                    projectSpec.getUrl().replace("null", rootURL));
            System.out.println("New Project Url:" + projectSpec.getUrl());


        }


    }


}
