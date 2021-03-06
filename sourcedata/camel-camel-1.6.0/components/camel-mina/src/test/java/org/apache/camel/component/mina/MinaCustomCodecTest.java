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
package org.apache.camel.component.mina;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Unit test with custom codec.
 */
public class MinaCustomCodecTest extends ContextTestSupport {

    protected String uri = "mina:tcp://localhost:11300?sync=true&codec=myCodec";
    protected String badUri = "mina:tcp://localhost:11300?sync=true&codec=XXX";

    public void testMyCodec() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Bye World");

        Object out = template.requestBody(uri, "Hello World");
        assertEquals("Bye World", out);

        mock.assertIsSatisfied();
    }

    public void testBadConfiguration() throws Exception {
        try {
            template.sendBody(badUri, "Hello World");
            fail("Should have thrown a ResolveEndpointFailedException");
        } catch (ResolveEndpointFailedException e) {
            // ok
        }
    }

    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("myCodec", new MyCodec());
        return jndi;
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from(uri).transform(constant("Bye World")).to("mock:result");
            }
        };
    }

    private class MyCodec implements ProtocolCodecFactory {

        public ProtocolEncoder getEncoder() throws Exception {
            return new ProtocolEncoder() {
                public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput out)
                    throws Exception {
                    ByteBuffer bb = ByteBuffer.allocate(32).setAutoExpand(true);
                    String s = (String) message;
                    bb.put(s.getBytes());
                    bb.flip();
                    out.write(bb);
                }

                public void dispose(IoSession ioSession) throws Exception {
                    // do nothing
                }
            };

        }

        public ProtocolDecoder getDecoder() throws Exception {
            return new CumulativeProtocolDecoder() {
                protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
                    if (in.remaining() > 0) {
                        byte[] buf = MinaConverter.toByteArray(in);
                        out.write(new String(buf));
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }
    }

}
