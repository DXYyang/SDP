/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.userguide;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.apache.synapse.util.UUIDGenerator;
import org.wso2.mercury.util.MercuryClientConstants;
import samples.common.StockQuoteHandler;

import java.io.File;
import java.net.URL;

/**
 * See build.xml for options
 */
public class StockQuoteClient {

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0) {
            result = def;
        }
        return result;
    }

    public static void main(String[] args) {

        try {
            executeClient();

            if ("placeorder".equals(InnerStruct.MODE)) {
                System.out.println("Order placed for " + InnerStruct.QUANTITY
                        + " shares of stock " + InnerStruct.SYMBOL
                        + " at a price of $ " + InnerStruct.PRICE);
            } else {
                if ("customquote".equals(InnerStruct.MODE)) {
                    System.out.println("Custom :: Stock price = $" +
                            StockQuoteHandler.parseCustomQuoteResponse(InnerStruct.RESULT));
                } else if ("quote".equals(InnerStruct.MODE)) {
                    System.out.println("Standard :: Stock price = $" +
                            StockQuoteHandler.parseStandardQuoteResponse(InnerStruct.RESULT));
                } else if ("dualquote".equals(InnerStruct.MODE)) {
                    while (true) {
                        if (InnerStruct.COMPLETED) {
                            System.out.println("Standard dual channel :: Stock price = $" +
                                    StockQuoteHandler.parseStandardQuoteResponse(InnerStruct.RESULT));
                            System.exit(0);
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } else if ("fullquote".equals(InnerStruct.MODE)) {
                    System.out.println("Full :: Average price = $" +
                            StockQuoteHandler.parseFullQuoteResponse(InnerStruct.RESULT));
                } else if ("marketactivity".equals(InnerStruct.MODE)) {
                    System.out.println("Activity :: Average price = $" +
                            StockQuoteHandler.parseMarketActivityResponse(InnerStruct.RESULT));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OMElement executeTestClient() throws Exception {
        executeClient();
        return InnerStruct.RESULT;
    }

    public static void executeClient() throws Exception {

        // defaults
        String symbol = getProperty("symbol", "IBM");
        String mode = getProperty("mode", "quote");
        String addUrl = getProperty("addurl", null);
        String trpUrl = getProperty("trpurl", null);
        String prxUrl = getProperty("prxurl", null);
        String repo = getProperty("repository", "client_repo");
        String svcPolicy = getProperty("policy", null);
        String rest = getProperty("rest", null);
        String wsrm = getProperty("wsrm", null);
        String itr = getProperty("itr", "1");

        double price = 0;
        int quantity = 0;
        ConfigurationContext configContext = null;

        Options options = new Options();
        OMElement payload = null;
        ServiceClient serviceClient;

        if (repo != null && !"null".equals(repo)) {
            configContext =
                    ConfigurationContextFactory.
                            createConfigurationContextFromFileSystem(repo,
                                    repo + File.separator + "conf" + File.separator + "axis2.xml");
            serviceClient = new ServiceClient(configContext, null);
        } else {
            serviceClient = new ServiceClient();
        }

        if ("customquote".equals(mode)) {
            payload = StockQuoteHandler.createCustomQuoteRequest(symbol);
            options.setAction("urn:getQuote");
        } else if ("fullquote".equals(mode)) {
            payload = StockQuoteHandler.createFullQuoteRequest(symbol);
            options.setAction("urn:getFullQuote");
        } else if ("placeorder".equals(mode)) {
            price = getRandom(100, 0.9, true);
            quantity = (int) getRandom(10000, 1.0, true);
            payload = StockQuoteHandler.createPlaceOrderRequest(price, quantity, symbol);
            options.setAction("urn:placeOrder");
        } else if ("marketactivity".equals(mode)) {
            payload = StockQuoteHandler.createMarketActivityRequest();
            options.setAction("urn:getMarketActivity");
        } else if ("quote".equals(mode) || "dualquote".equals(mode)) {
            payload = StockQuoteHandler.createStandardQuoteRequest(
                    symbol, Integer.parseInt(itr));
            options.setAction("urn:getQuote");
            if ("dualquote".equals(mode)) {
                serviceClient.engageModule("addressing");
                options.setUseSeparateListener(true);
            }
        }

        // set addressing, transport and proxy url
        if (addUrl != null && !"null".equals(addUrl)) {
            serviceClient.engageModule("addressing");
            options.setTo(new EndpointReference(addUrl));
        }
        if (trpUrl != null && !"null".equals(trpUrl)) {
            options.setProperty(Constants.Configuration.TRANSPORT_URL, trpUrl);
        }
        if (prxUrl != null && !"null".equals(prxUrl)) {
            HttpTransportProperties.ProxyProperties proxyProperties =
                    new HttpTransportProperties.ProxyProperties();
            URL url = new URL(prxUrl);
            proxyProperties.setProxyName(url.getHost());
            proxyProperties.setProxyPort(url.getPort());
            proxyProperties.setUserName("");
            proxyProperties.setPassWord("");
            proxyProperties.setDomain("");
            options.setProperty(HTTPConstants.PROXY, proxyProperties);
        }

        // apply any service policies if any
        if (svcPolicy != null && !"null".equals(svcPolicy) && svcPolicy.length() > 0) {
            System.out.println("Using WS-Security");
            serviceClient.engageModule("addressing");
            serviceClient.engageModule("rampart");
            options.setProperty(
                    RampartMessageData.KEY_RAMPART_POLICY, loadPolicy(svcPolicy));
        }

        if (Boolean.parseBoolean(rest)) {
            System.out.println("Sending as REST");
            options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
        }
        if (Boolean.parseBoolean(wsrm)) {
            System.out.println("Using WS-RM");
            serviceClient.engageModule("Mercury");
            options.setProperty("MercuryLastMessage", Constants.VALUE_TRUE);
            options.setProperty(
                    MercuryClientConstants.SEQUENCE_OFFER, UUIDGenerator.getUUID());
        }

        serviceClient.setOptions(options);

        InnerStruct.MODE = mode;
        InnerStruct.SYMBOL = symbol;
        InnerStruct.PRICE = price;
        InnerStruct.QUANTITY = quantity;

        if ("placeorder".equals(mode)) {
            serviceClient.fireAndForget(payload);
            Thread.sleep(5000);

        } else if ("dualquote".equals(mode)) {
            serviceClient.sendReceiveNonBlocking(payload, new StockQuoteCallback());
        } else {
            InnerStruct.RESULT = serviceClient.sendReceive(payload);
            if (Boolean.parseBoolean(wsrm)) {
                // give some time for RM to terminate normally
                Thread.sleep(5000);
                if (configContext != null) {
                    configContext.getListenerManager().stop();
                }
                serviceClient.cleanup();
                System.exit(0);
            }
        }

//        try {
//            if (configContext != null) {
//                configContext.terminate();
//            }
            // the above statement was used on reccomendation by Chamikara as I remember, but
            // since using Axis2 1.3 - this causes some unexpected classloading issue on the
            // Axis2 server side - which cannot be described. This using the below as suggested
            // by Deepal
//            serviceClient.cleanup();
//        } catch (Exception ignore) {
//        }
    }

    public static class InnerStruct {
        static String MODE = null;
        static String SYMBOL = null;
        static int QUANTITY = 0;
        static double PRICE = 0;
        static boolean COMPLETED = false;
        static OMElement RESULT = null;
    }

    private static Policy loadPolicy(String xmlPath) throws Exception {
        StAXOMBuilder builder = new StAXOMBuilder(xmlPath);
        return PolicyEngine.getPolicy(builder.getDocumentElement());
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
                * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }

}
