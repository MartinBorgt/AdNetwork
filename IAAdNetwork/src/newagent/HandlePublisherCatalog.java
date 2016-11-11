/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newagent;

import tau.tac.adx.props.PublisherCatalog;

/**
 *
 * @author hsn
 */
public class HandlePublisherCatalog {

    public HandlePublisherCatalog() {
    }

    public void run(SampleAdNetworkModified adNetwork, PublisherCatalog publisherCatalog) {
        adNetwork.setPublisherCatalog(publisherCatalog);
        adNetwork.generatingAdxQuerySpace(adNetwork);
        adNetwork.getPublishersNames(adNetwork);
    }
}
