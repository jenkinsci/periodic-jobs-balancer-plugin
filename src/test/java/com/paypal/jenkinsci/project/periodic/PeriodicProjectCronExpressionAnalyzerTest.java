package com.paypal.jenkinsci.project.periodic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PeriodicProjectCronExpressionAnalyzer.class)
public class PeriodicProjectCronExpressionAnalyzerTest {

    @Test
    public void testIsValidCronExpression_NegativeCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testIsValidCronExpression_NegativeCases");
        System.out.println("-----------------------------------------------------------------------------------------");

        System.out.println("Test Case-1: Input Cron Expression is Null");
        String cronExpression = null;
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);

        System.out.println("Test Case-2: Input Cron Expression is Empty String");
        cronExpression = "";
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);

        System.out.println("Test Case-3: Input Cron Expression starts with @ but does no contain (midnight, daily, weekly or hourly)");
        cronExpression = "@midnightXXX";
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);

        System.out.println("Test Case-4: Input Cron Expression starts * but does not consist of 5 terms");
        cronExpression = "* */5 * *";
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);

        System.out.println("Test Case-5: Input Cron Expression starts with numeric but does not consist of 5 terms");
        cronExpression = "0 */5 * *";
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);


        System.out.println("Test Case-6: Input Cron Expression is invalid expression");
        cronExpression = "#This is comment";
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), false);


//		PowerMockito.mockStatic(PeriodicProjectCronExpressionAnalyzer.class);
//		PowerMockito.when(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression)).thenReturn(false); 


    }

    @Test
    public void testIsValidCronExpression_PositiveCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testIsValidCronExpression_PositiveCases");
        System.out.println("-----------------------------------------------------------------------------------------");
        String cronExpression = null;

        System.out.println("Test Case-1: Input Cron Expression is @midnight");
        cronExpression = "@midnight";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);


        System.out.println("Test Case-2: Input Cron Expression is @daily");
        cronExpression = "@daily";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);

        System.out.println("Test Case-3: Input Cron Expression is @hourly");
        cronExpression = "@hourly";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);

        System.out.println("Test Case-4: Input Cron Expression is @weekly");
        cronExpression = "@weekly";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);


        System.out.println("Test Case-5: Input Cron Expression is */2 * * * *");
        cronExpression = "*/2 * * * *";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);

        System.out.println("Test Case-5: Input Cron Expression is 2 * * * *");
        cronExpression = "2 * * * *";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);

        System.out.println("Test Case-5: Input Cron Expression is * * * * *");
        cronExpression = "* * * * *";
        System.out.println("Expected = " + true + " Actual = " + PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.isValidCronExpression(cronExpression), true);


    }

    @Test
    public void testScanForPredfinedSchedule_NegativeCases() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testScanForPredfinedSchedule_NegativeCases");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();
        periodicJobSchedule.put(0, 1);
        periodicJobSchedule.put(1, 1);
        periodicJobSchedule.put(2, 1);
        periodicJobSchedule.put(3, 1);
        periodicJobSchedule.put(4, 1);
        periodicJobSchedule.put(5, 1);
        periodicJobSchedule.put(6, 1);
        periodicJobSchedule.put(7, 1);
        periodicJobSchedule.put(8, 1);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 1);
        periodicJobSchedule.put(11, 0);
        periodicJobSchedule.put(12, 1);
        periodicJobSchedule.put(13, 1);
        periodicJobSchedule.put(14, 1);
        periodicJobSchedule.put(15, 1);
        periodicJobSchedule.put(16, 1);
        periodicJobSchedule.put(17, 1);
        periodicJobSchedule.put(18, 1);
        periodicJobSchedule.put(19, 1);
        periodicJobSchedule.put(20, 1);
        periodicJobSchedule.put(21, 1);
        periodicJobSchedule.put(22, 1);
        periodicJobSchedule.put(23, 0);

        boolean balanced = false;
        String cronExpression = "*/2 * * * *";
        System.out.println("Test Case-1: When the cron expression is not a special case and the schedule is not balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), false);

        balanced = true;
        System.out.println("Test Case-2: When the cron expression is not a special case and the schedule is balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), false);

    }

    @Test
    public void testScanForPredfinedSchedule_PositiveCases() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testScanForPredfinedSchedule_PositiveCases");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();
        periodicJobSchedule.put(0, 1);
        periodicJobSchedule.put(1, 1);
        periodicJobSchedule.put(2, 1);
        periodicJobSchedule.put(3, 1);
        periodicJobSchedule.put(4, 1);
        periodicJobSchedule.put(5, 1);
        periodicJobSchedule.put(6, 1);
        periodicJobSchedule.put(7, 1);
        periodicJobSchedule.put(8, 1);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 1);
        periodicJobSchedule.put(11, 0);
        periodicJobSchedule.put(12, 1);
        periodicJobSchedule.put(13, 1);
        periodicJobSchedule.put(14, 1);
        periodicJobSchedule.put(15, 1);
        periodicJobSchedule.put(16, 1);
        periodicJobSchedule.put(17, 1);
        periodicJobSchedule.put(18, 1);
        periodicJobSchedule.put(19, 1);
        periodicJobSchedule.put(20, 1);
        periodicJobSchedule.put(21, 1);
        periodicJobSchedule.put(22, 1);
        periodicJobSchedule.put(23, 0);

        boolean balanced = false;
        String cronExpression = "@midnight";
        System.out.println("Test Case-1: When the cron expression is a special case and the schedule is not balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), true);


        cronExpression = "@hourly";
        System.out.println("Test Case-2: When the cron expression is a special case (@hourly) and the schedule is not balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), true);


        balanced = true;
        cronExpression = "@midnight";
        System.out.println("Test Case-2: When the cron expression is a special case and the schedule is balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), true);

        cronExpression = "@hourly";
        System.out.println("Test Case-1: When the cron expression is a special case (@hourly) and the schedule is balanced");
        System.out.println("Expected = " + false + " Actual = " + PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced));
        assertEquals(PeriodicProjectCronExpressionAnalyzer.scanForPredfinedSchedule(job,
                cronExpression, periodicJobSchedule, balanced), true);

    }


    @Test
    public void testPopulateJobTimeSchedule() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testPopulateJobTimeSchedule");
        System.out.println("-----------------------------------------------------------------------------------------");
        PeriodicProjectSpecification job = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();
        periodicJobSchedule.put(0, 1);
        periodicJobSchedule.put(1, 1);
        periodicJobSchedule.put(2, 1);
        periodicJobSchedule.put(3, 1);
        periodicJobSchedule.put(4, 1);
        periodicJobSchedule.put(5, 1);
        periodicJobSchedule.put(6, 1);
        periodicJobSchedule.put(7, 1);
        periodicJobSchedule.put(8, 1);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 1);
        periodicJobSchedule.put(11, 0);
        periodicJobSchedule.put(12, 1);
        periodicJobSchedule.put(13, 1);
        periodicJobSchedule.put(14, 1);
        periodicJobSchedule.put(15, 1);
        periodicJobSchedule.put(16, 1);
        periodicJobSchedule.put(17, 1);
        periodicJobSchedule.put(18, 1);
        periodicJobSchedule.put(19, 1);
        periodicJobSchedule.put(20, 1);
        periodicJobSchedule.put(21, 1);
        periodicJobSchedule.put(22, 1);
        periodicJobSchedule.put(23, 0);


        String[] hours = {"0", "1", "2", "3", "4", "5"};
        String[] minutes = {"10", "12", "14", "16", "20", "24"};

        PeriodicProjectCronExpressionAnalyzer.populateJobTimeSchedule(minutes, hours, job, periodicJobSchedule, minutes.length);

        job.getBuildTriggersSchedule().getSchedule().clear();
        hours = new String[]{"*"};
        periodicJobSchedule.put(0, 1);
        periodicJobSchedule.put(1, 1);
        periodicJobSchedule.put(2, 1);
        periodicJobSchedule.put(3, 1);
        periodicJobSchedule.put(4, 1);
        periodicJobSchedule.put(5, 1);
        periodicJobSchedule.put(6, 1);
        periodicJobSchedule.put(7, 1);
        periodicJobSchedule.put(8, 1);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 1);
        periodicJobSchedule.put(11, 0);
        periodicJobSchedule.put(12, 1);
        periodicJobSchedule.put(13, 1);
        periodicJobSchedule.put(14, 1);
        periodicJobSchedule.put(15, 1);
        periodicJobSchedule.put(16, 1);
        periodicJobSchedule.put(17, 1);
        periodicJobSchedule.put(18, 1);
        periodicJobSchedule.put(19, 1);
        periodicJobSchedule.put(20, 1);
        periodicJobSchedule.put(21, 1);
        periodicJobSchedule.put(22, 1);
        periodicJobSchedule.put(23, 0);

        PeriodicProjectCronExpressionAnalyzer.populateJobTimeSchedule(minutes, hours, job, periodicJobSchedule, minutes.length);

    }

    @Test
    public void testExtractHoursFromCronSchedule_PositiveCase() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testExtractHoursFromCronSchedule_PositiveCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String[] schedule = new String[6];
        schedule[0] = "seconds:*";
        schedule[1] = "minutes:2,4,6,8,10";
        schedule[2] = "hours:0,4,8,12,16";
        schedule[3] = "days:1-4";
        schedule[4] = "months:*";
        schedule[5] = "years:*";

        String[] hours = PeriodicProjectCronExpressionAnalyzer.extractHoursFromCronSchedule(schedule);
        assertEquals(hours[0], "0");
        assertEquals(hours[1], "4");
        assertEquals(hours[2], "8");
        assertEquals(hours[3], "12");
        assertEquals(hours[4], "16");

    }


    @Test
    public void testExtractHoursFromCronSchedule_NegativeCase() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testExtractHoursFromCronSchedule_NegativeCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String[] schedule = new String[2];
        schedule[0] = "seconds:*";
        schedule[1] = "minutes:2,4,6,8,10";

        String[] hours = PeriodicProjectCronExpressionAnalyzer.extractHoursFromCronSchedule(schedule);
        assertTrue((hours == null));


    }

    @Test
    public void testExtractMinutesFromCronSchedule_PositiveCase() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testExtractMinutesFromCronSchedule_PositiveCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String[] schedule = new String[6];
        schedule[0] = "seconds:*";
        schedule[1] = "minutes:2,4,6,8,10";
        schedule[2] = "hours:0,4,8,12,16";
        schedule[3] = "days:1-4";
        schedule[4] = "months:*";
        schedule[5] = "years:*";

        String[] minutes = PeriodicProjectCronExpressionAnalyzer.extractMinutesFromCronSchedule(schedule);
        assertEquals(minutes[0], "2");
        assertEquals(minutes[1], "4");
        assertEquals(minutes[2], "6");
        assertEquals(minutes[3], "8");
        assertEquals(minutes[4], "10");

    }


    @Test
    public void testExtractMinutesFromCronSchedule_NegativeCase() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testExtractMinutesFromCronSchedule_NegativeCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String[] schedule = new String[1];
        schedule[0] = "seconds:*";


        String[] minutes = PeriodicProjectCronExpressionAnalyzer.extractMinutesFromCronSchedule(schedule);
        assertTrue((minutes == null));

    }


    @Test
    public void testparseQuartzCronExpression_PostiveCase() throws ParseException {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for parseQuartzCronExpression_PostiveCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String expression = "* */2 * * * ?";
        CronExpression expr = new CronExpression(expression);

        String[] schedule = PeriodicProjectCronExpressionAnalyzer.parseQuartzCronExpression(expr);

        assertTrue((schedule.length > 0));

    }


    @Test
    public void testparseQuartzCronExpression_NegativeCase() throws ParseException {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for parseQuartzCronExpression_NegativeCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String expression = "* */2 * * * * ?";
        CronExpression expr = null;

        String[] schedule = PeriodicProjectCronExpressionAnalyzer.parseQuartzCronExpression(expr);

        assertTrue((schedule == null));

    }


    @Test
    public void testGetOccurrencesPerHour_PositiveCase() throws ParseException {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testGetOccurrencesPerHour_PositiveCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String expression = "* */2 * * * ?";
        CronExpression expr = new CronExpression(expression);

        String[] schedule = PeriodicProjectCronExpressionAnalyzer.parseQuartzCronExpression(expr);

        assertTrue((schedule.length > 0));

        int occurences = PeriodicProjectCronExpressionAnalyzer.getOccurrencesPerHour(schedule);

        assertTrue((occurences > 0));

    }

    @Test
    public void GetOccurrencesPerHou_NegativeCase() throws ParseException {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for parseQuartzCronExpression_NegativeCase");
        System.out.println("-----------------------------------------------------------------------------------------");

        String expression = "* */2 * * * * ?";
        CronExpression expr = null;

        String[] schedule = PeriodicProjectCronExpressionAnalyzer.parseQuartzCronExpression(expr);

        assertTrue((schedule == null));

        int occurences = PeriodicProjectCronExpressionAnalyzer.getOccurrencesPerHour(schedule);

        assertTrue((occurences == 0));

    }


    @Test
    public void testPreprocessCronExpression() {

        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testPreprocessCronExpression");
        System.out.println("-----------------------------------------------------------------------------------------");

        String expression = "* 2 * * *";

        String processedExpression = PeriodicProjectCronExpressionAnalyzer.preprocessCronExpression(expression);

        System.out.println("#debug: " + processedExpression);

        assertEquals(processedExpression, "*/1 2 * *");

        expression = "* * * * *";

        processedExpression = PeriodicProjectCronExpressionAnalyzer.preprocessCronExpression(expression);

        assertEquals(processedExpression, "*/1 */1 * *");

        expression = "0 1 * * *";

        processedExpression = PeriodicProjectCronExpressionAnalyzer.preprocessCronExpression(expression);

        System.out.println("#debug: " + processedExpression);

        assertEquals(processedExpression, "0 1 * *");

        expression = "0 * * * *";

        processedExpression = PeriodicProjectCronExpressionAnalyzer.preprocessCronExpression(expression);

        System.out.println("#debug: " + processedExpression);

        assertEquals(processedExpression, "0 */1 * *");


        expression = null;

        processedExpression = PeriodicProjectCronExpressionAnalyzer.preprocessCronExpression(expression);

        System.out.println("#debug: " + processedExpression);


        assertNull(processedExpression);

    }

    @Test
    public void testAnalyzePeriodicJobSchedule_PositiveCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testAnalyzePeriodicJobSchedule_PositiveCases()");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        PeriodicProjectSpecification job2 = new PeriodicProjectSpecification("job-2", "periodic",
                "* * * * *", "localhost:8080\\jobs\\job-2", 22, 100.0);

        PeriodicProjectSpecification job3 = new PeriodicProjectSpecification("job-3", "periodic",
                "10 * * * *", "localhost:8080\\jobs\\job-3", 22, 100.0);

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();


        List<PeriodicProjectSpecification> jobList = new ArrayList<PeriodicProjectSpecification>();
        jobList.add(job1);
        jobList.add(job2);
        jobList.add(job3);


        PeriodicProjectCronExpressionAnalyzer.analyzePeriodicJobSchedule(periodicJobSchedule, jobList);


    }

    @Test
    public void testAnalyzePeriodicJobSchedule_NegativeCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testAnalyzePeriodicJobSchedule_NegativeCases()");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job3 = new PeriodicProjectSpecification("job-3", "periodic",
                "90 * * * *", "localhost:8080\\jobs\\job-3", 22, 100.0);

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();


        List<PeriodicProjectSpecification> jobList = new ArrayList<PeriodicProjectSpecification>();

        jobList.add(job3);


        PeriodicProjectCronExpressionAnalyzer.analyzePeriodicJobSchedule(periodicJobSchedule, jobList);
    }


    @Test
    public void testAnalyzeBalancedPeriodicJobSchedule_PositiveCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testAnalyzeBalancedPeriodicJobSchedule_PositiveCases()");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        PeriodicProjectSpecification job2 = new PeriodicProjectSpecification("job-2", "periodic",
                "* * * * *", "localhost:8080\\jobs\\job-2", 22, 100.0);

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();

        job1.setBalancedSpec("13 5 * * *");
        job2.setBalancedSpec("*/6 */6 * * *");


        List<PeriodicProjectSpecification> jobList = new ArrayList<PeriodicProjectSpecification>();
        jobList.add(job1);
        jobList.add(job2);

        PeriodicProjectCronExpressionAnalyzer.analyzePeriodicBalancedJobSchedule(periodicJobSchedule, jobList);

    }


    @Test
    public void testAnalyzeBalancedPeriodicJobSchedule_NegativeCases() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testAnalyzeBalancedPeriodicJobSchedule_NegativeCases()");
        System.out.println("-----------------------------------------------------------------------------------------");

        PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic",
                "@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
        PeriodicProjectSpecification job2 = new PeriodicProjectSpecification("job-2", "periodic",
                "* * * * *", "localhost:8080\\jobs\\job-2", 22, 100.0);

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();

        job1.setBalancedSpec("13 5 * * *");
        job2.setBalancedSpec("90 */6 * * *");


        List<PeriodicProjectSpecification> jobList = new ArrayList<PeriodicProjectSpecification>();
        jobList.add(job1);
        jobList.add(job2);

        PeriodicProjectCronExpressionAnalyzer.analyzePeriodicBalancedJobSchedule(periodicJobSchedule, jobList);

        System.out.println(PeriodicProjectCronExpressionAnalyzer.class);


    }


}
