package com.portafolio.servicesegundplan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class FloatingDialogService extends Service {

    private WindowManager windowManager;
    private View dialogView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        dialogView = createDialogView();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Requires SYSTEM_ALERT_WINDOW permission
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM;
        if (windowManager != null) {
            windowManager.addView(dialogView, params);
        } else {
            Log.e("FloatingDialogService", "WindowManager es nulo");
        }
    }

    private View createDialogView() {
        // Inflate your custom dialog layout
        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        Log.d("createDialogView", "createDialogView " + view);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView message = view.findViewById(R.id.dialog_message);
        Button closeButton = view.findViewById(R.id.dialog_close_button);

        title.setText("PETICION: " + Valores.Peticion);
        message.setText("SE VA A RESPONDER AL CLIENTE ");

        closeButton.setOnClickListener(v -> {
            MainActivity.Block = false;
            stopSelf();

        }); // Stop the service to remove the dialog

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogView != null) {
            windowManager.removeView(dialogView);
        }
    }
}

