package com.paypal.jenkinsci.project.scanner.load.balancing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.paypal.jenkinsci.project.scanner.load.PeriodicProjectLoadRules;



public final class PeriodicProjectLoadBalancingStrategy {
	 private static final Logger logger = Logger.getLogger(PeriodicProjectLoadBalancingStrategy.class.getName());
	private static final String subSpec = " * * *";

	 private PeriodicProjectLoadBalancingStrategy(){}
	 
	public static String calcBalancedSchedule(
			Map<Integer, Integer> periodicJobSchedule, String strategy, String spec, int window, String hourlyOptions, 
			double minutesReductionFactor, double hoursReductionFactor) {
		

		boolean isScheduleNull = (periodicJobSchedule == null);
		boolean isSpecNull = (null == spec); 
		boolean isHotSpotAvoidanceNull = (null == strategy);
		boolean isHourlyOptionsNull = (null == hourlyOptions);
		boolean isBalancingWindowInValid = (window <=0);
		boolean isReductionFactorInvalid = (minutesReductionFactor <=0 || hoursReductionFactor <=0);
		boolean areBalancingOptionsNull = (isScheduleNull || isSpecNull || isHotSpotAvoidanceNull || isHourlyOptionsNull);
		boolean areBalancingFactorInvalid =  isBalancingWindowInValid || isReductionFactorInvalid;
		
		if(areBalancingOptionsNull || areBalancingFactorInvalid){
			return null;
		}
		String balancedSpec = null;
		
		if(strategy.length() > 0){
			if (strategy.contains("Midnight")) {		
				logger.log(Level.INFO,"Applying the Midnight Balancing Stategy");
				balancedSpec = peformMidnightBalancingStrategy(periodicJobSchedule,spec,window);
			}
			if((null == balancedSpec) && strategy.contains("Hourly")){
				logger.log(Level.INFO,"Applying the Hourly Balancing Stategy with strategy"+hourlyOptions);
				balancedSpec = peformHourlyBalancingStrategy(periodicJobSchedule, spec, hourlyOptions, 
						 minutesReductionFactor, hoursReductionFactor);
			}
		}
		
		return balancedSpec;
	}

	private static boolean isNumeric(String value)
	 {
	    	return ((null != value) && value.matches("^[0-9]+$"));
	 }
	
	
	public static String peformMidnightBalancingStrategy(Map<Integer, Integer> periodicJobSchedule, String spec, int window){
		
		if(null == spec){
			return null;
		}
		
		Random rand = new Random();
		String balancedSpec = null;
		String[] schedule = spec.split(" ");
		int[] minutesIntheHourProfile = null;
		int minLoadHour = 0;
		int occurrences = periodicJobSchedule.get(0);
		int minLoadMinute = rand.nextInt(59); 
		if (PeriodicProjectLoadRules
				.doesBuildOnceAndOnlyAtMidnight(spec)) {
			// look into the number of hours in the window
			for (int i = 1; i <= window; i++){

				if (occurrences > periodicJobSchedule.get(i)) {
					occurrences = periodicJobSchedule.get(i);
					minLoadHour = i;
				}
			}
			balancedSpec = "" + minLoadMinute + " " + minLoadHour
					+ subSpec;

		}// The Project runs multiple times during the midnight 
		else if (PeriodicProjectLoadRules.doesBuildOnlyAtMidnight(spec)) {
			// hour check how many times it runs during the day
			if (schedule.length == 5) {
				
				// Handle the following cases:
				
				minutesIntheHourProfile = getBuildMinuesProfile(schedule);
				
			}

			// When the job runs multiple times in the
			// Determine how many times will run during the new schedule
			// Note that the value of window is from UI is (3, 6, 9 ,
			// 12)
			
			if(null != minutesIntheHourProfile){
				
				int balancedNumberOfTimesPerHour = (int) Math
						.ceil((double)minutesIntheHourProfile.length / window);
				logger.log(Level.INFO,"Debug: Balanced Num of times per Hour"+ balancedNumberOfTimesPerHour);
				String balancedMinutesPerHourStr = "";
				String balancedHourRange = "1-" + Math.min(minutesIntheHourProfile.length, window);
				
				StringBuffer balancedMinutesPerHourStrBuf = new StringBuffer();
				
				for (int i = 0; i < balancedNumberOfTimesPerHour; i++) {
					if (i == (balancedNumberOfTimesPerHour - 1)) {
						balancedMinutesPerHourStrBuf.append(minutesIntheHourProfile[i]);

					} else {
						balancedMinutesPerHourStrBuf.append(minutesIntheHourProfile[i]);
						balancedMinutesPerHourStrBuf.append(",");
					}
					
					
					balancedMinutesPerHourStr = balancedMinutesPerHourStrBuf.toString();
				} 

				balancedSpec = "" + balancedMinutesPerHourStr + " "
						+ balancedHourRange + subSpec;
			}
			
		}

		return balancedSpec;
	}
	
	
public static String peformHourlyBalancingStrategy(Map<Integer, Integer> periodicJobSchedule, 
		String spec, String option, double minutesReductionFactor, double hoursReductionFactor){
		
	// Preventitive Measure: 
	
	if(null == spec || (minutesReductionFactor <=0 || hoursReductionFactor <=0)){
		return null;
	}
		String balancedSpec = null;
		String[] schedule = spec.trim().split(" ");
	
		// When the job runs once every hour (just avoid the midnight hour )
		if (PeriodicProjectLoadRules
				.doesBuildOnceEveryHour(spec)) {
			
			logger.log(Level.INFO,"The Job is Hourly build:");
			balancedSpec =  getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(schedule,  option, minutesReductionFactor, periodicJobSchedule);

		}// The Project runs multiple times during the midnight
		else if (PeriodicProjectLoadRules.doesBuildMuiltpleTimeEveryHour(spec)) {
			// hour check how many times it runs during the day 
			logger.log(Level.INFO,"Length "+schedule.length);
			// user the minutes reduction factor 
			balancedSpec = getBalancedSpecWhenJobBuildEveryHour(schedule,  option, minutesReductionFactor, periodicJobSchedule);
			
			}
		else if(PeriodicProjectLoadRules.doesBuildNtimesInMhoursIntheDay(spec) ||
				PeriodicProjectLoadRules.doesBuildOnceInMhoursIntheDay(spec)){
			
			balancedSpec = getBalancedSpecWhenJobBuildMultipleTimesInTheDay(schedule, option,  minutesReductionFactor,
					 hoursReductionFactor,  periodicJobSchedule);
		}
		return balancedSpec;
	}
	 static boolean isValidMinuteRange(String value){
		
		if(isNumeric(value)){			
		 int intVal= Integer.parseInt(value);
		 if(intVal >= 0 && intVal < 60){
			 return true;
		 }
		}
		return false;
			
	}
		

	private static List<Integer> getTopThreeHotSpots(Map<Integer, Integer> periodicJobSchedule,
			boolean excludeMidnight){
	
		int spot1 = 0;
		int spot2 = 0;
		int spot3 = 0 ;
		int spot1Index = 0;
		int spot2Index = 0;
		int spot3Index = 0;
		
		int startIndex = 0;
		if(excludeMidnight){
			startIndex = 1;
		}
		
		for(int key = startIndex; key< 24; key++){
			
			if(spot1 < periodicJobSchedule.get(key)){
				spot3 = spot2;
				spot3Index = spot2Index;
				spot2 = spot1;
				spot2Index = spot1Index;
				spot1 = periodicJobSchedule.get(key);
				spot1Index = key;
			}else if(spot2 < periodicJobSchedule.get(key)){
				spot3 = spot2;
				spot3Index = spot2Index;
				spot2 = periodicJobSchedule.get(key);
				spot2Index = key;
			}else if(spot3 < periodicJobSchedule.get(key)){
				spot3 = periodicJobSchedule.get(key);
			}
			
		}
		
		List<Integer> result = new ArrayList<Integer>();
		result.add(spot1Index);
		result.add(spot2Index);
		result.add(spot3Index);
	
		
		return result;
	}
	
	
	public static int[] getBuildMinuesProfile(String[] schedule){
		
		if(null!= schedule && schedule.length == 5){
			
			int[] minutesIntheHourProfile = null;
			String[] timesIntheHour;
			// Every minute (*) 60 times in the mindnight hours 
			
			if((null != schedule[0]) && (schedule[0].length() == 1 && "*".equals(schedule[0]))){					
				minutesIntheHourProfile = new int[60];
				for (int i = 0; i < minutesIntheHourProfile.length; i++) {
					minutesIntheHourProfile[i] = i;
				}
			}else if((null != schedule[0]) && (schedule[0].split("/").length ==2)){
				timesIntheHour = schedule[0].split("/");
				if ("*".equals(timesIntheHour[0])
						&& isNumeric(timesIntheHour[1])) {
					int minutesFactor = Integer
							.parseInt(timesIntheHour[1]);
					minutesIntheHourProfile = new int[(int) Math
							.ceil((double) 60 / minutesFactor)];
					for (int i = 0; i < minutesIntheHourProfile.length; i++) {
						minutesIntheHourProfile[i] = i
								* minutesFactor;
					}
				}
			}else if((null != schedule[0]) && (schedule[0].split("-").length ==2)){
				timesIntheHour = schedule[0].split("-");
				if (isNumeric(timesIntheHour[0]) && isNumeric(timesIntheHour[1])) {
					
					// Add a method called is valid range.
					int low = Integer.parseInt(timesIntheHour[0]);
					int high = Integer.parseInt(timesIntheHour[1]);
					// need to assert that high > low
					int minute = low;
					minutesIntheHourProfile = new int[(high - low)+1];
					for (int i = 0; i < minutesIntheHourProfile.length; i++) {
						minutesIntheHourProfile[i] = minute;
						++minute;
					}
				}
			}else if((null != schedule[0]) && (schedule[0].split(",").length >= 2)){
				timesIntheHour = schedule[0].split(",");
				// Need to write a validation for comma seperated numbers
				minutesIntheHourProfile = new int[schedule[0].split(",").length];
				for (int i = 0; i < minutesIntheHourProfile.length; i++) {
					minutesIntheHourProfile[i] = Integer.parseInt(timesIntheHour[i]);
				}
			}
			
			return minutesIntheHourProfile;
		}
		return null;
	}
	
	
	public static int[] getHoursProfileWhenJobBuildsMultipleTimesInTheDay(String schedule, double hoursReductionFactor){
		
		if(null != schedule && schedule.length() > 0 && hoursReductionFactor > 0){
			int numberofTimesPerDay;
			int[] hours = null;
			
			if(schedule.startsWith("*/")){
				numberofTimesPerDay = Integer.parseInt(schedule.substring(schedule.indexOf('/')+1));
				
				numberofTimesPerDay = (int) (Math.ceil((double)numberofTimesPerDay*hoursReductionFactor));
				logger.log(Level.INFO,"New Hours Expression: "+ "*/"+numberofTimesPerDay);
				
				// Note: Make 
				hours = new int[(int) Math.floor(24.0/(double)numberofTimesPerDay)];
				
				for(int i = 0 ; i < hours.length;i++){
					hours[i] = i * numberofTimesPerDay;
				}
				
				logger.log(Level.INFO,"New Hours Expression: "+ "*/"+numberofTimesPerDay);
				
			}else if(schedule.split(",").length>1){
				int tempHours[] = new int[schedule.split(",").length];
				int i = 0;
				// reduce the number hours in the day (what to reduce ? : just omit the first x% of hours in the day)
				for(String h : schedule.split(",")){
					tempHours[i++] = Integer.parseInt(h);
				}
				
				// Make another pass 
				
				numberofTimesPerDay = (int) (Math.ceil((double)tempHours.length/hoursReductionFactor));
				hours = new int[numberofTimesPerDay];
				
				int offset = (tempHours.length - hours.length);
				logger.log(Level.INFO,"Debug : tmpsHours Length "+tempHours.length + " - Balanced Hours Length "+ hours.length +
						" Offset: "+ offset);
				int j = 0;
				for(i = offset; i < tempHours.length;i++){
					hours[j++] = tempHours[i];
				}
				
				
			}else if(schedule.split("-").length > 1){
				String[] hrs = schedule.split("-");
				int startHour = Integer.parseInt(hrs[0]);
				int endHour= (int) (Math.ceil(Double.parseDouble(hrs[1]))/hoursReductionFactor);
				hours = new int[(endHour - startHour)+1];
				int indx = 0;
				for(int i = startHour; i <= endHour; i++){
					hours[indx++] = i;
				}
			
			}	
			
			return hours;
			
			
		}
		return null;
	}
	
	public static String getMinuteStringWhenJobBuildsMultipleTimesInTheDay(String schedule, double minutesReductionFactor){
		
		if(null != schedule && schedule.length() > 0 && (minutesReductionFactor > 0)){
			int numberofTimesPerMinute = 0;
			String minutesStr = "";
			
			// Special Case: When schedule is Valid Numeric Minututes, then return it as it !
			
			if(schedule.equals("*")){
				schedule = "*/1";
			}
			
			if(schedule.startsWith("*/")){
				numberofTimesPerMinute = Integer.parseInt(schedule.substring(schedule.indexOf('/')+1));
				numberofTimesPerMinute = (int) Math.min((Math.ceil((double)numberofTimesPerMinute*minutesReductionFactor)), 59.0);
				minutesStr = "*/"+ numberofTimesPerMinute;
				
				logger.log(Level.INFO,"Old Minute Schedule: "+schedule + " -> New Schedule :"+minutesStr);
			}else if(schedule.split(",").length > 1){
				String[] minutes = schedule.split(",");
				numberofTimesPerMinute = (int) (Math.ceil((double)minutes.length/minutesReductionFactor));
				StringBuffer minutesStrBuf = new StringBuffer();
				for(int i = 0; i <numberofTimesPerMinute; i++){
					if(i == numberofTimesPerMinute-1){
						minutesStrBuf.append(minutes[i]);
					}else{
						minutesStrBuf.append(minutes[i]);
						minutesStrBuf.append(",");
						
					}
					
				}
				
				minutesStr = minutesStrBuf.toString();
			}else if(schedule.split("-").length > 1){
				String[] minutes = schedule.split("-");
				// the division would result in value greater than 60 				
				numberofTimesPerMinute = (int) (Math.ceil(Double.parseDouble(minutes[1])/minutesReductionFactor));
				minutesStr = minutes[0]+"-"+numberofTimesPerMinute+"";					
			
			}else if(isValidMinuteRange(schedule)){ 
				minutesStr = schedule;				
			}
			
			
			return minutesStr;
		}
		return null;
	}
	
	public static String getBalancedSpecWhenJobBuildMultipleTimesInTheDay(String[] schedule, String option, double minutesReductionFactor,
			double hoursReductionFactor, Map<Integer, Integer> periodicJobSchedule){
		
		Random rand = new Random();

		int minLoadHour = rand.nextInt(23);
		
		if(null != schedule && schedule.length == 5){
			
			String minutesStr = "";
			String hoursStr = "";
			int[] hours = null;
			String balancedSpec = null;
			
			
			minutesStr = getMinuteStringWhenJobBuildsMultipleTimesInTheDay(schedule[0],  minutesReductionFactor);
			
			logger.log(Level.INFO,"### DEBUG The hour segment: "+schedule[1]);
			hours = getHoursProfileWhenJobBuildsMultipleTimesInTheDay(schedule[1],  hoursReductionFactor);
				
			if(option.equals("Midnight-Only")){
				logger.log(Level.INFO,"Applying the Option : Midnight-Only ");
				logger.log(Level.INFO,"The length of hours : "+hours.length);

				// Corner Case : When the length is 1 and the build only run at Midnight 
				StringBuffer hoursStrBuf = new StringBuffer();				
					for(int h : hours){
						if(h !=0 && hours.length > 1){
							hoursStrBuf.append(h);
							hoursStrBuf.append(",");

						}else{
							hoursStrBuf.append(minLoadHour);
							hoursStrBuf.append(",");
						}
					}
					
					hoursStr = hoursStrBuf.toString();
					logger.log(Level.INFO,"### hoursStr: "+hoursStr);
					balancedSpec = ""+ minutesStr + " "+hoursStr.substring(0,hoursStr.length()-1) + subSpec;					

				}
			
			else if(option.equals("Hot-Spots-Only")){
				logger.log(Level.INFO,"Applying the Option : Hot-Spots-Only");

				List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, false);
				StringBuffer filteredHoursStrBuf = new StringBuffer();
				for(int h : hours){
					if(!hotspotHours.contains(h)){
						filteredHoursStrBuf.append(h);
						filteredHoursStrBuf.append(",");
						
					}
				}					
				hoursStr = filteredHoursStrBuf.toString();
				// When all the hours are in the hot-spot 
				if(hoursStr.length() < 2){
					logger.log(Level.INFO,"### The new hours Expression: "+ hoursStr);
					logger.log(Level.INFO,"### The length of hours"+ hours.length);

					balancedSpec = ""+ minutesStr + " "+hours[0] + subSpec;
				}
				else{
					balancedSpec = ""+ minutesStr + " "+hoursStr.substring(0,hoursStr.length()-1) + subSpec;
				}					

			}else if(option.equals("Hot-Spots-Plus-Midnight")){
				logger.log(Level.INFO,"Applying the Option : Hot-Spots-Only");
				List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, false);
				
				StringBuffer hoursStrBuff = new StringBuffer();

				for(int h : hours){
					if(!hotspotHours.contains(h) && h!=0){  
						hoursStrBuff.append(h);
						hoursStrBuff.append(",");
					}
				}
				
				hoursStr = hoursStrBuff.toString();
				
				// When all the hours are in the hot-spot 
				if(hoursStr.length() < 2){
					logger.log(Level.INFO,"### The new hours Expression: "+ hoursStr);
					logger.log(Level.INFO,"### The length of hours"+ hours.length);

					balancedSpec = ""+ minutesStr + " "+hours[0] + subSpec;
				}
				else{
					balancedSpec = ""+ minutesStr + " "+hoursStr.substring(0,hoursStr.length()-1) + subSpec;
				}
			
			}
			
			return balancedSpec;
			
		}
		
		
		return null;
	}
	// change the method name.
	public static String getBalancedSpecWhenJobBuildMultipleTimeAtMidnight(String[] schedule, String option, double minutesReductionFactor,
			Map<Integer, Integer> periodicJobSchedule){
		
		if(null != schedule){
			boolean condition = (schedule.length == 1 && "@hourly".equals(schedule[0]));
			if((schedule.length == 5 || condition)
					&& minutesReductionFactor > 0){ 
				String balancedSpec = null;
				Random rand = new Random();

				int minLoadMinute = rand.nextInt(59); 
				
				if(option.contains("Midnight-Only")){
					balancedSpec = ""+ minLoadMinute + " 1-23 * * *";
					
				}else if(option.contains("Hot-Spots-Only")){
					List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, false);
					
					String filteredHours = "";
					StringBuffer filteredHoursStrBuff = new StringBuffer();
					
					for(int i = 0; i < 24; i++){
						if(!hotspotHours.contains(i)){
							filteredHoursStrBuff.append(i);
							filteredHoursStrBuff.append(",");

						}
					}
					filteredHours = filteredHoursStrBuff.toString();
					balancedSpec = ""+ minLoadMinute + " "+filteredHours.substring(0, filteredHours.length()-1) +subSpec;
					
				}else if(option.contains("Hot-Spots-Plus-Midnight")){
					List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, true);
					StringBuffer filteredHoursStrBuff = new StringBuffer();

					String filteredHours = "";
					for(int i = 1; i < 24; i++){
						if(!hotspotHours.contains(i)){
							filteredHoursStrBuff.append(i);
							filteredHoursStrBuff.append(",");
						}
					}
					filteredHours = filteredHoursStrBuff.toString();
					balancedSpec = ""+ minLoadMinute + " "+filteredHours.substring(0, filteredHours.length()-1) +subSpec;			
				}
				
				return balancedSpec;
			}
			
		}
		
		
		
		return null;
		
		
	}
	
	public static String getBalancedSpecWhenJobBuildEveryHour(String[] schedule, String option, double minutesReductionFactor,
			Map<Integer, Integer> periodicJobSchedule){
			
		if (null != schedule && schedule.length == 5 && minutesReductionFactor > 0) {
			
			int numberofTimesPerMinute;
			String minutesStr = null;
			String balancedSpec = null;
			
			if(schedule[0].equals("*")){
				numberofTimesPerMinute = (int) Math.min((Math.ceil(1.0*minutesReductionFactor)), 59.0);
				minutesStr = "*/"+ numberofTimesPerMinute;
			}
			else if(schedule[0].startsWith("*/")){
				numberofTimesPerMinute = Integer.parseInt(schedule[0].substring(schedule[0].indexOf('/')+1));
				numberofTimesPerMinute = (int) Math.min((Math.ceil((double)numberofTimesPerMinute*minutesReductionFactor)), 59.0);
				minutesStr = "*/"+ numberofTimesPerMinute;

			}else if(schedule[0].split(",").length > 1){
				String[] minutes = schedule[0].split(",");
				numberofTimesPerMinute = (int) (Math.ceil((double)minutes.length/minutesReductionFactor));
				StringBuffer minutesStrBuf = new StringBuffer();
				for(int i = 0; i <numberofTimesPerMinute; i++){
					if(i == numberofTimesPerMinute-1){
						minutesStrBuf.append(minutes[i]);
					}else{
						minutesStrBuf.append(minutes[i]);
						minutesStrBuf.append(",");	
					}
					
				}
				minutesStr = minutesStrBuf.toString();
				
			}else if(schedule[0].split("-").length > 1){
				String[] minutes = schedule[0].split("-");
				numberofTimesPerMinute = (int) (Math.ceil(Double.parseDouble(minutes[1])/minutesReductionFactor));
				minutesStr = minutes[0]+"-"+numberofTimesPerMinute+"";					
				
			}
			
			
			if(option.equals("Midnight-Only")){
				logger.log(Level.INFO,"Applying the Option : Midnight-Only ");

				balancedSpec = ""+ minutesStr + " 1-23 * * *";
				
			}else if(option.equals("Hot-Spots-Only")){
				logger.log(Level.INFO,"Applying the Option : Hot-Spots-Only");

				List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, false);
				
				String filteredHours = "";
				StringBuffer filteredHoursStrBuf = new StringBuffer();
				for(int i = 0; i < 24; i++){
					if(!hotspotHours.contains(i)){
						filteredHoursStrBuf.append(i);
						filteredHoursStrBuf.append(",");
						
					}
				}
				filteredHours = filteredHoursStrBuf.toString(); 
				balancedSpec = ""+ minutesStr +" "+ filteredHours.substring(0, filteredHours.length()-1) +subSpec;
				
			}	
			else if(option.equals("Hot-Spots-Plus-Midnight")){
				logger.log(Level.INFO,"Applying the Option : Hot-Spots-Plus-Midnigh");
				List<Integer> hotspotHours = getTopThreeHotSpots(periodicJobSchedule, true);
				StringBuffer filteredHoursStrBuf = new StringBuffer();

				String filteredHours = "";
				for(int i = 1; i < 24; i++){
					if(!hotspotHours.contains(i)){
						filteredHoursStrBuf.append(i);
						filteredHoursStrBuf.append(",");
					}
				}
				filteredHours = filteredHoursStrBuf.toString(); 
				balancedSpec = ""+ minutesStr + " "+filteredHours.substring(0, filteredHours.length()-1) +subSpec;
				
			}
			
			return balancedSpec;
		}
		
		return null;
		
	}
	
	
}
