/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
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

package org.wso2.carbon.module.pdf;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.module.pdf.utils.PDFConstants;
import org.wso2.carbon.module.pdf.utils.PDFUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MergeDocuments extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {
        String contentPropertyName = "";
        Object streamListObj;
        List<String> byteStreamList = new ArrayList<String>();
        String resultPropertyName = "";
        ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
        try {
            contentPropertyName = PDFUtils.lookUpStringParam(messageContext, PDFConstants.CONTENT_PROPERTY_NAME_PARAM);
            resultPropertyName = PDFUtils.lookUpStringParam(messageContext, PDFConstants.RESULT_PROPERTY_NAME_PARAM);
            streamListObj = messageContext.getProperty(contentPropertyName);
            if (streamListObj != null && streamListObj instanceof List) {
                byteStreamList = (List<String>)streamListObj;
            }
            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            for (int i=0; i < byteStreamList.size(); i++) {
                byte[] pdfData = Base64.getDecoder().decode(byteStreamList.get(i));
                pdfMerger.addSource(new ByteArrayInputStream(pdfData));
            }
            pdfMerger.setDestinationStream(mergedOutputStream);
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
            byte[] pdfBytes = mergedOutputStream.toByteArray();
            byte[] encodedContent  = Base64.getEncoder().encode(pdfBytes);
            messageContext.setProperty(resultPropertyName, new String(encodedContent));
        } catch (ConnectException | IOException e) {
            log.error("Error while merging PDF file", e);
            handleException(e.getMessage(), messageContext);
        } finally {
            try {
                mergedOutputStream.close();
            } catch (IOException e) {
                handleException(e.getMessage(), messageContext);
            }
        }
        return true;
    }

}
