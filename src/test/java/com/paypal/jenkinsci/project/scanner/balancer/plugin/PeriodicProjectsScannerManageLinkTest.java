package com.paypal.jenkinsci.project.scanner.balancer.plugin;


import antlr.ANTLRException;
import com.paypal.jenkinsci.project.scanner.balancer.plugin.PeriodicProjectsScannerManageLink.DescriptorImpl;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.tasks.Shell;
import hudson.triggers.TimerTrigger;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.mock;


@PrepareForTest({Hudson.class, StaplerRequest.class, HttpSession.class, JSONObject.class,
        Stapler.class})
public class PeriodicProjectsScannerManageLinkTest extends HudsonTestCase {


    @Test
    @PrepareForTest(Stapler.class)
    public void testdoCalculatePeriodicProjectsLoadDistribution_NegativeFlow() throws ServletException, IOException, ANTLRException {

        PeriodicProjectsScannerManageLink periodicScannerManager = PeriodicProjectsScannerManageLink.getInstance();

        assertNull(periodicScannerManager.getPeriodicJobList());
        StaplerResponse res = createMockResponse("/jenkins");

        StaplerRequest req = createMockRequest(contextPath);
        Mockito.doNothing().when(res).forwardToPreviousPage(req);
        Mockito.doNothing().when(res).sendRedirect2("periodicprojects");
        Mockito.doNothing().when(res).sendRedirect2("analytics");
        Mockito.doNothing().when(res).sendRedirect2("balancing");
        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        periodicScannerManager.doBalancePeriodicProjectsSchedule(req, res);
        periodicScannerManager.doRevertPeriodicProjectsSchedule(req, res);


    }

    @Test
    @PrepareForTest(Stapler.class)
    public void testdoCalculatePeriodicProjectsLoadDistribution_PostiveFlow() throws ServletException, IOException, ANTLRException {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));
        project.addTrigger(new TimerTrigger("@hourly"));
        project.save();

        FreeStyleProject project2 = createFreeStyleProject("job-2");
        project2.getBuildersList().add(new Shell("echo hello job2"));
        project2.addTrigger(new TimerTrigger("@midnight"));
        project2.save();


        FreeStyleProject project3 = createFreeStyleProject("job-3");
        project3.getBuildersList().add(new Shell("echo hello job3"));
        project3.addTrigger(new TimerTrigger("0-10 * * * *"));
        project3.save();

        FreeStyleProject project4 = createFreeStyleProject("job-4");
        project4.getBuildersList().add(new Shell("echo hello job4"));
        project4.addTrigger(new TimerTrigger("*/10 */4 * * *"));
        project4.save();

        FreeStyleProject project5 = createFreeStyleProject("job-5");
        project5.getBuildersList().add(new Shell("echo hello job5"));
        project5.addTrigger(new TimerTrigger("0 */2 * * *"));
        project5.save();

        FreeStyleProject project6 = createFreeStyleProject("job-6");
        project6.getBuildersList().add(new Shell("echo hello job6"));
        project6.addTrigger(new TimerTrigger("*/6 1-6 * * *"));
        project6.save();

        FreeStyleProject project7 = createFreeStyleProject("job-7");
        project7.getBuildersList().add(new Shell("echo hello job7"));
        project7.addTrigger(new TimerTrigger("* 0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18,19,20 * * *"));
        project7.save();

        PeriodicProjectsScannerManageLink periodicScannerManager = PeriodicProjectsScannerManageLink.getInstance();

        StaplerResponse res = createMockResponse("/jenkins");

        StaplerRequest req = createMockRequest(contextPath);
        Mockito.doNothing().when(res).forwardToPreviousPage(req);
        Mockito.doNothing().when(res).sendRedirect2("periodicprojects");
        Mockito.doNothing().when(res).sendRedirect2("analytics");
        Mockito.doNothing().when(res).sendRedirect2("balancing");

        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);


    }

    @Test
    @PrepareForTest(Stapler.class)
    public void testdoCalculateBalancedPeriodicProjectsLoadDistribution_NegativeFlow() throws ServletException, IOException, ANTLRException {

        PeriodicProjectsScannerManageLink periodicScannerManager = PeriodicProjectsScannerManageLink.getInstance();

        StaplerResponse res = createMockResponse("/jenkins");

        StaplerRequest req = createMockRequest(contextPath);
        Mockito.doNothing().when(res).forwardToPreviousPage(req);
        Mockito.doNothing().when(res).sendRedirect2("periodicprojects");
        Mockito.doNothing().when(res).sendRedirect2("analytics");
        Mockito.doNothing().when(res).sendRedirect2("balancing");


        // periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        periodicScannerManager.doCalculateBalancedPeriodicProjectsLoadDistribution(req, res);

    }


    @SuppressWarnings("static-access")
    @Test
    @PrepareForTest(Stapler.class)
    public void testPeriodicProjectsScannerManageLinkCompleteFlow() throws ServletException, IOException, ANTLRException {

        // Create Free Style Project (It will be added automatically to the Hudson instance provided by the HudsonTestCase)
        FreeStyleProject project = createFreeStyleProject("job-1");
        project.getBuildersList().add(new Shell("echo hello"));

        project.addTrigger(new TimerTrigger("@hourly"));

        project.save();

        FreeStyleProject project2 = createFreeStyleProject("job-2");
        project2.getBuildersList().add(new Shell("echo hello job2"));

        project2.addTrigger(new TimerTrigger("@midnight"));

        project2.save();


        FreeStyleProject project3 = createFreeStyleProject("job-3");
        project3.getBuildersList().add(new Shell("echo hello job3"));
        project3.addTrigger(new TimerTrigger("0-10 * * * *"));
        project3.save();

        FreeStyleProject project4 = createFreeStyleProject("job-4");
        project4.getBuildersList().add(new Shell("echo hello job4"));
        project4.addTrigger(new TimerTrigger("*/10 */4 * * *"));
        project4.save();


        PeriodicProjectsScannerManageLink periodicScannerManager = PeriodicProjectsScannerManageLink.getInstance();// new PeriodicProjectsScannerManageLink();
        assertNotNull(periodicScannerManager);
        assertNotNull(periodicScannerManager.getDescription());
        assertNotNull(periodicScannerManager.getDisplayName());
        assertNotNull(periodicScannerManager.getUrlName());
        assertNotNull(periodicScannerManager.ICON);
        assertNotNull(periodicScannerManager.getRequiredPermission());
        assertNotNull(periodicScannerManager.getIconFileName());
        assertFalse(periodicScannerManager.isBalancedGraphReady());
        assertFalse(periodicScannerManager.isDataReady());
        assertFalse(periodicScannerManager.isGraphReady());
        assertFalse(periodicScannerManager.isLoadBalancingEnabled());
        assertFalse(periodicScannerManager.isRevertLoadBalancingEnabled());


        // Print out the getters
        System.out.println(periodicScannerManager.getDescriptor().getDisplayName());
        System.out.println(periodicScannerManager.getDescriptor().getConfigPage().toString());
        System.out.println(periodicScannerManager.getDescriptor().getJsonSafeClassName().toString());


        periodicScannerManager.setBalancingWindow("6");
        System.out.println(periodicScannerManager.getBalancingWindow());
        periodicScannerManager.setHotspotsAvoidance("HotSpots plus Midnight"); // "Midnight-Only" "HotSpots plus Midnight"
        System.out.println(periodicScannerManager.getHotspotsAvoidance());

        String contextPath = "/jenkins";
        StaplerRequest req = createMockRequest(contextPath);
        // getParameter("selectedFrequencyLevel")
        PowerMockito.when(req.getParameter("selectedFrequencyLevel")).thenReturn("Low");
        PowerMockito.when(req.getParameter("balancingWindow")).thenReturn("6");
        PowerMockito.when(req.getParameter("hotspotsAvoidance")).thenReturn("HotSpots plus Midnight");
        PowerMockito.when(req.getParameter("minutesBalancingFactor")).thenReturn("50%");
        PowerMockito.when(req.getParameter("hoursBalancingFactor")).thenReturn("75%");


        StaplerResponse res = createMockResponse("/jenkins");


        Mockito.doNothing().when(res).forwardToPreviousPage(req);
        Mockito.doNothing().when(res).sendRedirect2("periodicprojects");
        Mockito.doNothing().when(res).sendRedirect2("analytics");
        Mockito.doNothing().when(res).sendRedirect2("balancing");

        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        assertEquals(periodicScannerManager.getPeriodicJobList().size(), 4);
        assertNotNull(periodicScannerManager.getOverallGraph());
        assertNotNull(periodicScannerManager.getProjectFrequency());

        periodicScannerManager.setSelectedFrequencyLevel("Low");

        assertNotNull(periodicScannerManager.getPeriodicJobList());
        // To clear the list
        assertNotNull(periodicScannerManager.getPeriodicJobList());

        assertNotNull(periodicScannerManager.getPeriodicJobSchedule());

        // Redo the Periodic Job Analysis with different Paramters "Midnight-Only";
        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        assertEquals(periodicScannerManager.getPeriodicJobList().size(), 4);

        periodicScannerManager.doFilterProjectsByFrequency(req, res);
        periodicScannerManager.doCalculateBalancedPeriodicProjectsLoadDistribution(req, res);
        periodicScannerManager.doViewPeriodicProjectsRedirect(req, res);
        periodicScannerManager.doBalancePeriodicProjectsRedirect(req, res);
        periodicScannerManager.doViewPeriodicProjectsAnalyticsRedirect(req, res);
        assertNotNull(periodicScannerManager.getBalancedGraph());

        periodicScannerManager.setHudsonInstanceHostURL("http://localhost");
        periodicScannerManager.setHudsonInstanceHostPortNumber(56823);

        assertEquals(periodicScannerManager.getHudsonInstanceHostURL(), "http://localhost");
        assertEquals(periodicScannerManager.getHudsonInstanceHostPortNumber(), 56823);

        // Need to augment the plugin with the with the server name and posrt # paramrters

        assertTrue(periodicScannerManager.isLoadBalancingEnabled());
        periodicScannerManager.doBalancePeriodicProjectsSchedule(req, res);
        assertTrue(periodicScannerManager.isRevertLoadBalancingEnabled());
        periodicScannerManager.doRevertPeriodicProjectsSchedule(req, res);

        assertNotNull(periodicScannerManager.getLoadBalancingLog());
        assertNotNull(periodicScannerManager.getLoadRevertLog());
        assertTrue(periodicScannerManager.isBalancedGraphReady());
        assertTrue(periodicScannerManager.isDataReady());
        assertTrue(periodicScannerManager.isGraphReady());
        assertNotNull(periodicScannerManager.getProjectsTimeSchedule());

        periodicScannerManager.setSelectedFrequencyLevel("Low");
        assertNotNull(periodicScannerManager.getSelectedFrequencyLevel());

        PowerMockito.when(req.getParameter("minutesBalancingFactor")).thenReturn("25%");
        PowerMockito.when(req.getParameter("hotspotsAvoidance")).thenReturn("Midnight-Only");

        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        assertEquals(periodicScannerManager.getPeriodicJobList().size(), 4);

        periodicScannerManager.doFilterProjectsByFrequency(req, res);
        periodicScannerManager.doCalculateBalancedPeriodicProjectsLoadDistribution(req, res);


        PowerMockito.when(req.getParameter("minutesBalancingFactor")).thenReturn("30%");
        PowerMockito.when(req.getParameter("hotspotsAvoidance")).thenReturn("HotSpots");

        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        assertEquals(periodicScannerManager.getPeriodicJobList().size(), 4);

        periodicScannerManager.doFilterProjectsByFrequency(req, res);
        periodicScannerManager.doCalculateBalancedPeriodicProjectsLoadDistribution(req, res);


        PowerMockito.when(req.getParameter("minutesBalancingFactor")).thenReturn("30%");
        PowerMockito.when(req.getParameter("hotspotsAvoidance")).thenReturn("XXX");

        periodicScannerManager.doCalculatePeriodicProjectsLoadDistribution(req, res);
        assertEquals(periodicScannerManager.getPeriodicJobList().size(), 4);

        periodicScannerManager.doFilterProjectsByFrequency(req, res);
        periodicScannerManager.doCalculateBalancedPeriodicProjectsLoadDistribution(req, res);


    }


    private static StaplerRequest createMockRequest(String contextPath) {
        StaplerRequest req = mock(StaplerRequest.class);
        PowerMockito.when(req.getContextPath()).thenReturn(contextPath);
        return req;
    }

    private static StaplerResponse createMockResponse(String contextPath) {
        StaplerResponse res = mock(StaplerResponse.class);

        return res;
    }


    @Test
    @PrepareForTest(Stapler.class)
    public void testPeriodicProjectsScannerManageLinkDescriptor() throws ServletException, IOException, ANTLRException {

        PeriodicProjectsScannerManageLink.DescriptorImpl DESCRIPTOR = (DescriptorImpl) PeriodicProjectsScannerManageLink.getInstance().getDescriptor();

        assertNotNull(DESCRIPTOR.getBalacingWindowOptions());
        assertNotNull(DESCRIPTOR.getHostSpotAvoidanceOptions());
        assertNotNull(DESCRIPTOR.getHourlyBalancingOptions());
        assertNotNull(DESCRIPTOR.getMinutesBalancingOptions());
        assertNull(DESCRIPTOR.getDisplayName());

    }


}
