package com.giantvpn;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by osx on 03/08/15.
 */
public class ProgressDialogClass {
    public static ProgressDialog dialog;

    public static void ShowDialog(Context context,String message){

        if(dialog==null || !dialog.isShowing()){
            System.out.println("dialog is showing");
            dialog=new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
        }
    }
   public static void Dismiss(){
       if(dialog!=null || dialog.isShowing()){
           dialog.dismiss();
       }
   }

}
