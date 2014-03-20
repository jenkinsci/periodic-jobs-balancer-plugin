package com.paypal.jenkinsci.project.scanner.load.balancing;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PeriodicProjectLoadBalancingStrategyTest {

    @Test
    public void testCalculateBalancedSchedule() {

        String spec = "*/5 */2 * * *";
        double minutesReductionFactor = 2;
        double hoursReductionFactor = 2;
        String option = "Midnight-Only";
        String balancedSpec = null;
        int window = 6;
        Map<Integer, Integer> periodicJobSchedule = createTestPeriodicJobSchedule();
        String strategy = "Midnight;Hourly";

        balancedSpec = PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, window,
                option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "@midnight";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, window,
                option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(null, strategy, spec, window,
                option, minutesReductionFactor, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, null, spec, window,
                option, minutesReductionFactor, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, null, window,
                option, minutesReductionFactor, hoursReductionFactor));


        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, 0,
                option, minutesReductionFactor, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, window,
                null, minutesReductionFactor, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, window,
                option, 0.0, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule(periodicJobSchedule, strategy, spec, window,
                option, minutesReductionFactor, 0.0));


    }


    @Test
    public void testPeformMidnightBalancingStrategy() {

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();
        String strategy = "Midnight";
        String spec = "@midnight";
        int window = 6;

        periodicJobSchedule.put(0, 20);
        periodicJobSchedule.put(1, 10);
        periodicJobSchedule.put(2, 10);
        periodicJobSchedule.put(3, 10);
        periodicJobSchedule.put(4, 10);
        periodicJobSchedule.put(5, 5);
        periodicJobSchedule.put(6, 15);
        periodicJobSchedule.put(7, 10);
        periodicJobSchedule.put(8, 5);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 2);
        periodicJobSchedule.put(11, 3);
        periodicJobSchedule.put(12, 7);
        periodicJobSchedule.put(13, 6);
        periodicJobSchedule.put(14, 4);
        periodicJobSchedule.put(15, 2);
        periodicJobSchedule.put(16, 3);
        periodicJobSchedule.put(17, 7);
        periodicJobSchedule.put(18, 6);
        periodicJobSchedule.put(19, 4);
        periodicJobSchedule.put(20, 2);
        periodicJobSchedule.put(21, 3);
        periodicJobSchedule.put(22, 7);
        periodicJobSchedule.put(23, 6);


        System.out.println("When the Job only builds once @ Midnight");
        String balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);

        System.out.println("Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);
        assertTrue(balancedSpec.endsWith("* * *"));


        System.out.println("When the Job only builds @ Midnight");

        spec = "*/2 0 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);
        System.out.println("Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "* 0 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);
        System.out.println("Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "0-15 0 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);
        System.out.println("Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "0,1,2 0 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);
        System.out.println("Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = null;
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformMidnightBalancingStrategy(periodicJobSchedule, spec, window);
        System.out.println("Balanced Spec : " + balancedSpec);
        assertNull(balancedSpec);


    }


    @Test
    public void testGetBuildMinuesProfile() {

        String spec = "*/2 0 * * *";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));

        spec = "* 0 * * *";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));

        spec = "0-15 0 * * *";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));

        spec = "0,1,2 0 * * *";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));

        spec = "1 * * * *";
        assertNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));


        assertNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(null));

        spec = "1 * * * * *";
        assertNull(PeriodicProjectLoadBalancingStrategy.getBuildMinuesProfile(spec.split(" ")));


    }


    @Test
    public void testGetHoursProfileWhenJobBuildsMultipleTimesInTheDay() {

        String hoursSubspec = "*/5";
        double hoursReductionFactor = 2;
        assertTrue(PeriodicProjectLoadBalancingStrategy.getHoursProfileWhenJobBuildsMultipleTimesInTheDay(hoursSubspec, hoursReductionFactor).length > 0);

        hoursSubspec = "0-6";
        hoursReductionFactor = 4;
        assertTrue(PeriodicProjectLoadBalancingStrategy.getHoursProfileWhenJobBuildsMultipleTimesInTheDay(hoursSubspec, hoursReductionFactor).length > 0);

        hoursSubspec = "0,1,2,3,5";
        hoursReductionFactor = 2;
        assertTrue(PeriodicProjectLoadBalancingStrategy.getHoursProfileWhenJobBuildsMultipleTimesInTheDay(hoursSubspec, hoursReductionFactor).length > 0);

        hoursReductionFactor = 2;
        assertNull(PeriodicProjectLoadBalancingStrategy.getHoursProfileWhenJobBuildsMultipleTimesInTheDay(null, hoursReductionFactor));

        hoursSubspec = "0,1,2,3,5";
        hoursReductionFactor = 0;
        assertNull(PeriodicProjectLoadBalancingStrategy.getHoursProfileWhenJobBuildsMultipleTimesInTheDay(hoursSubspec, hoursReductionFactor));


    }


    @Test
    public void testGetMinuteStringWhenJobBuildsMultipleTimesInTheDay() {

        String minutesSubspec = "*/5";
        double minutesReductionFactor = 2;

        assertNotNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(minutesSubspec, minutesReductionFactor));

        minutesSubspec = "1-6";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(minutesSubspec, minutesReductionFactor));

        minutesSubspec = "1,2,3,4";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(minutesSubspec, minutesReductionFactor));

        minutesSubspec = "10";
        assertNotNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(minutesSubspec, minutesReductionFactor));


        minutesReductionFactor = 0;
        assertNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(minutesSubspec, minutesReductionFactor));

        minutesReductionFactor = 2;
        assertNull(PeriodicProjectLoadBalancingStrategy.getMinuteStringWhenJobBuildsMultipleTimesInTheDay(null, minutesReductionFactor));

    }


    @Test
    public void testGetBalancedSpecWhenJobBuildMultipleTimesInTheDay() {

        String spec = "*/5 */2 * * *";
        double minutesReductionFactor = 2;
        double hoursReductionFactor = 2;
        String option = "Midnight-Only";
        String balancedSpec;

        Map<Integer, Integer> periodicJobSchedule = createTestPeriodicJobSchedule();

        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Only";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);
        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Plus-Midnight";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);
        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "*/10 0,2 * * *";
        option = "Hot-Spots-Only";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);
        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        spec = "*/10 0,2 * * *";
        option = "Hot-Spots-Plus-Midnight";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);
        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        // This is an experimental case:

        spec = "*/1 0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18,19,20 * * *";
        option = "Hot-Spots-Plus-Midnight";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(spec.split(" "), option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule);
        System.out.println("#####New Balanced Spec : " + balancedSpec);


        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimesInTheDay(null, option, minutesReductionFactor,
                hoursReductionFactor, periodicJobSchedule));


    }

    @Test
    public void testGetBalancedSpecWhenJobBuildMultipleTimeAtMidnight() {

        String spec = "*/5 0 * * *";
        double minutesReductionFactor = 2;
        double hoursReductionFactor = 2;
        String option = "Midnight-Only";
        String balancedSpec;
        Map<Integer, Integer> periodicJobSchedule = createTestPeriodicJobSchedule();


        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Only";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Plus-Midnight";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(spec.split(" "), option, 0, periodicJobSchedule));

        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(null, option, 0, periodicJobSchedule));

        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(null, option, minutesReductionFactor, periodicJobSchedule));


    }


    @Test
    public void testGetBalancedSpecWhenJobBuildEveryHour() {

        String spec = "*/5 * * * *";
        double minutesReductionFactor = 2;
        String option = "Midnight-Only";
        String balancedSpec;
        Map<Integer, Integer> periodicJobSchedule = createTestPeriodicJobSchedule();

        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "*/5 0-23 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Only";
        spec = "*/5 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        spec = "*/5 */1 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "0-5 */1 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Plus-Midnight";
        spec = "0,2,4,6 */1 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        option = "Hot-Spots-Plus-Midnight";
        spec = "* * * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(spec.split(" "), option, minutesReductionFactor,
                periodicJobSchedule);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(null, option, minutesReductionFactor,
                periodicJobSchedule));


        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(null, option, 0, periodicJobSchedule));

        assertNull(PeriodicProjectLoadBalancingStrategy.getBalancedSpecWhenJobBuildEveryHour(null, option, minutesReductionFactor, periodicJobSchedule));


    }


    @Test
    public void testPeformHourlyBalancingStrategy() {

        String spec = "*/5 * * * *";
        double minutesReductionFactor = 2;
        double hoursReductionFactor = 2;
        String option = "Midnight-Only";
        String balancedSpec;
        Map<Integer, Integer> periodicJobSchedule = createTestPeriodicJobSchedule();

        // Negative Scnearios

        assertNull(PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                null, option, minutesReductionFactor, hoursReductionFactor));


        assertNull(PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, 0, hoursReductionFactor));

        assertNull(PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, 0, 0));

        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        spec = "0-10 */1 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        spec = "0 */1 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);


        spec = "*/5 */2 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

        spec = "1 */2 * * *";
        balancedSpec = PeriodicProjectLoadBalancingStrategy.peformHourlyBalancingStrategy(periodicJobSchedule,
                spec, option, minutesReductionFactor, hoursReductionFactor);

        System.out.println("New Balanced Spec : " + balancedSpec);
        assertNotNull(balancedSpec);

    }


    @Test
    public void testisValidMinuteRange() {

        assertFalse(PeriodicProjectLoadBalancingStrategy.isValidMinuteRange(null));
        assertFalse(PeriodicProjectLoadBalancingStrategy.isValidMinuteRange("70"));
        assertTrue(PeriodicProjectLoadBalancingStrategy.isValidMinuteRange("59"));


    }


    private Map<Integer, Integer> createTestPeriodicJobSchedule() {

        Map<Integer, Integer> periodicJobSchedule = new HashMap<Integer, Integer>();


        periodicJobSchedule.put(0, 200);
        periodicJobSchedule.put(1, 10);
        periodicJobSchedule.put(2, 100);
        periodicJobSchedule.put(3, 10);
        periodicJobSchedule.put(4, 10);
        periodicJobSchedule.put(5, 5);
        periodicJobSchedule.put(6, 15);
        periodicJobSchedule.put(7, 10);
        periodicJobSchedule.put(8, 5);
        periodicJobSchedule.put(9, 1);
        periodicJobSchedule.put(10, 2);
        periodicJobSchedule.put(11, 3);
        periodicJobSchedule.put(12, 7);
        periodicJobSchedule.put(13, 6);
        periodicJobSchedule.put(14, 4);
        periodicJobSchedule.put(15, 2);
        periodicJobSchedule.put(16, 3);
        periodicJobSchedule.put(17, 7);
        periodicJobSchedule.put(18, 6);
        periodicJobSchedule.put(19, 4);
        periodicJobSchedule.put(20, 2);
        periodicJobSchedule.put(21, 3);
        periodicJobSchedule.put(22, 7);
        periodicJobSchedule.put(23, 6);

        return periodicJobSchedule;

    }

}
