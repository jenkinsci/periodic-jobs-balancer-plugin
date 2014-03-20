package com.paypal.jenkinsci.project.periodic;

import com.paypal.jenkinsci.project.scanner.load.PeriodicProjectLoadRules;
import com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil;


public class PeriodicProjectSpecification {

    private String jobName;
    private String periodicType;
    private String spec;
    private String url;
    private PeriodicProjectSchedule buildTriggersSchedule;
    private String frequency;
    private String iconFileName;
    private int buildCount;
    private double buildDurationAverage;
    private String balancedSpec;

    public PeriodicProjectSpecification(String name, String periodic,
                                        String specification, String jobUrl, int buildCount, double buildDurationAverage) {
        jobName = name;
        periodicType = periodic;
        spec = specification;
        url = jobUrl;
        buildTriggersSchedule = new PeriodicProjectSchedule();
        buildTriggersSchedule.setProjectName(name);
        this.buildCount = buildCount;
        this.buildDurationAverage = buildDurationAverage;
        this.balancedSpec = spec;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getPeriodicType() {
        return periodicType;
    }

    public void setPeriodicType(String periodicType) {
        this.periodicType = periodicType;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PeriodicProjectSchedule getBuildTriggersSchedule() {
        return buildTriggersSchedule;
    }

    public String getProjectsTriggersScheduleAsString() {

        if (this.getBuildTriggersSchedule().getSchedule().size() > 0) {
            StringBuilder str = new StringBuilder();
            for (String time : this.getBuildTriggersSchedule().getSchedule()) {
                str.append(time);
                str.append(", ");
            }

            return str.toString().substring(0, str.toString().lastIndexOf(","));
        }

        return "Once in the Day scheduled by Jenkins";

    }

    public String getFrequency() {

        // Get the frequency based on the length of the number of time triggers
        // Please make sure that the Collection clears before updation
        if (buildTriggersSchedule.getSchedule().size() <= 6) {
            frequency = "Low";
        } else if (buildTriggersSchedule.getSchedule().size() > 6 &&
                buildTriggersSchedule.getSchedule().size() <= 12) {
            frequency = "Medium Low";
        } else if (buildTriggersSchedule.getSchedule().size() > 12 &&
                buildTriggersSchedule.getSchedule().size() <= 24) {
            frequency = "Medium High";
        } else if (buildTriggersSchedule.getSchedule().size() > 24 &&
                buildTriggersSchedule.getSchedule().size() <= 36) {
            frequency = "High";
        } else if (buildTriggersSchedule.getSchedule().size() > 36 &&
                buildTriggersSchedule.getSchedule().size() <= 60) {
            frequency = "Very High";
        } else {
            frequency = "Extremely High";
        }

        return frequency;
    }

    // Get it as Icon fileName


    public String getIconFileName() {

        // Get the frequency based on the length of the number of time triggers
        // Please make sure that the Collection clears before updation

        if (buildTriggersSchedule.getSchedule().size() <= 6) {
            iconFileName = PropertiesUtil.getInstance().getProperty("lowFrequencyIconFileName");
        } else if (buildTriggersSchedule.getSchedule().size() > 6 &&
                buildTriggersSchedule.getSchedule().size() <= 12) {
            iconFileName = PropertiesUtil.getInstance().getProperty("mediumLowFrequencyIconFileName");
        } else if (buildTriggersSchedule.getSchedule().size() > 12 &&
                buildTriggersSchedule.getSchedule().size() <= 24) {
            iconFileName = PropertiesUtil.getInstance().getProperty("mediumHighFrequencyIconFileName");
        } else if (buildTriggersSchedule.getSchedule().size() > 24 &&
                buildTriggersSchedule.getSchedule().size() <= 36) {
            iconFileName = PropertiesUtil.getInstance().getProperty("highFrequencyIconFileName");
        } else if (buildTriggersSchedule.getSchedule().size() > 36 &&
                buildTriggersSchedule.getSchedule().size() <= 60) {
            iconFileName = PropertiesUtil.getInstance().getProperty("veryHighFrequencyIconFileName");
        } else {
            iconFileName = PropertiesUtil.getInstance().getProperty("extHighFrequencyIconFileName");
        }

        return iconFileName;
    }

    public int getProjectFrequencyrCount() {

        // Add the case when the spec is based on the new Jenkins 1.5.09
        if (PeriodicProjectLoadRules.isBuildSelfBalanced(spec)) {
            return 1;
        }

        return buildTriggersSchedule.getSchedule().size();
    }

    public int getBuildCount() {
        return buildCount;
    }

    public void setBuildCount(int buildCount) {
        this.buildCount = buildCount;
    }

    public double getBuildDurationAverage() {
        return buildDurationAverage;
    }

    public void setBuildDurationAverage(double buildDurationAverage) {
        this.buildDurationAverage = buildDurationAverage;
    }

    public String getBalancedSpec() {
        return balancedSpec;
    }

    public void setBalancedSpec(String balancedSpec) {
        this.balancedSpec = balancedSpec;
    }

}
