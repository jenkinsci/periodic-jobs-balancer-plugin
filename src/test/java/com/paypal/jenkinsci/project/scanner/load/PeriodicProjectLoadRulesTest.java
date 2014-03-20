package com.paypal.jenkinsci.project.scanner.load;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PeriodicProjectLoadRulesTest {


    @Test
    public void testDoesBuildOnceAndOnlyAtMidnight() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for testDoesBuildOnceAndOnlyAtMidnight");
        System.out.println("-----------------------------------------------------------------------------------------");

        String spec = "@midnight";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));

        spec = "11 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));

        spec = "*/2 0 * * *";

        assertFalse(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));

        spec = "0-10 0 * * *";

        assertFalse(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));


        spec = "0 0 * *";

        assertFalse(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));

        spec = null;

        assertFalse(PeriodicProjectLoadRules.doesBuildOnceAndOnlyAtMidnight(spec));

    }


    @Test
    public void testisBuildBalancedByJenkinsCI() {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Test Cases for BuildBalancedByJenkinsCI");
        System.out.println("-----------------------------------------------------------------------------------------");

        String spec = "H H(0-12) * * *";

        assertTrue(PeriodicProjectLoadRules.isBuildSelfBalanced(spec));
    }


    @Test
    public void testDoesBuildOnlyAtMidnight() {

        String spec = "* 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));

        spec = "*/5 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));

        spec = "2,4,5,6 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));


        spec = "2-6 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));

        spec = "59 0 * * *";

        assertTrue(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));

        spec = "59 * * * *";

        assertFalse(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));

        spec = "0 * * * *";

        assertFalse(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));


        spec = null;

        assertFalse(PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec));


    }

    @Test
    public void testDoesBuildOnceEveryHour() {

        String spec = "@hourly";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "0 * * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "59 0-23 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "59 */1 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "59 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "*/5 0-23 * * *";
        assertFalse(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = "60 0-23 * * *";
        assertFalse(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));

        spec = null;
        assertFalse(PeriodicProjectLoadRules.doesBuildOnceEveryHour(spec));


    }

    @Test
    public void testDoesBuildMuiltpleTimeEveryHour() {

        String spec = "* 0-23 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = "* * * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = "*/5 * * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = "0-10 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = "0,10 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = null;
        assertFalse(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));

        spec = "* 1 * * *";
        assertFalse(PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec));


    }


    @Test
    public void testDoesBuildNtimesInMhoursIntheDay() {

        String spec = "*/2 */2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "*/2 0-2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "*/2 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));


        spec = "1,2 */2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1,2 0-2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1,2 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));


        spec = "1-2 */2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1,2 0-2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1,2 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "* 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1,2 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1-2 0-2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "1-2 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = null;
        assertFalse(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));

        spec = "";
        assertFalse(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec));


    }

    @Test
    public void testDoesBuildOnceInMhoursIntheDay() {

        String spec = "10 */2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(spec));

        spec = "10 0-2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(spec));

        spec = "10 0,2 * * *";
        assertTrue(PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(spec));

        spec = "70 0,2 * * *";
        assertFalse(PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(spec));

        assertFalse(PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(null));


    }

}
