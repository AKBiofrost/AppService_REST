package com.portafolio.servicesegundplan;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class ServerService extends Service {

    private HttpServer server;
    private HandlerThread handlerThread;
    private Handler handler;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ServerServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread("ServiceHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

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

        // Perform the task on a background thread
        //  handler.post(() -> {
        // Long-running task here
        // ...
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Aquí puedes realizar tareas en segundo plano
                // Crear una notificación para el servicio en primer plano
                Intent notificationIntent = new Intent(ServerService.this, MainActivity.class);
                PendingIntent pendingIntent;
                Log.i("ServerService", "Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    pendingIntent = PendingIntent.getActivity(ServerService.this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
                    try {
                        Notification notification = new NotificationCompat.Builder(ServerService.this, CHANNEL_ID)
                                .setContentTitle("Servicio en ejecución")
                                .setContentText("El servicio está en segundo plano")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentIntent(pendingIntent)
                                .build();

                        // Iniciar el servicio en primer plano
                        startForeground(NOTIFICATION_ID, notification);
                    } catch (Exception e) {
                        Log.i("ServerService", "Build.VERSION.SDK_INT " + e.getMessage());
                    }

                }
            }
        });


        // Si el servicio es eliminado, se reiniciará automáticamente
        // Stop the service when done
        // stopSelf();
        //    });


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
            // Leer el cuerpo de la solicitud
            Method method = session.getMethod();
            String uri = session.getUri();
            // Verificar si es una solicitud POST o PUT
            if (Method.POST.equals(method) || Method.PUT.equals(method)) {
                try {
                    // Obtener el cuerpo de la solicitud
                    Map<String, String> headers = session.getHeaders();
                    int contentLength = Integer.parseInt(headers.get("content-length"));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(session.getInputStream()));
                    char[] buffer = new char[contentLength];
                    reader.read(buffer, 0, contentLength);
                    String requestBody = new String(buffer);

                    // Aquí puedes procesar el cuerpo de la solicitud
                    System.out.println("Cuerpo de la solicitud: " + requestBody);
                    JSONObject jsonRequest;
                    jsonRequest = new JSONObject(requestBody);
                    // Aquí puedes acceder a los valores del JSON
                    String someKey = jsonRequest.getString("someKey");
                    Log.i("ServerService", "Valor de someKey: " + someKey);
                    Valores.Peticion = someKey;
                    // Responder con un mensaje de éxito
                  //  return newFixedLengthResponse(Response.Status.OK, "text/plain", "Solicitud recibida: " + requestBody);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error al leer el cuerpo de la solicitud");
                }
            }
            else {
                // Responder para otros métodos HTTP
                return newFixedLengthResponse(Response.Status.OK, "text/plain", "Método no soportado");
            }
            // Después de manejar la solicitud, inicias el FloatingDialogService
            startFloatingDialog();
// Crear un objeto JSON
            JSONObject jsonResponse = new JSONObject();
            try {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Solicitud recibida para " + Valores.Peticion);

            } catch (JSONException e) {

            }

            // Convertir el JSON a String
            String jsonString = jsonResponse.toString();
            Log.d("ServerService", "Servidor HTTP MainActivity.Block " + MainActivity.Block);

            do {
                Log.d("ServerService", "Servidor HTTP esperando accion usuario");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                }
            } while (MainActivity.Block);


            // Devolver una respuesta HTTP con el JSON en el cuerpo
            return newFixedLengthResponse(Response.Status.OK, "application/json", jsonString);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio en primer plano",
                    NotificationManager.IMPORTANCE_HIGH
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





