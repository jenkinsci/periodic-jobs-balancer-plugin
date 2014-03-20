package com.paypal.jenkinsci.project.periodic;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PeriodicProjectScheduleTest {
	
	@Test
	public void testCreateInstance(){
		
		System.out.println("-----------------------------------------------------------------------------------------");
		System.out.println("Test Cases for testCreateInstance()");
		System.out.println("-----------------------------------------------------------------------------------------");
		
		PeriodicProjectSpecification job1 = new PeriodicProjectSpecification("job-1", "periodic", 
				"@midnight", "localhost:8080\\jobs\\job-1", 22, 100.0);
		
		System.out.println(job1.getBuildTriggersSchedule().getProjectName());
		
		assertNotNull(job1.getBuildTriggersSchedule().getSchedule());
		
		
		
	}

}
