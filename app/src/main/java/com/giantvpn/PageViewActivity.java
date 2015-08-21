package com.giantvpn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PageViewActivity extends FragmentActivity {

    MyPageAdapter pageAdapter;
    ViewPager pager;
    Button b1;
    TextView tv;
    String msg1,msg2,msg3,msg4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_view);
        SharedPreferences prefs=getSharedPreferences("ServerInfo", MODE_PRIVATE);
            if(prefs.getString("serverIp","")!=null && !prefs.getString("serverIp","").equalsIgnoreCase("")){
                finish();
                Intent intent=new Intent(PageViewActivity.this,afterlogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        else{
            List<Fragment> fragments = getFragments();
            pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

            pager = (ViewPager) findViewById(R.id.viewpager);
            pager.setAdapter(pageAdapter);

            pager.setCurrentItem(0);
            pageAdapter.setTimer(pager, 5);

            actionsper();
        }
    }



    private List<Fragment> getFragments(){

        List<Fragment> fList = new ArrayList<Fragment>();

        msg1=getString(R.string.blues);
        msg2=getString(R.string.hide);
        msg3=getString(R.string.unlock);
        msg4=getString(R.string.encrypt);


        fList.add(MyFragment.newInstance(R.drawable.blues,msg1));
    	fList.add(MyFragment.newInstance(R.drawable.hide,msg2));
    	fList.add(MyFragment.newInstance(R.drawable.unlock,msg3));
        //fList.add(MyFragment.newInstance(R.drawable.unlock,msg4));

    	return fList;
    }


    private class MyPageAdapter extends FragmentPagerAdapter {


        final Handler handler = new Handler();
        public Timer swipeTimer ;
        private List<Fragment> fragments;


        public void setTimer(final ViewPager myPager, int time){

            final int size =4;
            final Runnable Update = new Runnable() {

                int NUM_PAGES =size;
                int currentPage = 0 ;
                public void run() {
                    if (currentPage == NUM_PAGES ) {
                        currentPage = 0;
                    }
                    myPager.setCurrentItem(currentPage++, true);
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 300, time*800);
        }




        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {

            return this.fragments.get(position);
        }
     
        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public void actionsper() {

        b1 = (Button) findViewById(R.id.account);

        b1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),login.class);
                startActivity(i);

            }
        } );

      tv = (TextView) findViewById(R.id.login);

        tv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                finish();
                Intent i = new Intent(PageViewActivity.this,login_a.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        } );




    }


}
