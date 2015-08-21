package com.giantvpn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.blinkt.openvpn.LaunchCVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.api.IOpenVPNAPIService;
import de.blinkt.openvpn.api.IOpenVPNStatusCallback;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.OpenVPNService;
import de.greenrobot.event.EventBus;

public class afterlogin extends Activity implements View.OnClickListener{

    Button b_conn ;
    TextView t_sett,t_webv,timeLeft,accountType;
    ImageView uk,us,de,nl;
    SharedPreferences preff;
    SharedPreferences.Editor edit;
    private Handler mHandler;
    private static final int MSG_UPDATE_STATE = 0;
    private IOpenVPNAPIService mService = null;
    private static final int ICS_OPENVPN_PERMISSION = 7;
    ToggleButton adBlocker, alwaysConnected;
    public static String statusVPN="Connect";
    CountDownTimer timer;
    TextView textView9;
    List<BasicNameValuePair> list;
    ProgressDialog dialog;
    private VpnProfile mResult = null;
    public static final String CHANGE_COUNTRY="http://fondol.com/fastvpn_an/changeCon_v1.php";
    public static final String UPDATE_ACCOUNT="http://fondol.com/fastvpn_an/ad_block_v2.php";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterlogin);

        b_conn = (Button) findViewById(R.id.conn);
        t_sett = (TextView) findViewById(R.id.sett);
        t_webv = (TextView) findViewById(R.id.webv);
        textView9=(TextView)findViewById(R.id.textView9);
        alwaysConnected = (ToggleButton) findViewById(R.id.alwaysConnected);
        adBlocker = (ToggleButton) findViewById(R.id.switch1);

        timeLeft = (TextView) findViewById(R.id.textView3);
        accountType = (TextView) findViewById(R.id.textView5);

        uk = (ImageView) findViewById(R.id.imageView5);
        de = (ImageView) findViewById(R.id.imageView6);
        us = (ImageView) findViewById(R.id.imageView7);
        nl = (ImageView) findViewById(R.id.imageView8);
        uk.setOnClickListener(this);
        de.setOnClickListener(this);
        us.setOnClickListener(this);
        nl.setOnClickListener(this);
        preff = getSharedPreferences("ServerInfo", MODE_PRIVATE);
        edit=preff.edit();
        long values=preff.getLong("haveUntil", 0);
        long value=System.currentTimeMillis();

        long millisUntilFinished=value-values;
        System.out.println("time" + millisUntilFinished);
        String time = String.format("%02d:%02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(values), TimeUnit.MILLISECONDS.toHours(values) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(values)),
                TimeUnit.MILLISECONDS.toMinutes(values) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(values)),
                TimeUnit.MILLISECONDS.toSeconds(values) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(values)));
        timeLeft.setText(time);
        String accounttype=preff.getString("accountType","");
        accountType.setText(accounttype);
        actioned();

        adBlocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<BasicNameValuePair> list=new ArrayList<BasicNameValuePair>();
                list.add(new BasicNameValuePair("email",preff.getString("Username","")));
                if(((ToggleButton)view).isChecked()){
                    list.add(new BasicNameValuePair("on","1"));

                }
                else{
                    list.add(new BasicNameValuePair("on","0"));
                }

                new AdBlock().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,list);
            }
        });
        adBlocker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        adBlocker.setChecked(preff.getBoolean("check",false));
        StartTimer();

        EventBus.getDefault().register(this);
        System.out.println("inside on resume");
        b_conn.setText(statusVPN);
        bindservice();
        doParseConfig(Utils.ReadOpenVpn(afterlogin.this));


    }
    private void unbindService() {
        afterlogin.this.unbindService(mConnection);
    }
    @Override
    protected void onPause() {
        super.onPause();
        StopTimer();
        EventBus.getDefault().unregister(this);
        unbindService();
    }

    public void actioned() {
        t_sett.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(afterlogin.this, settings.class);
                startActivity(i);


            }
        });


        t_webv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(afterlogin.this, web_view.class);
                startActivity(i);


            }
        });

        b_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mResult != null) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    doParseConfig(Utils.ReadOpenVpn(afterlogin.this));
                    String username = preff.getString("Username", "");
                    String password = preff.getString("Password", "");
                    mResult.mUsername = username;
                    mResult.mPassword = Utils.md5(password);
                    boolean bol = preff.getBoolean("Connect", false);
                    System.out.println("boolean value is" + bol);
                    if (bol) {
                        Disconnect();
                    } else {
                        Connect();
                    }
                }

            }
        });


    }

    public static String setText() {

        if (statusVPN.equals("Connect")) {
            //statusVPN = "Connect";
            return "Disconnect";
        } else {
            //statusVPN = "Disconnect";
            return "Connect";
        }
    }



    @Override
    public void onClick(View v) {
        list=new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("email", preff.getString("Username", "")));
        boolean bol = preff.getBoolean("Connect", false);
        switch (v.getId()) {
            case R.id.imageView5:

                    if(bol){
                        Toast.makeText(afterlogin.this,"Already connected,please disconnect",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        list.add(new BasicNameValuePair("conid", "0"));
                        if(CommonClass.checkInternetConnection(afterlogin.this)){
                            new GetCountry().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
                        }
                        else{
                            Toast.makeText(afterlogin.this,"No network connection",Toast.LENGTH_SHORT).show();
                        }
                    }


                break;
            case R.id.imageView6:


                    if(bol){
                        Toast.makeText(afterlogin.this,"Already connected,please disconnect",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        list.add(new BasicNameValuePair("conid", "1"));
                        if(CommonClass.checkInternetConnection(afterlogin.this)){
                            new GetCountry().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        }
                        else{
                            Toast.makeText(afterlogin.this,"No network connection",Toast.LENGTH_SHORT).show();
                        }

                    }


                break;
            case R.id.imageView7:
                    if(bol){
                        Toast.makeText(afterlogin.this,"Already connected,please disconnect",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        list.add(new BasicNameValuePair("conid", "2"));
                        if(CommonClass.checkInternetConnection(afterlogin.this)){
                            new GetCountry().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        }
                        else{
                            Toast.makeText(afterlogin.this,"No network connection",Toast.LENGTH_SHORT).show();
                        }

                    }

                break;
            case R.id.imageView8:



                    if(bol){
                        Toast.makeText(afterlogin.this,"Already connected,please disconnect",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        list.add(new BasicNameValuePair("conid", "3"));
                        if(CommonClass.checkInternetConnection(afterlogin.this)){
                            new GetCountry().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                        }
                        else{
                            Toast.makeText(afterlogin.this,"No network connection",Toast.LENGTH_SHORT).show();
                        }

                    }

                break;
        }

    }
    private ServiceConnection mConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.

            mService = IOpenVPNAPIService.Stub.asInterface(service);
            // IOpenVPNAPIService. .LocalBinder binder = (OpenVPNService.LocalBinder) service;
            // mService = binder.getService();
            try {
                // Request permission to use the API
                Intent i = mService.prepare(afterlogin.this.getPackageName());
                if (i != null) {
                    startActivityForResult(i, ICS_OPENVPN_PERMISSION);
                } else {
                    onActivityResult(ICS_OPENVPN_PERMISSION, Activity.RESULT_OK, null);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            Log.v("TAG PROCESS", "DISCONNECT");
            mService = null;

        }
    };

    private void bindservice(){
        Intent intent = new Intent(this, IOpenVPNAPIService.class);
        //intent.setAction(IOpenVPNAPIService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("inside onactivity result of afterlogin");
        if(resultCode==100){

        }
        else{

        }
    }
    public void doParseConfig(String configStr) {
        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfigStr(configStr);
            mResult = cp.convertProfile();
            mResult.mName = preff.getString("serverIp","");
            mResult.mServerName = preff.getString("serverIp","");
            mResult.mUseUdp=true;
            mResult.mIPv4Address=preff.getString("serverIp","");;
            return;
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    public void onEventMainThread(String status){
        System.out.println("status is" + status);
        ProgressDialogClass.Dismiss();
        b_conn.setText(status);
        textView9.setText("Connected");
        edit.putBoolean("Connect", true);
        edit.commit();

        boolean value=preff.getBoolean("Connect", false);
        System.out.println("value is" + value);


    }

    class GetCountry extends AsyncTask<String,String,String>{

        String data;
        @Override
        protected String doInBackground(String... strings) {
            data=CommonClass.getJSONFromUrl(CHANGE_COUNTRY,list);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogClass.ShowDialog(afterlogin.this, "Loading...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogClass.Dismiss();
            if(data!=null){
                System.out.println("data" + data);
                if(data.contains("<x>")){
                    String dataget[]=data.split("<x>");
                    String serverip=dataget[0];
                    String type=dataget[1];
                    System.out.println("type is"+type);
                    edit.putString("serverIp", serverip);
                    edit.putString("type", type);
                    edit.commit();
                    Disconnect();
                }
                else{
                    Toast.makeText(afterlogin.this,"Unable to fetch the data",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(afterlogin.this,"Unable to fetch the data",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Connect(){
        b_conn.setText("Connecting...");
        statusVPN="Connecting...";
        ProgressDialogClass.ShowDialog(afterlogin.this, "Connecting...");
        Intent intent = new Intent(afterlogin.this, LaunchCVPN.class);
        intent.putExtra(LaunchCVPN.EXTRA_KEY, mResult);
        Log.v("TEST", mResult.getUUID().toString());
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }

    public void Disconnect(){

        VpnService ss = new VpnService();
        VpnService.prepare(getApplicationContext());
        ss.onDestroy();
        ss.onLowMemory();
        ss.onRevoke();
        edit.putBoolean("Connect", false);
        edit.commit();
        OpenVPNService open = new OpenVPNService();
        open.onRevoke();
        System.out.println("inside disconnect");
        b_conn.setText("Connect");
        textView9.setText("Disconnected");
        if (mService != null) {

            try {
                mService.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void StartTimer(){
        if(timer==null){
            long values=preff.getLong("haveUntil",0);
            System.out.println("long value" + values);
            timer=new CountDownTimer(values,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String time = String.format("%02d:%02d:%02d:%02d", TimeUnit.MILLISECONDS.toDays(millisUntilFinished),TimeUnit.MILLISECONDS.toHours(millisUntilFinished)-TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                    timeLeft.setText(time);

                }

                @Override
                public void onFinish() {
                    timeLeft.setText("00:00:00:00");
                }
            };
            timer.start();
        }
    }
    public void StopTimer(){
        if(timer!=null){
            System.out.println("time"+converttimeTomili());
            preff.edit().putLong("haveUntil",converttimeTomili()).commit();
            timer.cancel();
            timer=null;
        }
    }

    public  long converttimeTomili(){
        long value=0;
        try{
            String timelleft=timeLeft.getText().toString();
            String values[]=timelleft.split(":");
            value=Integer.parseInt(values[0])*24*60*60*1000+Integer.parseInt(values[1])*60*60*1000+Integer.parseInt(values[2])*60*1000+Integer.parseInt(values[3])*1000;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    class AdBlock extends AsyncTask<List<BasicNameValuePair>,String,String>{

        String data="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogClass.ShowDialog(afterlogin.this,"Loading...");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ProgressDialogClass.Dismiss();
            if(data!=null){
                System.out.println("inside post execute"+data);
                String value=data.trim().toString();
                if(value.equals("no")){
                    Toast.makeText(afterlogin.this,"please update to premium account to enable this feature",Toast.LENGTH_LONG).show();
                    adBlocker.setChecked(false);
                }
                else if(value.equals("ok")){
                    Toast.makeText(afterlogin.this,"successfully updated",Toast.LENGTH_LONG).show();
                    if(adBlocker.isChecked()){
                        preff.edit().putBoolean("check",true).commit();
                    }
                    else{
                        preff.edit().putBoolean("check",false).commit();
                    }
                }
            }
            else{
                adBlocker.setChecked(false);
                Toast.makeText(afterlogin.this,"Unable to fetch data",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(List<BasicNameValuePair>... lists) {

            data=CommonClass.getJSONFromUrl(UPDATE_ACCOUNT,lists[0]);
            return null;
        }
    }

}