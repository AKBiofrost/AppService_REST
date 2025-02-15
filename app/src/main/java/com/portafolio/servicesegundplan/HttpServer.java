package com.portafolio.servicesegundplan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {

    public HttpServer(int port) {
        super(port); // Inicia el servidor en el puerto especificado
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Aquí manejas las peticiones
        String msg = "<html><body><h1>Hello server</h1>\n";
        msg += "<p>We serve " + session.getUri() + " !</p>";
        // Aquí manejas la solicitud HTTP
        // Después de manejar la solicitud, inicias el FloatingDialogService
        return newFixedLengthResponse(msg + "</body></html>\n");
    }


}
