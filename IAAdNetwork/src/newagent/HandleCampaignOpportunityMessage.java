/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Random;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

/**
 *
 * @author hsn
 */
public class HandleCampaignOpportunityMessage {

    public HandleCampaignOpportunityMessage() {
    }
    
    public void run(SampleAdNetworkModified adNetwork, CampaignOpportunityMessage com) {
        adNetwork.setDay(com.getDay());

        adNetwork.setPendingCampaign(new CampaignData(com));
        System.out.println("Day " + adNetwork.getDay() + ": Campaign opportunity - " + adNetwork.getPendingCampaign());

        /*
         * The campaign requires com.getReachImps() impressions. The competing
         * Ad Networks bid for the total campaign Budget (that is, the ad
         * network that offers the lowest budget gets the campaign allocated).
         * The advertiser is willing to pay the AdNetwork at most 1$ CPM,
         * therefore the total number of impressions may be treated as a reserve
         * (upper bound) price for the auction.
         */

        Random random = new Random();
        long cmpimps = com.getReachImps();
        long cmpBidMillis = random.nextInt((int) cmpimps);

        System.out.println("Day " + adNetwork.getDay() + ": Campaign total budget bid (millis): " + cmpBidMillis);

        /*
         * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
         * user classification service is piggybacked
         */

        if (adNetwork.getAdNetworkDailyNotification() != null) {
            double ucsLevel = adNetwork.getAdNetworkDailyNotification().getServiceLevel();
            adNetwork.setUcsBid(0.1 + random.nextDouble() / 10.0);
            System.out.println("Day " + adNetwork.getDay() + ": ucs level reported: " + ucsLevel);
        } else {
            System.out.println("Day " + adNetwork.getDay() + ": Initial ucs bid is " + adNetwork.getUcsBid());
        }

        /*
         * Record Log
         */
        CampaignLogReport logReport = new CampaignLogReport();
        logReport.setCampaignId(adNetwork.getPendingCampaign().id);
        logReport.setDay(adNetwork.getDay());
        logReport.setReachImps(adNetwork.getPendingCampaign().reachImps);
        logReport.setDayStart((int) adNetwork.getPendingCampaign().dayStart);
        logReport.setDayEnd((int) adNetwork.getPendingCampaign().dayEnd);
        logReport.setTargetSegment(adNetwork.getPendingCampaign().targetSegment);
        logReport.setVideoCoef(adNetwork.getPendingCampaign().videoCoef);
        logReport.setMobileCoef(adNetwork.getPendingCampaign().mobileCoef);
        logReport.setCampaignQueries(adNetwork.getPendingCampaign().campaignQueries);
        logReport.setStats(adNetwork.getPendingCampaign().stats);
        logReport.setBudget(adNetwork.getPendingCampaign().budget);
        logReport.setUcsLevel(adNetwork.getUcsBid());
        logReport.setBudgetMilis(cmpBidMillis);
        adNetwork.getLogReports().add(logReport);
        
        /*
         * Note: Campaign bid is in millis
         */
        AdNetBidMessage bids = new AdNetBidMessage(adNetwork.getUcsBid(), adNetwork.getPendingCampaign().id, cmpBidMillis);
        adNetwork.sendResponse(adNetwork.getDemandAgentAddress(), bids);
    }
}
