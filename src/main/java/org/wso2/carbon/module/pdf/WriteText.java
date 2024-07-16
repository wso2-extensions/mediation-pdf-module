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

public class WriteText extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {
        String content;
        String propertyName;
        String pageSize;
        int textStartAtX;
        int textStartAtY;
        String font;
        int fontSize;
        try {
            content = PDFUtils.lookUpStringParam(messageContext, PDFConstants.CONTENT_PARAM);
            propertyName = PDFUtils.lookUpStringParam(messageContext, PDFConstants.RESULT_PROPERTY_NAME_PARAM);
            pageSize  = PDFUtils.lookUpStringParam(messageContext, PDFConstants.PAGE_SIZE, PDFConstants.DEFAULT_PAGE_SIZE);
            textStartAtX  = PDFUtils.lookUpIntegerParam(messageContext, PDFConstants.TEXT_STARTS_AT_X,
                    PDFConstants.DEFAULT_TEXT_STARTS_AT_X);
            textStartAtY  = PDFUtils.lookUpIntegerParam(messageContext, PDFConstants.TEXT_STARTS_AT_Y,
                    PDFConstants.DEFAULT_TEXT_STARTS_AT_Y);
            font  = PDFUtils.lookUpStringParam(messageContext, PDFConstants.FONT, PDFConstants.DEFAULT_FONT);
            fontSize  = PDFUtils.lookUpIntegerParam(messageContext, PDFConstants.FONT_SIZE,
                    PDFConstants.DEFAULT_FONT_SIZE);
            byte[] pdfBytes = writeFile(content, pageSize, textStartAtX, textStartAtY, font, fontSize);
            byte[] encodedContent  = Base64.getEncoder().encode(pdfBytes);
            messageContext.setProperty(propertyName, new String(encodedContent));
        } catch (ConnectException | IOException e) {
            log.error("Error while writing content to a pdf file", e);
            handleException(e.getMessage(), messageContext);
        }
        return true;
    }

    private byte[] writeFile(String content, String pageSize, int textStartAtX, int textStartAtY, String font,
                             int fontSize) throws IOException {
        PDDocument document = new PDDocument();

        // Add a page to the document
        PDPage page = PDFUtils.createPDPage(pageSize);
        document.addPage(page);

        // Create a content stream for the page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Add content to the page
        contentStream.beginText();
        contentStream.newLineAtOffset(textStartAtX, textStartAtY);
        PDFUtils.setFont(contentStream, font, fontSize);
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
