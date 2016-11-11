/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import edu.umich.eecs.tac.props.Ad;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;

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

        if ((dayBiddingFor >= adNetwork.getCurrCampaign().dayStart)
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

            double impressionLimit = adNetwork.getCurrCampaign().impsTogo();
            double budgetLimit = adNetwork.getCurrCampaign().budget;
            adNetwork.getBidBundle().setCampaignDailyLimit(adNetwork.getCurrCampaign().id,
                    (int) impressionLimit, budgetLimit);

            System.out.println("Day " + adNetwork.getDay() + ": Updated " + entCount
                    + " Bid Bundle entries for Campaign id " + adNetwork.getCurrCampaign().id);
        }

        if (adNetwork.getBidBundle() != null) {
            System.out.println("Day " + adNetwork.getDay() + ": Sending BidBundle");
            adNetwork.sendResponse(adNetwork.getAdxAgentAddress(), adNetwork.getBidBundle());
        }
    }
}
