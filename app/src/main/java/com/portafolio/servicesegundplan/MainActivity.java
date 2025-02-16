package com.portafolio.servicesegundplan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_OVERLAY_PERMISSION = 1000;

    public static boolean Block = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
            }
        }

        TextView textView = findViewById(R.id.IP);
        textView.setText(getIpAddress(this));
        // Crear una solicitud de trabajo única
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RestartServiceWorker.class)
                .build();

        // Programar el trabajo con WorkManager
        WorkManager.getInstance(this).enqueue(workRequest);

        // Iniciar el servicio manualmente
        Intent serviceIntent = new Intent(this, ServerService.class);
        startForegroundService(serviceIntent);

    }


    public static String getIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(WIFI_SERVICE);
        String ipAddress = intToInetAddress(wifiManager.getDhcpInfo().ipAddress).toString();
        ipAddress = ipAddress.substring(1);
        return ipAddress;
    }

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};
        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // Permiso concedido, puedes crear la ventana flotante
                } else {
                    // Permiso denegado, muestra un mensaje al usuario
                }
            }
        }
    }

}