package com.giantvpn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class login_a extends Activity implements ServiceCallback {

    Button b1;
    ImageView iv1;

    String str_email,str_password;
    EditText email, password;

    SharedPreferences.Editor edit ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        email = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        b1 = (Button) findViewById(R.id.button2);
        iv1 = (ImageView) findViewById(R.id.signinclose);


        edit = getSharedPreferences("ServerInfo",MODE_PRIVATE).edit();


        actioned();

    }


    public void actioned() {

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!email.getText().toString().trim()
                        .matches(Patterns.EMAIL_ADDRESS.pattern())){
                    email.setError("Invalid email address");
                }
                else if(password.getText().toString().trim().length()==0){
                    password.setError("Invalid password");
                }
                else{
                    List<BasicNameValuePair> list=new ArrayList<BasicNameValuePair>();
                    list.add(new BasicNameValuePair("email",email.getText().toString().trim()));
                    list.add(new BasicNameValuePair("password",password.getText().toString().trim()));
                    CallService service=new CallService(login_a.this,login_a.this,list);
                    service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,URLS.LOGIN_URL);
                }
            }
        });


        iv1.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(login_a.this, PageViewActivity.class);
                startActivity(i);

            }
        });

    }


    @Override
    public void callbackCall(String response) {
        if(response!=null){

            if(response.contains("<x>")){
                String resp[]=response.split("<x>");
                String ip_address=resp[0];
                String time_interval=resp[1];
                String type=resp[2];
                String accountType=resp[3];
                edit.putString("Username",email.getText().toString().trim());
                edit.putString("Password",password.getText().toString().trim());
                edit.putString("serverIp", ip_address);
                edit.putLong("haveUntil", Long.valueOf(time_interval));
                edit.putString("type",type);
                edit.putBoolean("check",false);
                edit.putString("accountType", accountType);
                edit.commit();
                Intent i=new Intent(login_a.this,afterlogin.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(login_a.this,response,Toast.LENGTH_SHORT).show();
            }


        }
        else{
            Toast.makeText(login_a.this,"Oops!Something went wrong, unable to login",Toast.LENGTH_SHORT).show();
        }
    }
}