package com.giantvpn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class MyFragment extends Fragment {

	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	public static String mssg , k1 ,i1;
	
	public static final MyFragment newInstance(int img,String msg)
	{

		MyFragment f = new MyFragment();
		Bundle bdl = new Bundle(1);
		bdl.putInt("i1",img);
		bdl.putCharSequence("k1",msg);

		f.setArguments(bdl);
		return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		int img = getArguments().getInt("i1");
		String messag= getArguments().getString("k1");
		View v = inflater.inflate(R.layout.myfragment_layout, container, false);
		ImageView im = (ImageView) v.findViewById(R.id.imageView);
		im.setImageResource(img);
		TextView tv = (TextView) v.findViewById(R.id.tv1);
		tv.setText(messag);
		return v;
    }



}
