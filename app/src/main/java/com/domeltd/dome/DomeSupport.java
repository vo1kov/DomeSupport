package com.domeltd.dome;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;


/**
 * Created by vo1kov on 31.07.15.
 */
public class DomeSupport {




    static String TAG = "MMV";

    DomeRestClient dome;

    String DdeviceId;// = "dQCIspkx_JA";
    String DclientId;// = "62455803-a037-e511-80ba-000d3a214540";

    public String getApiID() {
        return apiID;
    }

    public String getApiKey() {
        return apiKey;
    }

    private String apiID;
    private String apiKey;


    SharedPreferences mSettings;

    public DomeSupport(String _apiID, String _apiKey,  SharedPreferences _mSettings)
    {
        apiID = _apiID;
        apiKey = _apiKey;
        mSettings = _mSettings;
        dome = new DomeRestClient(apiID, apiKey);
    }


    void Connect()
    {

        if (mSettings.contains(Saver.CLI_ID)) {
            if (mSettings.contains(Saver.DEV_ID)) {
                DdeviceId = mSettings.getString(Saver.DEV_ID, "");
            }

            // Получаем число из настроек
            DclientId = mSettings.getString(Saver.CLI_ID,"");
            if(DclientId!="")
                this.ResumeConnection();
        }
        else {
            while (!mSettings.contains(Saver.DEV_ID)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            // Получаем число из настроек
            DdeviceId = mSettings.getString(Saver.DEV_ID, "");
            if (DdeviceId != "")
                this.EstablishConnection();
        }
    }


    void ResumeConnection()
    {
   RequestParams params = new RequestParams();
        params.put("deviceId", DdeviceId);
        params.put("clientId", DclientId);

        DomeRestClient.put("client", params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "ResumeConnection:" + responseBody);
                //System.out.println(responseBody);
                // Successfully got a response
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, responseBody);

                // Response failed :(
            }

        });
    }

    void ProvideToken(String deviceToken)//, deviceToken:String)
    {

        RequestParams params = new RequestParams();
        params.put("clientId", DclientId);
        params.put("deviceToken", deviceToken);

        if (mSettings.contains(Saver.CLI_ID)) {
            if (mSettings.contains(Saver.DEV_ID)) {
                DdeviceId = mSettings.getString(Saver.DEV_ID, "");
            }

            // Получаем число из настроек
            DclientId = mSettings.getString(Saver.CLI_ID, "");
        }


        DomeRestClient.put("client", params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "TOKEN:" + responseBody);
                //System.out.println(responseBody);
                // Successfully got a response
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, "TOKEN:" + responseBody);

                // Response failed :(
            }

        });
    }


    void SendMessage(String message)//(message :String)
    {

        RequestParams params = new RequestParams();
        params.put("clientId", DclientId);
        params.put("text", message);

        DomeRestClient.post("messages", params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "SEND: " + responseBody);
                //System.out.println(responseBody);
                // Successfully got a response
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, "SEND: " + responseBody);

                // Response failed :(
            }

        });
    }


    void ResumeConnection2()
    {
        RequestParams params = new RequestParams();
        params.put("deviceId", DdeviceId);
        params.put("clientId", DclientId);

        DomeRestClient.put("client", params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "ResumeConnection:" + responseBody);
                //System.out.println(responseBody);
                // Successfully got a response
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, responseBody);

                // Response failed :(
            }

        });
    }


    void SendInfo(RequestParams params)//(message :String)
    {



        DomeRestClient.put("settings", params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "INFO: " + responseBody);
                //System.out.println(responseBody);
                // Successfully got a response
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, "INFO: " + responseBody);

                // Response failed :(
            }

        });
    }

    void EstablishConnection()//(deviceId :String)
    {
        RequestParams params = new RequestParams();
        params.put("deviceId", DdeviceId);


        //InstanceID instanceID = InstanceID.getInstance(this);




        DomeRestClient.post("client", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (responseBody != null)
                    Log.d(TAG, "EstablishConnection:" + responseBody);

                DclientId = responseBody.substring(1, responseBody.length() - 1);

                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(Saver.CLI_ID, DclientId);
                editor.apply();

                //var clientId = string!.stringByReplacingOccurrencesOfString( "\"", withString: "" ) as String
                //clientId = clientId.substringWithRange(Range<String.Index>(start: advance(clientId.startIndex, 1), end: advance(clientId.endIndex, -1))) //"llo, playgroun"
                //self.DclientId = clientId

                //self.ProvideToken(clientId)
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable
                    error) {
                if (responseBody != null) {
                    Log.d(TAG, "EstablishConnection:" + responseBody);

                }
            }

        });
    }





}
