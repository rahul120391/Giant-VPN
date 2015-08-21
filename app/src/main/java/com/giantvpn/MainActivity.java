package com.giantvpn;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    String URL = "http://fondol.com/fastvpn_an/signup_v1.php";
    HttpResponse response = null;
    ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void onSignUp(View v){


        pg = new ProgressDialog(this);
        new AsyncCaller().execute();
        /*//try{

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);



            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", "umesh.appsmaven@gmail.com"));
                nameValuePairs.add(new BasicNameValuePair("password", "1111"));
                nameValuePairs.add(new BasicNameValuePair("deviceUqID", "myid"));
                //nameValuePairs.add(new BasicNameValuePair("deviceType  ", "Phone"));
                *//*nameValuePairs.add(new BasicNameValuePair("latitude", "5552"));
                nameValuePairs.add(new BasicNameValuePair("longitude", "5565"));
                nameValuePairs.add(new BasicNameValuePair("email", "Kaifi123@gmail.com"));
                nameValuePairs.add(new BasicNameValuePair("device_token", "kfjgjkg"));
                nameValuePairs.add(new BasicNameValuePair("gender", ""+0));
                nameValuePairs.add(new BasicNameValuePair("profile_pic", ""));
                nameValuePairs.add(new BasicNameValuePair("register_type", ""+0));
                nameValuePairs.add(new BasicNameValuePair("password", "11111"));*//*
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                response = httpclient.execute(httppost);
               // httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
    //}
        //catch(Exception e){
        //Log.e("","");

       // }

    }


    private class AsyncCaller extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pg.setMessage("Loading ...");
            pg.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            /*try{

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);

                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("email", "uknanu@gmail.com"));
                    nameValuePairs.add(new BasicNameValuePair("password", "1111"));
                    nameValuePairs.add(new BasicNameValuePair("deviceUqID", "myid"));
                    nameValuePairs.add(new BasicNameValuePair("deviceType  ", "Phone"));
                    *//*nameValuePairs.add(new BasicNameValuePair("latitude", "5552"));
                    nameValuePairs.add(new BasicNameValuePair("longitude", "5565"));
                    nameValuePairs.add(new BasicNameValuePair("email", "Kaifi123@gmail.com"));
                    nameValuePairs.add(new BasicNameValuePair("device_token", "kfjgjkg"));
                    nameValuePairs.add(new BasicNameValuePair("gender", ""+0));
                    nameValuePairs.add(new BasicNameValuePair("profile_pic", ""));
                    nameValuePairs.add(new BasicNameValuePair("register_type", ""+0));
                    nameValuePairs.add(new BasicNameValuePair("password", "11111"));*//*
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    response = httpclient.execute(httppost);
                    // httpclient.execute(httppost);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                catch(Exception e){
                Log.e("Error",""+e);
                }*/


            List<NameValuePair> userInfo = new ArrayList<NameValuePair>(4);
            String URL = "http://fondol.com/fastvpn_an/signup_v1.php";
            String response = "no response";

            HttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            HttpPost httpPost = new HttpPost(URL);

            userInfo.add(new BasicNameValuePair("email", "uknanu@gmail.com"));
            userInfo.add(new BasicNameValuePair("password", "1111"));
            userInfo.add(new BasicNameValuePair("deviceUqID", "myid"));
            userInfo.add(new BasicNameValuePair("deviceType  ", "Phone"));

            try{
                httpPost.setEntity(new UrlEncodedFormEntity(userInfo, HTTP.UTF_8));
                httpResponse = httpClient.execute(httpPost);
                Log.e("httpResponse",""+httpResponse);
                httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pg.dismiss();
            Log.e("Error in Sing Up ", "-" + result);

        }

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
}
