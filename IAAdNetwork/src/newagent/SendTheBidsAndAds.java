/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Range;

import edu.umich.eecs.tac.props.Ad;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.report.adn.MarketSegment;

/**
 *
 * @author hsn
 */
public class SendTheBidsAndAds {

	public SendTheBidsAndAds() {
	}

	public void Run(SampleAdNetworkModified adNetwork) {
		adNetwork.setBidBundle(new AdxBidBundle());

		/*
		 *
		 */

		int dayBiddingFor = adNetwork.getDay() + 1;

		/*
		 * A fixed random bid, for all queries of the campaign
		 */
		/*
		 * Note: bidding per 1000 imps (CPM) - no more than average budget
		 * revenue per imp
		 */

		double rbid = 10000.0;

		/*
		 * add bid entries w.r.t. each active campaign with remaining contracted
		 * impressions.
		 *
		 * for now, a single entry per active campaign is added for queries of
		 * matching target segment.
		 */

		/*
		 * The first five days, we try to get all impressions to bring our
		 * quality rating up and that of the opponents down.
		 */
		// if (dayBiddingFor <= 5) {
		// for (AdxQuery query : adNetwork.getCurrCampaign().campaignQueries) {

		// /*
		// * Impression Strategy
		// */
		//
		// CampaignLogReport currentCampaign = null;
		// for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
		// if (adNetwork.getLogReports().get(i).getCampaignId() ==
		// adNetwork.getCurrCampaign().id) {
		// currentCampaign = adNetwork.getLogReports().get(i);
		// break;
		// }
		// }
		//
		// double totalImpCost = currentCampaign.getCost();
		// double impressionLimit =
		// adNetwork.getCurrCampaign().impsTogo();
		// double budgetLimit = adNetwork.getCurrCampaign().budget -
		// totalImpCost;
		// double n = impressionLimit / currentCampaign.getReachImps();
		// double budgetAvailable = budgetLimit * 0.9;
		//
		// /*
		// * Calculate how much the value is of bids on this day using
		// pop(s,
		// * t) by iterating all winning campaign
		// */
		// double popSt = 0;
		// for (Iterator<MarketSegment> it =
		// currentCampaign.getTargetSegment().iterator(); it.hasNext();)
		// {
		// MarketSegment marketSegment = it.next();
		// popSt += predictOneDayPriceIndex(adNetwork, marketSegment,
		// dayBiddingFor);
		// }
		// popSt =
		// popSt/UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment());
		//
		// double valueOfBid = popSt;
		// boolean reachSmall = false;
		// if
		// (UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment())
		// > impressionLimit) {
		// reachSmall = true;
		// }
		//
		// if (valueOfBid > budgetAvailable && reachSmall) {
		// budgetAvailable =
		// UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment())
		// * impressionLimit;
		// }
		//
		// double ni = 1 - (1 / ((currentCampaign.getDayEnd() -
		// currentCampaign.getDayStart()) - 1));
		// if (dayBiddingFor == currentCampaign.getDayEnd() && n < ni) {
		// budgetAvailable = 2 * budgetAvailable;
		// }
		//
		// adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
		// (int) (budgetAvailable / impressionLimit), budgetAvailable);

//		adNetwork.getBidBundle().addQuery(query, rbid, new Ad(null), adNetwork.getCurrCampaign().id, 1);
		// TODO: find best impression limit, probably as large as
		// possible
		// double impressionLimit = 1000000;
		//
		// // TODO: find best budget limit
		// double budgetLimit = 1000000;
		// adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
		// (int) impressionLimit,
		// budgetLimit);
		// }
		// } else
		if ((dayBiddingFor >= adNetwork.getCurrCampaign().dayStart)
				&& (dayBiddingFor <= adNetwork.getCurrCampaign().dayEnd)
				&& (adNetwork.getCurrCampaign().impsTogo() > 0)) {

			int entCount = 0;

			/*
			 * Impression Strategy
			 */

			CampaignLogReport currentCampaign = null;
			for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
				if (adNetwork.getLogReports().get(i).getCampaignId() == adNetwork.getCurrCampaign().id) {
					currentCampaign = adNetwork.getLogReports().get(i);
					break;
				}
			}

			double totalImpCost = currentCampaign.getCost();
			double impressionLimit = adNetwork.getCurrCampaign().impsTogo();
			double budgetLimit = adNetwork.getCurrCampaign().budget - totalImpCost;
			double n = impressionLimit / currentCampaign.getReachImps();
			double budgetAvailable = budgetLimit * 0.9;

			/*
			 * Calculate how much the value is of bids on this day using pop(s,
			 * t) by iterating all winning campaign
			 */
			double popSt = 0;
			PredictImpressionCost predictImpressionCost = new PredictImpressionCost(adNetwork);
			for (Iterator<MarketSegment> it = currentCampaign.getTargetSegment().iterator(); it.hasNext();) {
				MarketSegment marketSegment = it.next();
				popSt += predictImpressionCost.predictOneDayPriceIndex(marketSegment, dayBiddingFor);
			}
			popSt = popSt / UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment());

			double valueOfBid = popSt;
			boolean reachSmall = false;
			if (UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment()) > impressionLimit) {
				reachSmall = true;
			}

			if (valueOfBid > budgetAvailable && reachSmall) {
				budgetAvailable = UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment())
						* impressionLimit;
			}

			double ni = 1 - (1 / ((currentCampaign.getDayEnd() - currentCampaign.getDayStart()) - 1));
			if (dayBiddingFor == currentCampaign.getDayEnd() && n < ni) {
				budgetAvailable = 2 * budgetAvailable;
			}

			for (AdxQuery query : adNetwork.getCurrCampaign().campaignQueries) {
				if (adNetwork.getCurrCampaign().impsTogo() - entCount > 0) {
					/*
					 * among matching entries with the same campaign id, the AdX
					 * randomly chooses an entry according to the designated
					 * weight. by setting a constant weight 1, we create a
					 * uniform probability over active campaigns(irrelevant
					 * because we are bidding only on one campaign)
					 */
					if (query.getDevice() == Device.pc) {
						if (query.getAdType() == AdType.text) {
							entCount++;
						} else {
							entCount += adNetwork.getCurrCampaign().videoCoef;
						}
					} else {
						if (query.getAdType() == AdType.text) {
							entCount += adNetwork.getCurrCampaign().mobileCoef;
						} else {
							entCount += adNetwork.getCurrCampaign().videoCoef + adNetwork.getCurrCampaign().mobileCoef;
						}

					}
					adNetwork.getBidBundle().addQuery(query, budgetAvailable / impressionLimit, new Ad(null),
							adNetwork.getCurrCampaign().id, 1);
				}
			}

			adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id, (int) impressionLimit,
					budgetAvailable);
			// adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
			// (int) impressionLimit, budgetLimit);

			System.out.println("Day " + adNetwork.getDay() + ": Updated " + entCount
					+ " Bid Bundle entries for Campaign id " + adNetwork.getCurrCampaign().id);
		}

		if (adNetwork.getBidBundle() != null) {
			System.out.println("Day " + adNetwork.getDay() + ": Sending BidBundle");
			adNetwork.sendResponse(adNetwork.getAdxAgentAddress(), adNetwork.getBidBundle());
		}
	}

	// need working
	public double predictCampaignCost(SampleAdNetworkModified adNetwork) {
		/*
		 * bid lower when winning contract is important big higher when contract
		 * is too difficult to complete(based on Price Index value) minimun
		 * campaign budget = impression cost + UCS level cost
		 * 
		 * if we have higher Quality rating, it will make profit
		 */
		double bidValue = 0;

		return bidValue;
	}

	// need working
	public double predictImpressionCost(SampleAdNetworkModified adNetwork) {
		/*
		 * publisher initial reserve price 0.005 always impression cost should
		 * be higher than publisher RP check in % of completion of contract
		 * 
		 * get campaign data (reachImp, start date, end date, target segment,
		 * videoceof, mobileceof)
		 * 
		 * check the length of campaing
		 * 
		 * if Too little budget with small reach then budget = RContractMax
		 * (0.001) · budget;
		 * 
		 * if Only one day left and amount of completion < amount to be
		 * completed (maximise quality rating) then Double the budget; return
		 * budget/reach;
		 */
		double bidValue = 0.00;
		return bidValue;
	}
}
