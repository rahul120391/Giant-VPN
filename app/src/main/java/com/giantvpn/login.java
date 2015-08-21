package com.giantvpn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class login extends Activity {


    ImageView iv1;
    Button signUp;
    String str_email,str_password;
    EditText email, password;

    String URL = "http://fondol.com/fastvpn_an/signup_v1.php";
    HttpResponse response = null;
    ProgressDialog pg;
    SharedPreferences.Editor edit ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        iv1 = (ImageView) findViewById(R.id.signupclose);
        signUp = (Button) findViewById(R.id.btn_login);
        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        pg = new ProgressDialog(this);
        edit = getSharedPreferences("ServerInfo",MODE_PRIVATE).edit();
        try{
            StringBuilder buf=new StringBuilder();
            InputStream json=getResources().getAssets().open("openvpn.conf");
            System.out.println("jsonn"+json);
            BufferedReader in=
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            System.out.println("Buffer"+buf.toString());
            in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        action();

        
    }


    private void action() {

        iv1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view){

                Intent i = new Intent(login.this, PageViewActivity.class);
                startActivity(i);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view){

                str_email = email.getText().toString();
                str_password = password.getText().toString();

                if(str_email.equals("") ){
                    email.setError("");

                } else if(str_password.equals("")) {
                    password.setError("");
                } else {
                    new AsyncCaller().execute();
                }

            }
        });
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

            List<NameValuePair> userInfo = new ArrayList<NameValuePair>(4);
            String URL = "http://fondol.com/fastvpn_an/signup_v1.php";
            String response = "no response";

            HttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;
            HttpPost httpPost = new HttpPost(URL);

            userInfo.add(new BasicNameValuePair("email", str_email));
            userInfo.add(new BasicNameValuePair("password", str_password));
            userInfo.add(new BasicNameValuePair("deviceUqID", "myid"));
            userInfo.add(new BasicNameValuePair("deviceType  ", "Phone"));

            try{
                httpPost.setEntity(new UrlEncodedFormEntity(userInfo, HTTP.UTF_8));
                httpResponse = httpClient.execute(httpPost);
                Log.e("httpResponse", "" + httpResponse);
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

            if(result.equals("ok")) {
                Intent i = new Intent(login.this, PageViewActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), ""+result, Toast.LENGTH_LONG).show();
            }


        }

    }
    public String ReadFromfile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }


}