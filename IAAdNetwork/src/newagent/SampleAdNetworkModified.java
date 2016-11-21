/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import edu.umich.eecs.tac.props.BankStatus;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.demand.campaign.auction.CampaignAuctionReport;
import tau.tac.adx.report.publisher.AdxPublisherReport;

/**
 *
 * @author Mariano Schain modified by hsn
 */
public class SampleAdNetworkModified extends Agent {

    private static final Logger log = Logger.getLogger(SampleAdNetworkModified.class.getName());

    /*
     * Basic simulation information. An agent should receive the {@link
     * StartInfo} at the beginning of the game or during recovery.
     */
    @SuppressWarnings("unused")
    private StartInfo startInfo;
    /**
     * Messages received:
     *
     * We keep all the {@link CampaignReport campaign reports} delivered to the
     * agent. We also keep the initialization messages {@link PublisherCatalog}
     * and {@link InitialCampaignMessage} and the most recent messages and
     * reports {@link CampaignOpportunityMessage}, {@link CampaignReport}, and
     * {@link AdNetworkDailyNotification}.
     */
    private final Queue<CampaignReport> campaignReports;
    private PublisherCatalog publisherCatalog;
    private InitialCampaignMessage initialCampaignMessage;
    private AdNetworkDailyNotification adNetworkDailyNotification;

    /*
     * The addresses of server entities to which the agent should send the daily
     * bids data
     */
    private String demandAgentAddress;
    private String adxAgentAddress;

    /*
     * we maintain a list of queries - each characterized by the web site (the
     * publisher), the device type, the ad type, and the user market segment
     */
    private AdxQuery[] queries;
    /**
     * Information regarding the latest campaign opportunity announced
     */
    private CampaignData pendingCampaign;
    /**
     * We maintain a collection (mapped by the campaign id) of the campaigns won
     * by our agent.
     */
    private Map<Integer, CampaignData> myCampaigns;

    /*
     * the bidBundle to be sent daily to the AdX
     */
    private AdxBidBundle bidBundle;

    /*
     * The current bid level for the user classification service
     */
    double ucsBid;

    /*
     * The targeted service level for the user classification service
     */
    double ucsTargetLevel;

    /*
     * current day of simulation
     */
    private int day;
    private int iteration;
    private String[] publisherNames;
    private CampaignData currCampaign;
    
    /*
     * Log records
     */
    private List<CampaignLogReport> logReports = new ArrayList<CampaignLogReport>();
    
    /*
     * Winning Campaign(s)
     */
    private List<CampaignLogReport> winCampaigns = new ArrayList<CampaignLogReport>();

    /*
     * Losing Campaign(s)
     */
    private List<CampaignLogReport> lostCampaigns = new ArrayList<CampaignLogReport>();

    /*
     * Conflicting Campaign(s)
     */
    private List<CampaignLogReport> conflictingCampaigns = new ArrayList<CampaignLogReport>();

    public SampleAdNetworkModified() {
        campaignReports = new LinkedList<CampaignReport>();
    }

    public List<CampaignLogReport> getConflictingCampaigns() {
        return conflictingCampaigns;
    }

    public void setConflictingCampaigns(List<CampaignLogReport> conflictingCampaigns) {
        this.conflictingCampaigns = conflictingCampaigns;
    }

    public List<CampaignLogReport> getLostCampaigns() {
        return lostCampaigns;
    }

    public void setLostCampaigns(List<CampaignLogReport> lostCampaigns) {
        this.lostCampaigns = lostCampaigns;
    }

    public List<CampaignLogReport> getWinCampaigns() {
        return winCampaigns;
    }

    public void setWinCampaigns(List<CampaignLogReport> winCampaigns) {
        this.winCampaigns = winCampaigns;
    }

    public List<CampaignLogReport> getLogReports() {
        return logReports;
    }

    public void setLogReports(List<CampaignLogReport> logReports) {
        this.logReports = logReports;
    }

    public Queue<CampaignReport> getCampaignReports() {
        return campaignReports;
    }

    public AdNetworkDailyNotification getAdNetworkDailyNotification() {
        return adNetworkDailyNotification;
    }

    public void setAdNetworkDailyNotification(AdNetworkDailyNotification adNetworkDailyNotification) {
        this.adNetworkDailyNotification = adNetworkDailyNotification;
    }

    public String getAdxAgentAddress() {
        return adxAgentAddress;
    }

    public void setAdxAgentAddress(String adxAgentAddress) {
        this.adxAgentAddress = adxAgentAddress;
    }

    public AdxBidBundle getBidBundle() {
        return bidBundle;
    }

    public void setBidBundle(AdxBidBundle bidBundle) {
        this.bidBundle = bidBundle;
    }

    public CampaignData getCurrCampaign() {
        return currCampaign;
    }

    public void setCurrCampaign(CampaignData currCampaign) {
        this.currCampaign = currCampaign;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDemandAgentAddress() {
        return demandAgentAddress;
    }

    public void setDemandAgentAddress(String demandAgentAddress) {
        this.demandAgentAddress = demandAgentAddress;
    }

    public InitialCampaignMessage getInitialCampaignMessage() {
        return initialCampaignMessage;
    }

    public void setInitialCampaignMessage(InitialCampaignMessage initialCampaignMessage) {
        this.initialCampaignMessage = initialCampaignMessage;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public Map<Integer, CampaignData> getMyCampaigns() {
        return myCampaigns;
    }

    public void setMyCampaigns(Map<Integer, CampaignData> myCampaigns) {
        this.myCampaigns = myCampaigns;
    }

    public CampaignData getPendingCampaign() {
        return pendingCampaign;
    }

    public void setPendingCampaign(CampaignData pendingCampaign) {
        this.pendingCampaign = pendingCampaign;
    }

    public PublisherCatalog getPublisherCatalog() {
        return publisherCatalog;
    }

    public void setPublisherCatalog(PublisherCatalog publisherCatalog) {
        this.publisherCatalog = publisherCatalog;
    }

    public String[] getPublisherNames() {
        return publisherNames;
    }

    public void setPublisherNames(String[] publisherNames) {
        this.publisherNames = publisherNames;
    }

    public AdxQuery[] getQueries() {
        return queries;
    }

    public void setQueries(AdxQuery[] queries) {
        this.queries = queries;
    }

    public StartInfo getStartInfo() {
        return startInfo;
    }

    public void setStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
    }

    public double getUcsBid() {
        return ucsBid;
    }

    public void setUcsBid(double ucsBid) {
        this.ucsBid = ucsBid;
    }

    public double getUcsTargetLevel() {
        return ucsTargetLevel;
    }

    public void setUcsTargetLevel(double ucsTargetLevel) {
        this.ucsTargetLevel = ucsTargetLevel;
    }

    @Override
    protected void messageReceived(Message message) {
        try {
            Transportable content = message.getContent();

            // log.fine(message.getContent().getClass().toString());

            if (content instanceof InitialCampaignMessage) {
                // done
                System.out.println("============== InitialCampaignMessage ============== " + iteration);
                handleInitialCampaignMessage((InitialCampaignMessage) content);
            } else if (content instanceof CampaignOpportunityMessage) {
                // done
                System.out.println("============== CampaignOpportunityMessage ============== " + iteration);
                handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);
            } else if (content instanceof CampaignReport) {
                // done
                System.out.println("============== CampaignReport ============== " + iteration);
                handleCampaignReport((CampaignReport) content);
            } else if (content instanceof AdNetworkDailyNotification) {
                // done
                System.out.println("============== AdNetworkDailyNotification ============== " + iteration);
                handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);
            } else if (content instanceof AdxPublisherReport) {
                // done
                System.out.println("============== AdxPublisherReport ============== " + iteration);
                handleAdxPublisherReport((AdxPublisherReport) content);
            } else if (content instanceof SimulationStatus) {
                // done
                System.out.println("============== SimulationStatus ============== " + iteration);
                handleSimulationStatus((SimulationStatus) content);
            } else if (content instanceof PublisherCatalog) {
                // done
                System.out.println("============== PublisherCatalog ============== " + iteration);
                handlePublisherCatalog((PublisherCatalog) content);
            } else if (content instanceof AdNetworkReport) {
                // skip
                System.out.println("============== AdNetworkReport ============== " + iteration);
                handleAdNetworkReport((AdNetworkReport) content);
            } else if (content instanceof StartInfo) {
                // skip
                System.out.println("============== StartInfo ============== " + iteration);
                handleStartInfo((StartInfo) content);
            } else if (content instanceof BankStatus) {
                // skip
                System.out.println("============== BankStatus ============== " + iteration);
                handleBankStatus((BankStatus) content);
            } else if (content instanceof CampaignAuctionReport) {
                // skip
                System.out.println("============== CampaignAuctionReport ============== " + iteration);
                handleCampaignAuctionReport((CampaignAuctionReport) content);
            } else {
                System.out.println("UNKNOWN Message Received: " + content);
            }
            iteration++;
        } catch (NullPointerException e) {
            SampleAdNetworkModified.log.log(Level.SEVERE, "Exception thrown while trying to parse message.{0}", e);
        }
    }

    private void handleCampaignAuctionReport(CampaignAuctionReport content) {
        // ingoring
    }

    private void handleBankStatus(BankStatus content) {
        System.out.println("Day " + day + " :" + content.toString());

        /*
         * Record Log
         */
        for (int i = 0; i < logReports.size(); i++) {
            if (logReports.get(i).getDay() == day) {
                logReports.get(i).setBankStatus(content.getAccountBalance());
            }
        }
    }

    /**
     * Processes the start information.
     *
     * @param startInfo the start information.
     */
    protected void handleStartInfo(StartInfo startInfo) {
        this.startInfo = startInfo;
    }

    /**
     * Process the reported set of publishers
     *
     * @param publisherCatalog
     */
    private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
        new HandlePublisherCatalog().run(this, publisherCatalog);
    }

    /**
     * On day 0, a campaign (the "initial campaign") is allocated to each
     * competing agent. The campaign starts on day 1. The address of the
     * server's AdxAgent (to which bid bundles are sent) and DemandAgent (to
     * which bids regarding campaign opportunities may be sent in subsequent
     * days) are also reported in the initial campaign message
     */
    private void handleInitialCampaignMessage(InitialCampaignMessage campaignMessage) {
        new HandleCampaignMessage().run(this, campaignMessage);
    }

    /**
     * On day n ( > 0) a campaign opportunity is announced to the competing
     * agents. The campaign starts on day n + 2 or later and the agents may send
     * (on day n) related bids (attempting to win the campaign). The allocation
     * (the winner) is announced to the competing agents during day n + 1.
     */
    private void handleICampaignOpportunityMessage(CampaignOpportunityMessage com) {
        new HandleCampaignOpportunityMessage().run(this, com);
    }

    public void sendResponse(String demandAgentAddress, Transportable bids) {
        sendMessage(demandAgentAddress, bids);
    }

    /**
     * On day n ( > 0), the result of the UserClassificationService and Campaign
     * auctions (for which the competing agents sent bids during day n -1) are
     * reported. The reported Campaign starts in day n+1 or later and the user
     * classification service level is applicable starting from day n+1.
     */
    private void handleAdNetworkDailyNotification(AdNetworkDailyNotification notificationMessage) {
        new HandleAdNetworkDailyNotification().run(this, notificationMessage);
    }

    /**
     * The SimulationStatus message received on day n indicates that the
     * calculation time is up and the agent is requested to send its bid bundle
     * to the AdX.
     */
    private void handleSimulationStatus(SimulationStatus simulationStatus) {
        new HandleSimulationStatus().run(this, simulationStatus);
    }

    /**
     *
     */
    protected void sendBidAndAds(SampleAdNetworkModified adNetwork) {
        new SendTheBidsAndAds().Run(adNetwork);
    }

    /**
     * Campaigns performance w.r.t. each allocated campaign
     */
    private void handleCampaignReport(CampaignReport campaignReport) {
        new HandleCampaignReport().run(this, campaignReport);
    }

    /**
     * Users and Publishers statistics: popularity and ad type orientation
     */
    private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
        new HandleAdxPublisherReport().run(this, adxPublisherReport);
    }

    /**
     *
     * @param AdNetworkReport
     */
    private void handleAdNetworkReport(AdNetworkReport adnetReport) {

        System.out.println("Day " + day + " : AdNetworkReport");
        /*
         * for (AdNetworkKey adnetKey : adnetReport.keys()) {
         *
         * double rnd = Math.random(); if (rnd > 0.95) { AdNetworkReportEntry
         * entry = adnetReport .getAdNetworkReportEntry(adnetKey);
         * System.out.println(adnetKey + " " + entry); } }
         */
    }

    @Override
    protected void simulationSetup() {

        day = 0;
        bidBundle = new AdxBidBundle();

        /*
         * initial bid between 0.1 and 0.2
         */
        ucsBid = 0.2;

        myCampaigns = new HashMap<Integer, CampaignData>();
        log.log(Level.FINE, "AdNet {0} simulationSetup", getName());
    }

    @Override
    protected void simulationFinished() {
        campaignReports.clear();
        bidBundle = null;
    }

    /**
     * A user visit to a publisher's web-site results in an impression
     * opportunity (a query) that is characterized by the the publisher, the
     * market segment the user may belongs to, the device used (mobile or
     * desktop) and the ad type (text or video).
     *
     * An array of all possible queries is generated here, based on the
     * publisher names reported at game initialization in the publishers catalog
     * message
     */
    private void generateAdxQuerySpace(SampleAdNetworkModified adNetwork) {
        new GeneratingAdxQuerySpace().run(adNetwork);
    }

    public void generatingAdxQuerySpace(SampleAdNetworkModified adNetwork) {
        generateAdxQuerySpace(adNetwork);
    }

    /*
     * genarates an array of the publishers names
     *
     */
    public void getPublishersNames(SampleAdNetworkModified adNetwork) {
        if (null == adNetwork.getPublisherNames() && adNetwork.getPublisherCatalog() != null) {
            ArrayList<String> names = new ArrayList<String>();
            for (PublisherCatalogEntry pce : adNetwork.getPublisherCatalog()) {
                names.add(pce.getPublisherName());
            }

            adNetwork.setPublisherNames(new String[names.size()]);
            names.toArray(adNetwork.getPublisherNames());
        }
    }

    /*
     * genarates the campaign queries relevant for the specific campaign, and
     * assign them as the campaigns campaignQueries field
     */
    public void genCampaignQueries(CampaignData campaignData) {
        Set<AdxQuery> campaignQueriesSet = new HashSet<AdxQuery>();
        for (String PublisherName : publisherNames) {
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaignData.targetSegment, Device.mobile, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaignData.targetSegment, Device.mobile, AdType.video));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaignData.targetSegment, Device.pc, AdType.text));
            campaignQueriesSet.add(new AdxQuery(PublisherName,
                    campaignData.targetSegment, Device.pc, AdType.video));
        }

        campaignData.campaignQueries = new AdxQuery[campaignQueriesSet.size()];
        campaignQueriesSet.toArray(campaignData.campaignQueries);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + Arrays.toString(campaignData.campaignQueries) + "!!!!!!!!!!!!!!!!");
    }
}
