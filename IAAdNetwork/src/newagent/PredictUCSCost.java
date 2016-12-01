package newagent;

import java.util.List;
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
		List<CampaignLogReport> logReport = adNetwork.getLogReports();

		CampaignData currCamp = adNetwork.getCurrCampaign();
		CampaignLogReport currLog = null;

		int UCSBidDay = adNetwork.getDay() + 1; 	// ucs bidding day
		int dailyReach = 0;	// total reach on bidding day
		
		for(CampaignLogReport cr: logReport){
			Range<Integer> campRange = Range.between(cr.getDayStart(), cr.getDayEnd() + 1);
			int period = (cr.getDayEnd() + 1) - cr.getDayStart();
			
			if(campRange.contains(UCSBidDay)){
				int reach = (int) (cr.getTargetedImps() / period);
				dailyReach += reach;
			}
			
			if(currCamp.id == cr.getCampaignId()){
				currLog = cr;
			}
		}
		
		double previousLevel = currLog.getUcsLevel();
		double previousUcsBid = currLog.getPrice();
		double GUCS = 0.2; // can
		double supply = 1000;
		
		double r = dailyReach; //total reach for the day
		double p = dailyReach/supply; // demand/supply
		double ro = 3/4 * dailyReach;
		double pAvg = 0.9 * p;
		double c = 1.00; // constant need to verify it value
		
		// ( r( (p - p')r - 2b(g + 1) ) )/2 + C
		double utilityIncrement = (1/ro * ( ( (r * ( ((p - pAvg) * r) - (2 *  previousUcsBid * (GUCS + 1)) ) ) / 2 ) + c ) );
		
		double secondCondition = ( (20/3) * ((1 + GUCS) / utilityIncrement));
		double roDivideBid = ro/previousUcsBid;
		
		if(previousLevel > 0.9){
			return previousUcsBid / GUCS;
		} else if(previousLevel < 0.8  && roDivideBid >= secondCondition){
			return (1 + GUCS) * previousUcsBid;
		} else {
			return previousUcsBid;
		}
	}
}
