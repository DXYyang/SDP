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
package org.apache.camel.component.file.remote;

import java.io.File;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.component.mock.MockEndpoint;

/**
 * Unit test to verify that we can pool an ASCII file from the FTP Server and store it on a local file path
 */
public class FromFtpToAsciiFileTest extends FtpServerTestSupport {

    private int port = 20013;
    private String ftpUrl = "ftp://admin@localhost:" + port + "/tmp3/camel?password=admin&binary=false";

    public void testFtpRoute() throws Exception {
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedBodiesReceived("Hello World from FTPServer");
        resultEndpoint.assertIsSatisfied();

        // wait until the file producer has written the file
        Thread.sleep(1000);

        // assert the file
        File file = new File("target/ftptest/deleteme.txt");
        assertTrue("The ASCII file should exists", file.exists());
        assertTrue("File size wrong", file.length() > 10);

        // let some time pass to let the consumer etc. properly do its business before closing
        Thread.sleep(1000);
    }

    public int getPort() {
        return port;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prepareFtpServer();
    }

    private void prepareFtpServer() throws Exception {
        // prepares the FTP Server by creating a file on the server that we want to unit
        // test that we can pool and store as a local file
        Endpoint endpoint = context.getEndpoint(ftpUrl);
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody("Hello World from FTPServer");
        exchange.getIn().setHeader(FileComponent.HEADER_FILE_NAME, "hello.txt");
        Producer producer = endpoint.createProducer();
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                String fileUrl = "file:target/ftptest/?append=false&noop=true";
                from(ftpUrl).setHeader(FileComponent.HEADER_FILE_NAME, constant("deleteme.txt")).
                        convertBodyTo(String.class).to(fileUrl).to("mock:result");
            }
        };
    }

}


