/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;

/**
 *
 * @author hsn
 */
public class HandleAdxPublisherReport {

    public HandleAdxPublisherReport() {
    }

    public void run(SampleAdNetworkModified adNetwork, AdxPublisherReport adxPublisherReport) {
        System.out.println("Publishers Report: ");
        for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
            AdxPublisherReportEntry entry = adxPublisherReport.getEntry(publisherKey);
            System.out.println(entry.toString());
        }
    }
}
