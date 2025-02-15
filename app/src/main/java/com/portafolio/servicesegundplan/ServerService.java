package com.portafolio.servicesegundplan;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import fi.iki.elonen.NanoHTTPD;


public class ServerService extends Service {

    private HttpServer server;

    @Override
    public void onCreate() {
        super.onCreate();
        server = new HttpServer(8080); // Inicia el servidor en el puerto 8080
        try {
            server.start(); // Inicia el servidor
            Log.d("ServerService", "Servidor HTTP iniciado");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ServerService", "Error al iniciar el servidor: " + e.getMessage());
        }
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

    private void startFloatingDialog() {
        // Inicia el FloatingDialogService
        Log.d("ServerService", "FloatingDialogService iniciado");
        Intent intent = new Intent(this, FloatingDialogService.class);
        startService(intent);

    }
}





