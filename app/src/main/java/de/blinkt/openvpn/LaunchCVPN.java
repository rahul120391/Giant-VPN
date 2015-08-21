/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.giantvpn.ProgressDialogClass;
import com.giantvpn.R;
import com.giantvpn.afterlogin;

import java.io.IOException;

import de.blinkt.openvpn.activities.LogWindow;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

/**
 * Created by ruslan on 10/21/14.
 */
public class LaunchCVPN extends Activity {
    public static final String EXTRA_KEY = "de.blinkt.openvpn.shortcutProfileUUID";
    public static final String EXTRA_NAME = "de.blinkt.openvpn.shortcutProfileName";
    public static final String EXTRA_HIDELOG = "de.blinkt.openvpn.showNoLogWindow";

    private static final int START_VPN_PROFILE = 70;


    private ProfileManager mPM;
    private VpnProfile mSelectedProfile;
    private boolean mhideLog = false;

    private boolean mCmfixed = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Resolve the intent

        final Intent intent = getIntent();
        final String action = intent.getAction();

        // If the intent is a request to create a shortcut, we'll do that and exit
        //
        mSelectedProfile = (VpnProfile) intent.getSerializableExtra(EXTRA_KEY);
        String username=mSelectedProfile.mUsername;
        String password=mSelectedProfile.mPassword;
        System.out.println("username"+username);
        System.out.println("password"+password);
        System.out.println("servername"+mSelectedProfile.mServerName);
        launchVPN();
        // new startOpenVpnThread().start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
               // ProgressDialogClass.ShowDialog(LaunchCVPN.this,"Connecting...");
                Log.e(" === Dismiss c3", "" + Activity.RESULT_OK);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                boolean showLogWindow = prefs.getBoolean("showlogwindow", true);

                if (!mhideLog && showLogWindow)
                     showLogWindow();
                    new startOpenVpnThread().start();

            }

        }
    }

    void showLogWindow() {

        Intent startLW = new Intent(getBaseContext(), LogWindow.class);
        startLW.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(startLW);

    }

    void showConfigErrorDialog(int vpnok) {
        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setTitle(R.string.config_error_found);
        d.setMessage(vpnok);
        d.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });
        d.show();
    }

    void launchVPN() {
        int vpnok = mSelectedProfile.checkProfile(this);
        System.out.println("vpnok value"+vpnok);
        if (vpnok != R.string.no_error_found) {
            //showConfigErrorDialog(vpnok);
            return;
        }


        Intent intent = VpnService.prepare(this);
        // Check if we want to fix /dev/tun
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean usecm9fix = prefs.getBoolean("useCM9Fix", false);
        boolean loadTunModule = prefs.getBoolean("loadTunModule", false);

        if (loadTunModule)
            execeuteSUcmd("insmod /system/lib/modules/tun.ko");

        if (usecm9fix && !mCmfixed) {
            execeuteSUcmd("chown system /dev/tun");
        }


        if (intent != null) {
            System.out.println("intent is not null");
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
                showLogWindow();
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                VpnStatus.logError(R.string.no_vpn_support_image);
                showLogWindow();
            }
        } else {
            System.out.println("inside thread"+"intent is null");
            new startOpenVpnThread().start();
            showLogWindow();
            // onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }

    }

    private void execeuteSUcmd(String command) {
        ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
        try {
            Process p = pb.start();
            int ret = p.waitFor();
            if (ret == 0)
                mCmfixed = true;
        } catch (InterruptedException e) {
            VpnStatus.logException("SU command", e);

        } catch (IOException e) {
            VpnStatus.logException("SU command", e);
        }
    }

    private class startOpenVpnThread extends Thread {

        @Override
        public void run() {

            System.out.println("inside openvpn thread"+mSelectedProfile);
            VPNLaunchHelper.startOpenVpn(mSelectedProfile, getBaseContext());
            finish();

        }

    }


}
