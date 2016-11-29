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
        if (dayBiddingFor <= 5){
            for (AdxQuery query : adNetwork.getCurrCampaign().campaignQueries) {
            	adNetwork.getBidBundle().addQuery(query, rbid, new Ad(null),
                    adNetwork.getCurrCampaign().id, 1);
            	//TODO: find best impression limit, probably as large as possible
            	double impressionLimit = 1000000;
            	double test = this.predictImpressionCost(adNetwork);
            	System.out.println("checking 1 " + test);
            	double test2 = this.predictMultidayPriceIndex(adNetwork);
            	System.out.println("checking 2 " + test2);
            	//TODO: find best budget limit
            	double budgetLimit = 1000000;
            	adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
						(int) impressionLimit, budgetLimit);
            }
        }
        else if ((dayBiddingFor >= adNetwork.getCurrCampaign().dayStart)
                && (dayBiddingFor <= adNetwork.getCurrCampaign().dayEnd)
                && (adNetwork.getCurrCampaign().impsTogo() > 0)) {

            int entCount = 0;

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
                    adNetwork.getBidBundle().addQuery(query, rbid, new Ad(null),
                            adNetwork.getCurrCampaign().id, 1);
                }
            }

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
            for (int i = 0; i < adNetwork.getLogReports().size(); i++) {
                if (adNetwork.getLogReports().get(i).getDayStart() >= dayBiddingFor && adNetwork.getLogReports().get(i).getDayStart() <= dayBiddingFor) {
                    for (Iterator<MarketSegment> it = currentCampaign.getTargetSegment().iterator(); it.hasNext();) {
                        MarketSegment marketSegment = it.next();
                        for (Iterator<MarketSegment> it1 = adNetwork.getLogReports().get(i).getTargetSegment().iterator(); it1.hasNext();) {
                            MarketSegment marketSegment1 = it1.next();
                            if (marketSegment == marketSegment1) {
                                List<String> markets = new ArrayList<String>();
                                markets.add(marketSegment.name());
                                popSt += adNetwork.getLogReports().get(i).getReachImps() / (UserPopulationProbabilities.getProbabilityString(markets) * ((adNetwork.getLogReports().get(i).getDayEnd() - adNetwork.getLogReports().get(i).getDayStart()) + 1));
                            }
                        }
                    }
                }
            }

            double valueOfBid = popSt;
            boolean reachSmall = false;
            if (UserPopulationProbabilities.getProbability(currentCampaign.getTargetSegment()) > impressionLimit) {
                reachSmall = true;
            }

            // what is budgetAvailable = Maximumamount * ReachLeft
            if (valueOfBid > budgetAvailable && reachSmall) {
                budgetAvailable = currentCampaign.getBudget() * impressionLimit;
            }

            double ni = 1 - (1 / ((currentCampaign.getDayEnd() - currentCampaign.getDayStart()) - 1));
            if (dayBiddingFor == currentCampaign.getDayEnd() && n < ni) {
                budgetAvailable = 2 * budgetAvailable;
            }

            adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
                    (int) (budgetAvailable / impressionLimit), budgetAvailable);
//            adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
//                    (int) impressionLimit, budgetLimit);

            System.out.println("Day " + adNetwork.getDay() + ": Updated " + entCount
                    + " Bid Bundle entries for Campaign id " + adNetwork.getCurrCampaign().id);
        }

        if (adNetwork.getBidBundle() != null) {
            System.out.println("Day " + adNetwork.getDay() + ": Sending BidBundle");
            adNetwork.sendResponse(adNetwork.getAdxAgentAddress(), adNetwork.getBidBundle());
        }
    }
    
    public double predictCampaignCost(SampleAdNetworkModified adNetwork){
    	/*
    	 * bid lower when winning contract is important
    	 * big higher when contract is too difficult to complete(based on Price Index value)
    	 * minimun campaign budget = impression cost + UCS level cost
    	 * 
    	 * if we have higher Quality rating, it will make profit
    	 */
    	SendTheBidsAndAds sendBid = new SendTheBidsAndAds();
    	
    	double bidValue = sendBid.predictMultidayPriceIndex(adNetwork);
    	
    	return bidValue;
    }
    
    public double predictImpressionCost(SampleAdNetworkModified adNetwork){
    	/*
    	 * publisher initial reserve price 0.005
    	 * always impression cost should be higher than publisher RP
    	 * check in % of completion of contract
    	 * 
    	 * get campaign data (reachImp, start date, end date, target segment, videoceof, mobileceof)
    	 * 
    	 * check the length of campaing
    	 * 
    	 * if Too little budget with small reach then
				budget = RContractMax (0.001) · budget;
				
 			if Only one day left and amount of completion < amount to be completed (maximise quality rating) then
 				Double the budget;
 			return budget/reach;
    	 */
    	SendTheBidsAndAds sendBid = new SendTheBidsAndAds();
    	
    	//double bidValue = sendBid.predictOneDayPriceIndex(adNetwork, adNetwork.getCurrCampaign().targetSegment, adNetwork.getDay());
    	double bidValue = 0.00;
    	return bidValue;
    }
    
    /*
     * parameter previous winning UCS bid and UCS level
     */
    public double predictUCSCost(SampleAdNetworkModified adNetwork){
    	double cost = 0;
    	
    	/*
    	impression price rises when demand is more
    	impression price falls when supply is more
    	r = minimum impression agent will reach tomorrow
    	b = previous winniing bid
    	l= previous won UCS level
    	
    	r = 3/4·DailyReach();
    	
		if l > 0.9 then
			return b/(1 + GUCS );
		else if l < 0.81 and r0/b >= 20/3 · (1+GUCS)/E[p] then
			if(if segment population is lower and many bidders for same segment on that particular day) then
				// aim for 2 or 3 UCS level
				times = 0.9 - l
				GUCS = 5 % b
				return (times + GUCS )b;
		else
			return b;
    	 *
    	
    	// predicting impression to get tomorrow
    	int r = (3/4) * adNetwork.getCurrCampaign().impsTogo();
    	
    	
    	if(adNetwork.ucsTargetLevel == 0){
    		
    	} else {
        	if (adNetwork.ucsTargetLevel > 0.9){
        		
        	} else {
        		
        	}
    	}
    	*/
    	
    	return cost;
    }
    
    /*
     * cost prediction of impression segment
     * also indicate to difficult to complete, in this case bid higher for campaign contract
     * higher bidding for segment can result increase in baseline
     * crucial that biding of popular segment is made higher than others
     * 
     *     	 * One-day predicting request - estimating PI for next day (used for bid bundle)
    	 * Multi-day predicting request - estimating average PI for collection of segment for following days(used for determining contract bid)
    	 * 
    	 * 
    	 * check for user population probability
    	 * check for other bidder bidding on the same targed segment on the same day or series of day(take average)
    	 * 
    	 * if user population probability is lower 
    	 * then bid little high
    	 * if there are multiplebidders
    	 * then bid higher
    	 * 
    	 * return segment bid value
     */
    public double predictOneDayPriceIndex(SampleAdNetworkModified adNetwork, MarketSegment segment, int currentDay){
    	
    	List<CampaignLogReport> lostCampaigns = new ArrayList<CampaignLogReport>();
    	
    	for(CampaignLogReport camp: adNetwork.getLostCampaigns()){
    		Range<Integer> campPeriod = Range.between(camp.getDayStart(), camp.getDayEnd() + 1);
    		
    		if(camp.getTargetSegment().contains(segment) && campPeriod.contains(currentDay)){
    			lostCampaigns.add(camp);
    		}
    	}
    	
    	//System.out.println("testing 1");
    	
    	double popularity = 0.00;
    	UserPopulationProbabilities usr = new UserPopulationProbabilities();
    	
    	for(CampaignLogReport r: lostCampaigns){
			double reach = r.getReachImps();
			int segmentPopulation = usr.getProbability(r.getTargetSegment());
			int campaignPeriod = ((r.getDayEnd() + 1) - r.getDayStart());
			
    		double value = (reach / (segmentPopulation * campaignPeriod));
    		popularity += value;
    	}
    	
    	return popularity;
    }
    
	/*
	 * One-day predicting request - estimating PI for next day (used for bid bundle)
	 * Multi-day predicting request - estimating average PI for collection of segment for following days(used for determining contract bid)
	 * 
	 * 
	 * check for user population probability
	 * check for other bidder bidding on the same targed segment on the same day or series of day(take average)
	 * 
	 * if user population probability is lower 
	 * then bid little high
	 * if there are multiplebidders
	 * then bid higher
	 * 
	 * return segment bid value
	 * 
	 */
    public double predictMultidayPriceIndex(SampleAdNetworkModified adNetwork){
    	
    	CampaignData currCamp = adNetwork.getCurrCampaign();
    	Set<MarketSegment> segment = currCamp.targetSegment;
    	int campPeriod = (int) ((currCamp.dayEnd + 1) - currCamp.dayStart);
    	
    	double popularity = 0.00;
    	double storeValue = 0.00;
    	
    	UserPopulationProbabilities usr = new UserPopulationProbabilities();
    	SendTheBidsAndAds sendBid = new SendTheBidsAndAds();

    	for(MarketSegment s: segment){
    		Set<MarketSegment> singleSeg = new HashSet<MarketSegment>();
    		
    		for(int i = (int) currCamp.dayStart; i <= currCamp.dayEnd; i++){
    			double population = usr.getProbability(singleSeg);
    			double segmentPopularity = sendBid.predictOneDayPriceIndex(adNetwork, s, i);
    			storeValue += (population * segmentPopularity);
    		}
    	}

    	popularity = (storeValue / (campPeriod * usr.getProbability(segment)));
    	
    	return popularity;
    }
    
    /*
     * value that shows how despereate agent wants to win the contract
     * higher value indicate low bidding price for contract and vice-versa
     */
    public double predictCompetingIndex(SampleAdNetworkModified adNetwork){
    	double value = 0.00;
    	/*
    	 * losing contract results higher CI value
    	 * CI value gets lower when won auction is standard second class auction and not random
    	 */
    	
    	return value;
    }
}
