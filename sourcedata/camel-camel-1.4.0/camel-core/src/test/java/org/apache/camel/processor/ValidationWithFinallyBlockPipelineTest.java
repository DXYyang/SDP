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
package org.apache.camel.processor;

import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;

/**
 * The handle catch clause has a pipeline processing the exception.
 *
 * @author <a href="mailto:nsandhu">nsandhu</a>
 *
 */
public class ValidationWithFinallyBlockPipelineTest extends ValidationTest {
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:start")
                    .tryBlock()
                        .process(validator)
                        .setHeader("valid", constant(true))
                    .handle(ValidationException.class)
                        .setHeader("valid", constant(false))
                    .finallyBlock()
                        .setBody(body())
                        .choice()
                        .when(header("valid").isEqualTo(true))
                        .to("mock:valid")
                        .otherwise()
                        .to("mock:invalid");
            }
        };
    }

}
