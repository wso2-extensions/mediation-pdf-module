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

import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.module.pdf.utils.PDFConstants;
import org.wso2.carbon.module.pdf.utils.PDFUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;


public class ConvertTIFFToPDF extends AbstractMediator {
    @Override
    public boolean mediate(MessageContext messageContext) {
        String content;
        String propertyName;
        try {
            content = PDFUtils.lookUpStringParam(messageContext, PDFConstants.CONTENT_PARAM);
            propertyName = PDFUtils.lookUpStringParam(messageContext, PDFConstants.RESULT_PROPERTY_NAME_PARAM);
            byte[] tiffBytes = Base64.getDecoder().decode(new String(content).getBytes("UTF-8"));
            // Convert TIFF to PDF
            byte[] pdfBytes = convertTiffToPdf(tiffBytes);
            byte[] encodedContent  = Base64.getEncoder().encode(pdfBytes);
            messageContext.setProperty(propertyName, new String(encodedContent));
        } catch (ConnectException | IOException | ImageReadException e) {
            log.error("Error while converting tiff content to a PDF", e);
            handleException(e.getMessage(), messageContext);
        }
        return true;
    }

    private byte[] convertTiffToPdf(byte[] tiffBytes) throws IOException, ImageReadException {
        PDDocument document = new PDDocument();
        List<BufferedImage> bimages = Imaging.getAllBufferedImages(tiffBytes);
        for (BufferedImage bi : bimages) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // the .08F can be tweaked. Go up for better quality,
                // but the size of the PDF will increase
                PDImageXObject image = JPEGFactory.createFromImage(document, bi);
                Dimension scaledDim = getScaledDimension(new Dimension(image.getWidth(), image.getHeight()),
                        new Dimension((int) page.getMediaBox().getWidth(), (int) page.getMediaBox().getHeight()));
                contentStream.drawImage(image, 1, 1, scaledDim.width, scaledDim.height);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();
        byte[] pffDoc = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        // Get the byte array
        return pffDoc;
    }

    private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            // scale width to fit
            new_width = bound_width;
            // scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            // scale height to fit instead
            new_height = bound_height;
            // scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
