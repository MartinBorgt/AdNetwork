/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.Iterator;
import java.util.Random;

import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;

/**
 *
 * @author hsn
 */
public class HandleCampaignOpportunityMessage {

    public HandleCampaignOpportunityMessage() {
    }
    
    public void run(SampleAdNetworkModified adNetwork, CampaignOpportunityMessage com) {
        adNetwork.setDay(com.getDay());

        adNetwork.setPendingCampaign(new CampaignData(com));
        System.out.println("Day " + adNetwork.getDay() + ": Campaign opportunity - " + adNetwork.getPendingCampaign());

        /*
         * The campaign requires com.getReachImps() impressions. The competing
         * Ad Networks bid for the total campaign Budget (that is, the ad
         * network that offers the lowest budget gets the campaign allocated).
         * The advertiser is willing to pay the AdNetwork at most 1$ CPM,
         * therefore the total number of impressions may be treated as a reserve
         * (upper bound) price for the auction.
         */

        /*
         * Check today campaign that conflict with Winning campaign(s)
         * use isConflictWithWinningCampaign to check conflicting status
         */
        boolean isConflictWithWinningCampaign = false;
        for (Iterator<CampaignLogReport> it = adNetwork.getWinCampaigns().iterator(); it.hasNext();) {
            CampaignLogReport clr = it.next();
            for (Iterator<MarketSegment> it1 = clr.getTargetSegment().iterator(); it1.hasNext();) {
                MarketSegment marketSegment = it1.next();
                for (Iterator<MarketSegment> it2 = adNetwork.getPendingCampaign().targetSegment.iterator(); it2.hasNext();) {
                    MarketSegment marketSegment1 = it2.next();
                    if (marketSegment == marketSegment1) {
                        isConflictWithWinningCampaign = true;
                        break;
                    }
                }
                if (isConflictWithWinningCampaign) {
                    break;
                }
            }
            if (isConflictWithWinningCampaign) {
                break;
            }
        }

        /*
         * Check today campaign that conflict with Other campaign(s)
         * use isConflictWithOtherCampaign to check conflicting status
         */
        boolean isConflictWithOtherCampaign = false;
        for (Iterator<CampaignLogReport> it = adNetwork.getLostCampaigns().iterator(); it.hasNext();) {
            CampaignLogReport clr = it.next();
            for (Iterator<MarketSegment> it1 = clr.getTargetSegment().iterator(); it1.hasNext();) {
                MarketSegment marketSegment = it1.next();
                for (Iterator<MarketSegment> it2 = adNetwork.getPendingCampaign().targetSegment.iterator(); it2.hasNext();) {
                    MarketSegment marketSegment1 = it2.next();
                    if (marketSegment == marketSegment1) {
                        isConflictWithOtherCampaign = true;
                        break;
                    }
                }
                if (isConflictWithOtherCampaign) {
                    break;
                }
            }
            if (isConflictWithOtherCampaign) {
                break;
            }
        }

        /*
         * Check today campaign that conflict with our winning campaign's duration day
         * use isDaysConflictWithWinningCampaign to check conflicting status
         */
        boolean isDaysConflictWithWinningCampaign = false;
        for (Iterator<CampaignLogReport> it = adNetwork.getWinCampaigns().iterator(); it.hasNext();) {
            CampaignLogReport clr = it.next();
            if ((adNetwork.getPendingCampaign().dayEnd == clr.getDayStart() && adNetwork.getPendingCampaign().dayStart <= clr.getDayStart()) || (adNetwork.getPendingCampaign().dayStart <= clr.getDayStart() && adNetwork.getPendingCampaign().dayEnd >= clr.getDayStart()) || (adNetwork.getPendingCampaign().dayStart >= clr.getDayStart() && adNetwork.getPendingCampaign().dayEnd <= clr.getDayEnd()) || (adNetwork.getPendingCampaign().dayStart == clr.getDayEnd() && adNetwork.getPendingCampaign().dayEnd >= clr.getDayEnd())) {
                isDaysConflictWithWinningCampaign = true;
                break;
            }
        }
        
        /*
         * Check today campaign that conflict with other campaign's duration day
         * use isDaysConflictWithOtherCampaign to check conflicting status
         */
        boolean isDaysConflictWithOtherCampaign = false;
        for (Iterator<CampaignLogReport> it = adNetwork.getLostCampaigns().iterator(); it.hasNext();) {
            CampaignLogReport clr = it.next();
            if ((adNetwork.getPendingCampaign().dayEnd == clr.getDayStart() && adNetwork.getPendingCampaign().dayStart <= clr.getDayStart()) || (adNetwork.getPendingCampaign().dayStart <= clr.getDayStart() && adNetwork.getPendingCampaign().dayEnd >= clr.getDayStart()) || (adNetwork.getPendingCampaign().dayStart >= clr.getDayStart() && adNetwork.getPendingCampaign().dayEnd <= clr.getDayEnd()) || (adNetwork.getPendingCampaign().dayStart == clr.getDayEnd() && adNetwork.getPendingCampaign().dayEnd >= clr.getDayEnd())) {
                isDaysConflictWithOtherCampaign = true;
                break;
            }
        }

        /*
         * Conflict Debug
         */
        if(isConflictWithWinningCampaign){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@ WARNING This campaign conflict with our winning campaign!@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
        if(isConflictWithOtherCampaign){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@ WARNING This campaign conflict with other campaign!      @");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
        if(isDaysConflictWithWinningCampaign){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@ WARNING duration days conflict with our winning campaign!@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
        if(isDaysConflictWithOtherCampaign){
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@ WARNING duration days conflict with other campaign!      @");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
        
        Random random = new Random();
        long cmpimps = com.getReachImps();
        
        /*
         * During the first five days we do not want to take on a campaign
         * in order to not compete with every opponent for impressions
         */
        long cmpBidMillis = random.nextInt((int) cmpimps);
        if(adNetwork.getDay() <= 5){
        	cmpBidMillis = cmpimps * 2;
        }

        System.out.println("Day " + adNetwork.getDay() + ": Campaign total budget bid (millis): " + cmpBidMillis);

        /*
         * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
         * user classification service is piggybacked
         */

        if (adNetwork.getAdNetworkDailyNotification() != null) {
            double ucsLevel = adNetwork.getAdNetworkDailyNotification().getServiceLevel();
            adNetwork.setUcsBid(0.1 + random.nextDouble() / 10.0);
            System.out.println("Day " + adNetwork.getDay() + ": ucs level reported: " + ucsLevel);
        } else {
            System.out.println("Day " + adNetwork.getDay() + ": Initial ucs bid is " + adNetwork.getUcsBid());
        }

        /*
         * Record Log
         */
        CampaignLogReport logReport = new CampaignLogReport();
        logReport.setCampaignId(adNetwork.getPendingCampaign().id);
        logReport.setDay(adNetwork.getDay());
        logReport.setReachImps(adNetwork.getPendingCampaign().reachImps);
        logReport.setDayStart((int) adNetwork.getPendingCampaign().dayStart);
        logReport.setDayEnd((int) adNetwork.getPendingCampaign().dayEnd);
        logReport.setTargetSegment(adNetwork.getPendingCampaign().targetSegment);
        logReport.setVideoCoef(adNetwork.getPendingCampaign().videoCoef);
        logReport.setMobileCoef(adNetwork.getPendingCampaign().mobileCoef);
        logReport.setCampaignQueries(adNetwork.getPendingCampaign().campaignQueries);
        logReport.setStats(adNetwork.getPendingCampaign().stats);
        logReport.setBudget(adNetwork.getPendingCampaign().budget);
        logReport.setUcsLevel(adNetwork.getUcsBid());
        logReport.setBudgetMilis(cmpBidMillis);
        adNetwork.getLogReports().add(logReport);
        /*
         * Example of how to use UserPopulationProbabilities
         * System.out.println("probability being computed");
         * UserPopulationProbabilities userPopulationProbabilities = new UserPopulationProbabilities();
         * int chance = userPopulationProbabilities.getProbability(adNetwork.getPendingCampaign().targetSegment);
         * System.out.println("total probability is " + chance);
         */
        /*
         * Note: Campaign bid is in millis
         */
//        PredictUCSCost ucsCost = new PredictUCSCost(adNetwork);
//        System.out.println("UCS Cost Prediction: " + ucsCost.predictUCSCost());
//        AdNetBidMessage bids = new AdNetBidMessage(ucsCost.predictUCSCost(), adNetwork.getPendingCampaign().id, cmpBidMillis);
//        adNetwork.sendResponse(adNetwork.getDemandAgentAddress(), bids);
        AdNetBidMessage bids = new AdNetBidMessage(adNetwork.getUcsBid(), adNetwork.getPendingCampaign().id, cmpBidMillis);
        adNetwork.sendResponse(adNetwork.getDemandAgentAddress(), bids);
    }
}
