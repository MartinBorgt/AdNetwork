/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportKey;

/**
 *
 * @author hsn
 */
public class HandleCampaignReport {

    public HandleCampaignReport() {
    }

    public void run(SampleAdNetworkModified adNetwork, CampaignReport campaignReport) {

        adNetwork.getCampaignReports().add(campaignReport);

        /*
         * for each campaign, the accumulated statistics from day 1 up to day
         * n-1 are reported
         */
        for (CampaignReportKey campaignKey : campaignReport.keys()) {
            int cmpId = campaignKey.getCampaignId();
            CampaignStats cstats = campaignReport.getCampaignReportEntry(
                    campaignKey).getCampaignStats();
            adNetwork.getMyCampaigns().get(cmpId).setStats(cstats);

            System.out.println("Day " + adNetwork.getDay() + ": Updating campaign " + cmpId + " stats: "
                    + cstats.getTargetedImps() + " tgtImps "
                    + cstats.getOtherImps() + " nonTgtImps. Cost of imps is "
                    + cstats.getCost());

            /*
             * Record Log
             */
            for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
                if(adNetwork.getLogReports().get(i).getCampaignId() == cmpId){
                    adNetwork.getLogReports().get(i).setTargetedImps(cstats.getTargetedImps());
                    adNetwork.getLogReports().get(i).setOtherImps(cstats.getOtherImps());
                    adNetwork.getLogReports().get(i).setCost(cstats.getCost());
                }
            }
        }

    }
}
