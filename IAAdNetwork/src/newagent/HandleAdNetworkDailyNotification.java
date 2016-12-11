/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Iterator;
import java.util.StringTokenizer;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;

/**
 *
 * @author hsn
 */
public class HandleAdNetworkDailyNotification {

	public HandleAdNetworkDailyNotification() {
	}

	public void run(SampleAdNetworkModified adNetwork, AdNetworkDailyNotification notificationMessage,
			Classifier classify) {

		adNetwork.setAdNetworkDailyNotification(notificationMessage);

		System.out.println("Day " + adNetwork.getDay() + ": Daily notification for campaign "
				+ adNetwork.getAdNetworkDailyNotification().getCampaignId());

		String campaignAllocatedTo = " allocated to " + notificationMessage.getWinner();

		if ((adNetwork.getPendingCampaign().id == adNetwork.getAdNetworkDailyNotification().getCampaignId())
				&& (notificationMessage.getCostMillis() != 0)) {

			/*
			 * add campaign to list of won campaigns
			 */
			adNetwork.getPendingCampaign().setBudget(notificationMessage.getCostMillis() / 1000.0);
			adNetwork.setCurrCampaign(adNetwork.getPendingCampaign());
			adNetwork.genCampaignQueries(adNetwork.getCurrCampaign());
			adNetwork.getMyCampaigns().put(adNetwork.getPendingCampaign().id, adNetwork.getPendingCampaign());

			campaignAllocatedTo = " WON at cost (Millis)" + notificationMessage.getCostMillis();

			/*
			 * Update ICvalue
			 */
			adNetwork.ICvalue = 1.5;
		} else {
			/*
			 * Update ICvalue
			 */
			adNetwork.ICvalue *= 0.9;
		}
		System.out.println("Day " + adNetwork.getDay() + ": " + campaignAllocatedTo + ". UCS Level set to "
				+ notificationMessage.getServiceLevel() + " at price " + notificationMessage.getPrice()
				+ " Quality Score is: " + notificationMessage.getQualityScore());
		// storing training dataset
		for (Iterator<CampaignLogReport> it = adNetwork.getWinCampaigns().iterator(); it.hasNext();) {
			CampaignLogReport winningCampaign = it.next();

			double currDay = adNetwork.getDay();

			if (winningCampaign.getDayEnd() == currDay) {
				System.out.println(" Start recording imp classifier data");
				double reachedImp = winningCampaign.getTargetedImps();

				double startBankStatus = 0.00;
				double endDayBankStatus = 0.00;
				double totalPayment = 0.00;

				// getting log report
				for (Iterator<CampaignLogReport> it2 = adNetwork.getLogReports().iterator(); it2.hasNext();) {
					CampaignLogReport logCampaign = it2.next();

					if (winningCampaign.getDayStart() == logCampaign.getDay()) {
						startBankStatus = logCampaign.getBankStatus();
					}

					if (winningCampaign.getDayEnd() == logCampaign.getDay()) {
						endDayBankStatus = logCampaign.getBankStatus();
					}
				}

				// checking for negative values
				if (endDayBankStatus < 0 && startBankStatus < 0) {
					totalPayment = -endDayBankStatus + startBankStatus;
				} else if (endDayBankStatus < 0) {
					totalPayment = -endDayBankStatus - startBankStatus;
				} else if (startBankStatus < 0) {
					totalPayment = endDayBankStatus + startBankStatus;
				} else {
					totalPayment = endDayBankStatus - startBankStatus;
				}

				if (totalPayment < 0) {
					totalPayment = -totalPayment;
				}
				System.out.println("end day Bank-status " + endDayBankStatus + " start day Bank-status "
						+ startBankStatus + " totalPay " + totalPayment);
				System.out.println("rached imp " + reachedImp);
				System.out.println("total Payment " + totalPayment);

				classify.addImpTrainingdata(winningCampaign, totalPayment, reachedImp);
				System.out.println(" Stop recording imp classifier data");
			}
		}

		System.out.println(" Start recording ucs classifier data");

		classify.addUcsTrainingData(notificationMessage.getServiceLevel(), notificationMessage.getPrice(),
				adNetwork.getDay() - 1);
		System.out.println(" Stop recording ucs classifier data");

		/*
		 * Record Log
		 */
		for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
			if (adNetwork.getLogReports().get(i).getCampaignId() == adNetwork.getAdNetworkDailyNotification()
					.getCampaignId()) {
				adNetwork.getLogReports().get(i).setWinner(campaignAllocatedTo);
				if (campaignAllocatedTo.contains(" WON at cost (Millis)")) {
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
