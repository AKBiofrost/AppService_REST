package com.portafolio.servicesegundplan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import fi.iki.elonen.NanoHTTPD;


public class ServerService extends Service {

    private HttpServer server;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ServerServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        server = new HttpServer(8080); // Inicia el servidor en el puerto 8080
        createNotificationChannel();
        try {
            server.start(); // Inicia el servidor
            Log.d("ServerService", "Servidor HTTP iniciado");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ServerService", "Error al iniciar el servidor: " + e.getMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServerService", "Service onStartCommand");

        // Aquí puedes realizar tareas en segundo plano
        // Crear una notificación para el servicio en primer plano
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        Log.i("ServerService", "Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Servicio en ejecución")
                    .setContentText("El servicio está en segundo plano")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();

            // Iniciar el servicio en primer plano
            startForeground(NOTIFICATION_ID, notification);
        }


        // Si el servicio es eliminado, se reiniciará automáticamente
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stop(); // Detiene el servidor cuando el servicio se destruye
            Log.d("ServerService", "Servidor HTTP detenido");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Clase interna para el servidor HTTP
    private class HttpServer extends NanoHTTPD {

        public HttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            // Aquí manejas la solicitud HTTP
            // Después de manejar la solicitud, inicias el FloatingDialogService
            startFloatingDialog();

            // Devuelve una respuesta HTTP
            return newFixedLengthResponse("Solicitud recibida");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio en primer plano",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void startFloatingDialog() {
        // Inicia el FloatingDialogService
        Log.d("ServerService", "FloatingDialogService iniciado");
        Intent intent = new Intent(this, FloatingDialogService.class);
        startService(intent);

    }
}





