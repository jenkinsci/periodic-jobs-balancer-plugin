package com.paypal.jenkinsci.project.periodic;

public class ProjectFrequencySummary {
    private String frequencyIconFileName;
    private String frequencyLevel;
    private int jobCount;
    private int buildCount;
    private String actionString;

    public ProjectFrequencySummary(String frequencyIconFileName,
                                   String frequencyLevel, int jobCount, int buildCount) {
        this.frequencyIconFileName = frequencyIconFileName;
        this.frequencyLevel = frequencyLevel;
        this.jobCount = jobCount;
        this.buildCount = buildCount;

        this.actionString = "calculateBalancedPeriodicProjectsLoadDistribution";
    }

    public String getFrequencyIconFileName() {
        return frequencyIconFileName;
    }

    public void setFrequencyIconFileName(String frequencyIconFileName) {
        this.frequencyIconFileName = frequencyIconFileName;
    }

    public String getFrequencyLevel() {
        return frequencyLevel;
    }

    public void setFrequencyLevel(String frequencyLevel) {
        this.frequencyLevel = frequencyLevel;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    public int getBuildCount() {
        return buildCount;
    }

    public void setBuildCount(int buildCount) {
        this.buildCount = buildCount;
    }

    public String getActionString() {
        return actionString;
    }


}
