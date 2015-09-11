package com.domeltd.dome;

/**
 * Created by vo1kov on 31.07.15.
 */


import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.hubs.HubConnection;


public class SignalRConnectTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        HubConnection connection = (HubConnection) objects[0];
        try {
            Thread.sleep(2000);
            connection.start().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
