package newagent;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Range;

public class PredictUCSCost {
	SampleAdNetworkModified adNetwork;
	
	public PredictUCSCost(SampleAdNetworkModified adNet){
		this.adNetwork = adNet;
	}
	
	/*
	 * parameter previous winning UCS bid and UCS level
	 */
	public double predictUCSCost() {
		double cost = 0;

		/*
		 * impression price rises when demand is more impression price falls
		 * when supply is more r = minimum impression agent will reach tomorrow
		 * b = previous winniing bid l= previous won UCS level
		 * 
		 * r = 3/4·DailyReach();
		 * 
		 * if l > 0.9 then return b/(1 + GUCS ); else if l < 0.81 and r0/b >=
		 * 20/3 · (1+GUCS)/E[p] then if(if segment population is lower and many
		 * bidders for same segment on that particular day) then // aim for 2 or
		 * 3 UCS level times = 0.9 - l GUCS = 5 % b return (times + GUCS )b;
		 * else return b;
		 */
		
		// stores all the campaigns
		Set<CampaignLogReport> camp = new HashSet<CampaignLogReport>();
		camp.addAll(adNetwork.getLostCampaigns());
		camp.addAll(adNetwork.getWinCampaigns());
		
		// campaings which runs on next bid day
		Set<CampaignLogReport> matchedCamp = new HashSet<CampaignLogReport>();
		
		//
		int UCSBidDay = adNetwork.getDay() + 1;
		
		for(CampaignLogReport cr: camp){
			Range<Integer> campPeriod = Range.between(cr.getDayStart(), cr.getDayEnd() + 1);
			if(campPeriod.contains(UCSBidDay)){
				matchedCamp.add(cr);
			}
		}
		
		return cost;
	}
}
