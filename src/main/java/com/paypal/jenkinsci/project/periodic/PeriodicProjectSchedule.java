package com.paypal.jenkinsci.project.periodic;

import java.util.ArrayList;
import java.util.List;


// This class models the schedule of a Periodic Project 
public class PeriodicProjectSchedule {
    private String projectName;
    private List<String> schedule;


    public PeriodicProjectSchedule() {
        schedule = new ArrayList<String>();
    }


    public String getProjectName() {
        return projectName;
    }


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public List<String> getSchedule() {
        return schedule;
    }


}
