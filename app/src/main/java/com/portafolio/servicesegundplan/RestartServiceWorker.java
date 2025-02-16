package com.portafolio.servicesegundplan;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RestartServiceWorker extends Worker {

    public RestartServiceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Reiniciar el servicio
        Intent serviceIntent = new Intent(getApplicationContext(), ServerService.class);
        getApplicationContext().startService(serviceIntent);
        return Result.success();
    }
}