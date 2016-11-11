/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import se.sics.tasim.props.SimulationStatus;

/**
 *
 * @author hsn
 */
public class HandleSimulationStatus {

    public HandleSimulationStatus() {
    }
    public void run(SampleAdNetworkModified adNetwork, SimulationStatus simulationStatus) {
        System.out.println("Day " + adNetwork.getDay() + " : Simulation Status Received");
        adNetwork.sendBidAndAds(adNetwork);
        System.out.println("Day " + adNetwork.getDay() + " ended. Starting next day");
        adNetwork.setDay(adNetwork.getDay()+1);
    }
}
