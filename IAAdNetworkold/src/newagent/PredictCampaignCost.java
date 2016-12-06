package newagent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Range;

import tau.tac.adx.report.adn.MarketSegment;

public class PredictCampaignCost {
	
	SampleAdNetworkModified adNetwork;
	
	public PredictCampaignCost(SampleAdNetworkModified adNet){
		this.adNetwork = adNet;
	}

	/*
	 * One-day predicting request - estimating PI for next day (used for bid
	 * bundle) Multi-day predicting request - estimating average PI for
	 * collection of segment for following days(used for determining contract
	 * bid)
	 * 
	 * 
	 * check for user population probability check for other bidder bidding on
	 * the same targed segment on the same day or series of day(take average)
	 * 
	 * if user population probability is lower then bid little high if there are
	 * multiplebidders then bid higher
	 * 
	 * return segment bid value
	 * 
	 */
	public double predictMultidayPriceIndex(SampleAdNetworkModified adNetwork) {
		
		CampaignData currCamp = adNetwork.getCurrCampaign();
		
		Set<MarketSegment> segment = currCamp.targetSegment;
		
		int campPeriod = (int) ((currCamp.dayEnd + 1) - currCamp.dayStart);

		double popularity = 0.00;
		double storeValue = 0.00;

		UserPopulationProbabilities usr = new UserPopulationProbabilities();
		SendTheBidsAndAds sendBid = new SendTheBidsAndAds();
		
		PredictImpressionCost predictImp = new PredictImpressionCost(adNetwork);

		for (MarketSegment s : segment) {
			Set<MarketSegment> singleSeg = new HashSet<MarketSegment>();

			for (int i = (int) currCamp.dayStart; i <= currCamp.dayEnd; i++) {
				double population = usr.getProbability(singleSeg);
				double segmentPopularity = predictImp.predictOneDayPriceIndex(s, i);
				storeValue += (population * segmentPopularity);
			}
		}

		popularity = (storeValue / (campPeriod * usr.getProbability(segment)));

		return popularity;
	}
	

	/*
	 * value that shows how despereate agent wants to win the contract higher
	 * value indicate low bidding price for contract and vice-versa
	 */
	public double predictCompetingIndex(SampleAdNetworkModified adNetwork) {
		double value = 0.00;
		/*
		 * losing contract results higher CI value CI value gets lower when won
		 * auction is standard second class auction and not random
		 */

		return value;
	}

}
