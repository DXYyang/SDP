/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.gdata.servlet.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.gdata.data.GDataAccount;
import org.apache.lucene.gdata.server.ServiceException;

/**
 * @author Simon Willnauer
 *
 */
public class UpdateAccountStrategy extends AbstractAccountHandler {

    private static final Log LOG = LogFactory.getLog(UpdateAccountStrategy.class);

    

    @Override
    protected void processServiceAction(GDataAccount account) throws ServiceException {
        try{
        this.service.updateAccount(account);
        }catch (ServiceException e) {
            LOG.error("Can't update account -- "+e.getMessage(),e);
            throw e;
        }
        
    }
}
