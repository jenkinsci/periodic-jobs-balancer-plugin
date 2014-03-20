package com.paypal.jenkinsci.project.scanner.load;

import java.util.logging.Level;
import java.util.logging.Logger;


public final class PeriodicProjectLoadRules {
	
	 private static final Logger logger = Logger.getLogger(PeriodicProjectLoadRules.class.getName());
	 
	 private PeriodicProjectLoadRules(){}

	 // This rule is related to the self-balancing periodic spec proposed by Jenkins
	 public static boolean isBuildSelfBalanced(String spec){
			String[] schedule = spec.split(" ");
			logger.log(Level.INFO,"isBuildSelfBalanced :" +spec + " ?");
		
			return (schedule[0].equals("H") && 
					schedule[1].startsWith("H(")
					);
		 
		 
	 }
	public static boolean doesBuildOnceAndOnlyAtMidnight(String spec){
		
		if(null != spec){
			String[] schedule = spec.split(" ");
			logger.log(Level.INFO,"doesBuildOnceAndOnlyAtMidnigh :" +spec + " ?");
			
			boolean midnightCaseOne = (("@midnight".equals(spec) || "@daily".equals(spec)));
			boolean midnightCaseTwo = ((schedule.length == 5) && "0".equals(schedule[1]) && isNumeric((schedule[0])));

			if(midnightCaseOne || midnightCaseTwo)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean doesBuildOnlyAtMidnight(String spec) {

		if (null != spec) {
			String[] schedule = spec.split(" ");
			logger.log(Level.INFO, "doesBuildOnlyAtMidnight :" + spec + " ?");

			boolean condition1 = (schedule.length == 5)
					&& ("0".equals(schedule[1]));
			boolean condition2 = ((schedule[0].split(",").length > 1)
					|| (schedule[0].startsWith("*")) || (schedule[0].split("-").length == 2));
			if (condition1 && (condition2 || isValidMinuteRange(schedule[0]))) {

				return true;

			}
		}
		return false;

	}
	
	
	public static boolean doesBuildOnceEveryHour(String spec){
				
		if(null != spec){
			String[] schedule = spec.split(" ");
			logger.log(Level.INFO,"doesBuildOnceEveryHour :" +spec + " ?");

			if((schedule.length == 1) && "@hourly".equals(schedule[0])){
				logger.log(Level.INFO,"#DEBUG : the cron expression is : "+spec);
				return true;
			}else if(schedule.length > 1 && (isValidMinuteRange(schedule[0]))){ 
				
				if(("*".equals(schedule[1]))
						|| ("*/1".equals(schedule[1]))
						|| ("0-23".equals(schedule[1]))
						|| ("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23".equals(schedule[1]))){
					return true;
					
				}				
			}
						
			return false;
		}else{
			return false;
		}
		
	}
	
	
	public static boolean doesBuildMuiltpleTimeEveryHour(String spec){
		
		boolean result = false;

		if(null != spec){
			
			String[] schedule = spec.split(" ");

			if(schedule.length > 1 && isHourly(schedule[1])){ 
				if(schedule[0].startsWith("*")){
					result = true;
				}else if(schedule[0].split(",").length > 1){
					result = true;
				}else if(schedule[0].split("-").length > 1){
					result = true;
				}
			}
			
			logger.log(Level.INFO,"doesBuildMuiltpleTimeEveryHour: "+spec + " ? "+result);

			return result;
		}
		return result;
		
	}
	
	
	
	public static boolean doesBuildNtimesInMhoursIntheDay(String spec){
		boolean result = false;
		if(spec != null){
			String[] schedule = spec.split(" ");
						
			if(schedule.length > 1){
				
				boolean minutesStartWithAstrick = ((schedule[0].startsWith("*/")) || (schedule[0].startsWith("*")));
				boolean minutesIsCommaSeperated = (schedule[0].split(",").length > 1);
				boolean minutesIsHyphenSeperated =  (schedule[0].split("-").length > 1); 
				boolean hoursStartsWithAstrick = schedule[1].startsWith("*/");
				boolean hourIsCommaSerperated = (schedule[1].split(",").length > 1);
				boolean hoursIsHyphenSeperated =  (schedule[1].split("-").length > 1); 
				
				if(minutesStartWithAstrick && hoursStartsWithAstrick){
					result = true;
				} if(minutesStartWithAstrick && hourIsCommaSerperated){
					result = true;
				} if((minutesStartWithAstrick && hoursStartsWithAstrick)){
					result = true;
				} if((minutesStartWithAstrick && hourIsCommaSerperated)){
					result = true;
				} if ((minutesStartWithAstrick && hoursIsHyphenSeperated)){
					result = true;
				} if((minutesIsCommaSeperated && hoursStartsWithAstrick)){
					result = true;
				} if((minutesIsCommaSeperated && hourIsCommaSerperated)){
					result = true;
				} if((minutesIsCommaSeperated && hoursIsHyphenSeperated)){
					result = true;
				} if((minutesIsHyphenSeperated && hoursStartsWithAstrick)){
					result = true;
				} if((minutesIsHyphenSeperated && hourIsCommaSerperated)){
					result = true;
				} if((minutesIsHyphenSeperated && hoursIsHyphenSeperated)){
					result = true;
				}
				
			}			
			logger.log(Level.INFO,"doesBuildNtimesInMhoursIntheDay: "+spec + " ? "+result);
		}		
		return result;
		
	} 
	
	
	public static boolean doesBuildOnceInMhoursIntheDay(String spec){
		boolean result = false;
		if(spec != null){
			String[] schedule = spec.split(" ");
			
			if(schedule.length > 1 && (isValidMinuteRange(schedule[0]))){
				if((schedule[1].startsWith("*/")) ||
					((schedule[1].split(",").length > 1)) ||
					((schedule[1].split("-").length > 1))	
					){
						result =  true;				
				}			
			}
			
			logger.log(Level.INFO,"doesBuildOnceInMhoursIntheDay: "+spec + " ? "+result);

			return result;
		}
		
		return result;
		
	}
	
	
	
	
	private static boolean isNumeric(String value)
	 {
	    	return (value.matches("^[0-9]+$"));
	 }
	
	private static boolean isValidMinuteRange(String value){
		 
		if(isNumeric(value)){			
		 int intVal= Integer.parseInt(value);
		 if(intVal >= 0 && intVal < 60){
			 return true;
		 	}
		 }
		return false;
		
	}
	

	private static boolean isHourly(String schedule){
		
		
		if(("*".equals(schedule) || "*/1".equals(schedule)) ||
				"0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23".equals(schedule) ||
				"0-23".equals(schedule) ){
			return true;
			
		}
		return false;
	}
	
}
