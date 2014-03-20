package com.paypal.jenkinsci.project.periodic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CronExpression;

import com.paypal.jenkinsci.project.scanner.load.PeriodicProjectLoadRules;



public final class PeriodicProjectCronExpressionAnalyzer {
	 private static final Logger logger = Logger.getLogger(PeriodicProjectCronExpressionAnalyzer.class.getName());
	 private static String blank = " ";

	 private PeriodicProjectCronExpressionAnalyzer(){}
	 
	 @SuppressWarnings("null")
	public static void analyzePeriodicJobSchedule(Map<Integer, Integer> periodicJobSchedule,
			List<PeriodicProjectSpecification> periodicJobList) {
		
		for (int i = 0; i < 24; i++) {
			periodicJobSchedule.put(i, 0);
		}
		
		for (PeriodicProjectSpecification job : periodicJobList) {

			String cronSchedule = job.getSpec();

			if(isValidCronExpression(cronSchedule)){
				// Consider all possible corner-cases			
					if(!scanForPredfinedSchedule(job,cronSchedule,periodicJobSchedule, false)) {
						try {
							
							cronSchedule = preprocessCronExpression(cronSchedule);
							
							// Convert from Jenkins Expression to Quartz expression
							CronExpression expr = new CronExpression("* "+ cronSchedule.trim() + " ?");
							// Use for dubugging purposes
							
							// Parse the Quartz Expression to determine the periodic job build schedule   
							// Note the Method getExpressionSummary() : will list schedule in list fashion
							String[] sched = parseQuartzCronExpression(expr);											
							
							// Occurrences in each hour 
							int occurences =  getOccurrencesPerHour(sched);							
							String[] minutes = extractMinutesFromCronSchedule(sched);
							String[] hours = extractHoursFromCronSchedule(sched);							
							
							// This is a special case
						
							populateJobTimeSchedule(minutes, hours, job, periodicJobSchedule, occurences);

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							logger.log(Level.SEVERE, e.getMessage());

						}
					}
				
			}
				
		}

	}
	
	
	
	
	/**
	 * Returns Pre-Processed Jenkins Cron Schedule
	 */
	
	 
	public static String preprocessCronExpression(String cronSchedule){
	
		String processedCronSchedlule = cronSchedule;	
		
		if(processedCronSchedlule != null){					
			String[] schedule = processedCronSchedlule.split(" ");
			StringBuffer strBuffer = new StringBuffer();
			boolean allMinutes = schedule[0].equals("*");
			boolean allHours = schedule[1].equals("*");
			
			if((schedule.length == 5)){
				
				if(allMinutes && allHours){
				strBuffer.append("*/1 */1");
				strBuffer.append(blank);
				strBuffer.append(schedule[2]);
				strBuffer.append(blank);
				strBuffer.append(schedule[3]);
				processedCronSchedlule = strBuffer.toString();
				}else if(allMinutes && !allHours){
				strBuffer.append("*/1");
				strBuffer.append(blank);
				strBuffer.append(schedule[1]);
				strBuffer.append(blank);
				strBuffer.append(schedule[2]);
				strBuffer.append(blank);
				strBuffer.append(schedule[3]);
				processedCronSchedlule = strBuffer.toString();
				}
				else if(!allMinutes && allHours){
					strBuffer.append(schedule[0]);
					strBuffer.append(blank);
					strBuffer.append("*/1");
					strBuffer.append(blank);
					strBuffer.append(schedule[2]);
					strBuffer.append(blank);
					strBuffer.append(schedule[3]);
					processedCronSchedlule = strBuffer.toString();
				}
				else if(!allMinutes && !allHours){
					logger.log(Level.INFO, "Case-4");

					strBuffer.append(schedule[0]);
					strBuffer.append(blank);
					strBuffer.append(schedule[1]);
					strBuffer.append(blank);
					strBuffer.append(schedule[2]);
					strBuffer.append(blank);
					strBuffer.append(schedule[3]);
					processedCronSchedlule = strBuffer.toString();
				}
			}
			
		}
		
		logger.log(Level.INFO, "### Debug: "+ processedCronSchedlule);
		return processedCronSchedlule;
		
	}
	
	public static void analyzePeriodicBalancedJobSchedule(Map<Integer, Integer> periodicJobSchedule,
			List<PeriodicProjectSpecification> balancedPeriodicJobList) {
		
		for (int i = 0; i < 24; i++) {
			periodicJobSchedule.put(i, 0);
		}
		
		for (PeriodicProjectSpecification job : balancedPeriodicJobList) {

			String cronSchedule = job.getBalancedSpec();

			if(isValidCronExpression(cronSchedule)){

				if(!scanForPredfinedSchedule(job,cronSchedule,periodicJobSchedule, true)) {
						try {
							
							cronSchedule = preprocessCronExpression(cronSchedule);
							logger.log(Level.INFO, "### Debug: "+ cronSchedule);
							CronExpression expr = new CronExpression("* "+ cronSchedule.trim() + " ?");
							
							String[] sched = parseQuartzCronExpression(expr);
							
							int occurences =  getOccurrencesPerHour(sched);
							String[] minutes = extractMinutesFromCronSchedule(sched);
							String[] hours = extractHoursFromCronSchedule(sched);							
							
							// This is a special case
							
							populateJobTimeSchedule(minutes, hours, job, periodicJobSchedule, occurences);
		
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							logger.log(Level.SEVERE, e.getMessage());

						}
					}

				
				
			}
				
		}

	}
	
	public static String[] parseQuartzCronExpression(CronExpression expr)
	{
		if(null != expr){
			return  expr.getExpressionSummary().split("\n");		

		}else{
			return null;
		}
		
	}
	
	public static int getOccurrencesPerHour(String[] schedule){
		if((null != schedule) && (schedule.length >=2 )){
			return schedule[1].split(",").length;
		}else{
			return 0;
		}
		 
	}
	
	public static String[] extractMinutesFromCronSchedule(String[] schedule){
		
		if((null != schedule) && (schedule.length >=2 )){
			return schedule[1].substring(schedule[1].indexOf(':')+1).split(",");
		}else{
			return null;
		}	
		 
	}
	
	public static String[] extractHoursFromCronSchedule(String[] schedule){
		
		if((null != schedule) && (schedule.length >=3)){
			return schedule[2].substring(schedule[2].indexOf(':')+1).split(",");
		}else{
			return null;
		}	
		 
	}
	
	
	public static void populateJobTimeSchedule(String[] minutes, String[] hours, PeriodicProjectSpecification job,
			Map<Integer, Integer> periodicJobSchedule, int occurences){
		if(hours.length == 1 && hours[0].trim().equals("*")){
			for(int i = 0 ; i < 24 ; i++){			
				for(String minute : minutes){
					job.getBuildTriggersSchedule().getSchedule().add(""+i+":"+minute.trim());
				}
			}
		}
		else{
			for(String hour : hours){
				for(String minute : minutes){
					job.getBuildTriggersSchedule().getSchedule().add(""+hour.trim()+":"+minute.trim());
				}
			}
		}						
		
		
		for(String hour : hours){
			if(hour.trim().equals("*")){
				for(int i = 0; i < 24;i++){
					int val = periodicJobSchedule.get(i)+occurences;
					periodicJobSchedule.put(i,val);
				}
			}else{
				int val = periodicJobSchedule.get(Integer.parseInt(hour.trim()))+occurences;
				periodicJobSchedule.put(Integer.parseInt(hour.trim()),val);

			}
		}
	}
	
	 	
	// To be trimmed later.
	public static boolean scanForPredfinedSchedule(PeriodicProjectSpecification job, String cronSchedule,
			Map<Integer, Integer> periodicJobSchedule, boolean balanced){
		logger.log(Level.INFO,"### Inside scanForPredfinedSchedule for the Spec: "+cronSchedule);
		if(balanced){
			
			if (cronSchedule.equals("@daily")
					|| cronSchedule.equals("@midnight")
					|| cronSchedule.equals("@weekly")) {
				periodicJobSchedule.put(0, periodicJobSchedule.get(0) + 1);
				return true;
			} else if (cronSchedule.equals("@hourly")) {
				for (int i = 0; i < 24; i++) {
					periodicJobSchedule.put(i,
							periodicJobSchedule.get(i) + 1);
				}
				return true;
			}
			else{
				return false;
			}
		}
		
		else{
			if (cronSchedule.equals("@daily")
					|| cronSchedule.equals("@midnight")
					|| cronSchedule.equals("@weekly")) {
				job.getBuildTriggersSchedule().getSchedule().add("0:0");
				periodicJobSchedule.put(0, periodicJobSchedule.get(0) + 1);
				return true;
			} else if (cronSchedule.equals("@hourly")) {
				logger.log(Level.INFO,"### DEBUG hourly ? "+cronSchedule);

				for (int i = 0; i < 24; i++) {
					periodicJobSchedule.put(i,
							periodicJobSchedule.get(i) + 1);
					job.getBuildTriggersSchedule().getSchedule().add(i+":0");

				}
				return true;
			}else{
				return false;
			}
		}
		
		
	}
	
	
	public static boolean isValidCronExpression(String expr){
		
		if((null == expr) || expr.length() == 0){
			return false;
		}
		else if (expr.equals("@daily")|| expr.equals("@midnight") || expr.equals("@hourly") || expr.equals("@weekly")){
			return true;
		}
		else if((expr.startsWith("*") ||isNumeric(""+expr.charAt(0)+"")) && (expr.split(" ").length == 5)){
			return true;
		}else if (PeriodicProjectLoadRules.isBuildSelfBalanced(expr)){ /* ignore self balanced spec*/
			return false; 
		}
		return false;
		
	}
	
	private static boolean isNumeric(String value)
	 {
	    	return (value.matches("^[0-9]+$"));
	 }
	

}
