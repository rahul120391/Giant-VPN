package com.giantvpn;

import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CallService extends AsyncTask<String, Void, String> {

	Context context;
	ServiceCallback back;
	ProgressDialog dialog;
	List<BasicNameValuePair> data;

	public CallService(Context context, ServiceCallback back,List<BasicNameValuePair> data) {
		this.context = context;
		this.back = back;
		this.data = data;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ProgressDialogClass.ShowDialog(context,"Loading...");
	}

	@Override
	protected String doInBackground(String... params) {
		return CommonClass.getJSONFromUrl(params[0], data);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ProgressDialogClass.Dismiss();
		back.callbackCall(result);
	}

}
