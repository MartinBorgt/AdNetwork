/*
 * To change this template, choose Tools | Templates
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Iterator;

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
		adNetwork.setBidBundle(null);

		try {

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

			// double rbid = 10000.0;

			/*
			 * add bid entries w.r.t. each active campaign with remaining
			 * contracted impressions.
			 *
			 * for now, a single entry per active campaign is added for queries
			 * of matching target segment.
			 */

			/*
			 * The first five days, we try to get all impressions to bring our
			 * quality rating up and that of the opponents down.
			 */
			// if (dayBiddingFor <= 5) {
			// for (AdxQuery query :
			// adNetwork.getCurrCampaign().campaignQueries) {

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

			// adNetwork.getBidBundle().addQuery(query, rbid, new Ad(null),
			// adNetwork.getCurrCampaign().id, 1);
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
			System.out.println("Impressions debug");
			if ((dayBiddingFor >= adNetwork.getCurrCampaign().dayStart)
					&& (dayBiddingFor <= adNetwork.getCurrCampaign().dayEnd)
					&& (adNetwork.getCurrCampaign().impsTogo() > 0)) {
				System.out.println("Impressions for : " + adNetwork.getCurrCampaign());

				adNetwork.setBidBundle(new AdxBidBundle());

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

				System.out.println("currentCampaign : " + currentCampaign);
				double totalImpCost = currentCampaign.getCost();
				System.out.println("totalImpCost : " + totalImpCost);
				double impressionLimit = adNetwork.getCurrCampaign().impsTogo();
				System.out.println("impressionLimit : " + impressionLimit);
				double budgetLimit = adNetwork.getCurrCampaign().budget - totalImpCost;
				System.out.println("budgetLimit : " + budgetLimit);
				double n = impressionLimit / currentCampaign.getReachImps();
				System.out.println("n : " + n);
				double budgetAvailable = budgetLimit * 0.9;
				System.out.println("budgetAvailable : " + budgetAvailable);

				/*
				 * Calculate how much the value is of bids on this day using
				 * pop(s, t) by iterating all winning campaign
				 */
				double popSt = 0;
				PredictImpressionCost predictImpressionCost = new PredictImpressionCost(adNetwork);
				for (Iterator<MarketSegment> it = currentCampaign.getTargetSegment().iterator(); it.hasNext();) {
					MarketSegment marketSegment = it.next();
					popSt += predictImpressionCost.predictOneDayPriceIndex(marketSegment, dayBiddingFor);
				}
				System.out.println("popularity before: " + popSt);

				// Hari: check the lines below against eachother
				// budgetAvailable = popSt * 0.9;
				if (budgetAvailable < popSt * 0.9)
					budgetAvailable = popSt * 0.9;

				popSt = popSt / UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment());
				System.out.println("popularity after: " + popSt + " divided by: "
						+ UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment()));

				double valueOfBid = popSt;
				boolean reachSmall = false;
				if (UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment()) > impressionLimit) {
					reachSmall = true;
					System.out.println("The reach to go is small");
				}

				if (valueOfBid > budgetAvailable && reachSmall) {
					budgetAvailable = UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment())
							* impressionLimit;
					System.out.println("We will try to finish our bidding this round by raising the limit");
				}

				double ni = 1 - (1 / ((currentCampaign.getDayEnd() - currentCampaign.getDayStart()) - 1));
				if (dayBiddingFor == currentCampaign.getDayEnd() && n < ni) {
					budgetAvailable = 2 * budgetAvailable;
					System.out.println("last day and a lot to go so double bids");
				}

				for (AdxQuery query : adNetwork.getCurrCampaign().campaignQueries) {
					if (adNetwork.getCurrCampaign().impsTogo() - entCount > 0) {
						/*
						 * among matching entries with the same campaign id, the
						 * AdX randomly chooses an entry according to the
						 * designated weight. by setting a constant weight 1, we
						 * create a uniform probability over active
						 * campaigns(irrelevant because we are bidding only on
						 * one campaign)
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
								entCount += adNetwork.getCurrCampaign().videoCoef
										+ adNetwork.getCurrCampaign().mobileCoef;
							}

						}
						adNetwork.getBidBundle().addQuery(query, (budgetAvailable / impressionLimit) * 10000000,
								new Ad(null), adNetwork.getCurrCampaign().id, 1);
					}
				}

				adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id, (int) impressionLimit,
						budgetAvailable);
				// adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
				// (int) impressionLimit, budgetLimit);

				System.out.println("impression limit: " + impressionLimit + " budget available: " + budgetAvailable
						+ " impression bid: " + ((budgetAvailable / impressionLimit) * 10000000));

				System.out.println("Day " + adNetwork.getDay() + ": Updated " + entCount
						+ " Bid Bundle entries for Campaign id " + adNetwork.getCurrCampaign().id);
			}

			if (adNetwork.getBidBundle() != null) {
				System.out.println("Day " + adNetwork.getDay()
						+ ": Sending BidBundle [main impression bid] for campaign: " + adNetwork.getCurrCampaign().id);
				adNetwork.sendResponse(adNetwork.getAdxAgentAddress(), adNetwork.getBidBundle());
			}

			/*
			 * Change impression bidding mechanism, continue bidding for all
			 * winning campaigns that have not reach target impression
			 */

			for (Iterator<CampaignLogReport> it = adNetwork.getWinCampaigns().iterator(); it.hasNext();) {
				CampaignLogReport winningCampaign = it.next();
				boolean send = false;
				if (winningCampaign.getCampaignId() != adNetwork.getCurrCampaign().id) {
					System.out.println("Impression to go: "
							+ ((int) Math.max(0, winningCampaign.getReachImps() - winningCampaign.getTargetedImps())));
					if ((dayBiddingFor >= winningCampaign.getDayStart())
							&& (dayBiddingFor <= winningCampaign.getDayEnd()) && ((int) Math.max(0,
									winningCampaign.getReachImps() - winningCampaign.getTargetedImps()) > 0)) {
						System.out.println("Impressions for : " + winningCampaign);

						adNetwork.setBidBundle(new AdxBidBundle());
						
						int entCount = 0;

						/*
						 * Impression Strategy
						 */

						CampaignLogReport currentCampaign = null;
						for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
							if (adNetwork.getLogReports().get(i).getCampaignId() == winningCampaign.getCampaignId()) {
								currentCampaign = adNetwork.getLogReports().get(i);
								break;
							}
						}

						System.out.println("currentCampaign : " + currentCampaign);
						double totalImpCost = currentCampaign.getCost();
						System.out.println("totalImpCost : " + totalImpCost);
						double impressionLimit = (int) Math.max(0,
								winningCampaign.getReachImps() - winningCampaign.getTargetedImps());
						System.out.println("impressionLimit : " + impressionLimit);
						double budgetLimit = winningCampaign.getBudgetMilis() - totalImpCost;
						System.out.println("budgetLimit : " + budgetLimit);
						double n = impressionLimit / currentCampaign.getReachImps();
						System.out.println("n : " + n);
						double budgetAvailable = budgetLimit * 0.9;
						System.out.println("budgetAvailable : " + budgetAvailable);

						/*
						 * Calculate how much the value is of bids on this day
						 * using pop(s, t) by iterating all winning campaign
						 */
						double popSt = 0;
						PredictImpressionCost predictImpressionCost = new PredictImpressionCost(adNetwork);
						
						for (Iterator<MarketSegment> it2 = currentCampaign.getTargetSegment().iterator(); it2
								.hasNext();) {
							MarketSegment marketSegment = it2.next();
							popSt += predictImpressionCost.predictOneDayPriceIndex(marketSegment, dayBiddingFor);
						}
						
						System.out.println("popularity before: " + popSt);

						// Hari: check the lines below against eachother
						// budgetAvailable = popSt * 0.9;
						if (budgetAvailable < popSt * 0.9)
							budgetAvailable = popSt * 0.9;

						popSt = popSt / UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment());
						System.out.println("popularity after: " + popSt + " divided by: "
								+ UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment()));

						double valueOfBid = popSt;
						boolean reachSmall = false;
						if (UserPopulationProbabilities
								.getProbability(currentCampaign.getTargetSegment()) > impressionLimit) {
							reachSmall = true;
							System.out.println("The reach to go is small");
						}

						if (valueOfBid > budgetAvailable && reachSmall) {
							budgetAvailable = UserPopulationProbabilities
									.getProbability(currentCampaign.getTargetSegment()) * impressionLimit;
							System.out.println("We will try to finish our bidding this round by raising the limit");
						}

						double ni = 1 - (1 / ((currentCampaign.getDayEnd() - currentCampaign.getDayStart()) - 1));
						if (dayBiddingFor == currentCampaign.getDayEnd() && n < ni) {
							budgetAvailable = 2 * budgetAvailable;
							System.out.println("last day and a lot to go so double bids " + budgetAvailable);
						}

						for (AdxQuery query : currentCampaign.getCampaignQueries()) {
							if (impressionLimit - entCount > 0) {
								/*
								 * among matching entries with the same campaign
								 * id, the AdX randomly chooses an entry
								 * according to the designated weight. by
								 * setting a constant weight 1, we create a
								 * uniform probability over active
								 * campaigns(irrelevant because we are bidding
								 * only on one campaign)
								 */
								if (query.getDevice() == Device.pc) {
									if (query.getAdType() == AdType.text) {
										entCount++;
									} else {
										entCount += currentCampaign.getVideoCoef();
									}
								} else {
									if (query.getAdType() == AdType.text) {
										entCount += currentCampaign.getMobileCoef();
									} else {
										entCount += currentCampaign.getVideoCoef() + currentCampaign.getMobileCoef();
									}

								}
								adNetwork.getBidBundle().addQuery(query, (budgetAvailable / impressionLimit) * 10000000,
										new Ad(null), currentCampaign.getCampaignId(), 1);
							}
						}

						adNetwork.getBidBundle().setCampaignDailyLimit(currentCampaign.getCampaignId(),
								(int) impressionLimit, budgetAvailable);

						System.out.println(
								"impression limit: " + impressionLimit + " budget available: " + budgetAvailable
										+ " impression bid: " + ((budgetAvailable / impressionLimit) * 10000000));

						System.out.println("Day " + adNetwork.getDay() + ": Updated " + entCount
								+ " Bid Bundle entries for Campaign id " + currentCampaign.getCampaignId());
						send = true;
					}

					if (send) {
						System.out.println("Day " + adNetwork.getDay()
								+ ": Sending BidBundle [other impression bid] for campaign: "
								+ winningCampaign.getCampaignId());
						adNetwork.sendResponse(adNetwork.getAdxAgentAddress(), adNetwork.getBidBundle());
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
