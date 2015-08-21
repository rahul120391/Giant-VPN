package com.giantvpn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CallServiceGet extends AsyncTask<String, Void, String> {

	Context context;
	ServiceCallbackGet back;
	ProgressDialog dialog;
    String url;
	public CallServiceGet(Context context, ServiceCallbackGet back,String url) {
		this.context = context;
		this.back = back;
		this.url=url;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ProgressDialogClass.ShowDialog(context, "Loading...");
	}

	@Override
	protected String doInBackground(String... params) {
		return CommonClass.getJSON(url);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ProgressDialogClass.Dismiss();
		back.callbackCallGet(result);
	}

}
