/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.ArrayList;
import java.util.Iterator;
import se.sics.tasim.props.SimulationStatus;
import tau.tac.adx.report.adn.MarketSegment;

/**
 *
 * @author hsn
 */
public class HandleSimulationStatus {

    public HandleSimulationStatus() {
    }

    public void run(SampleAdNetworkModified adNetwork, SimulationStatus simulationStatus) {
        /*
         * Log tracing
         */
//        System.out.println("##### LOG TRACING #####");
//        for (Iterator<CampaignLogReport> it = adNetwork.getLogReports().iterator(); it.hasNext();) {
//            CampaignLogReport clr = it.next();
//            System.out.println("Campaign ID: " + clr.getCampaignId() + " from day " + clr.getDayStart() + " to day " + clr.getDayEnd() + " Issued: day " + clr.getDay());
//            System.out.println("ReachImps: " + clr.getReachImps() + " Target Segment: " + clr.getTargetSegment() + " Vid Coef: " + clr.getVideoCoef() + " Mobile Coef: " + clr.getMobileCoef());
//            System.out.println("Stats: " + clr.getStats() + " Budget: " + clr.getBudget() + " UCS: " + clr.getUcsLevel());
//            System.out.println("Budget Milis: " + clr.getBudgetMilis() + " Target Imps: " + clr.getTargetedImps() + " Other Imps: " + clr.getOtherImps());
//            System.out.println("Cost: " + clr.getCost() + " Winner: " + clr.getWinner() + " Service Level: " + clr.getServiceLevel() + " Price: " + clr.getPrice());
//            if (clr.getWinner() != null && clr.getWinner().contains("WON")) {
//                System.out.println("First Price: " + clr.getBudgetMilis() + " Second Price: " + clr.getSecondPrice());
//            }
//            System.out.println("Bank Status: " + clr.getBankStatus());
//            System.out.println("----------------------------------------------------------");
//        }

        // updating sucessfull campaign
        System.out.println("############ Winning Campaign ############");
        adNetwork.setWinCampaigns(new ArrayList<CampaignLogReport>());
        int n = 0;
        for (Iterator<CampaignLogReport> it = adNetwork.getLogReports().iterator(); it.hasNext();) {
            CampaignLogReport campaignLogReport = it.next();
            if (n == 0) {
                adNetwork.getWinCampaigns().add(campaignLogReport);
                System.out.println("Campaign ID: " + campaignLogReport.getCampaignId() + " from day [" + campaignLogReport.getDayStart() + " to " + campaignLogReport.getDayEnd() + "] " + campaignLogReport.getTargetSegment() + " Reach Target: " + campaignLogReport.getReachImps() + " First Price: " + campaignLogReport.getBudgetMilis() + " Second Price: " + campaignLogReport.getSecondPrice() + " Service Level: " + campaignLogReport.getServiceLevel() + " Bank Status: " + campaignLogReport.getBankStatus());
                System.out.println("-----------------------------------");
            } else if (campaignLogReport.getSecondPrice() > 0) {
                adNetwork.getWinCampaigns().add(campaignLogReport);
                System.out.println("Campaign ID: " + campaignLogReport.getCampaignId() + " from day [" + campaignLogReport.getDayStart() + " to " + campaignLogReport.getDayEnd() + "] " + campaignLogReport.getTargetSegment() + " Reach Target: " + campaignLogReport.getReachImps() + " First Price: " + campaignLogReport.getBudgetMilis() + " Second Price: " + campaignLogReport.getSecondPrice() + " Service Level: " + campaignLogReport.getServiceLevel() + " Bank Status: " + campaignLogReport.getBankStatus());
                System.out.println("-----------------------------------");
            }
            n++;
        }

        // updating lost campaign
        System.out.println("");
        System.out.println("############ Losing Campaign ############");
        adNetwork.setLostCampaigns(new ArrayList<CampaignLogReport>());
        n = 0;
        for (Iterator<CampaignLogReport> it = adNetwork.getLogReports().iterator(); it.hasNext();) {
            CampaignLogReport campaignLogReport = it.next();
            if (n > 0) {
                if (campaignLogReport.getWinner() != null && campaignLogReport.getSecondPrice() == 0) {
                    adNetwork.getLostCampaigns().add(campaignLogReport);
                    System.out.println("Campaign ID: " + campaignLogReport.getCampaignId() + " from day [" + campaignLogReport.getDayStart() + " to " + campaignLogReport.getDayEnd() + "] " + campaignLogReport.getTargetSegment() + " Reach Target: " + campaignLogReport.getReachImps() + " First Price: " + campaignLogReport.getBudgetMilis() + " Second Price: " + campaignLogReport.getSecondPrice() + " Service Level: " + campaignLogReport.getServiceLevel() + " Bank Status: " + campaignLogReport.getBankStatus());
                    System.out.println("-----------------------------------");
                }
            }
            n++;
        }

        // updating conflicting campaign
        System.out.println("");
        System.out.println("############ Conflicting Campaign ############");
        adNetwork.setConflictingCampaigns(new ArrayList<CampaignLogReport>());
        for (Iterator<CampaignLogReport> it = adNetwork.getWinCampaigns().iterator(); it.hasNext();) {
            CampaignLogReport campaignLogReport = it.next();
            for (Iterator<CampaignLogReport> it1 = adNetwork.getLogReports().iterator(); it1.hasNext();) {
                CampaignLogReport campaignLogReport1 = it1.next();
                boolean found = false;
                for (Iterator<CampaignLogReport> it2 = adNetwork.getWinCampaigns().iterator(); it2.hasNext();) {
                    CampaignLogReport campaignLogReport2 = it2.next();
                    if (campaignLogReport2.getCampaignId() == campaignLogReport1.getCampaignId()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    boolean sameTarget = false;
                    for (Iterator<MarketSegment> it2 = campaignLogReport1.getTargetSegment().iterator(); it2.hasNext();) {
                        MarketSegment marketSegment = it2.next();
                        for (Iterator<MarketSegment> it3 = campaignLogReport.getTargetSegment().iterator(); it3.hasNext();) {
                            MarketSegment marketSegment1 = it3.next();
                            if (marketSegment == marketSegment1) {
                                sameTarget = true;
                                break;
                            }
                        }
                        if (sameTarget) {
                            break;
                        }
                    }
                    if (sameTarget) {
                        adNetwork.getConflictingCampaigns().add(campaignLogReport1);
                        System.out.println("Campaign ID: " + campaignLogReport1.getCampaignId() + " from day [" + campaignLogReport1.getDayStart() + " to " + campaignLogReport1.getDayEnd() + "] " + campaignLogReport1.getTargetSegment() + " Reach Target: " + campaignLogReport1.getReachImps() + " First Price: " + campaignLogReport1.getBudgetMilis() + " Second Price: " + campaignLogReport1.getSecondPrice() + " Service Level: " + campaignLogReport1.getServiceLevel() + " Bank Status: " + campaignLogReport1.getBankStatus());
                        System.out.println("-----------------------------------");
                    }
                }
            }
        }

        System.out.println("Day " + adNetwork.getDay() + " : Simulation Status Received");
        adNetwork.sendBidAndAds(adNetwork);
        System.out.println("Day " + adNetwork.getDay() + " ended. Starting next day");
        adNetwork.setDay(adNetwork.getDay() + 1);
    }
}
