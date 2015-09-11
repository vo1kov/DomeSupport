package com.domeltd.dome;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.iid.InstanceID;

import com.google.gson.JsonObject;
import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import microsoft.aspnet.signalr.client.*;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;


import com.google.gson.JsonElement;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class DomeSupportActivity extends ActionBarActivity{
    private static final String TAG = "MMV";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    DomeSupport domeSupport;


    private String apiID = "iq.085ee6951f374791";//"Testing_Project.e82c89baedeb4e3e";
    private String apiKey = "1494ea410d37e8802ded3beba74f6ce9";//"b622395c464e6a050988514eb9059718";

    void GetHistory()
    {

        RequestParams params = new RequestParams();
        params.put("clientId", domeSupport.DclientId);
        params.put("lastmessageId", "");
        DomeRestClient.get("messages", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                if (timeline != null) {
                    for (int i = 0; i < timeline.length(); i++) {
                        try {
                            JSONObject mes = timeline.getJSONObject(i);
                            ChatMessage msg = new ChatMessage();
                            msg.setId(1);
                            msg.setMe(mes.getInt("Type") == 0);
                            msg.setMessage(mes.getString("MessageText"));
                            msg.setDate(mes.getString("OperatorName") + " " + mes.getString("DateTime"));
                            msg.setUrl(mes.getString("OperatorImageUrl"));
                            displayMessage(msg);
                        } catch (JSONException e) {
                            Log.d(TAG, "PDTPLFHEK>!1111" + timeline.toString());
                        }
                    }
                    Log.d(TAG, "GetHistory:" + timeline.toString());
                }
            }

            //@Override
            public void onFailure(int statusCode, Header[] headers, JSONArray responseBody, Throwable
                    error) {
                if (responseBody != null)
                    Log.d(TAG, responseBody.toString());

                // Response failed :(
            }
        });
    }


    HubConnection connection;
    HubProxy mainHubProxy;
    void getInfo() {
        String versionApp = "";
        String versionNumApp = "";

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionApp = pInfo.versionName;
            versionNumApp = Integer.toString(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException a) {


        }

        String appName = getAppLable(this);


        String deviceModel = Devices.getDeviceName();


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //float batteryPct = level / (float) scale;

        String battery = Integer.toString(level);


        String storageTotal = StorageSpace.getTotalInternalMemorySize();
        String storageFree = StorageSpace.getAvailableInternalMemorySize();
        String versionOS = Integer.toString(android.os.Build.VERSION.SDK_INT);

        String connectionType = "wifi";

//        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
//        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
//
//        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
//            connectionType="mobile";
//        } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
//            connectionType="wifi";
//        }

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = telephonyManager.getNetworkOperatorName();

       String displayLanguage = Locale.getDefault().getDisplayLanguage();
        String country = Locale.getDefault().getISO3Country();

        String sdkVersion = "0.1";


        RequestParams params = new RequestParams();
        params.put("clientId", domeSupport.DclientId);
        params.put("versionApp", versionApp); //1. Название билда приложения
        params.put("versionNumApp", versionNumApp);//2. Версия билда
        params.put("appName", appName);//3. Название приложения
        params.put("deviceModel", deviceModel);//4. Модель устройства
        params.put("battery", battery);//5. Заряд аккумулятора
        params.put("storageTotal", storageTotal);//6. Место на дисках полное
        params.put("storageFree", storageFree);//7. Место на дисках занятое
        params.put("versionOS", versionOS);//8. Версия Операционки
        params.put("OS", "android");//9. Тип операционки(Айос, андроид)
        params.put("connectionType", connectionType);//10. Тип подключения(вайфай, либо оператор)
        //params.put("carrierName", carrierName);//11. Имя оператора (Билайн, МТС и др.)
        params.put("country", country);//12. Код страны(локализации)
        params.put("displayLanguage", displayLanguage);//13. Язык системы
        params.put("sdkVersion", sdkVersion);//14. Версия библиотеки Dome SDK

    domeSupport.SendInfo(params);

    }


    public String getAppLable(Context pContext) {
        PackageManager lPackageManager = pContext.getPackageManager();
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = lPackageManager.getApplicationInfo(pContext.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : "Unknown");
    }

    void ConnectR()
    {
        DomeCredentials cred = new DomeCredentials(apiID,apiKey, domeSupport.DclientId);
        connection = new HubConnection("https://dome.support",true);
       connection.setCredentials(new DomeCredentials(apiID,apiKey, domeSupport.DclientId));
        mainHubProxy = connection.createHubProxy("chat");

        connection.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState connectionState, ConnectionState connectionState2) {
                Log.i("MMV", connectionState.name() + "->" + connectionState2.name());
            }
        });

        connection.closed(new Runnable() {
            @Override
            public void run() {
                connectSignalr();
            }
        });


        mainHubProxy.subscribe("SupportResponse").addReceivedHandler(new Action<JsonElement[]>() {
            @Override
            public void run(JsonElement[] jsonElements) throws Exception {
                JsonObject jmsg = jsonElements[0].getAsJsonObject();
                ChatMessage msg = new ChatMessage();
                msg.setId(1);
                msg.setMe(jmsg.get("Type").getAsInt() == 0);
                msg.setMessage(jmsg.get("MessageText").getAsString());
                msg.setDate( jmsg.get("OperatorName").getAsString()  + " " + jmsg.get("DateTime").getAsString());
                msg.setUrl(jmsg.get("OperatorImageUrl").getAsString());
                displayMessage(msg);
            }
        });

        connectSignalr();
    }


    private void connectSignalr() {
        try {
            SignalRConnectTask signalRConnectTask = new SignalRConnectTask();
            signalRConnectTask.execute(connection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mSettings = this.getSharedPreferences(Saver.PREF_PATH, Context.MODE_PRIVATE);
        domeSupport = new DomeSupport(apiID, apiKey, mSettings);

        InstanceID instanceID = InstanceID.getInstance(this);
        String devId = instanceID.getId();
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Saver.DEV_ID, devId);
        editor.apply();




        
        domeSupport.Connect();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        initControls();
        GetHistory();
        ConnectR();
        getInfo();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);

        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);
                domeSupport.SendMessage(messageText);




            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();
        adapter = new ChatAdapter(DomeSupportActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
    }
}
