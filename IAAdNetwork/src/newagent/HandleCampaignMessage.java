/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import tau.tac.adx.report.demand.InitialCampaignMessage;

/**
 *
 * @author hsn
 */
public class HandleCampaignMessage {

    public HandleCampaignMessage() {
    }

    public void run(SampleAdNetworkModified adNetwork, InitialCampaignMessage campaignMessage) {
        System.out.println(campaignMessage.toString());

        adNetwork.setDay(0);

        adNetwork.setDemandAgentAddress(campaignMessage.getDemandAgentAddress());
        adNetwork.setAdxAgentAddress(campaignMessage.getAdxAgentAddress());

        CampaignData campaignData = new CampaignData(campaignMessage);
        campaignData.setBudget(campaignMessage.getBudgetMillis() / 1000.0);
        adNetwork.setCurrCampaign(campaignData);
        adNetwork.genCampaignQueries(adNetwork.getCurrCampaign());

        /*
         * The initial campaign is already allocated to our agent so we add it
         * to our allocated-campaigns list.
         */
        System.out.println("Day " + adNetwork.getDay() + ": Allocated campaign - " + campaignData);
        adNetwork.getMyCampaigns().put(campaignMessage.getId(), campaignData);

        /*
         * Record Log
         */
        CampaignLogReport logReport = new CampaignLogReport();
        logReport.setCampaignId(campaignData.id);
        logReport.setDay(adNetwork.getDay());
        logReport.setReachImps(campaignData.reachImps);
        logReport.setDayStart((int) campaignData.dayStart);
        logReport.setDayEnd((int) campaignData.dayEnd);
        logReport.setTargetSegment(campaignData.targetSegment);
        logReport.setVideoCoef(campaignData.videoCoef);
        logReport.setMobileCoef(campaignData.mobileCoef);
        logReport.setCampaignQueries(campaignData.campaignQueries);
        logReport.setStats(campaignData.stats);
        logReport.setBudget(campaignData.budget);
        adNetwork.getLogReports().add(logReport);
    }
}
