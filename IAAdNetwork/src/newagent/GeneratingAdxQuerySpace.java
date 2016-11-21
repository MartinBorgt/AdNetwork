/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import java.util.HashSet;
import java.util.Set;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.MarketSegment;

/**
 *
 * @author hsn
 */
public class GeneratingAdxQuerySpace {

    public GeneratingAdxQuerySpace() {
    }

    public void run(SampleAdNetworkModified adNetwork) {
        if (adNetwork.getPublisherCatalog() != null && adNetwork.getQueries() == null) {
            Set<AdxQuery> querySet = new HashSet<AdxQuery>();

            /*
             * for each web site (publisher) we generate all possible variations
             * of device type, ad type, and user market segment
             */
            for (PublisherCatalogEntry publisherCatalogEntry : adNetwork.getPublisherCatalog()) {
                String publishersName = publisherCatalogEntry.getPublisherName();
                for (MarketSegment userSegment : MarketSegment.values()) {
                    Set<MarketSegment> singleMarketSegment = new HashSet<MarketSegment>();
                    singleMarketSegment.add(userSegment);

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.text));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.mobile, AdType.video));

                    querySet.add(new AdxQuery(publishersName,
                            singleMarketSegment, Device.pc, AdType.video));

                }

                /**
                 * An empty segments set is used to indicate the "UNKNOWN"
                 * segment such queries are matched when the UCS fails to
                 * recover the user's segments.
                 */
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.mobile,
                        AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.mobile,
                        AdType.text));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.pc, AdType.video));
                querySet.add(new AdxQuery(publishersName,
                        new HashSet<MarketSegment>(), Device.pc, AdType.text));
            }
            adNetwork.setQueries(new AdxQuery[querySet.size()]);
            querySet.toArray(adNetwork.getQueries());
        }
    }
}
