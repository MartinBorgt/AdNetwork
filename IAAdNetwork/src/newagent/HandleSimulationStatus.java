/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Iterator;
import se.sics.tasim.props.SimulationStatus;

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
        System.out.println("##### LOG TRACING #####");
        for (Iterator<CampaignLogReport> it = adNetwork.getLogReports().iterator(); it.hasNext();) {
            CampaignLogReport clr = it.next();
            System.out.println("Campaign ID: " + clr.getCampaignId() + " from day " + clr.getDayStart() + " to day " + clr.getDayEnd() + " Issued: day " + clr.getDay());
            System.out.println("ReachImps: " + clr.getReachImps() + " Target Segment: " + clr.getTargetSegment() + " Vid Coef: " + clr.getVideoCoef() + " Mobile Coef: " + clr.getMobileCoef());
            System.out.println("Stats: " + clr.getStats() + " Budget: " + clr.getBudget() + " UCS: " + clr.getUcsLevel());
            System.out.println("Budget Milis: " + clr.getBudgetMilis() + " Target Imps: " + clr.getTargetedImps() + " Other Imps: " + clr.getOtherImps());
            System.out.println("Cost: " + clr.getCost() + " Winner: " + clr.getWinner() + " Service Level: " + clr.getServiceLevel() + " Price: " + clr.getPrice());
            if (clr.getWinner() != null && clr.getWinner().contains("WON")) {
                System.out.println("First Price: " + clr.getBudgetMilis() + " Second Price: " + clr.getSecondPrice());
            }
            System.out.println("Bank Status: " + clr.getBankStatus());
            System.out.println("----------------------------------------------------------");
        }

        System.out.println("Day " + adNetwork.getDay() + " : Simulation Status Received");
        adNetwork.sendBidAndAds(adNetwork);
        System.out.println("Day " + adNetwork.getDay() + " ended. Starting next day");
        adNetwork.setDay(adNetwork.getDay() + 1);
    }
}
