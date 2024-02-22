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

package org.wso2.carbon.module.pdf;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.module.pdf.utils.PDFConstants;
import org.wso2.carbon.module.pdf.utils.PDFUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class WriteText extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {
        String content;
        String propertyName;
        try {
            content = PDFUtils.lookUpStringParam(messageContext, PDFConstants.CONTENT_PARAM);
            byte[] decodedBytes  = Base64.getDecoder().decode(content);
            propertyName = PDFUtils.lookUpStringParam(messageContext, PDFConstants.RESULT_PROPERTY_NAME_PARAM);
            byte[] pdfBytes = writeFile(new String(decodedBytes));
            byte[] encodedContent  = Base64.getEncoder().encode(pdfBytes);
            messageContext.setProperty(propertyName, new String(encodedContent));
        } catch (ConnectException | IOException e) {
            log.error("Error while writing content to a pdf file", e);
            handleException(e.getMessage(), messageContext);
        }
        return true;
    }

    private byte[] writeFile(String content) throws IOException {
        PDDocument document = new PDDocument();

        // Add a page to the document
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Create a content stream for the page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Add content to the page
        contentStream.beginText();
        //contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.showText(content);
        contentStream.endText();

        // Close the content stream
        contentStream.close();

        // Save the document to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();

        // Get the byte array
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return pdfBytes;
    }
}
