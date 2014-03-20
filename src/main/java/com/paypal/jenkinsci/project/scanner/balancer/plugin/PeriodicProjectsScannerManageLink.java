package com.paypal.jenkinsci.project.scanner.balancer.plugin;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.security.Permission;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import net.sf.json.JSONArray;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.paypal.jenkinsci.project.periodic.PeriodicProjectCronExpressionAnalyzer;
import com.paypal.jenkinsci.project.periodic.PeriodicProjectSpecification;
import com.paypal.jenkinsci.project.periodic.ProjectFrequencySummary;
import com.paypal.jenkinsci.project.scanner.graph.PeriodicProjectsDistributionGraph;
import com.paypal.jenkinsci.project.scanner.load.balancing.PeriodicProjectLoadBalancingStrategy;
import com.paypal.jenkinsci.project.scanner.properties.PropertiesUtil;
import com.paypal.jenkinsci.project.scanner.thread.PeriodicProjectsScannerThread;



import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * @author otahboub
 *
 */
@Extension
public class PeriodicProjectsScannerManageLink extends ManagementLink implements Describable<PeriodicProjectsScannerManageLink>
{
	 private static final Logger logger = Logger.getLogger(PeriodicProjectsScannerManageLink.class.getName());
	    /**
	     * URL to the plugin.
	     */
	    protected static final String URL = "periodic-projects-scanner-balancer";

	    /**
	     * Icon used by this plugin.
	     */
	    protected static final String ICON = "/plugin/periodic-projects-scanner-balancer/icons/icon_analysis.png";
	    		

	    private static PeriodicProjectsScannerManageLink instance;
	    private transient List<PeriodicProjectSpecification> periodicJobList;
	    private transient Map<Integer,List<PeriodicProjectSpecification>> projectsTimeSchedule;
	    private transient Map<Integer,List<PeriodicProjectSpecification>> balancedProjectsTimeSchedule;
	    private transient final PeriodicProjectsScannerThread monitorThread = new PeriodicProjectsScannerThread();
	    private transient Map<Integer,Integer> periodicJobSchedule;
	    private transient Map<Integer,Integer> balancedPeriodicJobSchedule;
	 /* this is a flag which will serve as flag to display the both the graph and the table */
		private transient boolean dataReady = false; 
		private transient boolean graphReady = false;
		private transient boolean balancedGraphReady = false;
		private transient boolean loadBalancingEnabled = false;
		private transient boolean revertLoadBalancingEnabled = false;
		
		private transient List<ProjectFrequencySummary> projectFrequency;
		private transient ProjectFrequencySummary lowFreq;
		private transient ProjectFrequencySummary medLowFreq;
		private transient ProjectFrequencySummary medHighFreq;
		private transient ProjectFrequencySummary highFreq;
		private transient ProjectFrequencySummary veryHighFreq;
		private transient ProjectFrequencySummary extrHighFreq;
		private String selectedFrequencyLevel;
		private transient List<PeriodicProjectSpecification> filteredPeriodicJobList;
		private String balancingWindow;
		private String hotspotsAvoidance;
		private String minutesBalancingFactor;
		private String hoursBalancingFactor;
		private String loadBalancingLog = "";
		private String loadRevertLog = "";
		
		private transient String hudsonInstanceHostURL = null;
		private transient int hudsonInstanceHostPortNumber = 0;
		
		
		
	    @Extension
	    public static final class DescriptorImpl extends Descriptor<PeriodicProjectsScannerManageLink> {
	        @Override
	        public String getDisplayName() {
	            return null; 
	        }
	        
	        public String[] getBalacingWindowOptions(){
	        	return new String[]{"3","6","9","12"};
	        }
	        
	        public String[] getHostSpotAvoidanceOptions(){
	        	return new String[] {"Midnight Only","HotSpots Only","HotSpots plus Midnight"};
	        }
	        
	        public String[] getMinutesBalancingOptions(){
	        	return new String[] {"25%","50%","75%"};

	        }	        
	        public String[] getHourlyBalancingOptions(){
	        	return new String[] {"25%","50%","75%"};
	        }
	     
	    
	    }

	    /**
	     * Returns the instance of NodeManageLink.
	     * @return instance the NodeManageLink.
	     */
	    public static PeriodicProjectsScannerManageLink getInstance() {
	        List<ManagementLink> list = Hudson.getInstance().getManagementLinks();
	        for (ManagementLink link : list) {
	            if (link instanceof PeriodicProjectsScannerManageLink) {
	                instance = (PeriodicProjectsScannerManageLink)link;
	                break;
	            }
	        }
	        return instance;
	    }
	    
	    
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Periodic Projects Scanner & Load Analyzer";
	}
	
	 public String getDescription() {
	        return "This plugin scanns through periodic Porjects and measures their load distribution";
	    }

	 /**
	     * Returns required permission to use this plugin.
	     * @return Hudson administer permission.
	     */
	    public Permission getRequiredPermission() {
	        return Hudson.ADMINISTER;
	    }
	
	
	public String getIconFileName() {
		// TODO Auto-generated method stub
		return ICON;
	}

	
	public String getUrlName() {
		// TODO Auto-generated method stub
		return URL;
	}
	
	
	
	public Graph getOverallGraph(){

        DataSetBuilder<String, Integer> dsb = new DataSetBuilder<String, Integer>();
        for(Integer key : periodicJobSchedule.keySet()){
        	logger.log(Level.INFO,"Time :"+key + " with "+ periodicJobSchedule.get(key) + "occurences");
        	 dsb.add(periodicJobSchedule.get(key), "count", key);
        }
   
		return new PeriodicProjectsDistributionGraph(dsb.build());
	}

	
	public List<PeriodicProjectSpecification> getPeriodicJobList() {
		
		// TODO: check if there is a filter 		
	if(this.selectedFrequencyLevel != null && !this.selectedFrequencyLevel.equals("") ){
		if(filteredPeriodicJobList == null){
			filteredPeriodicJobList = new ArrayList<PeriodicProjectSpecification>();
		}else{
			filteredPeriodicJobList.clear();
			
		}
		if(periodicJobList != null){
			for(PeriodicProjectSpecification spec : periodicJobList){
				if(spec.getFrequency().equals(this.selectedFrequencyLevel)){
					filteredPeriodicJobList.add(spec);
				}
			}
		}
		return filteredPeriodicJobList;
		
		
	}else{
		return periodicJobList;
	}
		
		
	}


	public Map<Integer, Integer> getPeriodicJobSchedule() {
		return periodicJobSchedule;
	}
	
	private void analyzePeriodicJobSchedule() {
		
		if (null == periodicJobSchedule) {
			periodicJobSchedule = new HashMap<Integer, Integer>();
		} else {
			// Clear the collection in order to avoid memory leak.
			periodicJobSchedule.clear(); 			
		}
		
		
		if(null == projectsTimeSchedule){
			projectsTimeSchedule = new HashMap<Integer,List<PeriodicProjectSpecification>>();
			for(int i = 0; i < 24;i++){
				projectsTimeSchedule.put(i, new ArrayList<PeriodicProjectSpecification>());
			}
		}else{
			for(Integer key : projectsTimeSchedule.keySet()){
				projectsTimeSchedule.get(key).clear();
			}
		}
		
		// pass the it into the following method 
		
		PeriodicProjectCronExpressionAnalyzer.analyzePeriodicJobSchedule(periodicJobSchedule, periodicJobList);
		
		 generateProjectFrequencySummary();
	
	}
	
	
	private void analyzeBalancedPeriodicJobSchedule() {
		
		if (null == balancedPeriodicJobSchedule) {
			balancedPeriodicJobSchedule = new HashMap<Integer, Integer>();
		} else {
			// Clear the collection in order to avoid memory leak.
			balancedPeriodicJobSchedule.clear(); 			
		}
		
		
		if(null == balancedProjectsTimeSchedule){
			balancedProjectsTimeSchedule = new HashMap<Integer,List<PeriodicProjectSpecification>>();
			for(int i = 0; i < 24;i++){
				balancedProjectsTimeSchedule.put(i, new ArrayList<PeriodicProjectSpecification>());
			}
		}else{
			for(Integer key : balancedProjectsTimeSchedule.keySet()){
				balancedProjectsTimeSchedule.get(key).clear();
			}
		}
		
		// pass the it into the following method  
		
		PeriodicProjectCronExpressionAnalyzer.analyzePeriodicBalancedJobSchedule(balancedPeriodicJobSchedule, periodicJobList);
		
	}
	
	
	
	private void generateProjectFrequencySummary(){
		
		
		
		if(null == projectFrequency){
			projectFrequency = new ArrayList<ProjectFrequencySummary>();
			lowFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("lowFrequencyIconFileName"), "Low",0,0);
			medLowFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("mediumLowFrequencyIconFileName"), "Medium Low",0,0);
			medHighFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("mediumHighFrequencyIconFileName"), "Medium High",0,0);
			highFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("highFrequencyIconFileName"), "High",0,0);
			veryHighFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("veryHighFrequencyIconFileName"), "Very High",0,0);
			extrHighFreq = new ProjectFrequencySummary(PropertiesUtil.getInstance().getProperty("extHighFrequencyIconFileName"), "Extremely High",0,0);
			
		}else{
			lowFreq.setBuildCount(0);lowFreq.setJobCount(0);
			medLowFreq.setBuildCount(0);medLowFreq.setJobCount(0);
			medHighFreq.setBuildCount(0);medHighFreq.setJobCount(0);
			highFreq.setBuildCount(0);highFreq.setJobCount(0);
			veryHighFreq.setBuildCount(0);veryHighFreq.setJobCount(0);
			extrHighFreq.setBuildCount(0);extrHighFreq.setJobCount(0);
			projectFrequency.clear();
		}
		
		
		for(PeriodicProjectSpecification spec : periodicJobList){
			
			if(spec.getFrequency().equals("Low")){
				lowFreq.setJobCount(lowFreq.getJobCount()+1);
				lowFreq.setBuildCount(lowFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));				
			}else if(spec.getFrequency().equals("Medium Low")){
				medLowFreq.setJobCount(medLowFreq.getJobCount()+1);
				medLowFreq.setBuildCount(medLowFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));
			}else if(spec.getFrequency().equals("Medium High")){
				medHighFreq.setJobCount(medHighFreq.getJobCount()+1);
				medHighFreq.setBuildCount(medHighFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));
			}else if(spec.getFrequency().equals("High")){
				highFreq.setJobCount(highFreq.getJobCount()+1);
				highFreq.setBuildCount(highFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));
			}else if(spec.getFrequency().equals("Very High")){
				veryHighFreq.setJobCount(veryHighFreq.getJobCount()+1);
				veryHighFreq.setBuildCount(veryHighFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));
			}else if(spec.getFrequency().equals("Extremely High")){
				extrHighFreq.setJobCount(extrHighFreq.getJobCount()+1);
				extrHighFreq.setBuildCount(extrHighFreq.getBuildCount()+(spec.getBuildTriggersSchedule().getSchedule().size()));
			}
			
			
		}
		
		// Print out the Summary 
		
		logger.log(Level.INFO,"Low "+lowFreq.getJobCount()+"\t"+lowFreq.getBuildCount());
		logger.log(Level.INFO,"Medium Low "+medLowFreq.getJobCount()+"\t"+medLowFreq.getBuildCount());
		logger.log(Level.INFO,"Medium High "+medHighFreq.getJobCount()+"\t"+medHighFreq.getBuildCount());
		logger.log(Level.INFO,"High "+highFreq.getJobCount()+"\t"+highFreq.getBuildCount());
		logger.log(Level.INFO,"Very High "+veryHighFreq.getJobCount()+"\t"+veryHighFreq.getBuildCount());
		logger.log(Level.INFO,"Extremely High "+extrHighFreq.getJobCount()+"\t"+extrHighFreq.getBuildCount());

		
		
		projectFrequency.add(lowFreq);
		projectFrequency.add(medLowFreq);
		projectFrequency.add(medHighFreq);
		projectFrequency.add(highFreq);
		projectFrequency.add(veryHighFreq);
		projectFrequency.add(extrHighFreq);
	
		
	}
	
	
	public Graph getBalancedGraph(){
      String title = "Periodic Projects Distribution with Midnight Balancing Window ("
        +this.balancingWindow+ ") and HotSpot Avoidance Policy ("+this.hotspotsAvoidance+") and Frequency Reduction ("
        +this.minutesBalancingFactor+","+this.hoursBalancingFactor+")";		
        DataSetBuilder<String, Integer> dsb = new DataSetBuilder<String, Integer>();

       
        for(Integer key : periodicJobSchedule.keySet()){
        	 dsb.add(periodicJobSchedule.get(key), "Periodic Project Count", key);
        	 dsb.add(balancedPeriodicJobSchedule.get(key), "Balanced Periodic Project Count", key);

        }
   
		return new PeriodicProjectsDistributionGraph(dsb.build(),title);
	}
	
	
	
	// This method will Calculate the load distribution 
	
	 public void doCalculatePeriodicProjectsLoadDistribution(StaplerRequest req, StaplerResponse res) throws ServletException, IOException {
		 
		 	monitorThread.analyzePeriodicJobs();
			periodicJobList = monitorThread.getPeriodicJobList();
			analyzePeriodicJobSchedule();
			dataReady = true;
			graphReady = true;
			this.balancedGraphReady = false;
			this.selectedFrequencyLevel = null;
			this.loadBalancingLog = "";
			this.loadRevertLog = "";
	        res.forwardToPreviousPage(req);
		 
	 }

	 
	 public void doFilterProjectsByFrequency(StaplerRequest req, StaplerResponse res) throws ServletException, IOException {
		 		 
		 if(req.getParameter("selectedFrequencyLevel") != null){
			 logger.log(Level.INFO,"### Debug: "+ req.getParameter("selectedFrequencyLevel").toString() + "####");
			 this.selectedFrequencyLevel = req.getParameter("selectedFrequencyLevel").toString();
		 }
		 
		 res.forwardToPreviousPage(req);
		 
	 }
	 
	 
	 public void doCalculateBalancedPeriodicProjectsLoadDistribution(StaplerRequest req, StaplerResponse res) throws ServletException, IOException {
		 
		 	if(dataReady && graphReady){
		 		
		 		// Get the list of Periodic Projects Specs
		 		
		 		logger.log(Level.INFO,"### Balancing Window: "+ req.getParameter("balancingWindow"));
		 		logger.log(Level.INFO,"### Hot-Spot Avoidance Option: "+ req.getParameter("hotspotsAvoidance")); 
		 		logger.log(Level.INFO,"### Minutes Frequency Reduction Factor: "+ req.getParameter("minutesBalancingFactor"));
		 		logger.log(Level.INFO,"### Per-Hour Frequency Reduction Factor: "+ req.getParameter("hoursBalancingFactor"));

		 		
		 		
		 		this.balancingWindow = req.getParameter("balancingWindow");
		 		this.hotspotsAvoidance = req.getParameter("hotspotsAvoidance");
		 		this.minutesBalancingFactor = req.getParameter("minutesBalancingFactor");
		 		this.hoursBalancingFactor = req.getParameter("hoursBalancingFactor");
		 		
		 				 		
		 		for(PeriodicProjectSpecification projectSpec : this.periodicJobList){
		 			
		 			String balanedSpec = PeriodicProjectLoadBalancingStrategy.calcBalancedSchedule
		 				(periodicJobSchedule,"Midnight:Hourly",projectSpec.getSpec(), Integer.parseInt(this.balancingWindow),
		 						translateHostSpotsAvoidancePolicy(this.hotspotsAvoidance), 
		 						translateBalancingFactor(minutesBalancingFactor),
		 						translateBalancingFactor(hoursBalancingFactor));
		 			
		 			logger.log(Level.INFO,"#### New Balanced Spec : "+balanedSpec + " Old Spec: "+ projectSpec.getSpec());
		 			if(balanedSpec != null){
		 				logger.log(Level.INFO,"-New Balanced Spec : "+balanedSpec);
		 				projectSpec.setBalancedSpec(balanedSpec);
		 			}
		 		}
		 		
		 		analyzeBalancedPeriodicJobSchedule();
		 		// recalculate the distribution according to the new schedule 
			 	this.balancedGraphReady = true;
			 	loadBalancingEnabled = true;
		 	}
	        res.forwardToPreviousPage(req);
		 
	 }
	 
	 private String translateHostSpotsAvoidancePolicy(String hotspot){
		 if(hotspot.contains("plus")){
	 			return "Hot-Spots-Plus-Midnight";
	 		}else if(hotspot.contains("Midnight")){
	 			return "Midnight-Only";
	 		}else if(hotspot.contains("HotSpots")){
	 			return  "Hot-Spots-Only";
	 		}
		 
		 return "Midnight-Only";  
		 
	 }
	 
	 private double translateBalancingFactor(String factor){
		 
		 if(factor.contains("25%")){
			 return 1.32;
		 }else if(factor.contains("50%")){
			 return 2.0;
		 }else if(factor.contains("75%")){
			 return 4.0;
		 }
		 
		 return 1.0;
	 }

	 // All Form Actions
	 
	 public void doViewPeriodicProjectsRedirect(StaplerRequest req, StaplerResponse rsp) throws IOException {
	        Hudson.getInstance().checkPermission(getRequiredPermission());
	        rsp.sendRedirect2("periodicprojects");
	    }
	 
	 
	 public void doViewPeriodicProjectsAnalyticsRedirect(StaplerRequest req, StaplerResponse rsp) throws IOException {
	        Hudson.getInstance().checkPermission(getRequiredPermission());
	        rsp.sendRedirect2("analytics");
	    }
	 
	 
	 // Applies the new schedule 
	 public void doBalancePeriodicProjectsSchedule(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		 
		    Hudson.getInstance().checkPermission(getRequiredPermission());
		    
		    if(this.loadBalancingEnabled){
		    	applyBalancedProjectsPeriodicalSchedule();
		        this.revertLoadBalancingEnabled = true;
		        logger.log(Level.INFO,loadBalancingLog);
		    }else{
		    	loadBalancingLog = "No Balanced Periodic Projects Schedule Calcualted"+"\n"+
		                            "Please calculate the balanced periodic projects period applying it to the periodic jobs set";
		    }
	        
	        rsp.forwardToPreviousPage(req);
	    }
	 
	 public void doRevertPeriodicProjectsSchedule(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
	        Hudson.getInstance().checkPermission(getRequiredPermission());
	        
	        if(this.revertLoadBalancingEnabled){
		        revertToOldProjectsPeriodicalSchedule();
	        }else{
	        	loadRevertLog = "No Periodic Job Balancing is Performed. Could not Revert";
	        }
	        
	        rsp.forwardToPreviousPage(req);
	    }
	 
	 // This Method will pull some Metrics from Jenkins and from the data base in Future
	 public void doGetProjectLoadMetrics(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
	        Hudson.getInstance().checkPermission(getRequiredPermission());
	        rsp.setContentType("text/json");
	        int [] dataSeries = {100,200,300,500,50,100};
    		rsp.getWriter().write(JSONArray.fromObject(dataSeries).toString());
	    } 
	 
	 public void doBalancePeriodicProjectsRedirect(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
	        Hudson.getInstance().checkPermission(getRequiredPermission());
	        rsp.sendRedirect2("balancing");

	    }
	
	private void applyBalancedProjectsPeriodicalSchedule(){
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("The following Periodic Jobs were balanced: \n");
		this.monitorThread.updateBalancedTimerTriggerSpec(periodicJobList, logBuffer, hudsonInstanceHostURL,hudsonInstanceHostPortNumber);
		logger.log(Level.INFO,"### DEBUG :"+logBuffer.toString());
		this.loadBalancingLog = logBuffer.toString();
	}
	
	private void revertToOldProjectsPeriodicalSchedule(){
		
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("The following Periodic Jobs were reverted back to the old schedule: \n");
		this.monitorThread.revertOldTimerTriggerSpec(periodicJobList, logBuffer, hudsonInstanceHostURL,hudsonInstanceHostPortNumber);
		logger.log(Level.INFO,"### DEBUG :"+logBuffer.toString());
		this.loadRevertLog = logBuffer.toString();
	}
	
	
	public boolean isDataReady() {
		return dataReady;
	}


	public boolean isGraphReady() {
		return graphReady;
	}

	public boolean isBalancedGraphReady() {
		if(!dataReady && !graphReady){
			return false;
		}
		else{
			return balancedGraphReady;
		}
		
	}


	// Called from UI 
	public List<ProjectFrequencySummary> getProjectFrequency() {
		
		// For the first time, the plugin will initialize the list		
		return projectFrequency;
	}


	public Map<Integer, List<PeriodicProjectSpecification>> getProjectsTimeSchedule() {
		return projectsTimeSchedule;
	}


	public String getSelectedFrequencyLevel() {
		return selectedFrequencyLevel;
	}


	public void setSelectedFrequencyLevel(String selectedFrequencyLeve) {
		this.selectedFrequencyLevel = selectedFrequencyLeve;
	}
 	
	

	public Descriptor<PeriodicProjectsScannerManageLink> getDescriptor() {
		// TODO Auto-generated method stub
		 return Hudson.getInstance().getDescriptorByType(DescriptorImpl.class);
	}


	public String getBalancingWindow() {
		return balancingWindow;
	}


	public void setBalancingWindow(String balancingWindow) {
		this.balancingWindow = balancingWindow;
	}


	public String getHotspotsAvoidance() {
		return hotspotsAvoidance;
	}


	public void setHotspotsAvoidance(String hotspotsAvoidance) {
		this.hotspotsAvoidance = hotspotsAvoidance;
	}


	public String getLoadBalancingLog() {
		return loadBalancingLog;
	}

	

	public boolean isLoadBalancingEnabled() {
		return loadBalancingEnabled;
	}


	public String getLoadRevertLog() {
		return loadRevertLog;
	}


	public boolean isRevertLoadBalancingEnabled() {
		return revertLoadBalancingEnabled;
	}


	public String getHudsonInstanceHostURL() {
		return hudsonInstanceHostURL;
	}


	public void setHudsonInstanceHostURL(String hudsonInstanceHostURL) {
		this.hudsonInstanceHostURL = hudsonInstanceHostURL;
	}


	public int getHudsonInstanceHostPortNumber() {
		return hudsonInstanceHostPortNumber;
	}


	public void setHudsonInstanceHostPortNumber(int hudsonInstanceHostPortNumber) {
		this.hudsonInstanceHostPortNumber = hudsonInstanceHostPortNumber;
	}
	
}
