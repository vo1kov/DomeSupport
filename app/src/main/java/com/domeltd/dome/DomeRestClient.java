package com.domeltd.dome;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by vo1kov on 30.07.15.
 */
class DomeRestClient {
    private static final String BASE_URL = "https://dome.support/api/";
    //private static final String BASE_URL = "http://google.com/";
    private static String apiID;
    private static String apiKey;


    private static AsyncHttpClient client = new AsyncHttpClient();

    public DomeRestClient(String _apiID, String _apiKey)
    {

        apiID = _apiID;
        apiKey = _apiKey;
       client.addHeader("X-DOME-APIID", apiID);
       client.addHeader("X-DOME-APIKEY", apiKey);
    }

    //StringEntity entity = new StringEntity(jsonParams.toString());
    //entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
