/*
 * Copyright (C) 2017 Hendrik Spiegel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package space.polylog.protoburp;

import burp.*;
import space.polylog.burp.protobuf.BurpRequest;
import space.polylog.burp.protobuf.BurpResponse;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class ModifyHTTPRequest implements IHttpListener {

    private IBurpExtenderCallbacks callbacks;

    public ModifyHTTPRequest(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private IExtensionHelpers helpers() {
        return callbacks.getHelpers();
    }

    private static int SYNC_PORT = 32768;

    private static String HOST_NAME = "localhost";



    private void log(String msg) {
        Logging logger = Logging.getInstance();
        logger.log(getClass(), msg, Logging.INFO);
    }

    public void processHttpMessage(int toolFlag, boolean isRequest, IHttpRequestResponse ihrr) {
        /* TODO */

        IHttpService httpService = ihrr.getHttpService();

        if (!isRequest) {
            handleResponse(ihrr);
            return;
        }
        handleRequest(ihrr);
    }

    private void handleRequest(IHttpRequestResponse ihrr) {
        byte[] requestData = ihrr.getRequest();
        IRequestInfo iRequestInfo = helpers().analyzeRequest(ihrr);

        URL url = iRequestInfo.getUrl();

        int bodyOffset = iRequestInfo.getBodyOffset();

        byte contentType = iRequestInfo.getContentType();


        BurpRequest.Builder builder = BurpRequest.newBuilder()
                .addAllHeaders(iRequestInfo.getHeaders())
                .setState(BurpRequest.State.UNMODIFIED)
                .setRequestMethod(iRequestInfo.getMethod())
                .setContentType(BurpRequest.ContentType.valueOf(contentType));

        builder.setUrl(BurpRequest.URL.newBuilder()
                .setFile(url.getFile())
                .setHost(url.getHost())
                .setPort(url.getPort())
                .setProtocol(url.getProtocol())
        );

        for (IParameter parm : iRequestInfo.getParameters()) {
            builder = builder.addParameters(BurpRequest.Parameter.newBuilder()
                    .setName(parm.getName())
                    .setValue(parm.getValue()));
        }

        String encodedBody = helpers().base64Encode(Arrays.copyOfRange(requestData,
                iRequestInfo.getBodyOffset(),
                requestData.length));

        builder = builder.setBase64Body(encodedBody);
        BurpRequest request = builder.build();

        BurpRequest modifiedRequest;

        return;

    }

    private void handleResponse(IHttpRequestResponse ihrr) {
        byte[] responseData = ihrr.getResponse();
        IResponseInfo iResponseInfo = helpers().analyzeResponse(responseData);

        List<ICookie> cookies = iResponseInfo.getCookies();


        BurpResponse.Builder builder = BurpResponse.newBuilder();
        builder = builder.setInferredMimeType(iResponseInfo.getInferredMimeType())
                .setStatedMimeType(iResponseInfo.getStatedMimeType())
                .setStatusCode(iResponseInfo.getStatusCode())
                .addAllHeaders(iResponseInfo.getHeaders());

        for (ICookie ck : cookies) {
            builder = builder.addCookies(BurpResponse.Cookie.newBuilder()
                    .setValue(ck.getValue())
                    .setDomain(ck.getDomain())
                    .setName(ck.getName())
            );
        }

        String encodedBody = helpers().base64Encode(Arrays.copyOfRange(responseData,
                iResponseInfo.getBodyOffset(),
                responseData.length));

        builder = builder.setBase64Body(encodedBody);
        BurpResponse burpResponse = builder.build();

        return;
    }
}
