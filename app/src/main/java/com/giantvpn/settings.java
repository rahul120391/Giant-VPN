package com.giantvpn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class settings extends Activity {

    TextView back;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sttings);


        String values[] = {getString(R.string.termsofservice),getString(R.string.contactsupport),getString(R.string.reinstall),
                getString(R.string.restore),getString(R.string.logout)};

        back = (TextView) findViewById(R.id.setback);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listv, values);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        action();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences preff=getSharedPreferences("ServerInfo", MODE_PRIVATE);
                preff.edit().clear().commit();
                Intent logout = new Intent(settings.this, PageViewActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
            }
        });


    }

    public void action() {

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(settings.this, afterlogin.class);
                startActivity(i);
                finish();


            }
        });

    }


}
