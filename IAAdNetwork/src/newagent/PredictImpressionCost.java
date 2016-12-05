package newagent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Range;

import tau.tac.adx.report.adn.MarketSegment;

public class PredictImpressionCost {
	
	SampleAdNetworkModified adNetwork;
	
	public PredictImpressionCost(SampleAdNetworkModified adNet){
		this.adNetwork = adNet;
	}

	/*
	 * cost prediction of impression segment also indicate to difficult to
	 * complete, in this case bid higher for campaign contract higher bidding
	 * for segment can result increase in baseline crucial that biding of
	 * popular segment is made higher than others
	 * 
	 * * One-day predicting request - estimating PI for next day (used for bid
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
	 */
	public double predictOneDayPriceIndex(MarketSegment segment, int currentDay) {
		
		List<CampaignLogReport> totalCampaigns = new ArrayList<CampaignLogReport>();
		List<CampaignLogReport> matchedCampaigns = new ArrayList<CampaignLogReport>();
		
		totalCampaigns.addAll(adNetwork.getLostCampaigns());
		totalCampaigns.addAll(adNetwork.getWinCampaigns());
		
		for (CampaignLogReport camp : totalCampaigns) {
			Range<Integer> campPeriod = Range.between(camp.getDayStart(), camp.getDayEnd() + 1);

			if (camp.getTargetSegment().contains(segment) && campPeriod.contains(currentDay)) {
				matchedCampaigns.add(camp);
			}
		}

		// System.out.println("testing 1");

		CampaignData pendCamp = adNetwork.getPendingCampaign();

		UserPopulationProbabilities usr = new UserPopulationProbabilities();
		double popularity = ((double) pendCamp.impsTogo())/((double)(usr.getProbability(pendCamp.targetSegment)*(pendCamp.dayEnd-pendCamp.dayStart + 1)));
		
		for (CampaignLogReport r : matchedCampaigns) {
			double reach = r.getReachImps();
			usr = new UserPopulationProbabilities();
			int segmentPopulation = usr.getProbability(r.getTargetSegment());
			int campaignPeriod = ((r.getDayEnd() + 1) - r.getDayStart());

			double value = (((double)reach) / ((double)(segmentPopulation * campaignPeriod)));
			popularity += value;
		}

		return popularity;
	}
}
