package newagent;

import java.util.ArrayList;
import java.util.Arrays;
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
	public double predictMultidayPriceIndex(SampleAdNetworkModified adNetwork){
		
		CampaignData pendCamp = adNetwork.getPendingCampaign();
		
		Set<MarketSegment> segment = pendCamp.targetSegment;
		
		int campPeriod = (int) ((pendCamp.dayEnd + 1) - pendCamp.dayStart);

		double popularity = 0.00;
		double storeValue = 0.00;

		SendTheBidsAndAds sendBid = new SendTheBidsAndAds();
		
		PredictImpressionCost predictImp = new PredictImpressionCost(adNetwork);

		for (MarketSegment s : segment) {
			Set<MarketSegment> singleSeg = new HashSet<MarketSegment>(Arrays.asList(s));

			for (int i = (int) pendCamp.dayStart; i <= pendCamp.dayEnd; i++) {
				UserPopulationProbabilities usr = new UserPopulationProbabilities();
				double population = usr.getProbability(singleSeg);
				double segmentPopularity = predictImp.predictOneDayPriceIndex(s, i);
				storeValue += (population * segmentPopularity);
			}
		}
		UserPopulationProbabilities usr = new UserPopulationProbabilities();
		popularity = (storeValue / (campPeriod * usr.getProbability(segment)));
		
		//popularity = (storeValue / ((double)usr.getProbability(segment)));
		storeValue = storeValue / usr.getProbability(segment);
		System.out.println("popularity after normalization: " + storeValue);
		System.out.println("or: " + storeValue / campPeriod);
		System.out.println("Days running: " + campPeriod);
		System.out.println("group size: " + usr.getProbability(segment));
		
		//System.out.println("popularity bid: " + popularity);
		//System.out.println("popularity bid divided by campaign length: " + (storeValue / ((double)(campPeriod * usr.getProbability(segment)))));
		//popularity = ((double) storeValue) / ((double)(campPeriod * usr.getProbability(segment)));
		popularity = adNetwork.ICvalue * (20/(usr.getProbability(segment)*Math.pow(2,segment.size())));
		if(popularity > 0.001) {
			popularity = 0.001;
			adNetwork.ICvalue *=0.6; 
		}
		if(popularity < 0.0001){
			popularity = 0.001;
			adNetwork.ICvalue *=1.5; 
		}
		System.out.println("ICvalue = " + adNetwork.ICvalue);
		//Since bids are capped to reach we normalize it before sending it out
		//popularity = (double)(pendCamp.reachImps)* popularity;
		
		//We simply bid our ICvalue multiplied by the bidding value
		popularity = (double)(pendCamp.reachImps)* popularity;
		System.out.println("popularity bid: " + popularity);
		return popularity;
	}
}
