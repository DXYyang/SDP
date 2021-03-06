package org.apache.synapse.samples.n2n;

import org.apache.synapse.SynapseConstants;
import org.apache.axis2.AxisFault;
import samples.userguide.StockQuoteClient;

/**
 *
 */
public class SynapseSample_102_Integration extends AbstractAutomationTestCase {

    protected void setUp() throws Exception {
        System.setProperty(SynapseConstants.SYNAPSE_XML, SAMPLE_CONFIG_ROOT_PATH + "synapse_sample_102.xml");
        super.setUp();
    }

    public void testSample() throws Exception {
        System.setProperty("trpurl", "http://localhost:8080/soap/StockQuoteProxy");
        try {
            getStringResultOfTest(StockQuoteClient.executeTestClient());
        } catch (AxisFault f) {
            assertEquals("The service cannot be found for the endpoint reference (EPR) " +
                    "/soap/StockQuoteProxy", f.getReason());
        }

        System.setProperty("trpurl", "https://localhost:8443/soap/StockQuoteProxy");
        String resultString = getStringResultOfTest(StockQuoteClient.executeTestClient());
        assertXpathExists("ns:getQuoteResponse", resultString);
        assertXpathExists("ns:getQuoteResponse/ns:return", resultString);
    }
}
