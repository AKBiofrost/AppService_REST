package com.portafolio.servicesegundplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Reiniciando el servicio...");
        Intent serviceIntent = new Intent(context, ServerService.class);
        context.startService(serviceIntent);
    }
}
