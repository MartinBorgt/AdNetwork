package newagent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.Range;

public class PredictUCSCost {
	SampleAdNetworkModified adNetwork;
	
	public PredictUCSCost(SampleAdNetworkModified adNet){
		this.adNetwork = adNet;
	}
	
	/*
	 * parameter previous winning UCS bid and UCS level
	 */
	public double predictUCSCost(double ucsLevel, double ucsBid) {
		
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
		
		UserPopulationProbabilities usr = new UserPopulationProbabilities();

		int UCSBidDay = adNetwork.getDay() + 1; 	// ucs bidding day
		double dailyReach = 0.00;	// total reach on bidding day
		double supply = 0.00;
		
		
		for(CampaignLogReport cr: logReport){
			Range<Integer> campRange = Range.between(cr.getDayStart(), cr.getDayEnd() + 1);
			int period = (cr.getDayEnd() + 1) - cr.getDayStart();
			
			if(campRange.contains(UCSBidDay)){
				double reach = (cr.getReachImps() / period);
				dailyReach += reach;
				supply += usr.getProbability(cr.getTargetSegment());
			}
		}
		System.out.println("Daily Reach UCS " + dailyReach + "Supply: " + supply);
		Random rand = new Random();
		
		double previousLevel = ucsLevel;
		double previousUcsBid = 0.1 + rand.nextDouble() / 10.0;
		//double previousUcsBid = ucsBid;
		double GUCS = 0.2;
		
		if(ucsLevel < 8){
			double diff = 1 - ucsLevel;
			GUCS = this.generateRand(diff, 0.3);
		} 
		
		System.out.println("UCS test 2 previous level " + previousLevel + " previous bid " + previousUcsBid);
		
		double r = dailyReach; //total reach for the day
		double p = dailyReach/supply; // demand/supply
		double ro = (3 * dailyReach)/4;
		double pAvg = 0.9 * p;
		double c = 0.00; // constant need to verify it value
		
		// ( r( (p - p')r - 2b(g + 1) ) )/2 + C
		double utilityIncrement = (1/ro * ( ( (r * ( ((p - pAvg) * r) - (2 *  previousUcsBid * (GUCS + 1)) ) ) / 2 ) + c ) );
		
		double secondCondition = ( (20/3) * ((1 + GUCS) / utilityIncrement));
		double roDivideBid = ro/previousUcsBid;
		
		System.out.println(" p: " + p + "utility increment: "+ utilityIncrement + " second Condition " + secondCondition + "GUCS: " + GUCS + " roDivideBid" + roDivideBid);
		
		if(previousLevel > 0.9){
			System.out.println("ucs level > 0.9");
			return previousUcsBid / (1 + GUCS);
		} else if(previousLevel < 0.8  && roDivideBid >= secondCondition){
			System.out.println("ucs level < 0.8");
			return (1 + GUCS) * previousUcsBid;
		} else {
			return previousUcsBid;
		}
	}
	
	public double generateRand(double max, double min){
		double start = min;
		double end = max;
		double random = new Random().nextDouble();
		double result = start + (random * (end - start));
		
		return result;
	}
}