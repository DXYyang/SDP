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
package org.apache.camel.component.jms.issues;

import org.apache.camel.builder.RouteBuilder;

/**
 * RouteBuilder for {@link JmsResequencerTest}.
 */
public class JmsResequencerTestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:queue:in1").resequencer().body().batch().size(100).timeout(10000L).to("mock:result");
        from("activemq:queue:in2").resequencer().header("num").stream().timeout(2000L).to("mock:result");
    }

}
