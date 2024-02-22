/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.module.pdf.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

public class PDFUtils {

    /**
     * Looks up mandatory parameter. Value should be a String.
     *
     * @param msgCtx    Message context
     * @param paramName Name of the parameter to lookup
     * @return Value of the parameter
     * @throws ConnectException In case mandatory parameter is not provided
     */
    public static String lookUpStringParam(MessageContext msgCtx, String paramName) throws ConnectException {
        String value = (String) ConnectorUtils.lookupTemplateParamater(msgCtx, paramName);
        if (StringUtils.isEmpty(value)) {
            throw new ConnectException("Parameter '" + paramName + "' is not provided ");
        } else {
            return value;
        }
    }

}
