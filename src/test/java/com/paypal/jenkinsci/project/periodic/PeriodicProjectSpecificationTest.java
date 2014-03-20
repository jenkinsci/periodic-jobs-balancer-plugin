package com.paypal.jenkinsci.project.periodic;

import com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil;
import org.junit.Test;

import static org.junit.Assert.*;


public class PeriodicProjectSpecificationTest {


    @Test
    public void testCreateInstance() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testCreateInstance()");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);


        job1.setSpec("@midnight");
        assertEquals(job1.getSpec(), "@midnight");

        job1.setBuildCount(100);
        assertTrue(job1.getBuildCount() == 100);

        job1.setBuildDurationAverage(100);
        assertTrue(job1.getBuildDurationAverage() == 100.0);

        assertNotNull(job1.getFrequency());

        job1.setBalancedSpec("*/10 2 * * *");
        assertEquals(job1.getBalancedSpec(), "*/10 2 * * *");

        assertNotNull(job1.getIconFileName());

        job1.setUrl("localhost:8080\\jobs\\job-1");
        assertEquals(job1.getUrl(), "localhost:8080\\jobs\\job-1");

        System.out.println(job1.getBuildTriggersSchedule().getProjectName());
        assertNotNull(job1.getBuildTriggersSchedule().getSchedule());

        job1.setJobName("job-1x");
        assertEquals(job1.getJobName(), "job-1x");

        job1.setPeriodicType("TimeTriggered");
        assertEquals(job1.getPeriodicType(), "TimeTriggered");

    }


    @Test
    public void testGetFrequency() {
        PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);

        assertNotNull(job1.getBuildTriggersSchedule());

        // Emulate all frequencies Levels:

        job1.getBuildTriggersSchedule().getSchedule().add("00:00");
        job1.getBuildTriggersSchedule().getSchedule().add("00:01");
        job1.getBuildTriggersSchedule().getSchedule().add("00:02");
        job1.getBuildTriggersSchedule().getSchedule().add("00:03");
        job1.getBuildTriggersSchedule().getSchedule().add("00:04");
        job1.getBuildTriggersSchedule().getSchedule().add("00:05");

        assertEquals(job1.getProjectFrequencyrCount(), 6);
        assertEquals(job1.getFrequency(), "Low");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("lowFrequencyIconFileName").toString());

        job1.getBuildTriggersSchedule().getSchedule().add("00:06");
        job1.getBuildTriggersSchedule().getSchedule().add("00:08");
        job1.getBuildTriggersSchedule().getSchedule().add("00:09");
        job1.getBuildTriggersSchedule().getSchedule().add("00:10");
        job1.getBuildTriggersSchedule().getSchedule().add("00:11");
        job1.getBuildTriggersSchedule().getSchedule().add("00:12");

        assertEquals(job1.getFrequency(), "Medium Low");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("mediumLowFrequencyIconFileName").toString());


        job1.getBuildTriggersSchedule().getSchedule().add("00:13");
        job1.getBuildTriggersSchedule().getSchedule().add("00:14");
        job1.getBuildTriggersSchedule().getSchedule().add("00:15");
        job1.getBuildTriggersSchedule().getSchedule().add("00:16");
        job1.getBuildTriggersSchedule().getSchedule().add("00:17");
        job1.getBuildTriggersSchedule().getSchedule().add("00:18");
        job1.getBuildTriggersSchedule().getSchedule().add("00:19");
        job1.getBuildTriggersSchedule().getSchedule().add("00:20");
        job1.getBuildTriggersSchedule().getSchedule().add("00:21");
        job1.getBuildTriggersSchedule().getSchedule().add("00:22");
        job1.getBuildTriggersSchedule().getSchedule().add("00:23");

        assertEquals(job1.getFrequency(), "Medium High");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("mediumHighFrequencyIconFileName").toString());


        job1.getBuildTriggersSchedule().getSchedule().add("00:24");
        job1.getBuildTriggersSchedule().getSchedule().add("00:25");
        job1.getBuildTriggersSchedule().getSchedule().add("00:26");
        job1.getBuildTriggersSchedule().getSchedule().add("00:27");
        job1.getBuildTriggersSchedule().getSchedule().add("00:28");
        job1.getBuildTriggersSchedule().getSchedule().add("00:29");
        job1.getBuildTriggersSchedule().getSchedule().add("00:30");
        job1.getBuildTriggersSchedule().getSchedule().add("00:31");
        job1.getBuildTriggersSchedule().getSchedule().add("00:32");
        job1.getBuildTriggersSchedule().getSchedule().add("00:33");
        job1.getBuildTriggersSchedule().getSchedule().add("00:34");
        job1.getBuildTriggersSchedule().getSchedule().add("00:35");

        assertEquals(job1.getFrequency(), "High");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("highFrequencyIconFileName").toString());


        job1.getBuildTriggersSchedule().getSchedule().add("00:36");
        job1.getBuildTriggersSchedule().getSchedule().add("00:37");
        job1.getBuildTriggersSchedule().getSchedule().add("00:38");
        job1.getBuildTriggersSchedule().getSchedule().add("00:39");
        job1.getBuildTriggersSchedule().getSchedule().add("00:40");
        job1.getBuildTriggersSchedule().getSchedule().add("00:41");
        job1.getBuildTriggersSchedule().getSchedule().add("00:42");
        job1.getBuildTriggersSchedule().getSchedule().add("00:43");
        job1.getBuildTriggersSchedule().getSchedule().add("00:44");
        job1.getBuildTriggersSchedule().getSchedule().add("00:45");
        job1.getBuildTriggersSchedule().getSchedule().add("00:46");
        job1.getBuildTriggersSchedule().getSchedule().add("00:47");
        job1.getBuildTriggersSchedule().getSchedule().add("00:48");
        job1.getBuildTriggersSchedule().getSchedule().add("00:49");
        job1.getBuildTriggersSchedule().getSchedule().add("00:50");
        job1.getBuildTriggersSchedule().getSchedule().add("00:51");
        job1.getBuildTriggersSchedule().getSchedule().add("00:52");
        job1.getBuildTriggersSchedule().getSchedule().add("00:53");
        job1.getBuildTriggersSchedule().getSchedule().add("00:54");
        job1.getBuildTriggersSchedule().getSchedule().add("00:55");
        job1.getBuildTriggersSchedule().getSchedule().add("00:56");
        job1.getBuildTriggersSchedule().getSchedule().add("00:57");
        job1.getBuildTriggersSchedule().getSchedule().add("00:58");
        job1.getBuildTriggersSchedule().getSchedule().add("00:59");
        job1.getBuildTriggersSchedule().getSchedule().add("00:60");


        assertEquals(job1.getFrequency(), "Very High");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("veryHighFrequencyIconFileName").toString());


        job1.getBuildTriggersSchedule().getSchedule().add("01:00");

        assertEquals(job1.getFrequency(), "Extremely High");
        assertEquals(job1.getIconFileName(), PropertiesUtil.getInstance().getProperty("extHighFrequencyIconFileName").toString());

        assertNotNull(job1.getProjectsTriggersScheduleAsString());


    }


}
