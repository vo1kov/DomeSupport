package com.domeltd.dome;


import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.http.Request;

public class DomeCredentials implements Credentials {


    private String mapiID;
    private String mapiKey;
    private String clientId;

public DomeCredentials(String apiID, String apiKey, String clId) {
    mapiID = apiID;
    mapiKey = apiKey;
    clientId = clId;

}


    @Override
    public void prepareRequest(Request request) {
        request.addHeader("X-DOME-APIID", mapiID);
        request.addHeader("X-DOME-APIKEY", mapiKey);
        request.addHeader("X-DOME-CLIENT", clientId);

    }

}
