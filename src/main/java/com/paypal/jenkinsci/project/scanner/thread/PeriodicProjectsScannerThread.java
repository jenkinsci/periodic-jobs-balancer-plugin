package com.paypal.jenkinsci.project.scanner.thread;


import antlr.ANTLRException;
import com.paypal.jenkinsci.project.periodic.PeriodicProjectSpecification;
import com.paypal.jenkinsci.project.scanner.logger.EventsLogger;
import hudson.model.*;
import hudson.tasks.Builder;
import hudson.tasks.Shell;
import hudson.triggers.TimerTrigger;
import hudson.triggers.Trigger;
import hudson.util.DescribableList;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.events.EventException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class PeriodicProjectsScannerThread extends AsyncPeriodicWork {

    private static final Logger logger = Logger.getLogger(PeriodicProjectsScannerThread.class.getName());

    //trigger the job every 1 hours
    private transient boolean completed = false;
    private transient String result = "";
    private transient List<PeriodicProjectSpecification> periodicJobList;
    private int periodicJobsCount;
    private int connectionManagerTimeout = 15000;
    private int connectionTimeout = 15000;
    private int soTimeout = 15000;


    public static final int COUNT_INTERVAL_MINUTES = 1;

    public PeriodicProjectsScannerThread() {
        super("Fusion Periodic Jobs Monitor");
        periodicJobList = new ArrayList<PeriodicProjectSpecification>();
    }

    public long getRecurrencePeriod() {
        return 1000 * 60 * COUNT_INTERVAL_MINUTES;
    }


    @Override
    protected void execute(TaskListener listener) throws IOException, InterruptedException {
        logger.log(Level.INFO, "###Inside excute");
        completed = false;
        printJobInJenkins();
        printJobShellScripts();
        completed = true;
        result = "Complete";

    }

    public void printJobInJenkins() {

        logger.log(Level.INFO, "###Inside printJobInJenkins()");
        Hudson inst = Hudson.getInstance();
        for (AbstractProject<?, ?> p : inst.getAllItems(AbstractProject.class)) {
            logger.log(Level.INFO, "### Job Name: " + p.getName());
            // Get the build type from the Fusion DB
            for (@SuppressWarnings("rawtypes")
            Trigger t : p.getTriggers().values()) {
                logger.log(Level.INFO, "### Spec Type: "
                        + t.getSpec().toLowerCase());
                p.getAssignedLabel();
            }
        }
    }

    public void analyzePeriodicJobs() {

        logger.log(Level.INFO, "###Inside analyzePeriodicJobs");
        // In order to prevent memory leak we explicitly
        periodicJobList.clear();
        periodicJobsCount = 0;
        int buildCount;
        SortedMap<Integer, Run> map;
        double averageBuildTime = 0;
        Hudson inst = Hudson.getInstance();
        for (AbstractProject<?, ?> p : inst.getAllItems(AbstractProject.class)) {
            logger.log(Level.INFO, "### Job Name: " + p.getName());
            buildCount = 0;

            if (p.isBuildable()) {
                map = (SortedMap<Integer, Run>) p.getBuildsAsMap();
                averageBuildTime = 0.0;

                if ((null != map) && map.size() > 0) {
                    buildCount = map.size();
                    logger.log(Level.INFO, "Total number of Builds :" + buildCount);
                    this.getAverageBuildTime(map);
                }
            }

            // Get the build type from the Fusion DB

            if ((p.getTriggers()) != null && p.getTriggers().size() > 0) {

                for (@SuppressWarnings("rawtypes")
                Trigger t : p.getTriggers().values()) {
                    logger.log(Level.INFO, "Class Type: " + t.getClass().toString());
                    logger.log(Level.INFO, "Spec Type: "
                            + t.getSpec().toLowerCase());

                    String cornExpr = null;
                    // Trim the expression if it contains comments
                    String[] specLines = t.getSpec().split("\n");
                    // The cron expression is the first line which does not start with #

                    for (String line : specLines) {
                        if (!line.startsWith("#")) {
                            if (line.startsWith("H")) {
                                cornExpr = line;
                            } else {
                                cornExpr = extractCronExpression(t.getSpec().trim().toLowerCase());
                            }

                            break;

                        }
                    }
                    String baseUrl = Hudson.getInstance().getRootUrl();
                    String configFileUrl = baseUrl + p.getUrl();

                    PeriodicProjectSpecification spec = new PeriodicProjectSpecification(p.getName(), t.getClass().toString(),
                            cornExpr, configFileUrl, buildCount, averageBuildTime);


                    periodicJobList.add(spec);

                }

            }

        }

        periodicJobsCount = periodicJobList.size();
        logger.log(Level.INFO, "Periodic Project Count: " + periodicJobsCount);


    }


    public void updateBalancedTimerTriggerSpec(List<PeriodicProjectSpecification> projectsList, String hostName, int portNumber) {

        for (PeriodicProjectSpecification projectSpec : projectsList) {

            if (!projectSpec.getSpec().equals(projectSpec.getBalancedSpec())) {
                if (updateProjectTimerTrigger(projectSpec.getJobName(), projectSpec.getBalancedSpec(), hostName, portNumber)) {
                    logger.log(Level.INFO, "Periodic Project: " + projectSpec.getJobName() + "has been successfuly updated " +
                            "Old : " + projectSpec.getSpec() + " new Balanced: " + projectSpec.getBalancedSpec());

                }
            }

        }

    }


    public void updateBalancedTimerTriggerSpec(List<PeriodicProjectSpecification> projectsList, StringBuffer log, String hostName, int portNumber) {

        for (PeriodicProjectSpecification projectSpec : projectsList) {

            if (!projectSpec.getSpec().equals(projectSpec.getBalancedSpec())) {
                if (updateProjectTimerTrigger(projectSpec.getJobName(), projectSpec.getBalancedSpec(), hostName, portNumber)) {
                    logger.log(Level.INFO, "### Periodic Project: " + projectSpec.getJobName() + "has been successfully updated " +
                            "Old Spec: " + projectSpec.getSpec() + " new Balanced Spec: " + projectSpec.getBalancedSpec());

                    EventsLogger.getInstance().logInfo(projectSpec.getJobName() + "has been successfully updated " +
                            "Old Spec: " + projectSpec.getSpec() + " new Balanced Spec: " + projectSpec.getBalancedSpec());

                    if (null != log) {
                        log.append(projectSpec.getJobName() + ": " + projectSpec.getSpec() + " --> " + projectSpec.getBalancedSpec() + "\n");
                    }


                }
            }

        }

    }


    public void revertOldTimerTriggerSpec(List<PeriodicProjectSpecification> projectsList, String hostName, int portNumber) {

        for (PeriodicProjectSpecification projectSpec : projectsList) {

            if (!projectSpec.getSpec().equals(projectSpec.getBalancedSpec())) {
                if (updateProjectTimerTrigger(projectSpec.getJobName(), projectSpec.getSpec(), hostName, portNumber)) {
                    logger.log(Level.INFO, "### Periodic Project: " + projectSpec.getJobName() + "has been successfuly reverted back to " +
                            "Old Spec: " + projectSpec.getSpec() + " from Balanced Spec: " + projectSpec.getBalancedSpec());
                }
            }


        }

    }

    public void revertOldTimerTriggerSpec(List<PeriodicProjectSpecification> projectsList, StringBuffer log, String hostName, int portNumber) {

        for (PeriodicProjectSpecification projectSpec : projectsList) {

            if (!projectSpec.getSpec().equals(projectSpec.getBalancedSpec())) {
                if (updateProjectTimerTrigger(projectSpec.getJobName(), projectSpec.getSpec(), hostName, portNumber)) {
                    logger.log(Level.INFO, "### Periodic Project: " + projectSpec.getJobName() + "has been successfully reverted back to " +
                            "Old Spec: " + projectSpec.getSpec() + " from Balanced Spec: " + projectSpec.getBalancedSpec());

                    EventsLogger.getInstance().logInfo("### Periodic Project: " + projectSpec.getJobName() + "has been successfully reverted back to " +
                            "Old Spec: " + projectSpec.getSpec() + " from Balanced Spec: " + projectSpec.getBalancedSpec());


                    log.append(projectSpec.getJobName() + ": " + projectSpec.getBalancedSpec() + " --> " + projectSpec.getSpec() + "\n");

                }
            }


        }

    }


    private boolean updateProjectTimerTrigger(String projectName, String newSpec, String hostUrl, int portNumber) {

        boolean val = false;
        AbstractProject<?, ?> p = (AbstractProject<?, ?>) hudson.model.Hudson.getInstance().getItem(projectName);
        for (hudson.triggers.TriggerDescriptor td : p.getTriggers().keySet()) {

            if (p.getTriggers().get(td) instanceof hudson.triggers.TimerTrigger) {

                try {
                    p.addTrigger(new TimerTrigger(newSpec));
                    p.save();
                    val = true;

                    // dump the config.xml
                    String baseUrl = Hudson.getInstance().getRootUrl();
                    String configFileUrl = baseUrl + p.getUrl() + "/config.xml";
                    // In order to accommodate Hudson TestCases
                    String configXml = this.fetchProjectXml(projectName, true, configFileUrl, hostUrl, portNumber);
                    this.updateJobConfigulationXml(projectName, configXml, configFileUrl, hostUrl, portNumber);

                } catch (ANTLRException e) {
                    // TODO Auto-generated catch block
                    logger.log(Level.SEVERE, e.getMessage());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.log(Level.SEVERE, e.getMessage());

                }

            }

        }
        return val;
    }

    private boolean isNumeric(String value) {
        return (value.matches("^[0-9]+$"));
    }

    private String extractCronExpression(String expr) {

        String cronExpr = "";
        if (null == expr || expr.length() == 0) {
            logger.log(Level.INFO, "### Error: Empty Expression");

            cronExpr = "";
        } else {
            String[] lines = expr.split("\n");
            if (lines.length > 1) {
                logger.log(Level.INFO, "### Warning: Has Multiple lines");
                for (String str : lines) {
                    if (str.startsWith("*") || str.startsWith("@") || isNumeric("" + str.charAt(0) + "")) {
                        cronExpr = str;
                        break;
                    }

                }
            } else {
                logger.log(Level.INFO, "### Info: Single Line Expression");
                if (lines[0].startsWith("*") || lines[0].startsWith("@") || isNumeric("" + lines[0].charAt(0) + "")) {
                    cronExpr = lines[0];
                } else {
                    logger.log(Level.INFO, "### Error: Invalid Expression");
                    cronExpr = "";
                }
            }
        }

        return cronExpr;
    }

    private double getAverageBuildTime(@SuppressWarnings("rawtypes") SortedMap<Integer, Run> map) {

        double sum = 0.0;

        for (Map.Entry<Integer, Run> entry : map.entrySet()) {
            Run runx = entry.getValue();
            logger.log(Level.INFO, "Build (" + entry.getKey() + "): " + runx.getDuration() + "");
            sum += runx.getDuration();
        }
        return map.size() > 0 ? (sum / map.size()) / 60000.0 : 0.0;
    }


    // This method can parse into the Shell scripts

    private void printJobShellScripts() {
        logger.log(Level.INFO, "###Inside printJobShellScripts()");
        Hudson inst = Hudson.getInstance();

        for (AbstractProject<?, ?> p : inst.getAllItems(AbstractProject.class)) {
            List<String> shellContent = new ArrayList<String>();
            DescribableList<Builder, Descriptor<Builder>> buildersList = getBuildersList(p);

            Shell shell = (Shell) buildersList.get(Shell.class);
            if (shell != null) {
                shellContent.add(shell.getCommand());
                logger.log(Level.INFO, "Command : " + shell.getCommand());
            } else {
                shellContent.add("");
            }

        }

    }


    public List<PeriodicProjectSpecification> getPeriodicJobList() {
        return periodicJobList;
    }

    public boolean isCompleted() {
        return completed;
    }


    public String getResult() {
        return result;
    }


    @SuppressWarnings("unchecked")
    private DescribableList<Builder, Descriptor<Builder>> getBuildersList(
            AbstractProject<?, ?> item) {
        if (item instanceof FreeStyleProject) {
            return ((FreeStyleProject) item).getBuildersList();
        }
        if (item instanceof Project) {
            return ((Project) item).getBuildersList();
        } else {
            return null;
        }

    }


    private String fetchProjectXml(String jobName, boolean failIfNotExist, String jobUrl, String hostName, int hostPortNumber) {

        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(jobUrl);

        // This code will be used as HostConfig Settings  if passed as null, then it is ignored.

        if (null != hostName && hostPortNumber > 0) {
            HostConfiguration hf = new HostConfiguration();
            hf.setHost("http://localhost", 56823);
            getMethod.setHostConfiguration(hf);
        }


        client.getParams().setConnectionManagerTimeout(connectionManagerTimeout);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
        client.getParams().setSoTimeout(soTimeout);

        try {
            int status = client.executeMethod(getMethod);
            if (status != HttpStatus.SC_OK) {
                if (failIfNotExist) {
                    throw new EventException((short) status, "Get xml " + jobName + " failed : " + status);
                } else {
                    return null;
                }

            }
            return getMethod.getResponseBodyAsString();

        } catch (HttpException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());
        }

        return null;
    }

    // This method should be applied if Jenkins is pre-RTS
    @SuppressWarnings("deprecation")
    private void updateJobConfigulationXml(String jobName, String config, String jobUrl, String hostName, int hostPortNumber) {

        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(jobUrl);

        if (null != hostName && hostPortNumber > 0) {
            // This is an injected code
            HostConfiguration hf = new HostConfiguration();
            hf.setHost("http://localhost", 56823);
            postMethod.setHostConfiguration(hf);
        }

        client.getParams().setConnectionManagerTimeout(connectionManagerTimeout);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
        client.getParams().setSoTimeout(soTimeout);
        postMethod.setRequestHeader("Content-type", "application/xml; charset=ISO-8859-1");
        postMethod.setRequestBody(config);
        try {
            int status = client.executeMethod(postMethod);
            if (status != HttpStatus.SC_OK) {
                throw new EventException((short) status, "Get xml " + jobName + " failed : " + status);

            } else {
                logger.log(Level.INFO, "" + jobName + "was updated");
            }

        } catch (HttpException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE, e.getMessage());
        }
    }


}
