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
package org.wso2.carbon.module.pdf.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;

import java.io.IOException;

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

    /**
     * Looks up mandatory parameter. Value should be a String.
     *
     * @param msgCtx    Message context
     * @param paramName Name of the parameter to lookup
     * @return Value of the parameter
     * @throws ConnectException In case mandatory parameter is not provided
     */
    public static String lookUpStringParam(MessageContext msgCtx, String paramName, String defaultValue) throws ConnectException {
        String value = (String) ConnectorUtils.lookupTemplateParamater(msgCtx, paramName);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }

    /**
     * Looks up mandatory parameter. Value should be a String.
     *
     * @param msgCtx    Message context
     * @param paramName Name of the parameter to lookup
     * @return Value of the parameter
     * @throws ConnectException In case mandatory parameter is not provided
     */
    public static int lookUpIntegerParam(MessageContext msgCtx, String paramName, int defaultValue) throws ConnectException {
        String value = (String) ConnectorUtils.lookupTemplateParamater(msgCtx, paramName);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static PDPage createPDPage(String pageSize) {
        PDRectangle rectangle;
        switch (pageSize.toUpperCase()) {
            case "A0":
                rectangle = PDRectangle.A0;
                break;
            case "A1":
                rectangle = PDRectangle.A1;
                break;
            case "A2":
                rectangle = PDRectangle.A2;
                break;
            case "A3":
                rectangle = PDRectangle.A3;
                break;
            case "A4":
                rectangle = PDRectangle.A4;
                break;
            case "A5":
                rectangle = PDRectangle.A5;
                break;
            case "A6":
                rectangle = PDRectangle.A6;
                break;
            case "LETTER":
                rectangle = PDRectangle.LETTER;
                break;
            case "LEGAL":
                rectangle = PDRectangle.LEGAL;
                break;
            case "TABLOID":
                rectangle = PDRectangle.TABLOID;
                break;
            default:
                throw new IllegalArgumentException("Unknown page size: " + pageSize);
        }
        return new PDPage(rectangle);
    }

    public static void setFont(PDPageContentStream contentStream, String fontName, float fontSize) throws IOException {
        PDType1Font font;
        switch (fontName.toUpperCase()) {
            case "HELVETICA":
                font = PDType1Font.HELVETICA;
                break;
            case "HELVETICA_BOLD":
                font = PDType1Font.HELVETICA_BOLD;
                break;
            case "HELVETICA_OBLIQUE":
                font = PDType1Font.HELVETICA_OBLIQUE;
                break;
            case "HELVETICA_BOLD_OBLIQUE":
                font = PDType1Font.HELVETICA_BOLD_OBLIQUE;
                break;
            case "COURIER":
                font = PDType1Font.COURIER;
                break;
            case "COURIER_BOLD":
                font = PDType1Font.COURIER_BOLD;
                break;
            case "COURIER_OBLIQUE":
                font = PDType1Font.COURIER_OBLIQUE;
                break;
            case "COURIER_BOLD_OBLIQUE":
                font = PDType1Font.COURIER_BOLD_OBLIQUE;
                break;
            case "TIMES_ROMAN":
                font = PDType1Font.TIMES_ROMAN;
                break;
            case "TIMES_BOLD":
                font = PDType1Font.TIMES_BOLD;
                break;
            case "TIMES_ITALIC":
                font = PDType1Font.TIMES_ITALIC;
                break;
            case "TIMES_BOLD_ITALIC":
                font = PDType1Font.TIMES_BOLD_ITALIC;
                break;
            case "SYMBOL":
                font = PDType1Font.SYMBOL;
                break;
            case "ZAPF_DINGBATS":
                font = PDType1Font.ZAPF_DINGBATS;
                break;
            default:
                throw new IllegalArgumentException("Unknown font name: " + fontName);
        }
        contentStream.setFont(font, fontSize);
    }

}
