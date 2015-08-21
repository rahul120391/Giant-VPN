/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2. For full terms see the file doc/LICENSE.txt
 */

package com.giantvpn;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by arne on 13.10.13.
 */
public class LogWindow extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_window);
        finish();

    }
}
