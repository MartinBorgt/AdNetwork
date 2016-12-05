/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.StringTokenizer;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;

/**
 *
 * @author hsn
 */
public class HandleAdNetworkDailyNotification {

    public HandleAdNetworkDailyNotification() {
    }

    public void run(SampleAdNetworkModified adNetwork, AdNetworkDailyNotification notificationMessage) {

        adNetwork.setAdNetworkDailyNotification(notificationMessage);

        System.out.println("Day " + adNetwork.getDay() + ": Daily notification for campaign "
                + adNetwork.getAdNetworkDailyNotification().getCampaignId());

        String campaignAllocatedTo = " allocated to "
                + notificationMessage.getWinner();

        if ((adNetwork.getPendingCampaign().id == adNetwork.getAdNetworkDailyNotification().getCampaignId())
                && (notificationMessage.getCostMillis() != 0)) {

            /*
             * add campaign to list of won campaigns
             */
            adNetwork.getPendingCampaign().setBudget(notificationMessage.getCostMillis() / 1000.0);
            adNetwork.setCurrCampaign(adNetwork.getPendingCampaign());
            adNetwork.genCampaignQueries(adNetwork.getCurrCampaign());
            adNetwork.getMyCampaigns().put(adNetwork.getPendingCampaign().id, adNetwork.getPendingCampaign());

            campaignAllocatedTo = " WON at cost (Millis)"
                    + notificationMessage.getCostMillis();
        }

        System.out.println("Day " + adNetwork.getDay() + ": " + campaignAllocatedTo
                + ". UCS Level set to " + notificationMessage.getServiceLevel()
                + " at price " + notificationMessage.getPrice()
                + " Quality Score is: " + notificationMessage.getQualityScore());

        /*
         * Record Log
         */
        for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
            if (adNetwork.getLogReports().get(i).getCampaignId() == adNetwork.getAdNetworkDailyNotification().getCampaignId()) {
                adNetwork.getLogReports().get(i).setWinner(campaignAllocatedTo);
                if(campaignAllocatedTo.contains(" WON at cost (Millis)")){
                    StringTokenizer st = new StringTokenizer(campaignAllocatedTo, ")");
                    st.nextToken();
                    adNetwork.getLogReports().get(i).setSecondPrice(Long.parseLong(st.nextToken()));
                }
                adNetwork.getLogReports().get(i).setCampaignQueries(adNetwork.getCurrCampaign().campaignQueries);
                adNetwork.getLogReports().get(i).setServiceLevel(notificationMessage.getServiceLevel());
                adNetwork.getLogReports().get(i).setQualityScore(notificationMessage.getQualityScore());
                adNetwork.getLogReports().get(i).setPrice(notificationMessage.getPrice());
            }
        }
    }
}
