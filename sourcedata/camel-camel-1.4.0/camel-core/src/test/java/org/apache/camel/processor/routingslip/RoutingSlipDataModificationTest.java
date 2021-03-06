/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.routingslip;

import java.util.Map;

import javax.naming.Context;

import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Headers;
import org.apache.camel.OutHeaders;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RoutingSlipDataModificationTest extends ContextTestSupport {
    protected static final String ANSWER = "answer";
    protected static final String ROUTING_SLIP_HEADER = "routingSlipHeader";
    private static final transient Log LOG = LogFactory.getLog(RoutingSlipDataModificationTest.class);
    protected MyBean myBean = new MyBean(ROUTING_SLIP_HEADER);

    public void testModificationOfDataAlongRoute()
        throws Exception {
        MockEndpoint x = getMockEndpoint("mock:x");
        MockEndpoint y = getMockEndpoint("mock:y");
        MockEndpoint z = getMockEndpoint("mock:z");

        x.expectedBodiesReceived(ANSWER);
        y.expectedBodiesReceived(ANSWER + ANSWER);
        z.expectedBodiesReceived(ANSWER + ANSWER);

        sendBody();

        assertMockEndpointsSatisifed();
    }

    protected void sendBody() {
        template.sendBodyAndHeader("direct:a", ANSWER, ROUTING_SLIP_HEADER,
                "mock:x,bean:myBean?method=modifyData,mock:y,mock:z");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Object lookedUpBean = context.getRegistry().lookup("myBean");
        assertSame("Lookup of 'myBean' should return same object!", myBean, lookedUpBean);
    }

    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("myBean", myBean);
        return answer;
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // START SNIPPET: example
                from("direct:a").routingSlip(ROUTING_SLIP_HEADER);
                // END SNIPPET: example
            }
        };
    }

    public static class MyBean {
        private String routingSlipHeader;

        public MyBean() {
        }

        public MyBean(String routingSlipHeader) {
            this.routingSlipHeader = routingSlipHeader;
        }

        public String modifyData(
            @Body String body,
            @Headers Map<String, Object> headers,
            @OutHeaders Map<String, Object> outHeaders) {
            outHeaders.put(routingSlipHeader, headers.get(routingSlipHeader));
            return body + body;
        }
    }
}
